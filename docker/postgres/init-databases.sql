-- Script de inicialización automática para Docker PostgreSQL
-- Crea bases de datos aisladas siguiendo el patrón Database-per-Service

SELECT 'CREATE DATABASE db_clientes'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'db_clientes')\gexec

SELECT 'CREATE DATABASE db_cuentas'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'db_cuentas')\gexec
