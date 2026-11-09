
package br.ufpr.tads.agendamento.service;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import br.ufpr.tads.agendamento.dao.jdbc.ParticipanteDAOImpl;
import br.ufpr.tads.agendamento.dao.jdbc.ReuniaoDAOImpl;
import br.ufpr.tads.agendamento.model.Participante;

public class ParticipanteService {

    private final ParticipanteDAOImpl participanteDAO;
    private final ReuniaoDAOImpl reuniaoDAO;
    private final FinalizacaoService finalizacaoService;

    public ParticipanteService() {
        this.participanteDAO = new ParticipanteDAOImpl();
        this.reuniaoDAO = new ReuniaoDAOImpl();
        this.finalizacaoService = new FinalizacaoService();
    }

    public Participante cadastrar(
            Long reuniaoId,
            String nome,
            String email,
            boolean proponente) throws SQLException {

        if (reuniaoId == null || reuniaoId <= 0) {
            throw new IllegalArgumentException(
                    "Informe uma reuniao valida.");
        }

        if (reuniaoDAO.buscarPorId(reuniaoId) == null) {
            throw new IllegalArgumentException(
                    "Reuniao nao encontrada.");
        }

        verificarReuniaoAberta(reuniaoId);

        validarNome(nome);
        validarEmail(email);

        String emailNormalizado =
                email.trim().toLowerCase();

        if (emailJaCadastrado(reuniaoId, emailNormalizado)) {
            throw new IllegalArgumentException(
                    "Este email ja esta cadastrado na reuniao.");
        }

        Participante participante = new Participante();

        participante.setReuniaoId(reuniaoId);

        participante.setNome(
                nome == null ? null : nome.trim());

        participante.setEmail(emailNormalizado);

        participante.setToken(
                UUID.randomUUID().toString());

        participante.setProponente(proponente);

        participanteDAO.inserir(participante);

        return participante;
    }

    public Participante buscarPorId(Long id)
            throws SQLException {

        if (id == null || id <= 0) {
            throw new IllegalArgumentException(
                    "ID do participante invalido.");
        }

        return participanteDAO.buscarPorId(id);
    }

    public Participante buscarPorToken(String token)
            throws SQLException {

        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException(
                    "Token do participante invalido.");
        }

        return participanteDAO.buscarPorToken(token);
    }

    public List<Participante> listarPorReuniao(Long reuniaoId)
            throws SQLException {

        if (reuniaoId == null || reuniaoId <= 0) {
            throw new IllegalArgumentException(
                    "ID da reuniao invalido.");
        }

        return participanteDAO.listarPorReuniao(reuniaoId);
    }

    public void registrarResposta(Long participanteId)
            throws SQLException {

        Participante participante =
                verificarParticipante(participanteId);

        verificarReuniaoAberta(
                participante.getReuniaoId());

        participanteDAO.atualizarResposta(participanteId);
    }

    public void registrarEmailEnviado(Long participanteId)
            throws SQLException {

        verificarParticipante(participanteId);

        participanteDAO.atualizarEmailEnviado(participanteId);
    }

    public void excluir(Long participanteId)
            throws SQLException {

        Participante participante =
                verificarParticipante(participanteId);

        verificarReuniaoAberta(
                participante.getReuniaoId());

        participanteDAO.excluir(participanteId);
    }

    private Participante verificarParticipante(Long id)
            throws SQLException {

        if (id == null || id <= 0) {
            throw new IllegalArgumentException(
                    "Participante nao encontrado.");
        }

        Participante participante =
                participanteDAO.buscarPorId(id);

        if (participante == null) {
            throw new IllegalArgumentException(
                    "Participante nao encontrado.");
        }

        return participante;
    }

    private void verificarReuniaoAberta(Long reuniaoId)
            throws SQLException {

        Long horarioDefinitivo =
                finalizacaoService.buscarHorarioDefinitivo(
                        reuniaoId);

        if (horarioDefinitivo != null) {
            throw new IllegalArgumentException(
                    "Esta reuniao ja foi finalizada. "
                    + "Nao e possivel alterar os participantes.");
        }
    }

    private boolean emailJaCadastrado(
            Long reuniaoId,
            String email) throws SQLException {

        List<Participante> participantes =
                participanteDAO.listarPorReuniao(reuniaoId);

        for (Participante participante : participantes) {

            if (participante.getEmail()
                    .equalsIgnoreCase(email)) {
                return true;
            }
        }

        return false;
    }

    private void validarNome(String nome) {

        if (nome != null
                && nome.trim().length() > 100) {

            throw new IllegalArgumentException(
                    "O nome deve ter ate 100 caracteres.");
        }
    }

    private void validarEmail(String email) {

        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException(
                    "O email do participante e obrigatorio.");
        }

        String emailNormalizado = email.trim();

        if (emailNormalizado.length() > 254
                || !emailNormalizado.matches(
                        "^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {

            throw new IllegalArgumentException(
                    "Informe um email valido.");
        }
    }
}
