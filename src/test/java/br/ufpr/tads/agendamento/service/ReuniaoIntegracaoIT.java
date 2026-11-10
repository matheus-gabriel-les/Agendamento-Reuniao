
package br.ufpr.tads.agendamento.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import br.ufpr.tads.agendamento.model.ModoDivisao;
import br.ufpr.tads.agendamento.model.RespostaDisponibilidade;
import br.ufpr.tads.agendamento.model.Reuniao;
import br.ufpr.tads.agendamento.util.Conexao;

class ReuniaoIntegracaoIT {

    private static final String BANCO_TESTE =
            "agendamento_reunioes_test";

    private final ReuniaoService reuniaoService =
            new ReuniaoService();

    private final DisponibilidadeService disponibilidadeService =
            new DisponibilidadeService();

    private final FinalizacaoService finalizacaoService =
            new FinalizacaoService();

    private final ExclusaoReuniaoService exclusaoService =
            new ExclusaoReuniaoService();

    private Long reuniaoId;

    @BeforeAll
    static void verificarAmbiente() throws SQLException {

        String banco = System.getenv("DB_NAME");

        assertEquals(
                BANCO_TESTE,
                banco,
                "Os testes de integracao somente podem "
                + "utilizar o banco agendamento_reunioes_test.");

        try (Connection conexao = Conexao.conectar()) {

            String catalogo = conexao.getCatalog();

            assertEquals(
                    BANCO_TESTE,
                    catalogo,
                    "A conexao nao aponta para o banco de testes.");
        }
    }

    @BeforeEach
    void preparar() throws SQLException {

        Reuniao reuniao = novaReuniao();

        reuniaoService.criar(
                reuniao,
                List.of(
                        new ReuniaoService.ConvidadoInicial(
                                "Maria Teste",
                                "maria@exemplo.com"),
                        new ReuniaoService.ConvidadoInicial(
                                "Joao Teste",
                                "joao@exemplo.com")
                )
        );

        reuniaoId = reuniao.getId();

        assertNotNull(reuniaoId);
    }

    @AfterEach
    void limpar() throws SQLException {

        if (reuniaoId != null
                && contar(
                        "SELECT COUNT(*) FROM reuniao "
                        + "WHERE id = ?",
                        reuniaoId) > 0) {

            exclusaoService.excluir(reuniaoId);
        }
    }

    @Test
    void deveCriarReuniaoComHorariosEParticipantes()
            throws SQLException {

        assertEquals(
                1,
                contar(
                        "SELECT COUNT(*) FROM reuniao "
                        + "WHERE id = ?",
                        reuniaoId));

        assertEquals(
                2,
                contar(
                        "SELECT COUNT(*) FROM horario "
                        + "WHERE reuniao_id = ?",
                        reuniaoId));

        assertEquals(
                3,
                contar(
                        "SELECT COUNT(*) FROM participante "
                        + "WHERE reuniao_id = ?",
                        reuniaoId));

        assertEquals(
                1,
                contar(
                        "SELECT COUNT(*) FROM participante "
                        + "WHERE reuniao_id = ? "
                        + "AND proponente = TRUE",
                        reuniaoId));
    }

    @Test
    void deveRegistrarAlterarERemoverResposta()
            throws SQLException {

        String token = buscarTokenConvidado();
        Long horarioId = buscarPrimeiroHorario();

        disponibilidadeService.responder(
                token,
                horarioId,
                RespostaDisponibilidade.SIM);

        assertEquals(
                "SIM",
                buscarResposta(token, horarioId));

        disponibilidadeService.responder(
                token,
                horarioId,
                RespostaDisponibilidade.NAO);

        assertEquals(
                "NAO",
                buscarResposta(token, horarioId));

        assertEquals(
                1,
                contar(
                        "SELECT COUNT(*) "
                        + "FROM disponibilidade d "
                        + "JOIN participante p "
                        + "ON p.id = d.participante_id "
                        + "WHERE p.token = ? "
                        + "AND d.horario_id = ?",
                        token,
                        horarioId));

        disponibilidadeService.removerResposta(
                token,
                horarioId);

        assertNull(buscarResposta(token, horarioId));

        assertEquals(
                0,
                contar(
                        "SELECT COUNT(*) "
                        + "FROM participante "
                        + "WHERE token = ? "
                        + "AND respondido_em IS NOT NULL",
                        token));
    }

    @Test
    void deveImpedirAlteracoesAposFinalizacao()
            throws SQLException {

        Long horarioId = buscarPrimeiroHorario();
        String token = buscarTokenConvidado();

        finalizacaoService.finalizar(
                reuniaoId,
                horarioId);

        assertEquals(
                horarioId,
                finalizacaoService
                        .buscarHorarioDefinitivo(reuniaoId));

        assertThrows(
                IllegalArgumentException.class,
                () -> disponibilidadeService.responder(
                        token,
                        horarioId,
                        RespostaDisponibilidade.SIM));

        assertThrows(
                IllegalArgumentException.class,
                () -> finalizacaoService.finalizar(
                        reuniaoId,
                        horarioId));
    }

    @Test
    void deveExcluirReuniaoEDadosRelacionados()
            throws SQLException {

        Long horarioId = buscarPrimeiroHorario();
        String token = buscarTokenConvidado();

        disponibilidadeService.responder(
                token,
                horarioId,
                RespostaDisponibilidade.TALVEZ);

        finalizacaoService.finalizar(
                reuniaoId,
                horarioId);

        Long idExcluido = reuniaoId;

        exclusaoService.excluir(idExcluido);

        assertEquals(
                0,
                contar(
                        "SELECT COUNT(*) FROM reuniao "
                        + "WHERE id = ?",
                        idExcluido));

        assertEquals(
                0,
                contar(
                        "SELECT COUNT(*) FROM horario "
                        + "WHERE reuniao_id = ?",
                        idExcluido));

        assertEquals(
                0,
                contar(
                        "SELECT COUNT(*) FROM participante "
                        + "WHERE reuniao_id = ?",
                        idExcluido));

        assertEquals(
                0,
                contar(
                        "SELECT COUNT(*) "
                        + "FROM reuniao_finalizacao "
                        + "WHERE reuniao_id = ?",
                        idExcluido));

        assertEquals(
                0,
                contar(
                        "SELECT COUNT(*) FROM disponibilidade "
                        + "WHERE horario_id = ?",
                        horarioId));
    }

    @Test
    void deveFazerRollbackSeUmConvidadoFalhar()
            throws SQLException {

        Reuniao reuniao = novaReuniao();

        String titulo = reuniao.getTitulo();

        // A collation utf8mb4_unicode_ci do schema
        // considera letras acentuadas equivalentes
        // para a restricao de unicidade.

        List<ReuniaoService.ConvidadoInicial> convidados =
                List.of(
                        new ReuniaoService.ConvidadoInicial(
                                "Andre",
                                "andre@exemplo.com"),
                        new ReuniaoService.ConvidadoInicial(
                                "Andre Acentuado",
                                "andré@exemplo.com")
                );

        assertThrows(
                SQLException.class,
                () -> reuniaoService.criar(
                        reuniao,
                        convidados));

        assertEquals(
                0,
                contar(
                        "SELECT COUNT(*) FROM reuniao "
                        + "WHERE titulo = ?",
                        titulo));
    }

    private Reuniao novaReuniao() {

        Reuniao reuniao = new Reuniao();

        reuniao.setTitulo(
                "Teste Integracao "
                + UUID.randomUUID());

        reuniao.setDescricao(
                "Reuniao criada por teste automatizado.");

        reuniao.setLocalReuniao("Laboratorio");

        reuniao.setNomeProponente("Proponente Teste");

        reuniao.setEmailProponente(
                "proponente@exemplo.com");

        reuniao.setDataInicio(
                LocalDate.of(2030, 1, 10));

        reuniao.setDataFim(
                LocalDate.of(2030, 1, 11));

        reuniao.setModoDivisao(ModoDivisao.DIA);

        return reuniao;
    }

    private Long buscarPrimeiroHorario()
            throws SQLException {

        String sql =
                "SELECT id FROM horario "
                + "WHERE reuniao_id = ? ORDER BY id LIMIT 1";

        try (Connection conexao = Conexao.conectar();
             PreparedStatement stmt =
                     conexao.prepareStatement(sql)) {

            stmt.setLong(1, reuniaoId);

            try (ResultSet rs = stmt.executeQuery()) {
                assertTrue(rs.next());
                return rs.getLong(1);
            }
        }
    }

    private String buscarTokenConvidado()
            throws SQLException {

        String sql =
                "SELECT token FROM participante "
                + "WHERE reuniao_id = ? "
                + "AND proponente = FALSE "
                + "ORDER BY id LIMIT 1";

        try (Connection conexao = Conexao.conectar();
             PreparedStatement stmt =
                     conexao.prepareStatement(sql)) {

            stmt.setLong(1, reuniaoId);

            try (ResultSet rs = stmt.executeQuery()) {
                assertTrue(rs.next());
                return rs.getString(1);
            }
        }
    }

    private String buscarResposta(
            String token,
            Long horarioId)
            throws SQLException {

        String sql =
                "SELECT d.resposta "
                + "FROM disponibilidade d "
                + "JOIN participante p "
                + "ON p.id = d.participante_id "
                + "WHERE p.token = ? "
                + "AND d.horario_id = ?";

        try (Connection conexao = Conexao.conectar();
             PreparedStatement stmt =
                     conexao.prepareStatement(sql)) {

            stmt.setString(1, token);
            stmt.setLong(2, horarioId);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next()
                        ? rs.getString(1)
                        : null;
            }
        }
    }

    private long contar(
            String sql,
            Object... parametros)
            throws SQLException {

        try (Connection conexao = Conexao.conectar();
             PreparedStatement stmt =
                     conexao.prepareStatement(sql)) {

            for (int i = 0; i < parametros.length; i++) {
                stmt.setObject(i + 1, parametros[i]);
            }

            try (ResultSet rs = stmt.executeQuery()) {
                rs.next();
                return rs.getLong(1);
            }
        }
    }
}
