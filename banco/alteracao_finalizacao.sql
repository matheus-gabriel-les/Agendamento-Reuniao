
USE agendamento_reunioes;

CREATE TABLE IF NOT EXISTS reuniao_finalizacao (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,

    reuniao_id BIGINT NOT NULL,
    horario_id BIGINT NOT NULL,

    finalizada_em TIMESTAMP
        NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT uq_finalizacao_reuniao
        UNIQUE (reuniao_id),

    CONSTRAINT fk_finalizacao_reuniao
        FOREIGN KEY (reuniao_id)
        REFERENCES reuniao(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_finalizacao_horario
        FOREIGN KEY (horario_id)
        REFERENCES horario(id)
        ON DELETE RESTRICT
);
