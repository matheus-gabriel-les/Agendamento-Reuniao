
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
        * {
            box-sizing: border-box;
        }

        body {
            font-family: Arial, sans-serif;
            background: #f4f6f9;
            margin: 0;
            padding: 24px 15px;
            color: #263547;
        }

        .container {
            max-width: 950px;
            margin: 0 auto;
            background: white;
            padding: 30px;
            border-radius: 10px;
            box-shadow: 0 2px 12px #00000012;
        }

        h1 {
            margin-top: 0;
        }

        h2 {
            margin-top: 28px;
            font-size: 21px;
        }

        p {
            line-height: 1.6;
        }

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

        button,
        .botao {
            display: inline-block;
            margin-top: 18px;
            background: #235bb5;
            color: white;
            border: 0;
            padding: 12px 20px;
            border-radius: 5px;
            cursor: pointer;
            font-weight: bold;
            text-decoration: none;
        }

        button:hover,
        .botao:hover {
            background: #19488f;
        }

        .botao-resultados {
            background: #21683c;
        }

        .botao-resultados:hover {
            background: #18542f;
        }

        .botao-copiar {
            margin-top: 8px;
            padding: 8px 12px;
            font-size: 13px;
        }

        .mensagem {
            padding: 12px;
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

        .tabela th,
        .tabela td {
            padding: 12px;
            border-bottom: 1px solid #e4e7eb;
            text-align: left;
            vertical-align: top;
        }

        .tabela th {
            background: #f4f6f9;
        }

        .tabela td {
            overflow-wrap: anywhere;
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

        .painel h2 {
            margin-top: 0;
        }

        .aviso {
            margin-top: 20px;
            padding: 15px;
            background: #fff7e6;
            border-radius: 6px;
            color: #77571a;
            line-height: 1.6;
        }

        .endereco {
            font-family: monospace;
            overflow-wrap: anywhere;
            padding: 12px;
            background: white;
            border: 1px solid #d9e1ec;
            border-radius: 5px;
        }

        .rodape {
            margin-top: 28px;
        }

        a {
            color: #235bb5;
        }

        a.botao {
            color: white;
        }

        @media (max-width: 600px) {
            .container {
                padding: 20px;
            }
        }
    </style>
</head>

<body>

<div class="container">

    <h1>Gerenciar convidados</h1>

    <c:if test="${not empty erro}">
        <div class="erro">
            <c:out value="${erro}"/>
        </div>
    </c:if>

    <c:if test="${not empty mensagem}">
        <div class="mensagem">
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

            <h2>Adicionar convidado</h2>

            <form method="post"
                  action="${pageContext.request.contextPath}/reunioes/convidados">

                <input type="hidden"
                       name="acao"
                       value="adicionar">

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

                <h2>Compartilhar convites</h2>

                <p class="descricao">
                    Cada participante possui um token
                    individual para registrar suas respostas.
                </p>

                <p>
                    <strong>Endereço para responder:</strong>
                </p>

                <div class="endereco"
                     id="enderecoDisponibilidade"><c:out value="${pageContext.request.contextPath}"/>/reunioes/disponibilidade</div>

                <p class="descricao">
                    Utilize o botão Copiar convite ao lado
                    de cada participante. O convite contém
                    o endereço de acesso e o token individual.
                </p>

                <a class="botao"
                   href="${pageContext.request.contextPath}/reunioes/disponibilidade">
                    Abrir página de disponibilidade
                </a>

            </div>

            <div class="painel">

                <h2>Resultados da reunião</h2>

                <p class="descricao">
                    Consulte os horários com maior
                    disponibilidade entre os participantes.
                </p>

                <a class="botao botao-resultados"
                   href="${pageContext.request.contextPath}/reunioes/resultado">
                    Ver resultados da reunião
                </a>

            </div>

            <div class="aviso">

                <strong>Importante:</strong>

                <p>
                    Compartilhe cada token apenas com
                    o participante correspondente.
                </p>

                <p>
                    Nunca compartilhe o token
                    administrativo com os convidados.
                </p>

                <p>
                    O sistema ainda não envia e-mails
                    automaticamente. Os convites devem
                    ser encaminhados manualmente.
                </p>

            </div>

        </c:otherwise>

    </c:choose>

    <div class="rodape">

        <a href="${pageContext.request.contextPath}/reunioes/nova">
            Criar outra reunião
        </a>

    </div>

</div>

<script>
    async function copiarConvite(botao) {

        const linha = botao.closest("tr");
        const token = linha.querySelector(".token").textContent.trim();

        const caminho = document.getElementById(
            "enderecoDisponibilidade"
        ).textContent.trim();

        const endereco = new URL(
            caminho,
            window.location.origin
        ).href;

        const convite =
            "Voce foi convidado para informar sua disponibilidade.\n\n" +
            "Acesse: " + endereco + "\n\n" +
            "Seu token individual: " + token + "\n\n" +
            "Informe o token na pagina para responder.";

        const mensagem = document.getElementById(
            "mensagemCopia"
        );

        try {
            await navigator.clipboard.writeText(convite);

            mensagem.textContent =
                "Convite copiado. Agora voce pode compartilha-lo.";

        } catch (erro) {
            mensagem.textContent =
                "Nao foi possivel copiar automaticamente. " +
                "Copie o token e o endereco manualmente.";
        }
    }
</script>

</body>
</html>
