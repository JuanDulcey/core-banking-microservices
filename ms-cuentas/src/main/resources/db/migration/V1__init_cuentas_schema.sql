-- =============================================================================
-- Migración V1: Esquema contable de Cuentas, Movimientos y Proyección de Clientes
-- Microservicio: ms-cuentas
-- =============================================================================

CREATE TABLE cliente_snapshot (
    persona_id     UUID         NOT NULL,
    cliente_id     VARCHAR(50)  NOT NULL,
    nombre         VARCHAR(150) NOT NULL,
    identificacion VARCHAR(30),
    estado         BOOLEAN      NOT NULL DEFAULT TRUE,
    updated_at     TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT pk_cliente_snapshot PRIMARY KEY (persona_id),
    CONSTRAINT uq_cliente_snapshot_cliente_id UNIQUE (cliente_id)
);

CREATE TABLE cuentas (
    cuenta_id        UUID           NOT NULL,
    numero_cuenta    VARCHAR(30)    NOT NULL,
    tipo_cuenta      VARCHAR(20)    NOT NULL,
    saldo_inicial    NUMERIC(19, 2) NOT NULL,
    saldo_disponible NUMERIC(19, 2) NOT NULL,
    estado           BOOLEAN        NOT NULL DEFAULT TRUE,
    cliente_id       UUID           NOT NULL,
    version          BIGINT         NOT NULL DEFAULT 0,
    created_at       TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at       TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT pk_cuentas PRIMARY KEY (cuenta_id),
    CONSTRAINT uq_cuentas_numero UNIQUE (numero_cuenta),
    CONSTRAINT fk_cuentas_cliente_snapshot FOREIGN KEY (cliente_id) REFERENCES cliente_snapshot (persona_id),
    CONSTRAINT chk_cuentas_saldo_inicial CHECK (saldo_inicial >= 0),
    CONSTRAINT chk_cuentas_saldo_disponible CHECK (saldo_disponible >= 0)
);

CREATE TABLE movimientos (
    movimiento_id   UUID           NOT NULL,
    cuenta_id       UUID           NOT NULL,
    numero_cuenta   VARCHAR(30)    NOT NULL,
    fecha           TIMESTAMP      NOT NULL,
    tipo_movimiento VARCHAR(20)    NOT NULL,
    saldo_inicial   NUMERIC(19, 2) NOT NULL,
    valor           NUMERIC(19, 2) NOT NULL,
    saldo           NUMERIC(19, 2) NOT NULL,
    CONSTRAINT pk_movimientos PRIMARY KEY (movimiento_id),
    CONSTRAINT fk_movimientos_cuenta FOREIGN KEY (cuenta_id) REFERENCES cuentas (cuenta_id)
);

CREATE INDEX idx_cliente_snapshot_nombre ON cliente_snapshot (nombre);
CREATE INDEX idx_cuentas_cliente_id ON cuentas (cliente_id);
CREATE INDEX idx_movimientos_cuenta_fecha ON movimientos (cuenta_id, fecha DESC);
CREATE INDEX idx_movimientos_numero_cuenta ON movimientos (numero_cuenta);
