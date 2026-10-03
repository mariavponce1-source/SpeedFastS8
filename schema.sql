-- =========================================================
-- SCRIPT DE CREACIÓN Y CONFIGURACIÓN DE BASE DE DATOS
-- PROYECTO: SpeedFast - Gestión Integral de Operaciones
-- =========================================================

-- 1. Creación de la base de datos
CREATE DATABASE IF NOT EXISTS speedfast_db
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE speedfast_db;

-- 2. Limpieza de tablas previas (en orden inverso a dependencias)
DROP TABLE IF EXISTS entregas;
DROP TABLE IF EXISTS pedidos;
DROP TABLE IF EXISTS repartidores;

-- 3. Tabla Repartidores
CREATE TABLE repartidores (
                              id INT AUTO_INCREMENT PRIMARY KEY,
                              nombre VARCHAR(100) NOT NULL
) ENGINE=InnoDB;

-- 4. Tabla Pedidos
CREATE TABLE pedidos (
                         id INT AUTO_INCREMENT PRIMARY KEY,
                         direccion VARCHAR(100) NOT NULL,
                         tipo ENUM('COMIDA', 'ENCOMIENDA', 'EXPRESS') NOT NULL,
                         estado ENUM('PENDIENTE', 'EN_REPARTO', 'ENTREGADO') NOT NULL
) ENGINE=InnoDB;

-- 5. Tabla Entregas (Asocia Pedido y Repartidor)
CREATE TABLE entregas (
                          id INT AUTO_INCREMENT PRIMARY KEY,
                          id_pedido INT NOT NULL,
                          id_repartidor INT NOT NULL,
                          fecha DATE NOT NULL,
                          hora TIME NOT NULL,
                          CONSTRAINT fk_entrega_pedido
                              FOREIGN KEY (id_pedido) REFERENCES pedidos(id)
                                  ON DELETE CASCADE ON UPDATE CASCADE,
                          CONSTRAINT fk_entrega_repartidor
                              FOREIGN KEY (id_repartidor) REFERENCES repartidores(id)
                                  ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

-- 6. Datos iniciales de prueba (Opcional)
INSERT INTO repartidores (nombre) VALUES
                                      ('Carlos Mendoza'),
                                      ('Valentina Ríos');

INSERT INTO pedidos (direccion, tipo, estado) VALUES
                                                  ('Av. Libertador 450', 'COMIDA', 'ENTREGADO'),
                                                  ('Calle Los Aromos 1234', 'ENCOMIENDA', 'EN_REPARTO');

INSERT INTO entregas (id_pedido, id_repartidor, fecha, hora) VALUES
    (1, 1, '2026-10-03', '14:30:00');