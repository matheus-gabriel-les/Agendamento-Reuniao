
package br.ufpr.tads.agendamento.servlet;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import br.ufpr.tads.agendamento.model.ModoDivisao;
import br.ufpr.tads.agendamento.model.Reuniao;
import br.ufpr.tads.agendamento.service.ReuniaoService;
import br.ufpr.tads.agendamento.service.ReuniaoService.ConvidadoInicial;
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
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        response.setCharacterEncoding("UTF-8");
        response.setHeader("Cache-Control", "no-store");

        if ("/reunioes/criada".equals(
                request.getServletPath())) {

            mostrarConfirmacao(request, response);
            return;
        }

        abrirFormulario(request, response);
    }

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        if (!"/reunioes/nova".equals(
                request.getServletPath())) {

            response.sendError(
                    HttpServletResponse.SC_METHOD_NOT_ALLOWED);
            return;
        }

        request.setCharacterEncoding("UTF-8");

        HttpSession sessao = request.getSession(false);

        if (sessao == null) {
            response.sendError(
                    HttpServletResponse.SC_FORBIDDEN,
                    "Formulario expirado.");
            return;
        }

        String csrfEsperado =
                (String) sessao.getAttribute(
                        "csrfNovaReuniao");

        String csrfRecebido =
                request.getParameter("csrfToken");

        if (csrfEsperado == null
                || !csrfEsperado.equals(csrfRecebido)) {

            response.sendError(
                    HttpServletResponse.SC_FORBIDDEN,
                    "Formulario invalido ou expirado.");
            return;
        }

        try {
            Reuniao reuniao = montarReuniao(request);

            List<ConvidadoInicial> convidados =
                    montarConvidados(
                            request.getParameter(
                                    "convidados"));

            reuniaoService.criar(reuniao, convidados);

            // Evita reutilizar o mesmo token CSRF
            // apos um cadastro concluido.
            sessao.removeAttribute("csrfNovaReuniao");

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
                    + "Verifique os dados e tente novamente.");

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
            reuniao.setDataInicio(
                    LocalDate.parse(
                            request.getParameter(
                                    "dataInicio")));

            reuniao.setDataFim(
                    LocalDate.parse(
                            request.getParameter(
                                    "dataFim")));

            reuniao.setModoDivisao(
                    ModoDivisao.valueOf(
                            request.getParameter(
                                    "modoDivisao")));

            if (reuniao.getModoDivisao()
                    == ModoDivisao.HORAS) {

                reuniao.setHoraInicio(
                        LocalTime.parse(
                                request.getParameter(
                                        "horaInicio")));

                reuniao.setHoraFim(
                        LocalTime.parse(
                                request.getParameter(
                                        "horaFim")));

                reuniao.setDuracaoMinutos(
                        Integer.valueOf(
                                request.getParameter(
                                        "duracaoMinutos")));
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

    private List<ConvidadoInicial> montarConvidados(
            String texto) {

        List<ConvidadoInicial> convidados =
                new ArrayList<>();

        if (texto == null || texto.isBlank()) {
            return convidados;
        }

        String[] linhas = texto.split("\\R");

        for (int i = 0; i < linhas.length; i++) {

            String linha = linhas[i].trim();

            if (linha.isEmpty()) {
                continue;
            }

            if (convidados.size() >= 100) {
                throw new IllegalArgumentException(
                        "Informe no maximo 100 convidados.");
            }

            // Cada linha deve conter:
            // Nome;email@exemplo.com
            String[] partes = linha.split(";", -1);

            if (partes.length != 2) {
                throw new IllegalArgumentException(
                        "Convidado na linha " + (i + 1)
                        + " invalido. Use Nome;email.");
            }

            String nome = partes[0].trim();
            String email = partes[1].trim();

            if (nome.isEmpty() || email.isEmpty()) {
                throw new IllegalArgumentException(
                        "Preencha nome e email "
                        + "na linha " + (i + 1) + ".");
            }

            convidados.add(
                    new ConvidadoInicial(nome, email));
        }

        return convidados;
    }

    private void abrirFormulario(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession sessao = request.getSession();

        String csrfToken =
                (String) sessao.getAttribute(
                        "csrfNovaReuniao");

        if (csrfToken == null) {

            csrfToken = UUID.randomUUID().toString();

            sessao.setAttribute(
                    "csrfNovaReuniao",
                    csrfToken);
        }

        request.setAttribute("csrfToken", csrfToken);

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
