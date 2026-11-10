
package br.ufpr.tads.agendamento.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import br.ufpr.tads.agendamento.model.ModoDivisao;
import br.ufpr.tads.agendamento.model.RespostaDisponibilidade;
import br.ufpr.tads.agendamento.model.Reuniao;
import br.ufpr.tads.agendamento.util.Conexao;

class ConcorrenciaReuniaoIT {

    private final ReuniaoService reuniaoService =
            new ReuniaoService();

    private final FinalizacaoService finalizacaoService =
            new FinalizacaoService();

    private final DisponibilidadeService disponibilidadeService =
            new DisponibilidadeService();

    private final ParticipanteService participanteService =
            new ParticipanteService();

    private final ExclusaoReuniaoService exclusaoService =
            new ExclusaoReuniaoService();

    private Long reuniaoId;
    private Long horarioId;
    private String token;

    @BeforeAll
    static void verificarBancoTeste() throws SQLException {

        assertEquals(
                "agendamento_reunioes_test",
                System.getenv("DB_NAME"),
                "Configure DB_NAME=agendamento_reunioes_test.");

        try (Connection conexao = Conexao.conectar()) {

            assertEquals(
                    "agendamento_reunioes_test",
                    conexao.getCatalog(),
                    "A conexao deve utilizar o banco de testes.");
        }
    }

    @BeforeEach
    void preparar() throws SQLException {

        Reuniao reuniao = new Reuniao();

        reuniao.setTitulo(
                "Concorrencia " + UUID.randomUUID());

        reuniao.setDescricao(
                "Teste de concorrencia.");

        reuniao.setLocalReuniao("Laboratorio");

        reuniao.setNomeProponente(
                "Proponente Teste");

        reuniao.setEmailProponente(
                "proponente@exemplo.com");

        reuniao.setDataInicio(
                LocalDate.of(2030, 1, 10));

        reuniao.setDataFim(
                LocalDate.of(2030, 1, 10));

        reuniao.setModoDivisao(ModoDivisao.DIA);

        reuniaoService.criar(
                reuniao,
                List.of(
                        new ReuniaoService.ConvidadoInicial(
                                "Convidado",
                                "convidado@exemplo.com")
                )
        );

        reuniaoId = reuniao.getId();

        try (Connection conexao = Conexao.conectar()) {

            try (PreparedStatement stmt =
                    conexao.prepareStatement(
                            "SELECT id FROM horario "
                            + "WHERE reuniao_id = ?")) {

                stmt.setLong(1, reuniaoId);

                try (ResultSet rs = stmt.executeQuery()) {
                    assertTrue(rs.next());
                    horarioId = rs.getLong(1);
                }
            }

            try (PreparedStatement stmt =
                    conexao.prepareStatement(
                            "SELECT token FROM participante "
                            + "WHERE reuniao_id = ? "
                            + "AND proponente = FALSE")) {

                stmt.setLong(1, reuniaoId);

                try (ResultSet rs = stmt.executeQuery()) {
                    assertTrue(rs.next());
                    token = rs.getString(1);
                }
            }
        }
    }

    @AfterEach
    void limpar() throws SQLException {

        if (reuniaoId != null) {
            try {
                exclusaoService.excluir(reuniaoId);
            } catch (IllegalArgumentException e) {
                // A reuniao pode ter sido excluida no teste.
            }
        }
    }

    @Test
    void apenasUmaFinalizacaoSimultaneaDeveSerAceita()
            throws Exception {

        ExecutorService executor =
                Executors.newFixedThreadPool(2);

        CountDownLatch inicio =
                new CountDownLatch(1);

        try {

            Future<Boolean> primeira = executor.submit(() -> {
                inicio.await();

                try {
                    finalizacaoService.finalizar(
                            reuniaoId, horarioId);
                    return true;
                } catch (IllegalArgumentException e) {
                    return false;
                }
            });

            Future<Boolean> segunda = executor.submit(() -> {
                inicio.await();

                try {
                    finalizacaoService.finalizar(
                            reuniaoId, horarioId);
                    return true;
                } catch (IllegalArgumentException e) {
                    return false;
                }
            });

            inicio.countDown();

            int sucessos = 0;

            if (primeira.get(15, TimeUnit.SECONDS)) {
                sucessos++;
            }

            if (segunda.get(15, TimeUnit.SECONDS)) {
                sucessos++;
            }

            assertEquals(1, sucessos);

            assertEquals(
                    horarioId,
                    finalizacaoService
                            .buscarHorarioDefinitivo(reuniaoId));

            assertEquals(
                    1,
                    contarFinalizacoes());

        } finally {
            executor.shutdownNow();
        }
    }

    @Test
    void naoDeveAdicionarConvidadoAposFinalizacao()
            throws SQLException {

        finalizacaoService.finalizar(
                reuniaoId, horarioId);

        assertThrows(
                IllegalArgumentException.class,
                () -> participanteService.cadastrar(
                        reuniaoId,
                        "Novo Convidado",
                        "novo@exemplo.com",
                        false));

        assertEquals(2, contarParticipantes());
    }

    @Test
    void naoDeveAlterarRespostaAposFinalizacao()
            throws SQLException {

        finalizacaoService.finalizar(
                reuniaoId, horarioId);

        assertThrows(
                IllegalArgumentException.class,
                () -> disponibilidadeService.responder(
                        token,
                        horarioId,
                        RespostaDisponibilidade.SIM));

        assertEquals(0, contarRespostas());
    }

    @Test
    void respostaEFinalizacaoDevemManterEstadoConsistente()
            throws Exception {

        ExecutorService executor =
                Executors.newFixedThreadPool(2);

        CountDownLatch inicio =
                new CountDownLatch(1);

        try {

            Future<Boolean> resposta = executor.submit(() -> {
                inicio.await();

                try {
                    disponibilidadeService.responder(
                            token,
                            horarioId,
                            RespostaDisponibilidade.SIM);

                    return true;

                } catch (IllegalArgumentException e) {
                    return false;
                }
            });

            Future<Boolean> finalizacao = executor.submit(() -> {
                inicio.await();

                finalizacaoService.finalizar(
                        reuniaoId, horarioId);

                return true;
            });

            inicio.countDown();

            boolean respostaAceita =
                    resposta.get(15, TimeUnit.SECONDS);

            assertTrue(
                    finalizacao.get(15, TimeUnit.SECONDS));

            assertNotNull(
                    finalizacaoService
                            .buscarHorarioDefinitivo(reuniaoId));

            int quantidade = contarRespostas();

            if (respostaAceita) {
                assertEquals(1, quantidade);
            } else {
                assertEquals(0, quantidade);
            }

            // Depois da finalizacao, nenhuma nova
            // resposta pode ser registrada.
            assertThrows(
                    IllegalArgumentException.class,
                    () -> disponibilidadeService.responder(
                            token,
                            horarioId,
                            RespostaDisponibilidade.NAO));

        } finally {
            executor.shutdownNow();
        }
    }

    private int contarFinalizacoes()
            throws SQLException {

        return contar(
                "SELECT COUNT(*) "
                + "FROM reuniao_finalizacao "
                + "WHERE reuniao_id = ?",
                reuniaoId);
    }

    private int contarParticipantes()
            throws SQLException {

        return contar(
                "SELECT COUNT(*) FROM participante "
                + "WHERE reuniao_id = ?",
                reuniaoId);
    }

    private int contarRespostas()
            throws SQLException {

        return contar(
                "SELECT COUNT(*) FROM disponibilidade d "
                + "JOIN participante p "
                + "ON p.id = d.participante_id "
                + "WHERE p.reuniao_id = ?",
                reuniaoId);
    }

    private int contar(
            String sql,
            Long id) throws SQLException {

        try (Connection conexao = Conexao.conectar();
             PreparedStatement stmt =
                     conexao.prepareStatement(sql)) {

            stmt.setLong(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                assertTrue(rs.next());
                return rs.getInt(1);
            }
        }
    }
}
