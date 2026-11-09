
package br.ufpr.tads.agendamento.servlet;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;

import br.ufpr.tads.agendamento.model.ModoDivisao;
import br.ufpr.tads.agendamento.model.Reuniao;
import br.ufpr.tads.agendamento.service.ReuniaoService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(urlPatterns = {
        "/reunioes/nova",
        "/reunioes/criada"
})
public class NovaReuniaoServlet extends HttpServlet {

    private final ReuniaoService reuniaoService =
            new ReuniaoService();

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        response.setCharacterEncoding("UTF-8");

        String caminho = request.getServletPath();

        if ("/reunioes/criada".equals(caminho)) {
            mostrarConfirmacao(request, response);
            return;
        }

        abrirFormulario(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        if (!"/reunioes/nova".equals(
                request.getServletPath())) {
            response.sendError(
                    HttpServletResponse.SC_METHOD_NOT_ALLOWED);
            return;
        }

        request.setCharacterEncoding("UTF-8");

        try {
            Reuniao reuniao = montarReuniao(request);

            reuniaoService.criar(reuniao);

            HttpSession sessao = request.getSession();

            sessao.setAttribute(
                    "reuniaoCriada", reuniao);

            response.sendRedirect(
                    request.getContextPath()
                    + "/reunioes/criada");

        } catch (IllegalArgumentException e) {

            request.setAttribute(
                    "erro", e.getMessage());

            abrirFormulario(request, response);

        } catch (SQLException e) {

            getServletContext().log(
                    "Erro ao cadastrar reuniao", e);

            request.setAttribute(
                    "erro",
                    "Nao foi possivel salvar a reuniao. "
                    + "Tente novamente.");

            abrirFormulario(request, response);
        }
    }

    private Reuniao montarReuniao(
            HttpServletRequest request) {

        Reuniao reuniao = new Reuniao();

        reuniao.setTitulo(
                request.getParameter("titulo"));

        reuniao.setDescricao(
                request.getParameter("descricao"));

        reuniao.setLocalReuniao(
                request.getParameter("localReuniao"));

        reuniao.setNomeProponente(
                request.getParameter("nomeProponente"));

        reuniao.setEmailProponente(
                request.getParameter("emailProponente"));

        try {
            reuniao.setDataInicio(LocalDate.parse(
                    request.getParameter("dataInicio")));

            reuniao.setDataFim(LocalDate.parse(
                    request.getParameter("dataFim")));

            reuniao.setModoDivisao(ModoDivisao.valueOf(
                    request.getParameter("modoDivisao")));

            if (reuniao.getModoDivisao()
                    == ModoDivisao.HORAS) {

                reuniao.setHoraInicio(LocalTime.parse(
                        request.getParameter("horaInicio")));

                reuniao.setHoraFim(LocalTime.parse(
                        request.getParameter("horaFim")));

                reuniao.setDuracaoMinutos(Integer.valueOf(
                        request.getParameter("duracaoMinutos")));
            }

        } catch (DateTimeParseException
                 | NullPointerException
                 | NumberFormatException e) {

            throw new IllegalArgumentException(
                    "Confira as datas, os horarios "
                    + "e o modo de divisao.");
        }

        return reuniao;
    }

    private void abrirFormulario(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        request.getRequestDispatcher(
                "/WEB-INF/views/nova-reuniao.jsp")
                .forward(request, response);
    }

    private void mostrarConfirmacao(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession sessao = request.getSession(false);

        Reuniao reuniao = null;

        if (sessao != null) {
            reuniao = (Reuniao) sessao.getAttribute(
                    "reuniaoCriada");

            sessao.removeAttribute("reuniaoCriada");
        }

        if (reuniao == null) {
            response.sendRedirect(
                    request.getContextPath()
                    + "/reunioes/nova");
            return;
        }

        request.setAttribute("reuniao", reuniao);

        request.getRequestDispatcher(
                "/WEB-INF/views/reuniao-criada.jsp")
                .forward(request, response);
    }
}
