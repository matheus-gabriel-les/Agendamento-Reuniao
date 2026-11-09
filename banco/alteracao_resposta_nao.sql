
USE agendamento_reunioes;

-- Permite registrar uma indisponibilidade explicita.
-- Os registros existentes sao preservados.

ALTER TABLE disponibilidade
    DROP CHECK ck_disp_resposta,
    ADD CONSTRAINT ck_disp_resposta
        CHECK (resposta IN ('SIM', 'TALVEZ', 'NAO'));

-- Conferir os valores existentes.
SELECT
    resposta,
    COUNT(*) AS quantidade
FROM disponibilidade
GROUP BY resposta;
