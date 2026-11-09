
package br.ufpr.tads.agendamento.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;

import br.ufpr.tads.agendamento.dao.jdbc.HorarioDAOImpl;
import br.ufpr.tads.agendamento.dao.jdbc.ReuniaoDAOImpl;
import br.ufpr.tads.agendamento.model.Horario;
import br.ufpr.tads.agendamento.model.Reuniao;
import br.ufpr.tads.agendamento.util.Conexao;

public class ReuniaoService {

    private static final int LIMITE_CONVIDADOS = 100;

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");

    private final ReuniaoDAOImpl reuniaoDAO;
    private final HorarioDAOImpl horarioDAO;
    private final GeradorHorarios geradorHorarios;

    public ReuniaoService() {
        reuniaoDAO = new ReuniaoDAOImpl();
        horarioDAO = new HorarioDAOImpl();
        geradorHorarios = new GeradorHorarios();
    }

    public static class ConvidadoInicial {

        private final String nome;
        private final String email;

        public ConvidadoInicial(String nome, String email) {
            this.nome = nome;
            this.email = email;
        }

        public String getNome() {
            return nome;
        }

        public String getEmail() {
            return email;
        }
    }

    public Reuniao criar(Reuniao reuniao)
            throws SQLException {

        return criar(reuniao, List.of());
    }

    public Reuniao criar(
            Reuniao reuniao,
            List<ConvidadoInicial> convidados)
            throws SQLException {

        validarDados(reuniao);

        geradorHorarios.validar(reuniao);

        List<ConvidadoInicial> convidadosValidados =
                validarConvidados(reuniao, convidados);

        reuniao.setTokenPublico(
                UUID.randomUUID().toString());

        reuniao.setTokenAdmin(
                UUID.randomUUID().toString());

        List<Horario> horarios =
                geradorHorarios.gerar(reuniao);

        try (Connection conexao = Conexao.conectar()) {

            conexao.setAutoCommit(false);

            try {

                // Cadastro da reuniao.
                reuniaoDAO.inserir(reuniao, conexao);

                // Cadastro de todos os horarios.
                for (Horario horario : horarios) {

                    horario.setReuniaoId(
                            reuniao.getId());

                    horarioDAO.inserir(
                            horario, conexao);
                }

                // O proponente tambem participa.
                inserirParticipante(
                        conexao,
                        reuniao.getId(),
                        reuniao.getNomeProponente(),
                        reuniao.getEmailProponente(),
                        true);

                // Cadastro dos convidados iniciais.
                for (ConvidadoInicial convidado
                        : convidadosValidados) {

                    inserirParticipante(
                            conexao,
                            reuniao.getId(),
                            convidado.getNome(),
                            convidado.getEmail(),
                            false);
                }

                // Confirma somente se tudo funcionar.
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

    private void inserirParticipante(
            Connection conexao,
            Long reuniaoId,
            String nome,
            String email,
            boolean proponente)
            throws SQLException {

        String sql =
                "INSERT INTO participante "
                + "(reuniao_id, nome, email, token, proponente) "
                + "VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement stmt =
                conexao.prepareStatement(sql)) {

            stmt.setLong(1, reuniaoId);
            stmt.setString(2, nome);
            stmt.setString(3, email);
            stmt.setString(
                    4, UUID.randomUUID().toString());
            stmt.setBoolean(5, proponente);

            stmt.executeUpdate();
        }
    }

    private List<ConvidadoInicial> validarConvidados(
            Reuniao reuniao,
            List<ConvidadoInicial> convidados) {

        List<ConvidadoInicial> validados =
                new ArrayList<>();

        if (convidados == null) {
            return validados;
        }

        if (convidados.size() > LIMITE_CONVIDADOS) {
            throw new IllegalArgumentException(
                    "Informe no maximo 100 convidados.");
        }

        Set<String> emails = new HashSet<>();

        emails.add(
                reuniao.getEmailProponente()
                        .trim()
                        .toLowerCase(Locale.ROOT));

        for (ConvidadoInicial convidado : convidados) {

            if (convidado == null) {
                throw new IllegalArgumentException(
                        "Existe um convidado invalido.");
            }

            String nome = convidado.getNome();
            String email = convidado.getEmail();

            if (nome == null || nome.isBlank()) {
                throw new IllegalArgumentException(
                        "O nome de cada convidado e obrigatorio.");
            }

            nome = nome.trim();

            if (nome.length() > 100) {
                throw new IllegalArgumentException(
                        "O nome do convidado deve ter ate "
                        + "100 caracteres.");
            }

            if (email == null || email.isBlank()) {
                throw new IllegalArgumentException(
                        "O email de cada convidado e obrigatorio.");
            }

            email = email.trim();

            validarEmail(email);

            String chave =
                    email.toLowerCase(Locale.ROOT);

            if (!emails.add(chave)) {
                throw new IllegalArgumentException(
                        "Email repetido entre os participantes: "
                        + email);
            }

            validados.add(
                    new ConvidadoInicial(nome, email));
        }

        return validados;
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

        reuniao.setTitulo(
                reuniao.getTitulo().trim());

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

        reuniao.setNomeProponente(
                reuniao.getNomeProponente().trim());

        if (reuniao.getNomeProponente().length() > 100) {
            throw new IllegalArgumentException(
                    "O nome do proponente deve ter ate "
                    + "100 caracteres.");
        }

        if (reuniao.getEmailProponente() == null
                || reuniao.getEmailProponente().isBlank()) {
            throw new IllegalArgumentException(
                    "O email do proponente e obrigatorio.");
        }

        reuniao.setEmailProponente(
                reuniao.getEmailProponente().trim());

        validarEmail(reuniao.getEmailProponente());
    }

    private void validarEmail(String email) {

        if (email.length() > 254
                || !EMAIL_PATTERN.matcher(email).matches()) {

            throw new IllegalArgumentException(
                    "Informe um email valido: " + email);
        }
    }
}
