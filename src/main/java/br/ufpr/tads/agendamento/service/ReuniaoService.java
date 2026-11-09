
package br.ufpr.tads.agendamento.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import br.ufpr.tads.agendamento.dao.jdbc.HorarioDAOImpl;
import br.ufpr.tads.agendamento.dao.jdbc.ReuniaoDAOImpl;
import br.ufpr.tads.agendamento.model.Horario;
import br.ufpr.tads.agendamento.model.Reuniao;
import br.ufpr.tads.agendamento.util.Conexao;

public class ReuniaoService {

    private final ReuniaoDAOImpl reuniaoDAO;
    private final HorarioDAOImpl horarioDAO;
    private final GeradorHorarios geradorHorarios;

    public ReuniaoService() {
        this.reuniaoDAO = new ReuniaoDAOImpl();
        this.horarioDAO = new HorarioDAOImpl();
        this.geradorHorarios = new GeradorHorarios();
    }

    public Reuniao criar(Reuniao reuniao) throws SQLException {

        validarDados(reuniao);
        geradorHorarios.validar(reuniao);

        // Gera os tokens de acesso.
        reuniao.setTokenPublico(
                UUID.randomUUID().toString());

        reuniao.setTokenAdmin(
                UUID.randomUUID().toString());

        // Gera os horarios antes de salvar no banco.
        List<Horario> horarios =
                geradorHorarios.gerar(reuniao);

        try (Connection conexao = Conexao.conectar()) {

            conexao.setAutoCommit(false);

            try {
                // Primeiro insere a reuniao.
                reuniaoDAO.inserir(reuniao, conexao);

                // Depois insere os horarios associados.
                for (Horario horario : horarios) {
                    horario.setReuniaoId(reuniao.getId());
                    horarioDAO.inserir(horario, conexao);
                }

                // Confirma todas as operacoes.
                conexao.commit();

                return reuniao;

            } catch (SQLException | RuntimeException e) {

                try {
                    conexao.rollback();
                } catch (SQLException erroRollback) {
                    e.addSuppressed(erroRollback);
                }

                throw e;
            }
        }
    }

    private void validarDados(Reuniao reuniao) {

        if (reuniao == null) {
            throw new IllegalArgumentException(
                    "A reuniao nao pode ser nula.");
        }

        if (reuniao.getId() != null) {
            throw new IllegalArgumentException(
                    "A reuniao ja possui um ID.");
        }

        if (reuniao.getTitulo() == null
                || reuniao.getTitulo().isBlank()) {
            throw new IllegalArgumentException(
                    "O titulo da reuniao e obrigatorio.");
        }

        if (reuniao.getTitulo().length() > 150) {
            throw new IllegalArgumentException(
                    "O titulo deve ter ate 150 caracteres.");
        }

        if (reuniao.getDescricao() != null
                && reuniao.getDescricao().length() > 1000) {
            throw new IllegalArgumentException(
                    "A descricao deve ter ate 1000 caracteres.");
        }

        if (reuniao.getLocalReuniao() != null
                && reuniao.getLocalReuniao().length() > 200) {
            throw new IllegalArgumentException(
                    "O local deve ter ate 200 caracteres.");
        }

        if (reuniao.getNomeProponente() == null
                || reuniao.getNomeProponente().isBlank()) {
            throw new IllegalArgumentException(
                    "O nome do proponente e obrigatorio.");
        }

        if (reuniao.getNomeProponente().length() > 100) {
            throw new IllegalArgumentException(
                    "O nome do proponente deve ter ate 100 caracteres.");
        }

        if (reuniao.getEmailProponente() == null
                || reuniao.getEmailProponente().isBlank()) {
            throw new IllegalArgumentException(
                    "O email do proponente e obrigatorio.");
        }

        if (reuniao.getEmailProponente().length() > 254
                || !reuniao.getEmailProponente().contains("@")) {
            throw new IllegalArgumentException(
                    "Informe um email valido.");
        }
    }
}
