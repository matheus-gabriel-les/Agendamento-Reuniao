
package br.ufpr.tads.agendamento.servlet;

import java.io.IOException;
import java.sql.SQLException;
import java.util.UUID;

import br.ufpr.tads.agendamento.dao.jdbc.ReuniaoDAOImpl;
import br.ufpr.tads.agendamento.model.Reuniao;
import br.ufpr.tads.agendamento.service.FinalizacaoService;
import br.ufpr.tads.agendamento.service.ParticipanteService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/reunioes/convidados")
public class ParticipanteServlet extends HttpServlet {

    private final ParticipanteService participanteService =
            new ParticipanteService();

    private final FinalizacaoService finalizacaoService =
            new FinalizacaoService();

    private final ReuniaoDAOImpl reuniaoDAO =
            new ReuniaoDAOImpl();

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        response.setCharacterEncoding("UTF-8");

        HttpSession sessao = request.getSession(false);
        Long reuniaoId = obterReuniaoId(sessao);

        if (reuniaoId == null) {
            abrirPagina(request, response);
            return;
        }

        try {
            Reuniao reuniao =
                    reuniaoDAO.buscarPorId(reuniaoId);

            if (reuniao == null) {
                sessao.removeAttribute("adminReuniaoId");

                request.setAttribute(
                        "erro",
                        "Reuniao nao encontrada.");

                abrirPagina(request, response);
                return;
            }

            Long horarioDefinitivoId =
                    finalizacaoService.buscarHorarioDefinitivo(
                            reuniaoId);

            boolean finalizada =
                    horarioDefinitivoId != null;

            request.setAttribute("reuniao", reuniao);

            request.setAttribute(
                    "finalizada",
                    finalizada);

            request.setAttribute(
                    "participantes",
                    participanteService.listarPorReuniao(
                            reuniaoId));

            String csrfToken =
                    (String) sessao.getAttribute(
                            "csrfConvidados");

            if (csrfToken == null) {
                csrfToken = UUID.randomUUID().toString();

                sessao.setAttribute(
                        "csrfConvidados",
                        csrfToken);
            }

            request.setAttribute("csrfToken", csrfToken);

            Object mensagem =
                    sessao.getAttribute("mensagemConvidados");

            if (mensagem != null) {
                request.setAttribute(
                        "mensagem",
                        mensagem);

                sessao.removeAttribute(
                        "mensagemConvidados");
            }

            abrirPagina(request, response);

        } catch (SQLException e) {

            registrarErro(e);

            request.setAttribute(
                    "erro",
                    "Nao foi possivel consultar os convidados.");

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
            acessarGerenciamento(request, response);
            return;
        }

        if ("adicionar".equals(acao)) {
            adicionarConvidado(request, response);
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

    private void acessarGerenciamento(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        String tokenAdmin =
                request.getParameter("tokenAdmin");

        if (tokenAdmin == null
                || tokenAdmin.isBlank()) {

            request.setAttribute(
                    "erro",
                    "Informe o token administrativo.");

            abrirPagina(request, response);
            return;
        }

        try {
            Reuniao reuniao =
                    reuniaoDAO.buscarPorTokenAdmin(
                            tokenAdmin.trim());

            if (reuniao == null) {

                request.setAttribute(
                        "erro",
                        "Token administrativo invalido.");

                abrirPagina(request, response);
                return;
            }

            HttpSession sessao = request.getSession();

            request.changeSessionId();

            sessao.setAttribute(
                    "adminReuniaoId",
                    reuniao.getId());

            sessao.removeAttribute("csrfConvidados");

            response.sendRedirect(
                    request.getContextPath()
                    + "/reunioes/convidados");

        } catch (SQLException e) {

            registrarErro(e);

            request.setAttribute(
                    "erro",
                    "Nao foi possivel acessar a reuniao.");

            abrirPagina(request, response);
        }
    }

    private void adicionarConvidado(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession sessao = request.getSession(false);
        Long reuniaoId = obterReuniaoId(sessao);

        if (reuniaoId == null) {

            response.sendError(
                    HttpServletResponse.SC_FORBIDDEN,
                    "Acesso administrativo necessario.");
            return;
        }

        String csrfEsperado =
                (String) sessao.getAttribute(
                        "csrfConvidados");

        String csrfRecebido =
                request.getParameter("csrfToken");

        if (csrfEsperado == null
                || !csrfEsperado.equals(csrfRecebido)) {

            response.sendError(
                    HttpServletResponse.SC_FORBIDDEN,
                    "Formulario invalido ou expirado.");
            return;
        }

        String nome = request.getParameter("nome");
        String email = request.getParameter("email");

        try {
            if (finalizacaoService.buscarHorarioDefinitivo(
                    reuniaoId) != null) {

                throw new IllegalArgumentException(
                        "Esta reuniao ja foi finalizada. "
                        + "Nao e possivel adicionar convidados.");
            }

            participanteService.cadastrar(
                    reuniaoId,
                    nome,
                    email,
                    false
            );

            sessao.setAttribute(
                    "mensagemConvidados",
                    "Convidado cadastrado com sucesso!");

            response.sendRedirect(
                    request.getContextPath()
                    + "/reunioes/convidados");

        } catch (IllegalArgumentException e) {

            request.setAttribute("erro", e.getMessage());

            doGet(request, response);

        } catch (SQLException e) {

            registrarErro(e);

            request.setAttribute(
                    "erro",
                    "Nao foi possivel cadastrar o convidado.");

            doGet(request, response);
        }
    }

    private void sair(
            HttpServletRequest request,
            HttpServletResponse response)
            throws IOException {

        HttpSession sessao = request.getSession(false);

        if (sessao != null) {
            sessao.removeAttribute("adminReuniaoId");
            sessao.removeAttribute("csrfConvidados");
            sessao.removeAttribute("csrfFinalizacao");
        }

        response.sendRedirect(
                request.getContextPath()
                + "/reunioes/convidados");
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
                "/WEB-INF/views/convidados.jsp")
                .forward(request, response);
    }

    private void registrarErro(SQLException e) {

        getServletContext().log(
                "Erro no gerenciamento de convidados", e);
    }
}
