
<%@ page contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport"
          content="width=device-width, initial-scale=1">

    <title>Minha disponibilidade</title>

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
            border-radius: 10px;
            padding: 30px;
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

        .descricao {
            color: #657487;
        }

        label {
            display: block;
            margin: 15px 0 7px;
            font-weight: bold;
        }

        input,
        select {
            width: 100%;
            padding: 11px;
            font: inherit;
            border: 1px solid #ccd2dc;
            border-radius: 5px;
        }

        button {
            border: 0;
            padding: 11px 18px;
            border-radius: 5px;
            background: #235bb5;
            color: white;
            font-weight: bold;
            cursor: pointer;
        }

        button:hover {
            background: #19488f;
        }

        .mensagem,
        .erro {
            padding: 12px;
            margin: 15px 0;
            border-radius: 5px;
        }

        .mensagem {
            background: #e4f5e9;
            color: #21683c;
        }

        .erro {
            background: #ffe8e8;
            color: #a32626;
        }

        .informacoes {
            background: #f0f3f7;
            padding: 18px;
            border-radius: 6px;
            margin-top: 20px;
        }

        .informacoes p {
            margin: 8px 0;
        }

        .confirmacao {
            background: #e4f5e9;
            border: 1px solid #b8dfc3;
            color: #21683c;
            padding: 20px;
            border-radius: 7px;
            margin-top: 24px;
        }

        .confirmacao h2 {
            margin-top: 0;
        }

        .confirmacao p {
            margin: 8px 0;
        }

        .aviso {
            background: #fff7e6;
            color: #77571a;
            padding: 15px;
            border-radius: 6px;
            margin: 20px 0;
        }

        .tabela-container {
            overflow-x: auto;
            margin-top: 20px;
        }

        table {
            width: 100%;
            border-collapse: collapse;
        }

        th,
        td {
            text-align: left;
            padding: 14px 10px;
            border-bottom: 1px solid #e4e7eb;
            vertical-align: middle;
        }

        th {
            background: #f4f6f9;
        }

        .resposta-form {
            display: flex;
            gap: 10px;
            align-items: center;
            min-width: 230px;
        }

        .resposta-form select {
            flex: 1;
            min-width: 130px;
        }

        .resposta-form button {
            white-space: nowrap;
        }

        .status {
            font-weight: bold;
        }

        .status-sim {
            color: #21683c;
        }

        .status-talvez {
            color: #946200;
        }

        .status-sem {
            color: #657487;
        }

        .rodape {
            margin-top: 30px;
        }

        .sair {
            background: #657487;
        }

        .acesso button {
            margin-top: 18px;
        }

        @media (max-width: 600px) {
            .container {
                padding: 20px;
            }

            .resposta-form {
                flex-direction: column;
                align-items: stretch;
            }
        }
    </style>
</head>

<body>

<div class="container">

    <h1>Minha disponibilidade</h1>

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

        <c:when test="${empty participante}">

            <p class="descricao">
                Informe seu token individual para visualizar
                os horários da reunião e registrar
                sua disponibilidade.
            </p>

            <form class="acesso"
                  method="post"
                  action="${pageContext.request.contextPath}/reunioes/disponibilidade">

                <input type="hidden"
                       name="acao"
                       value="acessar">

                <label for="token">
                    Token do participante
                </label>

                <input type="text"
                       id="token"
                       name="token"
                       maxlength="36"
                       autocomplete="off"
                       required>

                <button type="submit">
                    Acessar horários
                </button>

            </form>

        </c:when>

        <c:otherwise>

            <p>
                Olá,
                <strong>
                    <c:out value="${participante.nome}"/>
                </strong>!
            </p>

            <div class="informacoes">

                <p>
                    <strong>E-mail:</strong>
                    <c:out value="${participante.email}"/>
                </p>

                <p>
                    <strong>Identificador da reunião:</strong>
                    <c:out value="${participante.reuniaoId}"/>
                </p>

                <p>
                    <strong>Situação:</strong>

                    <c:choose>
                        <c:when test="${finalizada}">
                            Finalizada
                        </c:when>
                        <c:otherwise>
                            Aguardando definição do horário
                        </c:otherwise>
                    </c:choose>
                </p>

            </div>

            <c:if test="${finalizada}">

                <div class="confirmacao">

                    <h2>Reunião finalizada!</h2>

                    <p>
                        O proponente já definiu o horário
                        em que a reunião será realizada.
                    </p>

                    <c:if test="${not empty horarioDefinitivo}">

                        <p>
                            <strong>Data confirmada:</strong>
                            <c:out value="${horarioDefinitivo.dia}"/>
                        </p>

                        <c:if test="${not empty horarioDefinitivo.periodo}">
                            <p>
                                <strong>Período:</strong>
                                <c:out value="${horarioDefinitivo.periodo}"/>
                            </p>
                        </c:if>

                        <c:if test="${not empty horarioDefinitivo.horaInicio}">
                            <p>
                                <strong>Início:</strong>
                                <c:out value="${horarioDefinitivo.horaInicio}"/>
                            </p>
                        </c:if>

                        <c:if test="${not empty horarioDefinitivo.horaFim}">
                            <p>
                                <strong>Fim:</strong>
                                <c:out value="${horarioDefinitivo.horaFim}"/>
                            </p>
                        </c:if>

                    </c:if>

                </div>

                <div class="aviso">
                    Esta reunião já foi finalizada.
                    As respostas anteriores podem ser
                    consultadas, mas não podem mais ser alteradas.
                </div>

            </c:if>

            <h2>
                <c:choose>
                    <c:when test="${finalizada}">
                        Histórico das disponibilidades
                    </c:when>
                    <c:otherwise>
                        Horários disponíveis
                    </c:otherwise>
                </c:choose>
            </h2>

            <c:if test="${not finalizada}">
                <p class="descricao">
                    Selecione SIM quando puder participar,
                    TALVEZ quando não tiver certeza ou
                    Sem resposta para remover sua seleção.
                    Clique em Salvar em cada horário alterado.
                </p>
            </c:if>

            <c:choose>

                <c:when test="${empty horarios}">

                    <p class="descricao">
                        Esta reunião não possui horários cadastrados.
                    </p>

                </c:when>

                <c:otherwise>

                    <div class="tabela-container">

                        <table>

                            <thead>
                            <tr>
                                <th>Data</th>
                                <th>Período</th>
                                <th>Início</th>
                                <th>Fim</th>
                                <th>Disponibilidade</th>
                            </tr>
                            </thead>

                            <tbody>

                            <c:forEach var="horario"
                                       items="${horarios}">

                                <tr>

                                    <td>
                                        <c:out value="${horario.dia}"/>
                                    </td>

                                    <td>
                                        <c:choose>
                                            <c:when test="${not empty horario.periodo}">
                                                <c:out value="${horario.periodo}"/>
                                            </c:when>
                                            <c:otherwise>-</c:otherwise>
                                        </c:choose>
                                    </td>

                                    <td>
                                        <c:choose>
                                            <c:when test="${not empty horario.horaInicio}">
                                                <c:out value="${horario.horaInicio}"/>
                                            </c:when>
                                            <c:otherwise>-</c:otherwise>
                                        </c:choose>
                                    </td>

                                    <td>
                                        <c:choose>
                                            <c:when test="${not empty horario.horaFim}">
                                                <c:out value="${horario.horaFim}"/>
                                            </c:when>
                                            <c:otherwise>-</c:otherwise>
                                        </c:choose>
                                    </td>

                                    <td>

                                        <c:choose>

                                            <c:when test="${finalizada}">

                                                <c:choose>
                                                    <c:when test="${respostasPorHorario[horario.id] eq 'SIM'}">
                                                        <span class="status status-sim">
                                                            SIM
                                                        </span>
                                                    </c:when>

                                                    <c:when test="${respostasPorHorario[horario.id] eq 'TALVEZ'}">
                                                        <span class="status status-talvez">
                                                            TALVEZ
                                                        </span>
                                                    </c:when>

                                                    <c:otherwise>
                                                        <span class="status status-sem">
                                                            Sem resposta
                                                        </span>
                                                    </c:otherwise>
                                                </c:choose>

                                            </c:when>

                                            <c:otherwise>

                                                <form class="resposta-form"
                                                      method="post"
                                                      action="${pageContext.request.contextPath}/reunioes/disponibilidade">

                                                    <input type="hidden"
                                                           name="acao"
                                                           value="responder">

                                                    <input type="hidden"
                                                           name="horarioId"
                                                           value="${horario.id}">

                                                    <select name="resposta"
                                                            aria-label="Disponibilidade do horário">

                                                        <option value="REMOVER"
                                                            <c:if test="${empty respostasPorHorario[horario.id]}">
                                                                selected
                                                            </c:if>>
                                                            Sem resposta
                                                        </option>

                                                        <option value="SIM"
                                                            <c:if test="${respostasPorHorario[horario.id] eq 'SIM'}">
                                                                selected
                                                            </c:if>>
                                                            SIM
                                                        </option>

                                                        <option value="TALVEZ"
                                                            <c:if test="${respostasPorHorario[horario.id] eq 'TALVEZ'}">
                                                                selected
                                                            </c:if>>
                                                            TALVEZ
                                                        </option>

                                                    </select>

                                                    <button type="submit">
                                                        Salvar
                                                    </button>

                                                </form>

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

            <div class="rodape">

                <form method="post"
                      action="${pageContext.request.contextPath}/reunioes/disponibilidade">

                    <input type="hidden"
                           name="acao"
                           value="sair">

                    <button class="sair" type="submit">
                        Sair
                    </button>

                </form>

            </div>

        </c:otherwise>

    </c:choose>

</div>

</body>
</html>
