
<%@ page contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport"
          content="width=device-width, initial-scale=1">

    <title>Gerenciar convidados</title>

    <style>
        * { box-sizing: border-box; }

        body {
            font-family: Arial, sans-serif;
            background: #f4f6f9;
            color: #263547;
            margin: 0;
            padding: 24px 15px;
        }

        .container {
            max-width: 950px;
            margin: 0 auto;
            background: white;
            padding: 30px;
            border-radius: 10px;
            box-shadow: 0 2px 12px #00000012;
        }

        h1 { margin-top: 0; }

        h2 {
            margin-top: 28px;
            font-size: 21px;
        }

        p { line-height: 1.6; }

        label {
            display: block;
            margin: 16px 0 6px;
            font-weight: bold;
        }

        input {
            width: 100%;
            padding: 11px;
            border: 1px solid #ccd2dc;
            border-radius: 5px;
            font: inherit;
        }

        button, .botao {
            display: inline-block;
            margin-top: 18px;
            padding: 12px 20px;
            background: #235bb5;
            color: white;
            border: 0;
            border-radius: 5px;
            cursor: pointer;
            font-weight: bold;
            text-decoration: none;
            font: inherit;
        }

        button:hover, .botao:hover {
            background: #19488f;
        }

        a.botao { color: white; }

        .botao-copiar {
            margin-top: 8px;
            padding: 8px 12px;
            font-size: 13px;
        }

        .botao-resultados,
        .botao-enviar {
            background: #21683c;
        }

        .botao-resultados:hover,
        .botao-enviar:hover {
            background: #18542f;
        }

        .botao-sair {
            background: #657487;
            margin-top: 0;
        }

        .botao-excluir {
            background: #a32626;
        }

        .botao-excluir:hover {
            background: #801b1b;
        }

        .mensagem {
            padding: 14px;
            border-radius: 5px;
            background: #e4f5e9;
            color: #21683c;
            margin-bottom: 16px;
        }

        .erro {
            padding: 12px;
            border-radius: 5px;
            background: #ffe8e8;
            color: #a32626;
            margin-bottom: 16px;
        }

        .confirmacao {
            margin: 22px 0;
            padding: 20px;
            background: #e4f5e9;
            border: 1px solid #b8dfc3;
            border-radius: 7px;
            color: #21683c;
        }

        .confirmacao h2 { margin-top: 0; }

        .descricao {
            color: #657487;
        }

        .tabela-container {
            overflow-x: auto;
        }

        .tabela {
            width: 100%;
            border-collapse: collapse;
            margin-top: 15px;
        }

        .tabela th, .tabela td {
            padding: 12px;
            border-bottom: 1px solid #e4e7eb;
            text-align: left;
            vertical-align: top;
            overflow-wrap: anywhere;
        }

        .tabela th {
            background: #f4f6f9;
        }

        .token {
            display: block;
            font-family: monospace;
            font-size: 12px;
            overflow-wrap: anywhere;
        }

        .painel {
            margin-top: 30px;
            padding: 20px;
            border-radius: 7px;
            background: #f0f3f7;
        }

        .painel h2 { margin-top: 0; }

        .painel-email {
            background: #edf7f0;
            border: 1px solid #c8e4d0;
        }

        .painel-exclusao {
            background: #fff1e9;
            border: 1px solid #efc7b4;
        }

        .painel-exclusao h2 {
            color: #a32626;
        }

        .endereco {
            font-family: monospace;
            overflow-wrap: anywhere;
            padding: 12px;
            background: white;
            border: 1px solid #d9e1ec;
            border-radius: 5px;
        }

        .aviso {
            margin-top: 20px;
            padding: 15px;
            background: #fff7e6;
            border-radius: 6px;
            color: #77571a;
        }

        .acoes {
            display: flex;
            flex-wrap: wrap;
            align-items: center;
            gap: 14px;
            margin-top: 28px;
        }

        .acoes button { margin-top: 0; }

        .estado {
            font-size: 13px;
            font-weight: bold;
        }

        .enviado { color: #21683c; }
        .pendente { color: #77571a; }

        a { color: #235bb5; }

        @media (max-width: 600px) {
            .container { padding: 20px; }
        }
    </style>
</head>

<body>

<div class="container">

    <h1>Gerenciar convidados</h1>

    <c:if test="${not empty erro}">
        <div class="erro" role="alert">
            <c:out value="${erro}"/>
        </div>
    </c:if>

    <c:if test="${not empty mensagem}">
        <div class="mensagem" role="status">
            <c:out value="${mensagem}"/>
        </div>
    </c:if>

    <c:choose>

        <c:when test="${empty reuniao}">

            <p class="descricao">
                Informe o token administrativo para
                acessar o gerenciamento da reunião.
            </p>

            <form method="post"
                  action="${pageContext.request.contextPath}/reunioes/convidados">

                <input type="hidden"
                       name="acao"
                       value="acessar">

                <label for="tokenAdmin">
                    Token administrativo
                </label>

                <input type="text"
                       id="tokenAdmin"
                       name="tokenAdmin"
                       maxlength="36"
                       autocomplete="off"
                       required>

                <button type="submit">
                    Acessar reunião
                </button>
            </form>

        </c:when>

        <c:otherwise>

            <p>
                <strong>Reunião:</strong>
                <c:out value="${reuniao.titulo}"/>
            </p>

            <p>
                <strong>Proponente:</strong>
                <c:out value="${reuniao.nomeProponente}"/>
            </p>

            <p>
                <strong>Situação:</strong>
                <c:choose>
                    <c:when test="${finalizada}">
                        Finalizada
                    </c:when>
                    <c:otherwise>
                        Aberta para respostas
                    </c:otherwise>
                </c:choose>
            </p>

            <c:choose>

                <c:when test="${finalizada}">

                    <div class="confirmacao">
                        <h2>Reunião finalizada!</h2>

                        <p>
                            O horário definitivo já foi
                            escolhido pelo proponente.
                        </p>

                        <p>
                            Não é possível adicionar
                            participantes ou modificar
                            suas disponibilidades.
                        </p>

                        <a class="botao botao-resultados"
                           href="${pageContext.request.contextPath}/reunioes/finalizar">
                            Consultar horário confirmado
                        </a>
                    </div>

                </c:when>

                <c:otherwise>

                    <h2>Adicionar convidado</h2>

                    <form method="post"
                          action="${pageContext.request.contextPath}/reunioes/convidados">

                        <input type="hidden"
                               name="acao"
                               value="adicionar">

                        <input type="hidden"
                               name="csrfToken"
                               value="<c:out value='${csrfToken}'/>">

                        <label for="nome">
                            Nome do convidado
                        </label>

                        <input type="text"
                               id="nome"
                               name="nome"
                               maxlength="100">

                        <label for="email">
                            E-mail do convidado *
                        </label>

                        <input type="email"
                               id="email"
                               name="email"
                               maxlength="254"
                               required>

                        <button type="submit">
                            Adicionar convidado
                        </button>

                    </form>

                </c:otherwise>

            </c:choose>

            <h2>Participantes cadastrados</h2>

            <c:choose>

                <c:when test="${empty participantes}">
                    <p class="descricao">
                        Nenhum participante cadastrado.
                    </p>
                </c:when>

                <c:otherwise>

                    <div class="tabela-container">

                        <table class="tabela">
                            <thead>
                                <tr>
                                    <th>Nome</th>
                                    <th>E-mail</th>
                                    <th>Respondeu?</th>
                                    <th>E-mail enviado?</th>
                                    <th>Convite individual</th>
                                </tr>
                            </thead>

                            <tbody>

                                <c:forEach var="p"
                                           items="${participantes}">

                                    <tr>
                                        <td>
                                            <c:out value="${p.nome}"/>
                                        </td>

                                        <td>
                                            <c:out value="${p.email}"/>
                                        </td>

                                        <td>
                                            <c:choose>
                                                <c:when test="${not empty p.respondidoEm}">
                                                    Sim
                                                </c:when>
                                                <c:otherwise>
                                                    Ainda não
                                                </c:otherwise>
                                            </c:choose>
                                        </td>

                                        <td>
                                            <c:choose>
                                                <c:when test="${not empty p.emailEnviadoEm}">
                                                    <span class="estado enviado">
                                                        Enviado
                                                    </span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="estado pendente">
                                                        Pendente
                                                    </span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>

                                        <td>
                                            <span class="token">
                                                <c:out value="${p.token}"/>
                                            </span>

                                            <button type="button"
                                                    class="botao-copiar"
                                                    onclick="copiarConvite(this)">
                                                Copiar convite
                                            </button>
                                        </td>
                                    </tr>

                                </c:forEach>

                            </tbody>
                        </table>

                    </div>

                    <p id="mensagemCopia"
                       class="descricao"
                       role="status"
                       aria-live="polite"></p>

                </c:otherwise>

            </c:choose>

            <div class="painel">

                <h2>Compartilhar convites manualmente</h2>

                <p class="descricao">
                    Cada participante possui um token
                    individual para acessar a reunião.
                </p>

                <p>
                    <strong>Endereço de acesso:</strong>
                </p>

                <div class="endereco"
                     id="enderecoDisponibilidade"><c:out value="${pageContext.request.contextPath}"/>/reunioes/disponibilidade</div>

                <p class="descricao">
                    Copie o convite correspondente
                    a cada participante.
                </p>

                <a class="botao"
                   href="${pageContext.request.contextPath}/reunioes/disponibilidade">
                    Abrir página do participante
                </a>

            </div>

            <c:if test="${not finalizada}">

                <div class="painel painel-email">

                    <h2>Enviar convites por e-mail</h2>

                    <p>
                        Envie um convite individual
                        aos participantes que ainda
                        não possuem envio registrado.
                    </p>

                    <p class="descricao">
                        O sistema utiliza o modo
                        de envio configurado no servidor.
                        No modo de simulação, os convites
                        aparecem apenas no console.
                    </p>

                    <form method="post"
                          action="${pageContext.request.contextPath}/reunioes/enviar-convites"
                          onsubmit="return confirm('Deseja processar os convites pendentes desta reunião?');">

                        <input type="hidden"
                               name="csrfToken"
                               value="<c:out value='${csrfToken}'/>">

                        <button type="submit"
                                class="botao-enviar">
                            Enviar convites pendentes
                        </button>

                    </form>

                </div>

            </c:if>

            <div class="painel">

                <h2>Resultados da reunião</h2>

                <p class="descricao">
                    Consulte a classificação dos horários
                    e as disponibilidades registradas.
                </p>

                <a class="botao botao-resultados"
                   href="${pageContext.request.contextPath}/reunioes/resultado">
                    Ver resultados da reunião
                </a>

            </div>

            <div class="aviso">

                <strong>Importante:</strong>

                <p>
                    Compartilhe os tokens individuais
                    somente com os participantes
                    correspondentes.
                </p>

                <p>
                    Nunca compartilhe o token
                    administrativo com os convidados.
                </p>

                <p>
                    O status de envio representa
                    os envios SMTP registrados pelo sistema.
                    Uma simulação não altera esse status.
                </p>

            </div>

            <div class="painel painel-exclusao">

                <h2>Excluir reunião</h2>

                <p>
                    Esta operação remove permanentemente
                    a reunião e todos os seus dados relacionados.
                </p>

                <a class="botao botao-excluir"
                   href="${pageContext.request.contextPath}/reunioes/excluir">
                    Excluir reunião
                </a>

            </div>

            <div class="acoes">

                <a href="${pageContext.request.contextPath}/reunioes/nova">
                    Criar outra reunião
                </a>

                <form method="post"
                      action="${pageContext.request.contextPath}/reunioes/convidados">

                    <input type="hidden"
                           name="acao"
                           value="sair">
                    <input type="hidden"
                           name="csrfToken"
                           value="<c:out value='${csrfToken}'/>">

                    <button type="submit"
                            class="botao-sair">
                        Sair do gerenciamento
                    </button>

                </form>

            </div>

        </c:otherwise>

    </c:choose>

</div>

<script>
    async function copiarConvite(botao) {

        const linha = botao.closest("tr");

        const token = linha.querySelector(
            ".token"
        ).textContent.trim();

        const caminho = document.getElementById(
            "enderecoDisponibilidade"
        ).textContent.trim();

        const endereco = new URL(
            caminho,
            window.location.origin
        ).href;

        const finalizada =
            document.querySelector(".confirmacao") !== null;

        const texto = finalizada
            ? "A reuniao ja foi finalizada. Consulte o horario confirmado."
            : "Voce foi convidado para informar sua disponibilidade.";

        const convite =
            texto + "\n\n" +
            "Acesse: " + endereco + "\n\n" +
            "Seu token individual: " + token + "\n\n" +
            "Informe o token na pagina para acessar a reuniao.";

        const mensagem = document.getElementById(
            "mensagemCopia"
        );

        try {

            await navigator.clipboard.writeText(convite);

            mensagem.textContent =
                "Convite copiado com sucesso.";

        } catch (erro) {

            mensagem.textContent =
                "Nao foi possivel copiar automaticamente. " +
                "Copie o token e o endereco manualmente.";
        }
    }
</script>

</body>
</html>
