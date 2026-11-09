
<%@ page contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport"
          content="width=device-width, initial-scale=1">

    <title>Finalizar reunião</title>

    <style>
        * {
            box-sizing: border-box;
        }

        body {
            font-family: Arial, sans-serif;
            background: #f4f6f9;
            color: #263547;
            margin: 0;
            padding: 24px 15px;
        }

        .container {
            max-width: 850px;
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
            font-size: 21px;
            margin-top: 28px;
        }

        p {
            line-height: 1.6;
        }

        .descricao {
            color: #657487;
        }

        .informacoes {
            padding: 18px;
            background: #f0f3f7;
            border-radius: 6px;
            margin: 20px 0;
        }

        .horario {
            display: flex;
            gap: 12px;
            align-items: center;
            padding: 15px;
            margin: 10px 0;
            border: 1px solid #d9e1ec;
            border-radius: 6px;
            cursor: pointer;
        }

        .horario:hover {
            background: #f7f9fc;
        }

        .horario input {
            flex-shrink: 0;
        }

        .mensagem {
            background: #e4f5e9;
            color: #21683c;
            padding: 14px;
            border-radius: 6px;
            margin: 16px 0;
        }

        .erro {
            background: #ffe8e8;
            color: #a32626;
            padding: 14px;
            border-radius: 6px;
            margin: 16px 0;
        }

        .aviso {
            background: #fff7e6;
            color: #77571a;
            padding: 15px;
            border-radius: 6px;
            margin: 18px 0;
        }

        .confirmado {
            padding: 20px;
            border-radius: 6px;
            background: #e4f5e9;
            color: #21683c;
        }

        button,
        .botao {
            display: inline-block;
            background: #235bb5;
            color: white;
            border: 0;
            border-radius: 5px;
            padding: 12px 20px;
            font-weight: bold;
            cursor: pointer;
            text-decoration: none;
        }

        button:hover,
        .botao:hover {
            background: #19488f;
        }

        .acoes {
            margin-top: 28px;
        }

        a {
            color: #235bb5;
        }
    </style>
</head>

<body>
<div class="container">

    <h1>Finalização da reunião</h1>

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

    <c:if test="${not empty reuniao}">

        <div class="informacoes">
            <p>
                <strong>Reunião:</strong>
                <c:out value="${reuniao.titulo}"/>
            </p>

            <p>
                <strong>Proponente:</strong>
                <c:out value="${reuniao.nomeProponente}"/>
            </p>
        </div>

        <c:choose>

            <c:when test="${not empty horarioDefinitivo}">

                <div class="confirmado">

                    <h2>Reunião finalizada!</h2>

                    <p>
                        O horário definitivo já foi registrado.
                    </p>

                    <c:forEach var="horario"
                               items="${horarios}">

                        <c:if test="${horario.id eq horarioDefinitivo}">

                            <p>
                                <strong>Data:</strong>
                                <c:out value="${horario.dia}"/>
                            </p>

                            <c:if test="${not empty horario.periodo}">
                                <p>
                                    <strong>Período:</strong>
                                    <c:out value="${horario.periodo}"/>
                                </p>
                            </c:if>

                            <c:if test="${not empty horario.horaInicio}">
                                <p>
                                    <strong>Início:</strong>
                                    <c:out value="${horario.horaInicio}"/>
                                </p>
                            </c:if>

                            <c:if test="${not empty horario.horaFim}">
                                <p>
                                    <strong>Fim:</strong>
                                    <c:out value="${horario.horaFim}"/>
                                </p>
                            </c:if>

                        </c:if>

                    </c:forEach>

                </div>

            </c:when>

            <c:otherwise>

                <h2>Escolher horário definitivo</h2>

                <p class="descricao">
                    Selecione um horário para confirmar
                    a realização da reunião.
                </p>

                <c:choose>

                    <c:when test="${empty horarios}">
                        <p>
                            Nenhum horário disponível
                            para finalizar esta reunião.
                        </p>
                    </c:when>

                    <c:otherwise>

                        <form method="post"
                              action="${pageContext.request.contextPath}/reunioes/finalizar"
                              onsubmit="return confirm('Confirma a escolha definitiva deste horário?');">

                            <input type="hidden"
                                   name="csrfToken"
                                   value="${csrfToken}">

                            <c:forEach var="horario"
                                       items="${horarios}">

                                <label class="horario">

                                    <input type="radio"
                                           name="horarioId"
                                           value="${horario.id}"
                                           required>

                                    <span>

                                        <strong>
                                            <c:out value="${horario.dia}"/>
                                        </strong>

                                        <c:if test="${not empty horario.periodo}">
                                            —
                                            <c:out value="${horario.periodo}"/>
                                        </c:if>

                                        <c:if test="${not empty horario.horaInicio}">
                                            —
                                            <c:out value="${horario.horaInicio}"/>
                                        </c:if>

                                        <c:if test="${not empty horario.horaFim}">
                                            até
                                            <c:out value="${horario.horaFim}"/>
                                        </c:if>

                                    </span>

                                </label>

                            </c:forEach>

                            <div class="aviso">
                                Atenção: após a confirmação,
                                não será possível escolher
                                outro horário por esta interface.
                            </div>

                            <button type="submit">
                                Confirmar horário definitivo
                            </button>

                        </form>

                    </c:otherwise>

                </c:choose>

            </c:otherwise>

        </c:choose>

    </c:if>

    <div class="acoes">
        <a href="${pageContext.request.contextPath}/reunioes/resultado">
            Voltar aos resultados
        </a>
    </div>

</div>
</body>
</html>
