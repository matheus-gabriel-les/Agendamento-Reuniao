
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
        body {
            font-family: Arial, sans-serif;
            background: #f4f6f9;
            padding: 30px 15px;
        }

        .container {
            background: white;
            max-width: 650px;
            margin: auto;
            padding: 30px;
            border-radius: 10px;
        }

        h1 {
            color: #22623e;
        }

        .token {
            padding: 12px;
            background: #f0f3f7;
            border-radius: 5px;
            overflow-wrap: anywhere;
            margin-bottom: 16px;
        }

        .aviso {
            color: #8b3e18;
            font-size: 14px;
        }

        a {
            color: #235bb5;
        }
    </style>
</head>
<body>
<div class="container">

    <h1>Reunião criada com sucesso!</h1>

    <p>
        <strong>Título:</strong>
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
        <strong>Modo:</strong>
        <c:out value="${reuniao.modoDivisao}"/>
    </p>

    <h2>Token público</h2>
    <div class="token">
        <c:out value="${reuniao.tokenPublico}"/>
    </div>

    <h2>Token administrativo</h2>
    <div class="token">
        <c:out value="${reuniao.tokenAdmin}"/>
    </div>

    <p class="aviso">
        Guarde o token administrativo.
        Não compartilhe esse token com os convidados.
    </p>

    <p>
        Os links de acesso serão integrados
        nas próximas etapas do projeto.
    </p>

    <a href="${pageContext.request.contextPath}/reunioes/nova">
        Criar outra reunião
    </a>
</div>
</body>
</html>
