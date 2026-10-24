-- =============================================================================
-- ARCHIVO: BaseDatos.sql
-- PROYECTO: Core Banking Microservices (Arquitectura Hexagonal)
-- MOTOR: PostgreSQL 17
-- PATRÓN: Database-per-Service (db_clientes y db_cuentas)
--
-- MODO DE EJECUCIÓN:
--   Opción A (Automática con Docker Compose):
--     Al levantar `docker compose up --build -d`, PostgreSQL crea db_clientes y
--     db_cuentas automáticamente y Flyway ejecuta el esquema limpio en cada
--     microservicio para poder probar la creación desde cero vía Postman.
--
--   Opción B (Ejecución directa de este script completo con datos semilla):
--     Por consola psql:
--       psql -U postgres -h localhost -p 5432 -f BaseDatos.sql
--     En DBeaver / pgAdmin:
--       Ejecute el bloque 1 conectado a `postgres`, el bloque 2 conectado a
--       `db_clientes` y el bloque 3 conectado a `db_cuentas`.
-- =============================================================================

-- -----------------------------------------------------------------------------
-- BLOQUE 1: CREACIÓN DE BASES DE DATOS AISLADAS POR MICROSERVICIO
-- -----------------------------------------------------------------------------
SELECT 'CREATE DATABASE db_clientes'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'db_clientes')\gexec

SELECT 'CREATE DATABASE db_cuentas'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'db_cuentas')\gexec


-- =============================================================================
-- BLOQUE 2: MICROSERVICIO ms-clientes (Base de datos: db_clientes)
-- =============================================================================
\connect db_clientes;

-- Tabla padre: personas (Herencia JPA InheritanceType.JOINED)
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

-- Tabla hija: clientes (Comparte clave primaria persona_id como FK hacia personas)
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

-- Datos semilla idempotentes (Caso de Uso 1 — Contraseñas cifradas con BCrypt)
INSERT INTO personas (persona_id, nombre, genero, edad, identificacion, direccion, telefono) VALUES
    ('11111111-1111-1111-1111-111111111111', 'Jose Lema',          'Masculino', 32, '1710012345', 'Otavalo sn y principal',  '098254785'),
    ('22222222-2222-2222-2222-222222222222', 'Marianela Montalvo', 'Femenino',  29, '1720054321', 'Amazonas y NNUU',         '097548965'),
    ('33333333-3333-3333-3333-333333333333', 'Juan Osorio',        'Masculino', 27, '1730098765', '13 junio y Equinoccial',  '098874587')
ON CONFLICT DO NOTHING;

INSERT INTO clientes (persona_id, cliente_id, contrasena, estado, created_at, updated_at)
SELECT v.persona_id::uuid, v.cliente_id, v.contrasena, v.estado, NOW(), NOW()
FROM (VALUES
    ('11111111-1111-1111-1111-111111111111', 'CLI-JOSE',      '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', TRUE),
    ('22222222-2222-2222-2222-222222222222', 'CLI-MARIANELA', '$2a$10$Dow1z0yH9v8F8sL9mN2pQeK8vR7xT5wY3uJ1cH6bG4dF2sA0kL8mO', TRUE),
    ('33333333-3333-3333-3333-333333333333', 'CLI-JUAN',      '$2a$10$wK8pL2mN4bV6cX9zQ1rT3eY5uI7oP0aS2dF4gH6jK8lZ0xC2vB4nM', TRUE)
) AS v(persona_id, cliente_id, contrasena, estado)
WHERE EXISTS (SELECT 1 FROM personas p WHERE p.persona_id = v.persona_id::uuid)
ON CONFLICT DO NOTHING;


-- =============================================================================
-- BLOQUE 3: MICROSERVICIO ms-cuentas (Base de datos: db_cuentas)
-- =============================================================================
\connect db_cuentas;

-- Proyección local asíncrona de clientes sincronizada vía RabbitMQ
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

-- Entidad Cuenta (Con control de concurrencia @Version + soporte PESSIMISTIC_WRITE)
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

-- Libro transaccional de Movimientos
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

-- Datos semilla idempotentes (Proyección de clientes + Casos de Uso 2, 3 y 4)
INSERT INTO cliente_snapshot (persona_id, cliente_id, nombre, identificacion, estado, updated_at) VALUES
    ('11111111-1111-1111-1111-111111111111', 'CLI-JOSE',      'Jose Lema',          '1710012345', TRUE, NOW()),
    ('22222222-2222-2222-2222-222222222222', 'CLI-MARIANELA', 'Marianela Montalvo', '1720054321', TRUE, NOW()),
    ('33333333-3333-3333-3333-333333333333', 'CLI-JUAN',      'Juan Osorio',        '1730098765', TRUE, NOW())
ON CONFLICT DO NOTHING;

INSERT INTO cuentas (cuenta_id, numero_cuenta, tipo_cuenta, saldo_inicial, saldo_disponible, estado, cliente_id, version, created_at, updated_at)
SELECT v.cuenta_id::uuid, v.numero_cuenta, v.tipo_cuenta, v.saldo_inicial, v.saldo_disponible, v.estado, v.cliente_id::uuid, v.version, NOW(), NOW()
FROM (VALUES
    ('aaaa1111-1111-1111-1111-111111111111', '478758', 'AHORROS',   2000.00, 1425.00, TRUE, '11111111-1111-1111-1111-111111111111', 1),
    ('aaaa2222-2222-2222-2222-222222222222', '225487', 'CORRIENTE',  100.00,  700.00, TRUE, '22222222-2222-2222-2222-222222222222', 1),
    ('aaaa3333-3333-3333-3333-333333333333', '495878', 'AHORROS',      0.00,  150.00, TRUE, '33333333-3333-3333-3333-333333333333', 1),
    ('aaaa4444-4444-4444-4444-444444444444', '496825', 'AHORROS',    540.00,    0.00, TRUE, '22222222-2222-2222-2222-222222222222', 1),
    ('aaaa5555-5555-5555-5555-555555555555', '585545', 'CORRIENTE', 1000.00, 1000.00, TRUE, '11111111-1111-1111-1111-111111111111', 0)
) AS v(cuenta_id, numero_cuenta, tipo_cuenta, saldo_inicial, saldo_disponible, estado, cliente_id, version)
WHERE EXISTS (SELECT 1 FROM cliente_snapshot cs WHERE cs.persona_id = v.cliente_id::uuid)
ON CONFLICT DO NOTHING;

INSERT INTO movimientos (movimiento_id, cuenta_id, numero_cuenta, fecha, tipo_movimiento, saldo_inicial, valor, saldo)
SELECT v.movimiento_id::uuid, v.cuenta_id::uuid, v.numero_cuenta, v.fecha::timestamp, v.tipo_movimiento, v.saldo_inicial, v.valor, v.saldo
FROM (VALUES
    ('bbbb1111-1111-1111-1111-111111111111', 'aaaa1111-1111-1111-1111-111111111111', '478758', '2022-02-10 12:00:00', 'RETIRO',   2000.00, -575.00, 1425.00),
    ('bbbb2222-2222-2222-2222-222222222222', 'aaaa2222-2222-2222-2222-222222222222', '225487', '2022-02-10 12:00:00', 'DEPOSITO',  100.00,  600.00,  700.00),
    ('bbbb3333-3333-3333-3333-333333333333', 'aaaa3333-3333-3333-3333-333333333333', '495878', '2022-02-09 12:00:00', 'DEPOSITO',    0.00,  150.00,  150.00),
    ('bbbb4444-4444-4444-4444-444444444444', 'aaaa4444-4444-4444-4444-444444444444', '496825', '2022-02-08 12:00:00', 'RETIRO',    540.00, -540.00,    0.00)
) AS v(movimiento_id, cuenta_id, numero_cuenta, fecha, tipo_movimiento, saldo_inicial, valor, saldo)
WHERE EXISTS (SELECT 1 FROM cuentas c WHERE c.cuenta_id = v.cuenta_id::uuid)
ON CONFLICT DO NOTHING;
