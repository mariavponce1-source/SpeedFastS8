# SpeedFast - Sistema Integral de Gestión de Entregas

Sistema de escritorio desarrollado en **Java Swing** y persistencia relacional con **MySQL (JDBC)** para la gestión operativa de pedidos, repartidores y entregas en la empresa **SpeedFast**.

---

## 📋 Descripción del Proyecto

Este proyecto corresponde a la actividad sumativa de la Semana 8. Implementa la lógica de negocio completa integrando la arquitectura por capas, operaciones **CRUD** (Crear, Leer, Actualizar y Eliminar), validaciones de interfaz y acceso seguro a datos mediante `PreparedStatement` y `ResultSet`.

### Principales Funcionalidades
- **Gestión de Repartidores:** Registro, edición, eliminación y listado en tiempo real.
- **Gestión de Pedidos:** Control de órdenes con tipos (`COMIDA`, `ENCOMIENDA`, `EXPRESS`) y estados (`PENDIENTE`, `EN_REPARTO`, `ENTREGADO`).
- **Gestión de Entregas:** Asignación relacional entre pedidos y repartidores con control de fecha y hora.
- **Sincronización Dinámica:** Actualización automática de `JComboBox` y `JTable` ante cualquier cambio en el sistema.
- **Validaciones y Seguridad:** Manejo de excepciones SQL (`try-catch`), validación de entradas obligatorias y prevención de inyecciones SQL mediante consultas parametrizadas.

---

## 🛠️ Tecnologías y Requisitos

- **Lenguaje:** Java 17 o superior
- **IDE:** IntelliJ IDEA
- **Interfaz Gráfica:** Java Swing
- **Base de Datos:** MySQL 8.x / MariaDB (XAMPP v3.3.0 o superior)
- **Conector de BD:** `mysql-connector-j` (8.x)

---

## 📁 Estructura del Proyecto

El código está estructurado bajo el patrón de separación de responsabilidades:

```text
src/
├── conexion/
│   ├── ConexionDB.java       # Manejo de conexión JDBC a MySQL
│   └── TestConexion.java     # Prueba de conectividad unitaria
├── modelo/
│   ├── Repartidor.java       # Entidad Repartidor
│   ├── Pedido.java           # Entidad Pedido
│   └── Entrega.java          # Entidad Entrega (relación)
├── dao/
│   ├── RepartidorDAO.java    # Operaciones CRUD para repartidores
│   ├── PedidoDAO.java        # Operaciones CRUD para pedidos
│   └── EntregaDAO.java       # Operaciones CRUD para entregas
└── vista/
    └── VentanaPrincipal.java # Interfaz gráfica Swing unificada
```

---

## 🗄️ Esquema de Base de Datos

El script SQL para configurar el entorno en MySQL / phpMyAdmin:

```sql
CREATE DATABASE IF NOT EXISTS speedfast_db;
USE speedfast_db;

CREATE TABLE IF NOT EXISTS repartidores (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS pedidos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    direccion VARCHAR(100) NOT NULL,
    tipo ENUM('COMIDA', 'ENCOMIENDA', 'EXPRESS') NOT NULL,
    estado ENUM('PENDIENTE', 'EN_REPARTO', 'ENTREGADO') NOT NULL
);

CREATE TABLE IF NOT EXISTS entregas (
    id INT AUTO_INCREMENT PRIMARY KEY,
    id_pedido INT NOT NULL,
    id_repartidor INT NOT NULL,
    fecha DATE NOT NULL,
    hora TIME NOT NULL,
    FOREIGN KEY (id_pedido) REFERENCES pedidos(id) ON DELETE CASCADE,
    FOREIGN KEY (id_repartidor) REFERENCES repartidores(id) ON DELETE CASCADE
);
```

---

## 🚀 Instrucciones de Instalación y Ejecución

1. **Configuración de Base de Datos:**
    - Inicia el servicio **MySQL** desde el panel de control de **XAMPP**.
    - Ingresa a phpMyAdmin (`http://localhost/phpmyadmin`).
    - Crea la base de datos `speedfast_db` y ejecuta el script SQL provisto arriba.

2. **Configuración de Credenciales en Java:**
    - Abre el archivo `src/conexion/ConexionDB.java`.
    - Verifica los parámetros de conexión por defecto de XAMPP:
        - `HOST`: `localhost`
        - `PUERTO`: `3306`
        - `USER`: `root`
        - `PASSWORD`: `""` (vacía por defecto)

3. **Vincular Conector MySQL en IntelliJ IDEA:**
    - Ve a **File** > **Project Structure...** > **Libraries**.
    - Haz clic en `+` > **Java** y selecciona el archivo `mysql-connector-j-8.x.x.jar`.
    - Aplica y guarda los cambios.

4. **Ejecución:**
    - Abre la clase `src/vista/VentanaPrincipal.java`.
    - Haz clic derecho y selecciona **Run 'VentanaPrincipal.main()'**.

---

## 👥 MARIA VICTORIA PONCE
- **Proyecto:** Actividad Sumativa - Ciclo Funcional CRUD JDBC
- **Entorno:** IntelliJ IDEA