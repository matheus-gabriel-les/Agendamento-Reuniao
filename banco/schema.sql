
CREATE DATABASE IF NOT EXISTS agendamento_reunioes
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE agendamento_reunioes;

-- ============================================
-- REUNIAO
-- ============================================

CREATE TABLE IF NOT EXISTS reuniao (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,

    titulo VARCHAR(150) NOT NULL,
    descricao VARCHAR(1000),
    local_reuniao VARCHAR(200),

    nome_proponente VARCHAR(100) NOT NULL,
    email_proponente VARCHAR(254) NOT NULL,

    token_publico VARCHAR(36) NOT NULL,
    token_admin VARCHAR(36) NOT NULL,

    data_inicio DATE NOT NULL,
    data_fim DATE NOT NULL,

    modo_divisao VARCHAR(10) NOT NULL,

    hora_inicio TIME,
    hora_fim TIME,
    duracao_minutos INTEGER,

    criada_em TIMESTAMP NOT NULL
        DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT uk_reuniao_token_publico
        UNIQUE (token_publico),

    CONSTRAINT uk_reuniao_token_admin
        UNIQUE (token_admin),

    CONSTRAINT ck_reuniao_datas
        CHECK (data_fim >= data_inicio),

    CONSTRAINT ck_reuniao_modo
        CHECK (
            modo_divisao IN (
                'DIA',
                'PERIODOS',
                'HORAS'
            )
        ),

    CONSTRAINT ck_reuniao_horas
        CHECK (
            modo_divisao <> 'HORAS'
            OR (
                hora_inicio IS NOT NULL
                AND hora_fim IS NOT NULL
                AND hora_fim > hora_inicio
                AND duracao_minutos > 0
            )
        ),

    INDEX idx_reuniao_data_fim (data_fim)

) ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4
COLLATE=utf8mb4_unicode_ci;


-- ============================================
-- HORARIO
-- ============================================

CREATE TABLE IF NOT EXISTS horario (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,

    reuniao_id BIGINT NOT NULL,

    dia DATE NOT NULL,
    periodo VARCHAR(10),

    hora_inicio TIME,
    hora_fim TIME,

    CONSTRAINT fk_horario_reuniao
        FOREIGN KEY (reuniao_id)
        REFERENCES reuniao(id)
        ON DELETE CASCADE,

    CONSTRAINT ck_horario_periodo
        CHECK (
            periodo IS NULL
            OR periodo IN (
                'MANHA',
                'TARDE',
                'NOITE'
            )
        ),

    INDEX idx_horario_reuniao (
        reuniao_id,
        dia
    )

) ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4
COLLATE=utf8mb4_unicode_ci;


-- ============================================
-- PARTICIPANTE
-- ============================================

CREATE TABLE IF NOT EXISTS participante (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,

    reuniao_id BIGINT NOT NULL,

    nome VARCHAR(100),
    email VARCHAR(254) NOT NULL,
    token VARCHAR(36) NOT NULL,

    proponente BOOLEAN NOT NULL DEFAULT FALSE,

    convidado_em TIMESTAMP NOT NULL
        DEFAULT CURRENT_TIMESTAMP,

    respondido_em TIMESTAMP NULL,
    email_enviado_em TIMESTAMP NULL,

    CONSTRAINT uk_participante_token
        UNIQUE (token),

    CONSTRAINT uk_participante_reuniao_email
        UNIQUE (reuniao_id, email),

    CONSTRAINT fk_participante_reuniao
        FOREIGN KEY (reuniao_id)
        REFERENCES reuniao(id)
        ON DELETE CASCADE

) ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4
COLLATE=utf8mb4_unicode_ci;


-- ============================================
-- DISPONIBILIDADE
-- ============================================

CREATE TABLE IF NOT EXISTS disponibilidade (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,

    participante_id BIGINT NOT NULL,
    horario_id BIGINT NOT NULL,

    resposta VARCHAR(10) NOT NULL,

    CONSTRAINT uk_disponibilidade
        UNIQUE (
            participante_id,
            horario_id
        ),

    CONSTRAINT fk_disp_participante
        FOREIGN KEY (participante_id)
        REFERENCES participante(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_disp_horario
        FOREIGN KEY (horario_id)
        REFERENCES horario(id)
        ON DELETE CASCADE,

    CONSTRAINT ck_disp_resposta
        CHECK (
            resposta IN (
                'SIM',
                'TALVEZ',
                'NAO'
            )
        ),

    INDEX idx_disp_horario (horario_id)

) ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4
COLLATE=utf8mb4_unicode_ci;


-- ============================================
-- REUNIAO_FINALIZACAO
-- ============================================

CREATE TABLE IF NOT EXISTS reuniao_finalizacao (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,

    reuniao_id BIGINT NOT NULL,
    horario_id BIGINT NOT NULL,

    finalizada_em TIMESTAMP NOT NULL
        DEFAULT CURRENT_TIMESTAMP,

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

) ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4
COLLATE=utf8mb4_unicode_ci;
