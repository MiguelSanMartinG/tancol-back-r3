CREATE TABLE precio_entity
(
    id           UUID NOT NULL,
    created_at   TIMESTAMP WITHOUT TIME ZONE,
    deleted      BOOLEAN  NOT NULL,
    enabled      BOOLEAN  NOT NULL,
    updated_at   TIMESTAMP WITHOUT TIME ZONE,
    precio       numeric(38, 2),
    producto_id  UUID,
    proveedor_id UUID,
    CONSTRAINT precio_entity_pkey PRIMARY KEY (id)
);

ALTER TABLE precio_entity
    ADD CONSTRAINT fknn4rkmna3sgec08w850fc8dhu FOREIGN KEY (producto_id) REFERENCES productos (id) ON DELETE NO ACTION;

ALTER TABLE precio_entity
    ADD CONSTRAINT fktr5a6fgag8cksa0ecae9nftso FOREIGN KEY (proveedor_id) REFERENCES proveedores (id) ON DELETE NO ACTION;