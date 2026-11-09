
package br.ufpr.tads.agendamento.servlet;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.SQLException;

import br.ufpr.tads.agendamento.dao.jdbc.ReuniaoDAOImpl;
import br.ufpr.tads.agendamento.model.Reuniao;
import br.ufpr.tads.agendamento.service.ConsoleEmailService;
import br.ufpr.tads.agendamento.service.EmailService;
import br.ufpr.tads.agendamento.service.EnvioConvitesService;
import br.ufpr.tads.agendamento.service.EnvioConvitesService.ResultadoEnvio;
import br.ufpr.tads.agendamento.service.FinalizacaoService;
import br.ufpr.tads.agendamento.service.SMTPEmailService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/reunioes/enviar-convites")
public class EnvioConvitesServlet extends HttpServlet {

    private final ReuniaoDAOImpl reuniaoDAO =
            new ReuniaoDAOImpl();

    private final FinalizacaoService finalizacaoService =
            new FinalizacaoService();

    private final EnvioConvitesService envioService =
            new EnvioConvitesService();

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws IOException {

        response.sendRedirect(
                request.getContextPath()
                + "/reunioes/convidados");
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
                (String) sessao.getAttribute("csrfConvidados");

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

            Reuniao reuniao =
                    reuniaoDAO.buscarPorId(reuniaoId);

            if (reuniao == null) {

                sessao.removeAttribute("adminReuniaoId");

                response.sendError(
                        HttpServletResponse.SC_NOT_FOUND,
                        "Reuniao nao encontrada.");
                return;
            }

            if (finalizacaoService
                    .buscarHorarioDefinitivo(reuniaoId)
                    != null) {

                sessao.setAttribute(
                        "mensagemConvidados",
                        "A reuniao ja foi finalizada. "
                        + "Nao e possivel enviar novos convites.");

                redirecionar(request, response);
                return;
            }

            EmailService emailService =
                    criarEmailService();

            String enderecoBase =
                    obterEnderecoBase(
                            request,
                            emailService.envioReal());

            ResultadoEnvio resultado =
                    envioService.enviarConvites(
                            reuniao,
                            enderecoBase,
                            emailService);

            String mensagem =
                    "Processamento concluido. "
                    + "Enviados: " + resultado.getEnviados()
                    + ". Simulados: " + resultado.getSimulados()
                    + ". Ja enviados: " + resultado.getIgnorados()
                    + ". Falhas: " + resultado.getFalhas()
                    + ".";

            if (!emailService.envioReal()) {
                mensagem +=
                        " Modo de simulacao: "
                        + "nenhum email foi enviado de verdade.";
            }

            sessao.setAttribute(
                    "mensagemConvidados", mensagem);

            redirecionar(request, response);

        } catch (SQLException e) {

            getServletContext().log(
                    "Erro ao consultar dados para envio", e);

            sessao.setAttribute(
                    "mensagemConvidados",
                    "Nao foi possivel consultar os dados "
                    + "necessarios para o envio.");

            redirecionar(request, response);

        } catch (IllegalArgumentException
                 | IllegalStateException e) {

            getServletContext().log(
                    "Configuracao de email invalida", e);

            sessao.setAttribute(
                    "mensagemConvidados",
                    "Configuracao de email invalida: "
                    + e.getMessage());

            redirecionar(request, response);
        }
    }

    private EmailService criarEmailService() {

        String modo = System.getenv("EMAIL_MODE");

        if (modo == null || modo.isBlank()
                || "console".equalsIgnoreCase(modo)) {

            return new ConsoleEmailService();
        }

        if ("smtp".equalsIgnoreCase(modo)) {
            return new SMTPEmailService();
        }

        throw new IllegalArgumentException(
                "EMAIL_MODE deve ser console ou smtp.");
    }

    private String obterEnderecoBase(
            HttpServletRequest request,
            boolean envioReal) {

        if (!envioReal) {

            return "http://localhost:8080"
                    + request.getContextPath();
        }

        String endereco =
                System.getenv("EMAIL_PUBLIC_BASE_URL");

        if (endereco == null || endereco.isBlank()) {
            throw new IllegalStateException(
                    "Defina EMAIL_PUBLIC_BASE_URL "
                    + "para realizar envios reais.");
        }

        endereco = endereco.trim();

        try {

            URI uri = new URI(endereco);

            if (!"https".equalsIgnoreCase(
                    uri.getScheme())
                    || uri.getHost() == null
                    || uri.getUserInfo() != null
                    || uri.getQuery() != null
                    || uri.getFragment() != null) {

                throw new IllegalArgumentException(
                        "EMAIL_PUBLIC_BASE_URL "
                        + "deve ser uma URL HTTPS valida.");
            }

            String caminho = uri.getPath();

            if (caminho == null
                    || !caminho.endsWith(
                            request.getContextPath())) {

                throw new IllegalArgumentException(
                        "A URL publica deve terminar "
                        + "com o contexto da aplicacao: "
                        + request.getContextPath());
            }

            return endereco;

        } catch (URISyntaxException e) {

            throw new IllegalArgumentException(
                    "EMAIL_PUBLIC_BASE_URL invalida.", e);
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

    private void redirecionar(
            HttpServletRequest request,
            HttpServletResponse response)
            throws IOException {

        response.sendRedirect(
                request.getContextPath()
                + "/reunioes/convidados");
    }
}
