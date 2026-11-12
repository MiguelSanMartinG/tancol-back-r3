CREATE TABLE almacen
(
    id         UUID    NOT NULL,
    enabled    BOOLEAN NOT NULL,
    deleted    BOOLEAN NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE,
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    nombre     VARCHAR(255),
    CONSTRAINT pk_almacen PRIMARY KEY (id)
);

CREATE TABLE categoria_producto
(
    id         UUID    NOT NULL,
    enabled    BOOLEAN NOT NULL,
    deleted    BOOLEAN NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE,
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    nombre     VARCHAR(255),
    CONSTRAINT pk_categoria_producto PRIMARY KEY (id)
);

CREATE TABLE departamento
(
    id         UUID         NOT NULL,
    nombre     VARCHAR(255) NOT NULL,
    activo     BOOLEAN,
    created_at TIMESTAMP WITHOUT TIME ZONE,
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_departamento PRIMARY KEY (id)
);

CREATE TABLE orden_entity
(
    id                     UUID    NOT NULL,
    enabled                BOOLEAN NOT NULL,
    deleted                BOOLEAN NOT NULL,
    created_at             TIMESTAMP WITHOUT TIME ZONE,
    updated_at             TIMESTAMP WITHOUT TIME ZONE,
    folio                  VARCHAR(255),
    titulo                 VARCHAR(255),
    proveedor_id           UUID,
    fecha_solicitud        TIMESTAMP WITHOUT TIME ZONE,
    fecha_facturacion      TIMESTAMP WITHOUT TIME ZONE,
    prioridad              VARCHAR(255),
    solicitante_estado     VARCHAR(255),
    post_autorizado_estado VARCHAR(255),
    folio_externo          VARCHAR(255),
    comentarios            VARCHAR(255),
    total_orden            DECIMAL,
    recepcion              BOOLEAN,
    CONSTRAINT pk_ordenentity PRIMARY KEY (id)
);

CREATE TABLE partida_orden_entity
(
    id                         UUID    NOT NULL,
    cantidad                   INTEGER NOT NULL,
    precio                     DECIMAL(21, 8),
    producto_id                UUID,
    solicitud_compra_entity_id UUID,
    iva                        DECIMAL(21, 8),
    CONSTRAINT pk_partidaordenentity PRIMARY KEY (id)
);

CREATE TABLE productos
(
    id                    UUID         NOT NULL,
    enabled               BOOLEAN      NOT NULL,
    deleted               BOOLEAN      NOT NULL,
    created_at            TIMESTAMP WITHOUT TIME ZONE,
    updated_at            TIMESTAMP WITHOUT TIME ZONE,
    nombre                VARCHAR(255) NOT NULL,
    descripcion           VARCHAR(255),
    sku                   VARCHAR(255),
    alias                 VARCHAR(255),
    categoria_producto_id UUID,
    CONSTRAINT pk_productos PRIMARY KEY (id)
);

CREATE TABLE productos_proveedores_entity
(
    id           UUID NOT NULL,
    producto_id  UUID,
    proveedor_id UUID,
    precio       DOUBLE PRECISION,
    CONSTRAINT pk_productosproveedoresentity PRIMARY KEY (id)
);

CREATE TABLE proveedores
(
    id           UUID         NOT NULL,
    enabled      BOOLEAN      NOT NULL,
    deleted      BOOLEAN      NOT NULL,
    created_at   TIMESTAMP WITHOUT TIME ZONE,
    updated_at   TIMESTAMP WITHOUT TIME ZONE,
    nombre       VARCHAR(255) NOT NULL,
    razon_social VARCHAR(255),
    CONSTRAINT pk_proveedores PRIMARY KEY (id)
);

CREATE TABLE roles
(
    id          UUID         NOT NULL,
    name        VARCHAR(255) NOT NULL,
    description VARCHAR(255) NOT NULL,
    created_at  TIMESTAMP WITHOUT TIME ZONE,
    updated_at  TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_roles PRIMARY KEY (id)
);

CREATE TABLE tipo_impuesto
(
    id         UUID    NOT NULL,
    enabled    BOOLEAN NOT NULL,
    deleted    BOOLEAN NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE,
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    nombre     VARCHAR(255),
    porcentaje BIGINT,
    CONSTRAINT pk_tipoimpuesto PRIMARY KEY (id)
);

CREATE TABLE uploaded_image
(
    id        UUID NOT NULL,
    extension VARCHAR(255),
    version   INTEGER,
    CONSTRAINT pk_uploadedimage PRIMARY KEY (id)
);

CREATE TABLE users
(
    id         UUID         NOT NULL,
    full_name  VARCHAR(255) NOT NULL,
    email      VARCHAR(100) NOT NULL,
    password   VARCHAR(255) NOT NULL,
    enabled    BOOLEAN,
    created_at TIMESTAMP WITHOUT TIME ZONE,
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    role_id    UUID         NOT NULL,
    CONSTRAINT pk_users PRIMARY KEY (id)
);

ALTER TABLE productos
    ADD CONSTRAINT uc_productos_nombre UNIQUE (nombre);

ALTER TABLE roles
    ADD CONSTRAINT uc_roles_name UNIQUE (name);

ALTER TABLE users
    ADD CONSTRAINT uc_users_email UNIQUE (email);

ALTER TABLE orden_entity
    ADD CONSTRAINT FK_ORDENENTITY_ON_PROVEEDOR FOREIGN KEY (proveedor_id) REFERENCES proveedores (id);

ALTER TABLE partida_orden_entity
    ADD CONSTRAINT FK_PARTIDAORDENENTITY_ON_PRODUCTO FOREIGN KEY (producto_id) REFERENCES productos (id);

ALTER TABLE partida_orden_entity
    ADD CONSTRAINT FK_PARTIDAORDENENTITY_ON_SOLICITUD_COMPRA_ENTITY FOREIGN KEY (solicitud_compra_entity_id) REFERENCES orden_entity (id);

ALTER TABLE productos_proveedores_entity
    ADD CONSTRAINT FK_PRODUCTOSPROVEEDORESENTITY_ON_PRODUCTO FOREIGN KEY (producto_id) REFERENCES productos (id);

ALTER TABLE productos_proveedores_entity
    ADD CONSTRAINT FK_PRODUCTOSPROVEEDORESENTITY_ON_PROVEEDOR FOREIGN KEY (proveedor_id) REFERENCES proveedores (id);

ALTER TABLE productos
    ADD CONSTRAINT FK_PRODUCTOS_ON_CATEGORIA_PRODUCTO FOREIGN KEY (categoria_producto_id) REFERENCES categoria_producto (id);

ALTER TABLE users
    ADD CONSTRAINT FK_USERS_ON_ROLE FOREIGN KEY (role_id) REFERENCES roles (id);