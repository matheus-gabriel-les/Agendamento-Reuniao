
<%@ page contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport"
          content="width=device-width, initial-scale=1">

    <title>Reunião criada</title>

    <style>
        * {
            box-sizing: border-box;
        }

        body {
            font-family: Arial, sans-serif;
            background: #f4f6f9;
            margin: 0;
            padding: 30px 15px;
            color: #263547;
        }

        .container {
            background: white;
            max-width: 650px;
            margin: 0 auto;
            padding: 30px;
            border-radius: 10px;
            box-shadow: 0 2px 12px #00000012;
        }

        h1 {
            color: #22623e;
            margin-top: 0;
        }

        h2 {
            margin-top: 28px;
            font-size: 21px;
        }

        .informacao {
            margin: 14px 0;
            line-height: 1.5;
        }

        .token {
            padding: 12px;
            background: #f0f3f7;
            border-radius: 5px;
            overflow-wrap: anywhere;
            margin-bottom: 16px;
            font-family: monospace;
        }

        .aviso {
            color: #8b3e18;
            font-size: 14px;
            line-height: 1.5;
        }

        .acoes {
            margin-top: 28px;
            display: flex;
            flex-direction: column;
            gap: 14px;
        }

        .botao {
            display: inline-block;
            padding: 12px 20px;
            background: #235bb5;
            color: white;
            border-radius: 5px;
            text-decoration: none;
            font-weight: bold;
            text-align: center;
        }

        .botao:hover {
            background: #19488f;
        }

        .link {
            color: #235bb5;
            text-decoration: underline;
            text-align: center;
        }

        .observacao {
            margin-top: 24px;
            padding: 15px;
            background: #eef3fa;
            border-radius: 5px;
            line-height: 1.5;
        }
    </style>
</head>

<body>

<div class="container">

    <h1>Reunião criada com sucesso!</h1>

    <p>
        Sua reunião foi cadastrada.
        Confira as informações abaixo.
    </p>

    <div class="informacao">
        <strong>Título:</strong>
        <c:out value="${reuniao.titulo}"/>
    </div>

    <div class="informacao">
        <strong>Proponente:</strong>
        <c:out value="${reuniao.nomeProponente}"/>
    </div>

    <div class="informacao">
        <strong>E-mail:</strong>
        <c:out value="${reuniao.emailProponente}"/>
    </div>

    <div class="informacao">
        <strong>Local:</strong>
        <c:out value="${reuniao.localReuniao}"/>
    </div>

    <div class="informacao">
        <strong>Data inicial:</strong>
        <c:out value="${reuniao.dataInicio}"/>
    </div>

    <div class="informacao">
        <strong>Data final:</strong>
        <c:out value="${reuniao.dataFim}"/>
    </div>

    <div class="informacao">
        <strong>Modo de divisão:</strong>
        <c:out value="${reuniao.modoDivisao}"/>
    </div>

    <c:if test="${reuniao.modoDivisao == 'HORAS'}">

        <div class="informacao">
            <strong>Hora inicial:</strong>
            <c:out value="${reuniao.horaInicio}"/>
        </div>

        <div class="informacao">
            <strong>Hora final:</strong>
            <c:out value="${reuniao.horaFim}"/>
        </div>

        <div class="informacao">
            <strong>Duração dos intervalos:</strong>
            <c:out value="${reuniao.duracaoMinutos}"/>
            minutos
        </div>

    </c:if>

    <h2>Token público</h2>

    <div class="token">
        <c:out value="${reuniao.tokenPublico}"/>
    </div>

    <h2>Token administrativo</h2>

    <div class="token">
        <c:out value="${reuniao.tokenAdmin}"/>
    </div>

    <p class="aviso">
        Guarde o token administrativo em um local seguro.
        Ele permite acessar o gerenciamento desta reunião.
        Não compartilhe esse token com os convidados.
    </p>

    <div class="observacao">
        <strong>Próximo passo:</strong>

        <p>
            Agora você pode cadastrar os participantes
            que serão convidados para a reunião.
        </p>

        <p>
            Para acessar o gerenciamento, utilize
            o token administrativo exibido acima.
        </p>
    </div>

    <div class="acoes">

        <a class="botao"
           href="${pageContext.request.contextPath}/reunioes/convidados">
            Gerenciar convidados
        </a>

        <a class="link"
           href="${pageContext.request.contextPath}/reunioes/nova">
            Criar outra reunião
        </a>

    </div>

</div>

</body>
</html>
