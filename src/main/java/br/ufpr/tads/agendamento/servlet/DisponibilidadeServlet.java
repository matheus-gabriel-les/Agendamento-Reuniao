
package br.ufpr.tads.agendamento.servlet;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.ufpr.tads.agendamento.model.Disponibilidade;
import br.ufpr.tads.agendamento.model.Horario;
import br.ufpr.tads.agendamento.model.Participante;
import br.ufpr.tads.agendamento.model.RespostaDisponibilidade;
import br.ufpr.tads.agendamento.service.DisponibilidadeService;
import br.ufpr.tads.agendamento.service.ParticipanteService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/reunioes/disponibilidade")
public class DisponibilidadeServlet extends HttpServlet {

    private final DisponibilidadeService disponibilidadeService =
            new DisponibilidadeService();

    private final ParticipanteService participanteService =
            new ParticipanteService();

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        response.setCharacterEncoding("UTF-8");

        HttpSession sessao = request.getSession(false);
        String token = obterToken(sessao);

        if (token == null) {
            abrirPagina(request, response);
            return;
        }

        try {
            Participante participante =
                    participanteService.buscarPorToken(token);

            if (participante == null) {
                sessao.removeAttribute("tokenParticipante");

                request.setAttribute(
                        "erro",
                        "O acesso deste participante nao e valido.");

                abrirPagina(request, response);
                return;
            }

            List<Horario> horarios =
                    disponibilidadeService.listarHorarios(token);

            List<Disponibilidade> respostas =
                    disponibilidadeService.listarRespostas(token);

            Map<Long, String> respostasPorHorario =
                    new HashMap<>();

            for (Disponibilidade disponibilidade : respostas) {
                respostasPorHorario.put(
                        disponibilidade.getHorarioId(),
                        disponibilidade.getResposta().name()
                );
            }

            request.setAttribute("participante", participante);
            request.setAttribute("horarios", horarios);

            request.setAttribute(
                    "respostasPorHorario",
                    respostasPorHorario);

            if (sessao != null) {
                Object mensagem = sessao.getAttribute(
                        "mensagemDisponibilidade");

                if (mensagem != null) {
                    request.setAttribute("mensagem", mensagem);

                    sessao.removeAttribute(
                            "mensagemDisponibilidade");
                }
            }

            abrirPagina(request, response);

        } catch (IllegalArgumentException e) {
            sessao.removeAttribute("tokenParticipante");

            request.setAttribute("erro", e.getMessage());

            abrirPagina(request, response);

        } catch (SQLException e) {
            registrarErro(e);

            request.setAttribute(
                    "erro",
                    "Nao foi possivel consultar os horarios.");

            abrirPagina(request, response);
        }
    }

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String acao = request.getParameter("acao");

        if ("acessar".equals(acao)) {
            acessar(request, response);
            return;
        }

        if ("responder".equals(acao)) {
            salvarResposta(request, response);
            return;
        }

        if ("sair".equals(acao)) {
            sair(request, response);
            return;
        }

        response.sendError(
                HttpServletResponse.SC_BAD_REQUEST,
                "Acao invalida.");
    }

    private void acessar(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        String token = request.getParameter("token");

        if (token == null || token.isBlank()) {
            request.setAttribute(
                    "erro",
                    "Informe o token do participante.");

            abrirPagina(request, response);
            return;
        }

        token = token.trim();

        try {
            Participante participante =
                    participanteService.buscarPorToken(token);

            if (participante == null) {
                request.setAttribute(
                        "erro",
                        "Token do participante invalido.");

                abrirPagina(request, response);
                return;
            }

            HttpSession sessao = request.getSession();

            request.changeSessionId();

            sessao.setAttribute(
                    "tokenParticipante",
                    token);

            response.sendRedirect(
                    request.getContextPath()
                    + "/reunioes/disponibilidade");

        } catch (IllegalArgumentException e) {
            request.setAttribute("erro", e.getMessage());

            abrirPagina(request, response);

        } catch (SQLException e) {
            registrarErro(e);

            request.setAttribute(
                    "erro",
                    "Nao foi possivel acessar a reuniao.");

            abrirPagina(request, response);
        }
    }

    private void salvarResposta(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession sessao = request.getSession(false);

        String token = obterToken(sessao);

        if (token == null) {
            response.sendError(
                    HttpServletResponse.SC_FORBIDDEN,
                    "Acesso do participante necessario.");
            return;
        }

        try {
            Long horarioId = Long.valueOf(
                    request.getParameter("horarioId"));

            String valor = request.getParameter("resposta");

            if ("REMOVER".equals(valor)) {
                disponibilidadeService.removerResposta(
                        token,
                        horarioId);
            } else {
                RespostaDisponibilidade resposta =
                        RespostaDisponibilidade.valueOf(valor);

                disponibilidadeService.responder(
                        token,
                        horarioId,
                        resposta);
            }

            sessao.setAttribute(
                    "mensagemDisponibilidade",
                    "Disponibilidade atualizada com sucesso!");

            response.sendRedirect(
                    request.getContextPath()
                    + "/reunioes/disponibilidade");

        } catch (IllegalArgumentException
                 | NullPointerException e) {

            request.setAttribute(
                    "erro",
                    "Horario ou resposta invalida.");

            doGet(request, response);

        } catch (SQLException e) {
            registrarErro(e);

            request.setAttribute(
                    "erro",
                    "Nao foi possivel salvar a resposta.");

            doGet(request, response);
        }
    }

    private void sair(
            HttpServletRequest request,
            HttpServletResponse response)
            throws IOException {

        HttpSession sessao = request.getSession(false);

        if (sessao != null) {
            sessao.removeAttribute("tokenParticipante");
        }

        response.sendRedirect(
                request.getContextPath()
                + "/reunioes/disponibilidade");
    }

    private String obterToken(HttpSession sessao) {

        if (sessao == null) {
            return null;
        }

        Object valor = sessao.getAttribute(
                "tokenParticipante");

        if (valor instanceof String) {
            return (String) valor;
        }

        return null;
    }

    private void abrirPagina(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        request.getRequestDispatcher(
                "/WEB-INF/views/disponibilidade.jsp")
                .forward(request, response);
    }

    private void registrarErro(SQLException e) {

        getServletContext().log(
                "Erro na disponibilidade do participante", e);
    }
}
