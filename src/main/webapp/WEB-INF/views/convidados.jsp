
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
            max-width: 800px;
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

        button {
            margin-top: 18px;
            background: #235bb5;
            color: white;
            border: 0;
            padding: 12px 20px;
            border-radius: 5px;
            cursor: pointer;
            font-weight: bold;
        }

        button:hover {
            background: #19488f;
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
            overflow-wrap: anywhere;
        }

        .tabela th {
            background: #f4f6f9;
        }

        .tabela-container {
            overflow-x: auto;
        }

        .rodape {
            margin-top: 28px;
        }

        a {
            color: #235bb5;
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
                Informe o token administrativo
                para acessar uma reunião.
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
                        Nenhum convidado cadastrado ainda.
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
                                </tr>
                            </c:forEach>
                            </tbody>
                        </table>
                    </div>

                </c:otherwise>
            </c:choose>

        </c:otherwise>

    </c:choose>

    <div class="rodape">
        <a href="${pageContext.request.contextPath}/reunioes/nova">
            Criar outra reunião
        </a>
    </div>

</div>
</body>
</html>
