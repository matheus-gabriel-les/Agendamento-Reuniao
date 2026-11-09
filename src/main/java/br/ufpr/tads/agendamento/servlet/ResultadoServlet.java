
package br.ufpr.tads.agendamento.servlet;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import br.ufpr.tads.agendamento.dao.jdbc.ReuniaoDAOImpl;
import br.ufpr.tads.agendamento.model.ResultadoHorario;
import br.ufpr.tads.agendamento.model.Reuniao;
import br.ufpr.tads.agendamento.service.ResultadoService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/reunioes/resultado")
public class ResultadoServlet extends HttpServlet {

    private final ResultadoService resultadoService =
            new ResultadoService();

    private final ReuniaoDAOImpl reuniaoDAO =
            new ReuniaoDAOImpl();

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        response.setCharacterEncoding("UTF-8");

        HttpSession sessao = request.getSession(false);

        if (sessao == null) {
            response.sendRedirect(
                    request.getContextPath()
                    + "/reunioes/convidados");
            return;
        }

        Object valor = sessao.getAttribute(
                "adminReuniaoId");

        if (!(valor instanceof Long)) {
            response.sendRedirect(
                    request.getContextPath()
                    + "/reunioes/convidados");
            return;
        }

        Long reuniaoId = (Long) valor;

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

            List<ResultadoHorario> resultados =
                    resultadoService.calcular(reuniaoId);

            request.setAttribute("reuniao", reuniao);
            request.setAttribute("resultados", resultados);

            request.getRequestDispatcher(
                    "/WEB-INF/views/resultado.jsp")
                    .forward(request, response);

        } catch (SQLException e) {

            getServletContext().log(
                    "Erro ao consultar resultados", e);

            request.setAttribute(
                    "erro",
                    "Nao foi possivel consultar os resultados.");

            request.getRequestDispatcher(
                    "/WEB-INF/views/resultado.jsp")
                    .forward(request, response);
        }
    }
}
