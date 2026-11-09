
package br.ufpr.tads.agendamento.servlet;

import br.ufpr.tads.agendamento.dao.jdbc.HorarioDAOImpl;
import br.ufpr.tads.agendamento.dao.jdbc.ReuniaoDAOImpl;
import br.ufpr.tads.agendamento.model.Horario;
import br.ufpr.tads.agendamento.model.Reuniao;
import br.ufpr.tads.agendamento.service.FinalizacaoService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

@WebServlet("/reunioes/finalizar")
public class FinalizacaoServlet extends HttpServlet {

    private final FinalizacaoService finalizacaoService =
            new FinalizacaoService();

    private final ReuniaoDAOImpl reuniaoDAO =
            new ReuniaoDAOImpl();

    private final HorarioDAOImpl horarioDAO =
            new HorarioDAOImpl();

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession sessao = request.getSession(false);

        Long reuniaoId = obterReuniaoId(sessao);

        if (reuniaoId == null) {
            response.sendRedirect(
                    request.getContextPath()
                    + "/reunioes/convidados");
            return;
        }

        try {
            Reuniao reuniao =
                    reuniaoDAO.buscarPorId(reuniaoId);

            if (reuniao == null) {
                sessao.removeAttribute("adminReuniaoId");

                response.sendRedirect(
                        request.getContextPath()
                        + "/reunioes/convidados");
                return;
            }

            List<Horario> horarios =
                    horarioDAO.listarPorReuniao(reuniaoId);

            Long horarioDefinitivo =
                    finalizacaoService.buscarHorarioDefinitivo(
                            reuniaoId);

            String csrfToken =
                    (String) sessao.getAttribute(
                            "csrfFinalizacao");

            if (csrfToken == null) {
                csrfToken = UUID.randomUUID().toString();

                sessao.setAttribute(
                        "csrfFinalizacao",
                        csrfToken);
            }

            request.setAttribute("reuniao", reuniao);
            request.setAttribute("horarios", horarios);

            request.setAttribute(
                    "horarioDefinitivo",
                    horarioDefinitivo);

            request.setAttribute("csrfToken", csrfToken);

            Object mensagem =
                    sessao.getAttribute("mensagemFinalizacao");

            if (mensagem != null) {
                request.setAttribute("mensagem", mensagem);

                sessao.removeAttribute(
                        "mensagemFinalizacao");
            }

            abrirPagina(request, response);

        } catch (SQLException e) {
            getServletContext().log(
                    "Erro ao consultar finalizacao", e);

            request.setAttribute(
                    "erro",
                    "Nao foi possivel consultar a reuniao.");

            abrirPagina(request, response);
        }
    }

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        HttpSession sessao = request.getSession(false);

        Long reuniaoId = obterReuniaoId(sessao);

        if (reuniaoId == null) {
            response.sendError(
                    HttpServletResponse.SC_FORBIDDEN,
                    "Acesso administrativo necessario.");
            return;
        }

        String tokenEsperado =
                (String) sessao.getAttribute(
                        "csrfFinalizacao");

        String tokenRecebido =
                request.getParameter("csrfToken");

        if (tokenEsperado == null
                || !tokenEsperado.equals(tokenRecebido)) {

            response.sendError(
                    HttpServletResponse.SC_FORBIDDEN,
                    "Formulario invalido ou expirado.");
            return;
        }

        try {
            Long horarioId = Long.valueOf(
                    request.getParameter("horarioId"));

            finalizacaoService.finalizar(
                    reuniaoId,
                    horarioId);

            sessao.removeAttribute("csrfFinalizacao");

            sessao.setAttribute(
                    "mensagemFinalizacao",
                    "Horario definitivo registrado com sucesso!");

            response.sendRedirect(
                    request.getContextPath()
                    + "/reunioes/finalizar");

        } catch (IllegalArgumentException e) {

            request.setAttribute("erro", e.getMessage());

            doGet(request, response);

        } catch (SQLException e) {

            getServletContext().log(
                    "Erro ao finalizar reuniao", e);

            request.setAttribute(
                    "erro",
                    "Nao foi possivel finalizar a reuniao.");

            doGet(request, response);
        }
    }

    private Long obterReuniaoId(HttpSession sessao) {

        if (sessao == null) {
            return null;
        }

        Object valor =
                sessao.getAttribute("adminReuniaoId");

        if (valor instanceof Long) {
            return (Long) valor;
        }

        return null;
    }

    private void abrirPagina(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        request.getRequestDispatcher(
                "/WEB-INF/views/finalizacao.jsp")
                .forward(request, response);
    }
}
