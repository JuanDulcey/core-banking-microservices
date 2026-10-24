-- =============================================================================
-- ARCHIVO: BaseDatos.sql
-- PROYECTO: Core Banking Microservices (Arquitectura Hexagonal)
-- MOTOR: PostgreSQL 17
-- PATRÓN: Database-per-Service (db_clientes y db_cuentas)
-- NOTA: Al iniciar con Docker Compose, Flyway ejecuta automáticamente estas
--       migraciones sobre cada microservicio. Este script consolida la creación
--       completa de bases de datos, tablas, restricciones, índices y datos base.
-- =============================================================================

-- -----------------------------------------------------------------------------
-- 1. CREACIÓN DE BASES DE DATOS AISLADAS POR MICROSERVICIO
-- -----------------------------------------------------------------------------
CREATE DATABASE db_clientes;
CREATE DATABASE db_cuentas;

-- =============================================================================
-- ESQUEMA MICROSERVICIO 1: ms-clientes (Conectarse a db_clientes)
-- =============================================================================
\connect db_clientes;

-- Tabla padre: personas (Estrategia JPA InheritanceType.JOINED)
CREATE TABLE IF NOT EXISTS personas (
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

-- Tabla hija: clientes (Hereda PK de personas mediante FK persona_id)
CREATE TABLE IF NOT EXISTS clientes (
    persona_id UUID                     NOT NULL,
    cliente_id VARCHAR(50)              NOT NULL,
    contrasena VARCHAR(255)             NOT NULL,
    estado     BOOLEAN                  NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT pk_clientes PRIMARY KEY (persona_id),
    CONSTRAINT fk_clientes_persona FOREIGN KEY (persona_id) REFERENCES personas (persona_id) ON DELETE CASCADE,
    CONSTRAINT uq_clientes_cliente_id UNIQUE (cliente_id)
);

CREATE INDEX IF NOT EXISTS idx_personas_nombre ON personas (nombre);
CREATE INDEX IF NOT EXISTS idx_clientes_estado ON clientes (estado);


-- =============================================================================
-- ESQUEMA MICROSERVICIO 2: ms-cuentas (Conectarse a db_cuentas)
-- =============================================================================
\connect db_cuentas;

-- Proyección local asíncrona de clientes sincronizada vía RabbitMQ (Event-Carried State Transfer)
CREATE TABLE IF NOT EXISTS cliente_snapshot (
    persona_id     UUID                     NOT NULL,
    cliente_id     VARCHAR(50)              NOT NULL,
    nombre         VARCHAR(150)             NOT NULL,
    identificacion VARCHAR(30),
    estado         BOOLEAN                  NOT NULL DEFAULT TRUE,
    updated_at     TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT pk_cliente_snapshot PRIMARY KEY (persona_id),
    CONSTRAINT uq_cliente_snapshot_cliente_id UNIQUE (cliente_id)
);

-- Entidad Cuenta (Incluye columna 'version' para Optimistic Locking + soporte Pessimistic Write Lock)
CREATE TABLE IF NOT EXISTS cuentas (
    cuenta_id        UUID                     NOT NULL,
    numero_cuenta    VARCHAR(30)              NOT NULL,
    tipo_cuenta      VARCHAR(20)              NOT NULL,
    saldo_inicial    NUMERIC(19, 2)           NOT NULL,
    saldo_disponible NUMERIC(19, 2)           NOT NULL,
    estado           BOOLEAN                  NOT NULL DEFAULT TRUE,
    cliente_id       UUID                     NOT NULL,
    version          BIGINT                   NOT NULL DEFAULT 0,
    created_at       TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at       TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT pk_cuentas PRIMARY KEY (cuenta_id),
    CONSTRAINT uq_cuentas_numero UNIQUE (numero_cuenta),
    CONSTRAINT fk_cuentas_cliente_snapshot FOREIGN KEY (cliente_id) REFERENCES cliente_snapshot (persona_id),
    CONSTRAINT chk_cuentas_saldo_inicial CHECK (saldo_inicial >= 0),
    CONSTRAINT chk_cuentas_saldo_disponible CHECK (saldo_disponible >= 0)
);

-- Entidad Movimientos (Libro mayor transaccional con trazabilidad de saldo previo y resultante)
CREATE TABLE IF NOT EXISTS movimientos (
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

CREATE INDEX IF NOT EXISTS idx_cliente_snapshot_nombre ON cliente_snapshot (nombre);
CREATE INDEX IF NOT EXISTS idx_cuentas_cliente_id ON cuentas (cliente_id);
CREATE INDEX IF NOT EXISTS idx_movimientos_cuenta_fecha ON movimientos (cuenta_id, fecha DESC);
CREATE INDEX IF NOT EXISTS idx_movimientos_numero_cuenta ON movimientos (numero_cuenta);
