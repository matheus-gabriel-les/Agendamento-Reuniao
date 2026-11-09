
<%@ page contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport"
          content="width=device-width, initial-scale=1">

    <title>Excluir reunião</title>

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
            max-width: 700px;
            margin: 0 auto;
            padding: 30px;
            background: white;
            border-radius: 10px;
            box-shadow: 0 2px 12px #00000012;
        }

        h1 {
            color: #a32626;
            margin-top: 0;
        }

        p {
            line-height: 1.6;
        }

        .informacoes {
            margin: 22px 0;
            padding: 18px;
            background: #f0f3f7;
            border-radius: 6px;
        }

        .informacoes p {
            margin: 8px 0;
        }

        .aviso {
            margin: 22px 0;
            padding: 18px;
            background: #fff1e9;
            border: 1px solid #efc7b4;
            border-radius: 6px;
            color: #853d25;
        }

        .erro {
            padding: 14px;
            margin: 16px 0;
            background: #ffe8e8;
            color: #a32626;
            border-radius: 6px;
        }

        label {
            display: block;
            margin: 18px 0 8px;
            font-weight: bold;
        }

        input {
            width: 100%;
            padding: 12px;
            border: 1px solid #ccd2dc;
            border-radius: 5px;
            font: inherit;
        }

        .acoes {
            display: flex;
            flex-wrap: wrap;
            gap: 12px;
            margin-top: 24px;
        }

        button,
        .botao {
            display: inline-block;
            border: 0;
            padding: 12px 18px;
            border-radius: 5px;
            text-decoration: none;
            font: inherit;
            font-weight: bold;
            cursor: pointer;
            text-align: center;
        }

        .botao-excluir {
            background: #a32626;
            color: white;
        }

        .botao-excluir:hover {
            background: #801b1b;
        }

        .botao-cancelar {
            background: #e8edf3;
            color: #263547;
        }

        .botao-cancelar:hover {
            background: #d5dee8;
        }

        @media (max-width: 600px) {
            .container {
                padding: 20px;
            }

            .acoes {
                flex-direction: column;
            }
        }
    </style>
</head>

<body>

<div class="container">

    <h1>Excluir reunião</h1>

    <p>
        Você está prestes a excluir uma reunião
        permanentemente do sistema.
    </p>

    <c:if test="${not empty erro}">
        <div class="erro" role="alert">
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

        </div>

        <div class="aviso">

            <strong>Atenção: esta ação não pode ser desfeita.</strong>

            <p>
                Ao excluir a reunião, também serão
                removidos seus horários, participantes,
                disponibilidades e horário definitivo,
                caso exista.
            </p>

            <p>
                Os links públicos e individuais
                deixarão de funcionar.
            </p>

        </div>

        <form method="post"
              action="${pageContext.request.contextPath}/reunioes/excluir"
              onsubmit="return confirm('Tem certeza de que deseja excluir esta reunião permanentemente?');">

            <input type="hidden"
                   name="csrfToken"
                   value="${csrfToken}">

            <label for="confirmacao">
                Digite EXCLUIR para confirmar:
            </label>

            <input type="text"
                   id="confirmacao"
                   name="confirmacao"
                   placeholder="EXCLUIR"
                   autocomplete="off"
                   pattern="EXCLUIR"
                   title="Digite EXCLUIR em letras maiúsculas"
                   required>

            <div class="acoes">

                <button type="submit"
                        class="botao-excluir">
                    Excluir reunião permanentemente
                </button>

                <a class="botao botao-cancelar"
                   href="${pageContext.request.contextPath}/reunioes/convidados">
                    Cancelar
                </a>

            </div>

        </form>

    </c:if>

</div>

</body>
</html>
