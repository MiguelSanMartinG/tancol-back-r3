-- Datos de RH del empleado: personales, domicilio actual, contactos de emergencia (hasta 3) y documentos.
-- Todo es opcional; idempotente para poder correr sobre bases que ya tengan alguna columna.

ALTER TABLE empleados
    ADD COLUMN IF NOT EXISTS nacionalidad              VARCHAR(20),
    ADD COLUMN IF NOT EXISTS sexo                      VARCHAR(20),
    ADD COLUMN IF NOT EXISTS estado_civil              VARCHAR(20),
    ADD COLUMN IF NOT EXISTS tipo_sangre               VARCHAR(5),
    ADD COLUMN IF NOT EXISTS dom_codigo_postal         VARCHAR(10),
    ADD COLUMN IF NOT EXISTS dom_tipo_vialidad         VARCHAR(100),
    ADD COLUMN IF NOT EXISTS dom_nombre_vialidad       VARCHAR(255),
    ADD COLUMN IF NOT EXISTS dom_numero_exterior       VARCHAR(50),
    ADD COLUMN IF NOT EXISTS dom_numero_interior       VARCHAR(50),
    ADD COLUMN IF NOT EXISTS dom_colonia               VARCHAR(255),
    ADD COLUMN IF NOT EXISTS dom_localidad             VARCHAR(255),
    ADD COLUMN IF NOT EXISTS dom_municipio             VARCHAR(255),
    ADD COLUMN IF NOT EXISTS dom_entidad_federativa    VARCHAR(255),
    ADD COLUMN IF NOT EXISTS dom_calle1                VARCHAR(255),
    ADD COLUMN IF NOT EXISTS dom_calle2                VARCHAR(255);

CREATE TABLE IF NOT EXISTS empleado_contactos_emergencia
(
    id          UUID         NOT NULL,
    created_at  TIMESTAMP WITHOUT TIME ZONE,
    updated_at  TIMESTAMP WITHOUT TIME ZONE,
    enabled     BOOLEAN      NOT NULL DEFAULT true,
    deleted     BOOLEAN      NOT NULL DEFAULT false,
    empleado_id UUID         NOT NULL,
    orden       INTEGER      NOT NULL,
    nombre      VARCHAR(255) NOT NULL,
    parentesco  VARCHAR(100),
    telefono    VARCHAR(30)  NOT NULL,
    correo      VARCHAR(255),
    CONSTRAINT pk_empleado_contactos_emergencia PRIMARY KEY (id),
    CONSTRAINT fk_empleado_contacto_empleado FOREIGN KEY (empleado_id) REFERENCES empleados (id)
);

CREATE INDEX IF NOT EXISTS idx_empleado_contactos_empleado ON empleado_contactos_emergencia (empleado_id);

CREATE TABLE IF NOT EXISTS empleado_documentos
(
    id             UUID         NOT NULL,
    created_at     TIMESTAMP WITHOUT TIME ZONE,
    updated_at     TIMESTAMP WITHOUT TIME ZONE,
    enabled        BOOLEAN      NOT NULL DEFAULT true,
    deleted        BOOLEAN      NOT NULL DEFAULT false,
    empleado_id    UUID         NOT NULL,
    tipo           VARCHAR(40)  NOT NULL,
    archivo        VARCHAR(300) NOT NULL,
    fecha          DATE,
    fecha_vigencia DATE,
    mes            VARCHAR(7),
    nivel_estudios VARCHAR(20),
    banco          VARCHAR(100),
    clabe          VARCHAR(18),
    CONSTRAINT pk_empleado_documentos PRIMARY KEY (id),
    CONSTRAINT fk_empleado_documento_empleado FOREIGN KEY (empleado_id) REFERENCES empleados (id)
);

CREATE INDEX IF NOT EXISTS idx_empleado_documentos_empleado ON empleado_documentos (empleado_id);
