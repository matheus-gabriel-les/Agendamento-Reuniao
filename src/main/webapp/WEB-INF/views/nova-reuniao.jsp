
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
        * {
            box-sizing: border-box;
        }

        body {
            font-family: Arial, sans-serif;
            background: #f4f6f9;
            color: #263547;
            margin: 0;
            padding: 30px 15px;
        }

        .container {
            max-width: 750px;
            margin: auto;
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
            padding-bottom: 10px;
            border-bottom: 1px solid #e4e7eb;
            font-size: 21px;
        }

        p {
            line-height: 1.6;
        }

        label {
            display: block;
            margin-top: 16px;
            margin-bottom: 6px;
            font-weight: bold;
        }

        input,
        textarea,
        select {
            width: 100%;
            padding: 11px;
            border: 1px solid #ccd2dc;
            border-radius: 5px;
            font: inherit;
        }

        textarea {
            min-height: 90px;
            resize: vertical;
        }

        .convidados {
            min-height: 155px;
            font-family: monospace;
            line-height: 1.6;
        }

        button {
            margin-top: 26px;
            padding: 13px 24px;
            background: #235bb5;
            color: white;
            border: 0;
            border-radius: 5px;
            cursor: pointer;
            font: inherit;
            font-weight: bold;
        }

        button:hover {
            background: #19488f;
        }

        .erro {
            padding: 14px;
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

        .ajuda {
            color: #657487;
            font-size: 14px;
            margin-top: 6px;
        }

        .exemplo {
            background: #f0f3f7;
            padding: 14px;
            border-radius: 5px;
            font-family: monospace;
            line-height: 1.8;
            overflow-wrap: anywhere;
        }

        .aviso {
            padding: 16px;
            background: #fff7e6;
            border-radius: 6px;
            color: #77571a;
            margin-top: 24px;
        }

        .oculto {
            display: none;
        }

        @media (max-width: 600px) {
            .container {
                padding: 20px;
            }

            button {
                width: 100%;
            }
        }
    </style>
</head>

<body>

<div class="container">

    <h1>Criar reunião</h1>

    <p>
        Preencha os dados da reunião,
        defina os horários disponíveis e
        informe os participantes.
    </p>

    <c:if test="${not empty erro}">
        <div class="erro" role="alert">
            <c:out value="${erro}"/>
        </div>
    </c:if>

    <form method="post"
          action="${pageContext.request.contextPath}/reunioes/nova">

        <input type="hidden"
               name="csrfToken"
               value="<c:out value='${csrfToken}'/>">

        <h2>Informações da reunião</h2>

        <label for="titulo">Título *</label>

        <input type="text"
               id="titulo"
               name="titulo"
               value="<c:out value='${param.titulo}'/>"
               maxlength="150"
               required>

        <label for="descricao">Descrição</label>

        <textarea id="descricao"
                  name="descricao"
                  maxlength="1000"><c:out value="${param.descricao}"/></textarea>

        <label for="localReuniao">Local</label>

        <input type="text"
               id="localReuniao"
               name="localReuniao"
               value="<c:out value='${param.localReuniao}'/>"
               maxlength="200">

        <h2>Proponente</h2>

        <p class="ajuda">
            O proponente será registrado
            automaticamente como participante.
        </p>

        <label for="nomeProponente">
            Nome do proponente *
        </label>

        <input type="text"
               id="nomeProponente"
               name="nomeProponente"
               value="<c:out value='${param.nomeProponente}'/>"
               maxlength="100"
               required>

        <label for="emailProponente">
            E-mail do proponente *
        </label>

        <input type="email"
               id="emailProponente"
               name="emailProponente"
               value="<c:out value='${param.emailProponente}'/>"
               maxlength="254"
               required>

        <h2>Datas e horários</h2>

        <label for="dataInicio">Data inicial *</label>

        <input type="date"
               id="dataInicio"
               name="dataInicio"
               value="<c:out value='${param.dataInicio}'/>"
               required>

        <label for="dataFim">Data final *</label>

        <input type="date"
               id="dataFim"
               name="dataFim"
               value="<c:out value='${param.dataFim}'/>"
               required>

        <label for="modoDivisao">
            Divisão dos horários *
        </label>

        <select id="modoDivisao"
                name="modoDivisao"
                onchange="alterarModo()"
                required>

            <option value="DIA"
                ${param.modoDivisao eq 'DIA' ? 'selected' : ''}>
                Por dia
            </option>

            <option value="PERIODOS"
                ${param.modoDivisao eq 'PERIODOS' ? 'selected' : ''}>
                Manhã, tarde e noite
            </option>

            <option value="HORAS"
                ${param.modoDivisao eq 'HORAS' ? 'selected' : ''}>
                Intervalos de horas
            </option>

        </select>

        <div id="camposHoras"
             class="horas oculto">

            <label for="horaInicio">
                Hora inicial
            </label>

            <input type="time"
                   id="horaInicio"
                   name="horaInicio"
                   value="<c:out value='${param.horaInicio}'/>">

            <label for="horaFim">
                Hora final
            </label>

            <input type="time"
                   id="horaFim"
                   name="horaFim"
                   value="<c:out value='${param.horaFim}'/>">

            <label for="duracaoMinutos">
                Duração em minutos
            </label>

            <input type="number"
                   id="duracaoMinutos"
                   name="duracaoMinutos"
                   value="<c:out value='${param.duracaoMinutos}'/>"
                   min="1">

        </div>

        <h2>Convidados iniciais</h2>

        <p>
            Você pode cadastrar convidados
            agora ou adicioná-los depois
            pelo gerenciamento da reunião.
        </p>

        <p class="ajuda">
            Para cadastrar vários convidados,
            informe um por linha usando
            nome e e-mail separados por ponto e vírgula.
        </p>

        <div class="exemplo">
            Maria Silva;maria@exemplo.com
            <div>João Souza;joao@exemplo.com</div>
            <div>Ana Costa;ana@exemplo.com</div>
        </div>

        <label for="convidados">
            Lista de convidados (opcional)
        </label>

        <textarea id="convidados"
                  name="convidados"
                  class="convidados"
                  placeholder="Maria Silva;maria@exemplo.com&#10;João Souza;joao@exemplo.com"><c:out value="${param.convidados}"/></textarea>

        <p class="ajuda">
            Máximo de 100 convidados.
            Não repita e-mails e não inclua
            o proponente novamente na lista.
        </p>

        <div class="aviso">
            <strong>Importante:</strong>

            <p>
                Após criar a reunião, você receberá
                os tokens de gerenciamento e consulta.
                Os tokens individuais dos participantes
                estarão disponíveis no gerenciamento.
            </p>

            <p>
                O cadastro não envia e-mails automaticamente.
                Essa funcionalidade será implementada
                em uma etapa posterior.
            </p>
        </div>

        <button type="submit">
            Criar reunião e cadastrar participantes
        </button>

    </form>

</div>

<script>
    function alterarModo() {

        const modo = document.getElementById(
            "modoDivisao"
        ).value;

        const campos = document.getElementById(
            "camposHoras"
        );

        const mostrar = modo === "HORAS";

        campos.classList.toggle(
            "oculto",
            !mostrar
        );

        [
            "horaInicio",
            "horaFim",
            "duracaoMinutos"
        ].forEach(function(id) {

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
