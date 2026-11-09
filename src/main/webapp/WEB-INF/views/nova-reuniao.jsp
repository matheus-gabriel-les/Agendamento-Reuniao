
<%@ page contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport"
          content="width=device-width, initial-scale=1">
    <title>Nova reunião</title>

    <style>
        body {
            font-family: Arial, sans-serif;
            background: #f4f6f9;
            margin: 0;
            padding: 30px 15px;
        }

        .container {
            max-width: 650px;
            margin: auto;
            background: white;
            padding: 30px;
            border-radius: 10px;
            box-shadow: 0 2px 12px #00000012;
        }

        h1 {
            margin-top: 0;
            color: #263547;
        }

        label {
            display: block;
            margin-top: 16px;
            margin-bottom: 6px;
            font-weight: bold;
        }

        input, textarea, select {
            width: 100%;
            padding: 10px;
            border: 1px solid #ccd2dc;
            border-radius: 5px;
            box-sizing: border-box;
            font: inherit;
        }

        textarea {
            min-height: 85px;
            resize: vertical;
        }

        button {
            margin-top: 24px;
            padding: 12px 22px;
            background: #235bb5;
            color: white;
            border: none;
            border-radius: 5px;
            cursor: pointer;
            font-weight: bold;
        }

        .erro {
            padding: 12px;
            margin-bottom: 18px;
            background: #ffe8e8;
            color: #a32626;
            border-radius: 5px;
        }

        .horas {
            margin-top: 16px;
            padding: 16px;
            background: #f4f6f9;
            border-radius: 6px;
        }

        .oculto {
            display: none;
        }
    </style>
</head>

<body>
<div class="container">
    <h1>Criar reunião</h1>

    <p>Preencha os dados para cadastrar uma nova reunião.</p>

    <c:if test="${not empty erro}">
        <div class="erro">
            <c:out value="${erro}"/>
        </div>
    </c:if>

    <form method="post"
          action="${pageContext.request.contextPath}/reunioes/nova">

        <label for="titulo">Título *</label>
        <input type="text" id="titulo" name="titulo"
               maxlength="150" required>

        <label for="descricao">Descrição</label>
        <textarea id="descricao" name="descricao"
                  maxlength="1000"></textarea>

        <label for="localReuniao">Local</label>
        <input type="text" id="localReuniao"
               name="localReuniao" maxlength="200">

        <label for="nomeProponente">Nome do proponente *</label>
        <input type="text" id="nomeProponente"
               name="nomeProponente" maxlength="100" required>

        <label for="emailProponente">E-mail do proponente *</label>
        <input type="email" id="emailProponente"
               name="emailProponente" maxlength="254" required>

        <label for="dataInicio">Data inicial *</label>
        <input type="date" id="dataInicio"
               name="dataInicio" required>

        <label for="dataFim">Data final *</label>
        <input type="date" id="dataFim"
               name="dataFim" required>

        <label for="modoDivisao">Divisão dos horários *</label>
        <select id="modoDivisao" name="modoDivisao"
                onchange="alterarModo()" required>
            <option value="DIA">Por dia</option>
            <option value="PERIODOS">Manhã, tarde e noite</option>
            <option value="HORAS">Intervalos de horas</option>
        </select>

        <div id="camposHoras" class="horas oculto">
            <label for="horaInicio">Hora inicial</label>
            <input type="time" id="horaInicio" name="horaInicio">

            <label for="horaFim">Hora final</label>
            <input type="time" id="horaFim" name="horaFim">

            <label for="duracaoMinutos">Duração em minutos</label>
            <input type="number" id="duracaoMinutos"
                   name="duracaoMinutos" min="1">
        </div>

        <button type="submit">Criar reunião</button>
    </form>
</div>

<script>
    function alterarModo() {
        const modo = document.getElementById(
                "modoDivisao").value;

        const campos = document.getElementById(
                "camposHoras");

        const mostrar = modo === "HORAS";

        campos.classList.toggle("oculto", !mostrar);

        ["horaInicio", "horaFim", "duracaoMinutos"]
                .forEach(function(id) {
                    const campo = document.getElementById(id);
                    campo.required = mostrar;

                    if (!mostrar) {
                        campo.value = "";
                    }
                });
    }

    alterarModo();
</script>
</body>
</html>
