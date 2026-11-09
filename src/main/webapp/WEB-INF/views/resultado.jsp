
<%@ page contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport"
          content="width=device-width, initial-scale=1">

    <title>Resultados da reunião</title>

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
            max-width: 1000px;
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
            margin-top: 30px;
            font-size: 21px;
        }

        p {
            line-height: 1.6;
        }

        .descricao {
            color: #657487;
        }

        .informacoes {
            background: #f0f3f7;
            padding: 18px;
            border-radius: 6px;
            margin-top: 20px;
        }

        .informacoes p {
            margin: 7px 0;
        }

        .destaque {
            background: #e9f6ed;
            border: 1px solid #b7ddc3;
            border-radius: 7px;
            padding: 20px;
            margin-top: 22px;
        }

        .destaque h2 {
            margin-top: 0;
            color: #21683c;
        }

        .destaque p {
            margin: 8px 0;
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
            padding: 13px 12px;
            border-bottom: 1px solid #e4e7eb;
        }

        th {
            background: #f0f3f7;
        }

        .primeiro {
            background: #f0faf3;
        }

        .numero {
            font-weight: bold;
            text-align: center;
        }

        .erro {
            padding: 12px;
            border-radius: 5px;
            background: #ffe8e8;
            color: #a32626;
            margin: 16px 0;
        }

        .aviso {
            margin-top: 22px;
            padding: 15px;
            background: #fff7e6;
            border-radius: 6px;
            color: #77571a;
        }

        .acoes {
            margin-top: 30px;
            display: flex;
            flex-wrap: wrap;
            gap: 12px;
            align-items: center;
        }

        .botao {
            display: inline-block;
            background: #235bb5;
            color: white;
            text-decoration: none;
            padding: 12px 18px;
            border-radius: 5px;
            font-weight: bold;
        }

        .botao:hover {
            background: #19488f;
        }

        .link {
            color: #235bb5;
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

    <h1>Resultados da reunião</h1>

    <p class="descricao">
        Consulte os horários com maior disponibilidade
        entre os participantes cadastrados.
    </p>

    <c:if test="${not empty erro}">
        <div class="erro">
            <c:out value="${erro}"/>
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

            <p>
                <strong>Data inicial:</strong>
                <c:out value="${reuniao.dataInicio}"/>
            </p>

            <p>
                <strong>Data final:</strong>
                <c:out value="${reuniao.dataFim}"/>
            </p>

            <p>
                <strong>Modo de divisão:</strong>
                <c:out value="${reuniao.modoDivisao}"/>
            </p>

        </div>

        <c:choose>

            <c:when test="${empty resultados}">

                <h2>Nenhum horário encontrado</h2>

                <p class="descricao">
                    Ainda não existem horários disponíveis
                    para calcular o resultado desta reunião.
                </p>

            </c:when>

            <c:otherwise>

                <c:set var="melhor"
                       value="${resultados[0]}"/>

                <div class="destaque">

                    <h2>Melhor horário até o momento</h2>

                    <p>
                        <strong>Data:</strong>
                        <c:out value="${melhor.horario.dia}"/>
                    </p>

                    <c:if test="${not empty melhor.horario.periodo}">
                        <p>
                            <strong>Período:</strong>
                            <c:out value="${melhor.horario.periodo}"/>
                        </p>
                    </c:if>

                    <c:if test="${not empty melhor.horario.horaInicio}">
                        <p>
                            <strong>Início:</strong>
                            <c:out value="${melhor.horario.horaInicio}"/>
                        </p>
                    </c:if>

                    <c:if test="${not empty melhor.horario.horaFim}">
                        <p>
                            <strong>Fim:</strong>
                            <c:out value="${melhor.horario.horaFim}"/>
                        </p>
                    </c:if>

                    <p>
                        <strong>Disponíveis:</strong>
                        <c:out value="${melhor.quantidadeSim}"/>
                    </p>

                    <p>
                        <strong>Talvez:</strong>
                        <c:out value="${melhor.quantidadeTalvez}"/>
                    </p>

                    <p>
                        <strong>Sem resposta registrada:</strong>
                        <c:out value="${melhor.quantidadeSemResposta}"/>
                    </p>

                </div>

                <h2>Classificação dos horários</h2>

                <div class="tabela-container">

                    <table>

                        <thead>
                        <tr>
                            <th>Posição</th>
                            <th>Data</th>
                            <th>Período</th>
                            <th>Início</th>
                            <th>Fim</th>
                            <th>SIM</th>
                            <th>TALVEZ</th>
                            <th>Sem resposta</th>
                        </tr>
                        </thead>

                        <tbody>

                        <c:forEach var="resultado"
                                   items="${resultados}"
                                   varStatus="status">

                            <tr class="${status.first ? 'primeiro' : ''}">

                                <td>
                                    <c:out value="${status.count}"/>
                                </td>

                                <td>
                                    <c:out value="${resultado.horario.dia}"/>
                                </td>

                                <td>
                                    <c:choose>
                                        <c:when test="${not empty resultado.horario.periodo}">
                                            <c:out value="${resultado.horario.periodo}"/>
                                        </c:when>
                                        <c:otherwise>-</c:otherwise>
                                    </c:choose>
                                </td>

                                <td>
                                    <c:choose>
                                        <c:when test="${not empty resultado.horario.horaInicio}">
                                            <c:out value="${resultado.horario.horaInicio}"/>
                                        </c:when>
                                        <c:otherwise>-</c:otherwise>
                                    </c:choose>
                                </td>

                                <td>
                                    <c:choose>
                                        <c:when test="${not empty resultado.horario.horaFim}">
                                            <c:out value="${resultado.horario.horaFim}"/>
                                        </c:when>
                                        <c:otherwise>-</c:otherwise>
                                    </c:choose>
                                </td>

                                <td class="numero">
                                    <c:out value="${resultado.quantidadeSim}"/>
                                </td>

                                <td class="numero">
                                    <c:out value="${resultado.quantidadeTalvez}"/>
                                </td>

                                <td class="numero">
                                    <c:out value="${resultado.quantidadeSemResposta}"/>
                                </td>

                            </tr>

                        </c:forEach>

                        </tbody>

                    </table>

                </div>

                <div class="aviso">
                    <strong>Como funciona a classificação?</strong>

                    <p>
                        Os horários são ordenados pela quantidade
                        de respostas SIM. Em caso de empate,
                        a quantidade de TALVEZ é considerada.
                    </p>

                    <p>
                        Uma ausência de resposta não significa
                        necessariamente que o participante
                        esteja indisponível. A classificação
                        é provisória e pode mudar quando
                        novas respostas forem registradas.
                    </p>
                </div>

            </c:otherwise>

        </c:choose>

    </c:if>

    <div class="acoes">

        <a class="botao"
           href="${pageContext.request.contextPath}/reunioes/convidados">
            Voltar aos convidados
        </a>

        <a class="link"
           href="${pageContext.request.contextPath}/reunioes/nova">
            Criar outra reunião
        </a>

    </div>

</div>

</body>
</html>
