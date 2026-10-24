-- =============================================================================
-- Migración V1: Esquema normalizado con herencia JOINED (personas -> clientes)
-- Microservicio: ms-clientes
-- =============================================================================

CREATE TABLE personas (
    persona_id     UUID         NOT NULL,
    nombre         VARCHAR(150) NOT NULL,
    genero         VARCHAR(30)  NOT NULL,
    edad           INTEGER,
    identificacion VARCHAR(30),
    direccion      VARCHAR(250) NOT NULL,
    telefono       VARCHAR(30)  NOT NULL,
    CONSTRAINT pk_personas PRIMARY KEY (persona_id),
    CONSTRAINT uq_personas_identificacion UNIQUE (identificacion),
    CONSTRAINT chk_personas_edad CHECK (edad IS NULL OR (edad >= 0 AND edad <= 130))
);

CREATE TABLE clientes (
    persona_id UUID         NOT NULL,
    cliente_id VARCHAR(50)  NOT NULL,
    contrasena VARCHAR(255) NOT NULL,
    estado     BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT pk_clientes PRIMARY KEY (persona_id),
    CONSTRAINT fk_clientes_persona FOREIGN KEY (persona_id) REFERENCES personas (persona_id) ON DELETE CASCADE,
    CONSTRAINT uq_clientes_cliente_id UNIQUE (cliente_id)
);

CREATE INDEX idx_personas_nombre ON personas (nombre);
CREATE INDEX idx_clientes_estado ON clientes (estado);
