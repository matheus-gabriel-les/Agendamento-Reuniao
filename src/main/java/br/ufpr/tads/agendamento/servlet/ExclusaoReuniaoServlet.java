
package br.ufpr.tads.agendamento.servlet;

import java.io.IOException;
import java.sql.SQLException;
import java.util.UUID;

import br.ufpr.tads.agendamento.dao.jdbc.ReuniaoDAOImpl;
import br.ufpr.tads.agendamento.model.Reuniao;
import br.ufpr.tads.agendamento.service.ExclusaoReuniaoService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/reunioes/excluir")
public class ExclusaoReuniaoServlet extends HttpServlet {

    private final ExclusaoReuniaoService exclusaoService =
            new ExclusaoReuniaoService();

    private final ReuniaoDAOImpl reuniaoDAO =
            new ReuniaoDAOImpl();

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

            String csrfToken =
                    (String) sessao.getAttribute(
                            "csrfExclusao");

            if (csrfToken == null) {
                csrfToken = UUID.randomUUID().toString();

                sessao.setAttribute(
                        "csrfExclusao",
                        csrfToken);
            }

            request.setAttribute("reuniao", reuniao);
            request.setAttribute("csrfToken", csrfToken);

            request.getRequestDispatcher(
                    "/WEB-INF/views/excluir-reuniao.jsp")
                    .forward(request, response);

        } catch (SQLException e) {

            getServletContext().log(
                    "Erro ao consultar reuniao para exclusao", e);

            response.sendError(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Nao foi possivel consultar a reuniao.");
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

        String csrfEsperado =
                (String) sessao.getAttribute("csrfExclusao");

        String csrfRecebido =
                request.getParameter("csrfToken");

        if (csrfEsperado == null
                || !csrfEsperado.equals(csrfRecebido)) {

            response.sendError(
                    HttpServletResponse.SC_FORBIDDEN,
                    "Formulario invalido ou expirado.");
            return;
        }

        String confirmacao =
                request.getParameter("confirmacao");

        if (!"EXCLUIR".equals(confirmacao)) {
            request.setAttribute(
                    "erro",
                    "Digite EXCLUIR para confirmar.");

            doGet(request, response);
            return;
        }

        try {
            exclusaoService.excluir(reuniaoId);

            sessao.removeAttribute("adminReuniaoId");
            sessao.removeAttribute("csrfExclusao");
            sessao.removeAttribute("csrfConvidados");
            sessao.removeAttribute("csrfFinalizacao");
            sessao.removeAttribute("mensagemConvidados");
            sessao.removeAttribute("mensagemFinalizacao");

            response.sendRedirect(
                    request.getContextPath()
                    + "/reunioes/nova?excluida=1");

        } catch (IllegalArgumentException e) {

            request.setAttribute("erro", e.getMessage());

            doGet(request, response);

        } catch (SQLException e) {

            getServletContext().log(
                    "Erro ao excluir reuniao", e);

            request.setAttribute(
                    "erro",
                    "Nao foi possivel excluir a reuniao.");

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
}
