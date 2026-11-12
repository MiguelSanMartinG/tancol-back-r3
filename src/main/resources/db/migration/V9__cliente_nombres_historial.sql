CREATE TABLE cliente_nombres_historial
(
    id         UUID                        NOT NULL,
    cliente_id UUID                        NOT NULL,
    tipo       VARCHAR(50)                 NOT NULL,
    valor      VARCHAR(255)                NOT NULL,
    usuario    VARCHAR(255)                NOT NULL,
    fecha      TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT pk_cliente_nombres_historial PRIMARY KEY (id),
    CONSTRAINT fk_cliente_nombres_historial_cliente FOREIGN KEY (cliente_id) REFERENCES clientes (id)
);

CREATE INDEX idx_cliente_nombres_historial_cliente_tipo_fecha
    ON cliente_nombres_historial (cliente_id, tipo, fecha DESC);
