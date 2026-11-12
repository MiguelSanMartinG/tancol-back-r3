CREATE SEQUENCE IF NOT EXISTS producto_rezagado_seq START WITH 1 INCREMENT BY 50;

CREATE SEQUENCE IF NOT EXISTS roles_seq START WITH 1 INCREMENT BY 50;

CREATE TABLE almacenes
(
    id         UUID    NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE,
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    enabled    BOOLEAN NOT NULL,
    deleted    BOOLEAN NOT NULL,
    nombre     VARCHAR(255),
    CONSTRAINT pk_almacenes PRIMARY KEY (id)
);

CREATE TABLE bitacora_rutas_ventas
(
    id_bitacora            UUID NOT NULL,
    folio                  VARCHAR(255),
    estatus_bitacora_venta VARCHAR(255),
    fecha                  date,
    created_at             TIMESTAMP WITHOUT TIME ZONE,
    updated_at             TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_bitacora_rutas_ventas PRIMARY KEY (id_bitacora)
);

CREATE TABLE bitacora_rutas_ventas_trayectos
(
    id_trayecto                            UUID NOT NULL,
    ruta_id                                UUID,
    chofer_id                              UUID,
    vehiculo_id                            UUID,
    id_imagen_firma                        VARCHAR(255),
    estado_venta_ruta                      VARCHAR(255),
    mandadoaliquidacion                    BOOLEAN,
    created_at                             TIMESTAMP WITHOUT TIME ZONE,
    updated_at                             TIMESTAMP WITHOUT TIME ZONE,
    bitacora_venta_ruta_entity_id_bitacora UUID,
    comentario                             VARCHAR(320),
    CONSTRAINT pk_bitacora_rutas_ventas_trayectos PRIMARY KEY (id_trayecto)
);

CREATE TABLE bitacora_rutas_ventas_trayectos_producto
(
    id_venta                    UUID NOT NULL,
    producto_id                 UUID,
    cantidad                    DECIMAL(19, 2),
    cantidad_salida             DECIMAL(19, 2),
    cantidad_llegada            DECIMAL(19, 2),
    trayecto_origen_id_trayecto UUID,
    huecos                      DECIMAL(19, 2),
    rotos                       DECIMAL(19, 2),
    created_at                  TIMESTAMP WITHOUT TIME ZONE,
    updated_at                  TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_bitacora_rutas_ventas_trayectos_producto PRIMARY KEY (id_venta)
);

CREATE TABLE bitacoras_medicion
(
    id                 UUID NOT NULL,
    medida_actual      DECIMAL(19, 2),
    medida_inicial     DECIMAL(19, 2),
    medidor_id_medidor UUID,
    comentario         VARCHAR(255),
    diferencia         DECIMAL,
    created_at         TIMESTAMP WITHOUT TIME ZONE,
    updated_at         TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_bitacoras_medicion PRIMARY KEY (id)
);

CREATE TABLE categoria_producto
(
    id         UUID    NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE,
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    enabled    BOOLEAN NOT NULL,
    deleted    BOOLEAN NOT NULL,
    nombre     VARCHAR(255),
    CONSTRAINT pk_categoria_producto PRIMARY KEY (id)
);

CREATE TABLE categoria_producto_comercial
(
    id         UUID    NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE,
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    enabled    BOOLEAN NOT NULL,
    deleted    BOOLEAN NOT NULL,
    nombre     VARCHAR(255),
    CONSTRAINT pk_categoria_producto_comercial PRIMARY KEY (id)
);

CREATE TABLE cliente_producto
(
    id                    UUID NOT NULL,
    cliente_id            UUID,
    producto_comercial_id UUID,
    created_at            TIMESTAMP WITHOUT TIME ZONE,
    descuento             DECIMAL(19, 2),
    CONSTRAINT pk_cliente_producto PRIMARY KEY (id)
);

CREATE TABLE clientes
(
    id                        UUID    NOT NULL,
    created_at                TIMESTAMP WITHOUT TIME ZONE,
    updated_at                TIMESTAMP WITHOUT TIME ZONE,
    enabled                   BOOLEAN NOT NULL,
    deleted                   BOOLEAN NOT NULL,
    nombre_comercial          VARCHAR(255),
    id_cliente                INTEGER,
    tipos_cliente_id          UUID,
    correo                    VARCHAR(255),
    telefono                  VARCHAR(255),
    fecha_alta                TIMESTAMP WITHOUT TIME ZONE,
    tipo_pago                 VARCHAR(255),
    dias_de_credito           BIGINT,
    ruta_asociada_id          UUID,
    comentario                VARCHAR(800),
    tipo_contribuyente_fiscal SMALLINT,
    razon_social_fiscal       VARCHAR(255),
    rfc                       VARCHAR(255),
    codigo_postal             VARCHAR(255),
    tipo_vialidad             VARCHAR(255),
    nombre_vialidad           VARCHAR(255),
    numero_exterior           VARCHAR(255),
    numero_interior           VARCHAR(255),
    colonia                   VARCHAR(255),
    localidad                 VARCHAR(255),
    municipio                 VARCHAR(255),
    entidad_federativa        VARCHAR(255),
    calle1                    VARCHAR(255),
    calle2                    VARCHAR(255),
    CONSTRAINT pk_clientes PRIMARY KEY (id)
);

CREATE TABLE comisiones_empleado_ruta
(
    id_comision              UUID NOT NULL,
    empleado_id              UUID,
    comision                 DECIMAL(19, 2),
    trayecto_origen_id_venta UUID,
    CONSTRAINT pk_comisiones_empleado_ruta PRIMARY KEY (id_comision)
);

CREATE TABLE departamentos
(
    id          UUID    NOT NULL,
    created_at  TIMESTAMP WITHOUT TIME ZONE,
    updated_at  TIMESTAMP WITHOUT TIME ZONE,
    enabled     BOOLEAN NOT NULL,
    deleted     BOOLEAN NOT NULL,
    nombre      VARCHAR(255),
    alias       VARCHAR(255),
    descripcion VARCHAR(255),
    CONSTRAINT pk_departamentos PRIMARY KEY (id)
);

CREATE TABLE empleados
(
    id                      UUID    NOT NULL,
    created_at              TIMESTAMP WITHOUT TIME ZONE,
    updated_at              TIMESTAMP WITHOUT TIME ZONE,
    enabled                 BOOLEAN NOT NULL,
    deleted                 BOOLEAN NOT NULL,
    id_empleado             BIGINT,
    user_name               VARCHAR(255),
    role_id                 BIGINT,
    nombre                  VARCHAR(255),
    primer_apellido         VARCHAR(255),
    segundo_apellido        VARCHAR(255),
    tipos_empleados_list_id UUID,
    correo                  VARCHAR(255),
    telefono                VARCHAR(255),
    fecha_nacimiento        TIMESTAMP WITHOUT TIME ZONE,
    contra                  VARCHAR(255),
    id_departamento_id      UUID,
    CONSTRAINT pk_empleados PRIMARY KEY (id)
);

CREATE TABLE inventarios
(
    id                        UUID    NOT NULL,
    created_at                TIMESTAMP WITHOUT TIME ZONE,
    updated_at                TIMESTAMP WITHOUT TIME ZONE,
    enabled                   BOOLEAN NOT NULL,
    deleted                   BOOLEAN NOT NULL,
    cantidad                  DECIMAL,
    almacen_id                UUID,
    producto_basico_entity_id UUID,
    unidades_medida_entity_id UUID,
    CONSTRAINT pk_inventarios PRIMARY KEY (id)
);

CREATE TABLE liquidacion
(
    id_liquidacion      UUID    NOT NULL,
    version             INTEGER,
    folio               VARCHAR(255),
    ruta_id             UUID,
    fecha               TIMESTAMP WITHOUT TIME ZONE,
    bloque_nota         VARCHAR(255),
    observaciones       VARCHAR(500),
    estados_liquidacion VARCHAR(255),
    is_from_bitacora    BOOLEAN,
    total_liquidado     DECIMAL(19, 2),
    created_at          TIMESTAMP WITHOUT TIME ZONE,
    updated_at          TIMESTAMP WITHOUT TIME ZONE,
    deleted             BOOLEAN NOT NULL,
    CONSTRAINT pk_liquidacion PRIMARY KEY (id_liquidacion)
);

CREATE TABLE liquidacion_conceptos_venta
(
    id         UUID    NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE,
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    enabled    BOOLEAN NOT NULL,
    deleted    BOOLEAN NOT NULL,
    concepto   VARCHAR(255),
    is_exclude BOOLEAN,
    descuento  BOOLEAN,
    name       VARCHAR(255),
    x          BOOLEAN NOT NULL,
    cancelado  BOOLEAN NOT NULL,
    CONSTRAINT pk_liquidacion_conceptos_venta PRIMARY KEY (id)
);

CREATE TABLE liquidacion_partidas
(
    id_partida                        UUID NOT NULL,
    folio                             BIGINT,
    conceptos_ventas_id               UUID,
    cliente_id                        UUID,
    producto_comercial_id             UUID,
    cantidad                          DECIMAL(19, 2),
    precio                            DECIMAL(19, 2),
    descuento                         DECIMAL(19, 2),
    importe                           DECIMAL(19, 2),
    importe_descontado                DECIMAL(19, 2),
    factura                           BOOLEAN,
    forma_pago                        VARCHAR(255),
    liquidacion_origen_id_liquidacion UUID,
    CONSTRAINT pk_liquidacion_partidas PRIMARY KEY (id_partida)
);

CREATE TABLE liquidacion_totalizado
(
    id                                UUID NOT NULL,
    producto_comercial_id             UUID,
    origen_liquidacion_id_liquidacion UUID,
    CONSTRAINT pk_liquidacion_totalizado PRIMARY KEY (id)
);

CREATE TABLE liquidacion_totalizado_empleado
(
    id                   UUID NOT NULL,
    empleado_id          UUID,
    cantidad             DECIMAL,
    fue_chofer           BOOLEAN,
    chofer_origen_id     UUID,
    origen_totalizado_id UUID,
    CONSTRAINT pk_liquidacion_totalizado_empleado PRIMARY KEY (id)
);

CREATE TABLE medidores
(
    id_medidor                         UUID    NOT NULL,
    created_at                         TIMESTAMP WITHOUT TIME ZONE,
    updated_at                         TIMESTAMP WITHOUT TIME ZONE,
    enabled                            BOOLEAN NOT NULL,
    deleted                            BOOLEAN NOT NULL,
    tipo_agua                          VARCHAR(255),
    medicion_actual                    DECIMAL(19, 2),
    sistema_de_almacenamiento_id       UUID,
    unidades_medida_id_unidades_medida UUID,
    nombre                             VARCHAR(255),
    alias                              VARCHAR(255),
    CONSTRAINT pk_medidores PRIMARY KEY (id_medidor)
);

CREATE TABLE modulos
(
    id          UUID    NOT NULL,
    created_at  TIMESTAMP WITHOUT TIME ZONE,
    updated_at  TIMESTAMP WITHOUT TIME ZONE,
    enabled     BOOLEAN NOT NULL,
    deleted     BOOLEAN NOT NULL,
    name        VARCHAR(255),
    ruta_modulo VARCHAR(255),
    parent_id   UUID,
    CONSTRAINT pk_modulos PRIMARY KEY (id)
);

CREATE TABLE ordenes
(
    id                  UUID    NOT NULL,
    created_at          TIMESTAMP WITHOUT TIME ZONE,
    updated_at          TIMESTAMP WITHOUT TIME ZONE,
    enabled             BOOLEAN NOT NULL,
    deleted             BOOLEAN NOT NULL,
    folio               VARCHAR(255),
    titulo              VARCHAR(255),
    proveedor_id        UUID,
    fecha_solicitud     TIMESTAMP WITHOUT TIME ZONE,
    fecha_facturacion   TIMESTAMP WITHOUT TIME ZONE,
    fecha_recibo        TIMESTAMP WITHOUT TIME ZONE,
    prioridad           VARCHAR(255),
    solicitante_estado  VARCHAR(255),
    facturacion_estado  VARCHAR(255),
    recepcion_estado    VARCHAR(255),
    folio_externo       VARCHAR(255),
    comentario_orden    VARCHAR(255),
    comentario_recibo   VARCHAR(255),
    total_orden         DECIMAL,
    almacen_id          UUID,
    usuario_provisional VARCHAR(255),
    CONSTRAINT pk_ordenes PRIMARY KEY (id)
);

CREATE TABLE ordenes_internas_history
(
    id_orden_interna    UUID    NOT NULL,
    created_at          TIMESTAMP WITHOUT TIME ZONE,
    updated_at          TIMESTAMP WITHOUT TIME ZONE,
    enabled             BOOLEAN NOT NULL,
    deleted             BOOLEAN NOT NULL,
    folio               VARCHAR(255),
    empleado_id         UUID,
    empleado_id_recibe  UUID,
    fecha_elaboracion   TIMESTAMP WITHOUT TIME ZONE,
    descripcion         VARCHAR(255),
    uso                 VARCHAR(255),
    estado_ordeninterna VARCHAR(255),
    CONSTRAINT pk_ordenes_internas_history PRIMARY KEY (id_orden_interna)
);

CREATE TABLE partida_orden_interna
(
    id_partida         UUID NOT NULL,
    orden_interna_id   UUID,
    almacen_id         UUID,
    producto_basico_id UUID,
    cantidad           DECIMAL(19, 2),
    unidades_medida_id UUID,
    comentario         VARCHAR(255),
    CONSTRAINT pk_partida_orden_interna PRIMARY KEY (id_partida)
);

CREATE TABLE partidas_orden
(
    id                                 UUID    NOT NULL,
    cantidad                           INTEGER NOT NULL,
    precio                             DECIMAL(21, 8),
    iva                                DECIMAL(21, 8),
    descuento                          DECIMAL,
    producto_id                        UUID,
    solicitud_compra_entity_id         UUID,
    unidades_recibidas                 INTEGER,
    comentarios_partida                VARCHAR(255),
    verificado                         BOOLEAN,
    almacen                            UUID,
    tipo_impuesto                      UUID,
    precio_proveedor_entity_id         UUID,
    proveedor_id                       UUID,
    unidades_medida_id_unidades_medida UUID,
    CONSTRAINT pk_partidas_orden PRIMARY KEY (id)
);

CREATE TABLE partidas_transferencia_material
(
    id_partida                UUID    NOT NULL,
    created_at                TIMESTAMP WITHOUT TIME ZONE,
    updated_at                TIMESTAMP WITHOUT TIME ZONE,
    enabled                   BOOLEAN NOT NULL,
    deleted                   BOOLEAN NOT NULL,
    comentarios               VARCHAR(255),
    almacen_origen_id         UUID,
    almacen_destino_id        UUID,
    cantidades                BIGINT,
    producto_basico_entity_id UUID,
    motivo                    VARCHAR(255),
    empleados_entity_id       UUID,
    id_transferencia_padre    UUID,
    unidad_medida_id          UUID,
    CONSTRAINT pk_partidas_transferencia_material PRIMARY KEY (id_partida)
);

CREATE TABLE perfiles
(
    id_perfil  UUID    NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE,
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    enabled    BOOLEAN NOT NULL,
    deleted    BOOLEAN NOT NULL,
    module_id  UUID,
    role_id    BIGINT,
    can_read   BOOLEAN NOT NULL,
    can_write  BOOLEAN NOT NULL,
    can_delete BOOLEAN NOT NULL,
    can_update BOOLEAN NOT NULL,
    CONSTRAINT pk_perfiles PRIMARY KEY (id_perfil)
);

CREATE TABLE presentaciones_producto_basico
(
    id           UUID NOT NULL,
    producto_id  UUID,
    proveedor_id UUID,
    precio       DECIMAL(21, 8),
    created_at   TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_presentaciones_producto_basico PRIMARY KEY (id)
);

CREATE TABLE producto_rezagado
(
    id          BIGINT  NOT NULL,
    created_at  TIMESTAMP WITHOUT TIME ZONE,
    updated_at  TIMESTAMP WITHOUT TIME ZONE,
    enabled     BOOLEAN NOT NULL,
    deleted     BOOLEAN NOT NULL,
    tipo_rezago SMALLINT,
    motivo      VARCHAR(400),
    CONSTRAINT pk_producto_rezagado PRIMARY KEY (id)
);

CREATE TABLE productos_basicos
(
    id                                       UUID         NOT NULL,
    created_at                               TIMESTAMP WITHOUT TIME ZONE,
    updated_at                               TIMESTAMP WITHOUT TIME ZONE,
    enabled                                  BOOLEAN      NOT NULL,
    deleted                                  BOOLEAN      NOT NULL,
    nombre                                   VARCHAR(255) NOT NULL,
    id_imagen                                VARCHAR(255),
    descripcion                              VARCHAR(510),
    sku                                      VARCHAR(255),
    alias                                    VARCHAR(255),
    categoria_producto_id                    UUID,
    unidad_medida_default_id_unidades_medida UUID,
    CONSTRAINT pk_productos_basicos PRIMARY KEY (id)
);

CREATE TABLE productos_comerciales
(
    id                                       UUID         NOT NULL,
    created_at                               TIMESTAMP WITHOUT TIME ZONE,
    updated_at                               TIMESTAMP WITHOUT TIME ZONE,
    enabled                                  BOOLEAN      NOT NULL,
    deleted                                  BOOLEAN      NOT NULL,
    nombre                                   VARCHAR(255) NOT NULL,
    id_imagen                                VARCHAR(255),
    descripcion                              VARCHAR(510),
    sku                                      VARCHAR(255),
    alias                                    VARCHAR(255),
    precio                                   DECIMAL(19, 2),
    categoria_comercial_id                   UUID,
    unidad_medida_default_id_unidades_medida UUID,
    CONSTRAINT pk_productos_comerciales PRIMARY KEY (id)
);

CREATE TABLE productos_fuera_especificacion
(
    idpfe                          UUID NOT NULL,
    concepto_id                    BIGINT,
    cantidad                       DECIMAL(19, 2),
    venta_trayecto_entity_id_venta UUID,
    created_at                     TIMESTAMP WITHOUT TIME ZONE,
    updated_at                     TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_productos_fuera_especificacion PRIMARY KEY (idpfe)
);

CREATE TABLE proveedores
(
    id                        UUID         NOT NULL,
    created_at                TIMESTAMP WITHOUT TIME ZONE,
    updated_at                TIMESTAMP WITHOUT TIME ZONE,
    enabled                   BOOLEAN      NOT NULL,
    deleted                   BOOLEAN      NOT NULL,
    nombre_comercial          VARCHAR(255) NOT NULL,
    tipo_proveedor_entity_id  UUID,
    tipo_pago                 VARCHAR(255),
    dias_de_credito           BIGINT,
    correo                    VARCHAR(255),
    telefono                  VARCHAR(255),
    tipo_contribuyente_fiscal SMALLINT,
    razon_social_fiscal       VARCHAR(255),
    rfc                       VARCHAR(255),
    codigo_postal             VARCHAR(255),
    tipo_vialidad             VARCHAR(255),
    nombre_vialidad           VARCHAR(255),
    numero_exterior           VARCHAR(255),
    numero_interior           VARCHAR(255),
    colonia                   VARCHAR(255),
    localidad                 VARCHAR(255),
    municipio                 VARCHAR(255),
    entidad_federativa        VARCHAR(255),
    calle1                    VARCHAR(255),
    calle2                    VARCHAR(255),
    CONSTRAINT pk_proveedores PRIMARY KEY (id)
);

CREATE TABLE roles
(
    id         BIGINT  NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE,
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    enabled    BOOLEAN NOT NULL,
    deleted    BOOLEAN NOT NULL,
    name       VARCHAR(255),
    CONSTRAINT pk_roles PRIMARY KEY (id)
);

CREATE TABLE rutas
(
    id                   UUID    NOT NULL,
    created_at           TIMESTAMP WITHOUT TIME ZONE,
    updated_at           TIMESTAMP WITHOUT TIME ZONE,
    enabled              BOOLEAN NOT NULL,
    deleted              BOOLEAN NOT NULL,
    numero               VARCHAR(255),
    nombre               VARCHAR(255),
    chofer_base          UUID,
    producto_base        UUID,
    vehiculos_id         UUID,
    letra_predeterminada VARCHAR(255),
    CONSTRAINT pk_rutas PRIMARY KEY (id)
);

CREATE TABLE rutas_empleados
(
    empleados_id UUID NOT NULL,
    rutas_id     UUID NOT NULL,
    CONSTRAINT pk_rutas_empleados PRIMARY KEY (empleados_id, rutas_id)
);

CREATE TABLE sistemas_almacenamiento
(
    id              UUID    NOT NULL,
    created_at      TIMESTAMP WITHOUT TIME ZONE,
    updated_at      TIMESTAMP WITHOUT TIME ZONE,
    enabled         BOOLEAN NOT NULL,
    deleted         BOOLEAN NOT NULL,
    nombre          VARCHAR(255),
    alias           VARCHAR(255),
    departamento_id UUID,
    capacidad       DECIMAL(19, 2),
    CONSTRAINT pk_sistemas_almacenamiento PRIMARY KEY (id)
);

CREATE TABLE solicitudes_compra
(
    id                      UUID    NOT NULL,
    created_at              TIMESTAMP WITHOUT TIME ZONE,
    updated_at              TIMESTAMP WITHOUT TIME ZONE,
    deleted                 BOOLEAN NOT NULL,
    prioridad               VARCHAR(255),
    empleado_solicitante_id UUID,
    uso                     VARCHAR(255),
    estado_solicitud        VARCHAR(255),
    comentario              VARCHAR(510),
    CONSTRAINT pk_solicitudes_compra PRIMARY KEY (id)
);

CREATE TABLE solicitudes_compra_partidas
(
    id                                  UUID NOT NULL,
    padre_id                            UUID,
    cantidad                            DECIMAL(19, 2),
    producto_insumo_id                  UUID,
    unidad_de_medida_id_unidades_medida UUID,
    CONSTRAINT pk_solicitudes_compra_partidas PRIMARY KEY (id)
);

CREATE TABLE tipo_impuesto
(
    id         UUID    NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE,
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    enabled    BOOLEAN NOT NULL,
    deleted    BOOLEAN NOT NULL,
    nombre     VARCHAR(255),
    porcentaje BIGINT,
    CONSTRAINT pk_tipo_impuesto PRIMARY KEY (id)
);

CREATE TABLE tipos_clientes
(
    id         UUID    NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE,
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    enabled    BOOLEAN NOT NULL,
    deleted    BOOLEAN NOT NULL,
    nombre     VARCHAR(255),
    alias      VARCHAR(255),
    CONSTRAINT pk_tipos_clientes PRIMARY KEY (id)
);

CREATE TABLE tipos_empleado
(
    id         UUID    NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE,
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    enabled    BOOLEAN NOT NULL,
    deleted    BOOLEAN NOT NULL,
    nombre     VARCHAR(255),
    alias      VARCHAR(255),
    CONSTRAINT pk_tipos_empleado PRIMARY KEY (id)
);

CREATE TABLE tipos_proveedores
(
    id         UUID    NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE,
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    enabled    BOOLEAN NOT NULL,
    deleted    BOOLEAN NOT NULL,
    nombre     VARCHAR(255),
    alias      VARCHAR(255),
    CONSTRAINT pk_tipos_proveedores PRIMARY KEY (id)
);

CREATE TABLE transferencias_material
(
    id_transferencia    UUID    NOT NULL,
    created_at          TIMESTAMP WITHOUT TIME ZONE,
    updated_at          TIMESTAMP WITHOUT TIME ZONE,
    enabled             BOOLEAN NOT NULL,
    deleted             BOOLEAN NOT NULL,
    id_empleado         UUID,
    motivo              VARCHAR(255),
    empleados_id        UUID,
    fecha_transferencia TIMESTAMP WITHOUT TIME ZONE,
    comentarios         VARCHAR(510),
    CONSTRAINT pk_transferencias_material PRIMARY KEY (id_transferencia)
);

CREATE TABLE unidades_de_medida
(
    id_unidades_medida UUID    NOT NULL,
    created_at         TIMESTAMP WITHOUT TIME ZONE,
    updated_at         TIMESTAMP WITHOUT TIME ZONE,
    enabled            BOOLEAN NOT NULL,
    deleted            BOOLEAN NOT NULL,
    nombre             VARCHAR(255),
    alias              VARCHAR(255),
    CONSTRAINT pk_unidades_de_medida PRIMARY KEY (id_unidades_medida)
);

CREATE TABLE uploaded_file
(
    id        UUID NOT NULL,
    extension VARCHAR(255),
    version   INTEGER,
    CONSTRAINT pk_uploadedfile PRIMARY KEY (id)
);

CREATE TABLE uploaded_image
(
    id        UUID NOT NULL,
    extension VARCHAR(255),
    version   INTEGER,
    CONSTRAINT pk_uploadedimage PRIMARY KEY (id)
);

CREATE TABLE vehiculos
(
    id               UUID    NOT NULL,
    created_at       TIMESTAMP WITHOUT TIME ZONE,
    updated_at       TIMESTAMP WITHOUT TIME ZONE,
    enabled          BOOLEAN NOT NULL,
    deleted          BOOLEAN NOT NULL,
    nombre           VARCHAR(255),
    capacidad_maxima INTEGER,
    unidad_medida_id UUID,
    placas           VARCHAR(255),
    estatus_vehiculo VARCHAR(255),
    numero_polisa    VARCHAR(255),
    vigencia_poliza  TIMESTAMP WITHOUT TIME ZONE,
    documento_poliza VARCHAR(255),
    descripcion      VARCHAR(510),
    CONSTRAINT pk_vehiculos PRIMARY KEY (id)
);

ALTER TABLE empleados
    ADD CONSTRAINT uc_empleados_user_name UNIQUE (user_name);

ALTER TABLE productos_basicos
    ADD CONSTRAINT uc_productos_basicos_nombre UNIQUE (nombre);

ALTER TABLE productos_comerciales
    ADD CONSTRAINT uc_productos_comerciales_nombre UNIQUE (nombre);

ALTER TABLE bitacora_rutas_ventas_trayectos_producto
    ADD CONSTRAINT FK_BITACORARUTASVENTASTRAYECTOSPROD_ON_TRAYECTOORIGENIDTRAYECTO FOREIGN KEY (trayecto_origen_id_trayecto) REFERENCES bitacora_rutas_ventas_trayectos (id_trayecto);

ALTER TABLE bitacora_rutas_ventas_trayectos
    ADD CONSTRAINT FK_BITACORARUTASVENTASTRAYECTOS_ON_BITACORAVENTARUTAENTITYIDBIT FOREIGN KEY (bitacora_venta_ruta_entity_id_bitacora) REFERENCES bitacora_rutas_ventas (id_bitacora);

ALTER TABLE bitacoras_medicion
    ADD CONSTRAINT FK_BITACORAS_MEDICION_ON_MEDIDOR_ID_MEDIDOR FOREIGN KEY (medidor_id_medidor) REFERENCES medidores (id_medidor);

ALTER TABLE bitacora_rutas_ventas_trayectos
    ADD CONSTRAINT FK_BITACORA_RUTAS_VENTAS_TRAYECTOS_ON_CHOFER FOREIGN KEY (chofer_id) REFERENCES empleados (id);

ALTER TABLE bitacora_rutas_ventas_trayectos
    ADD CONSTRAINT FK_BITACORA_RUTAS_VENTAS_TRAYECTOS_ON_RUTA FOREIGN KEY (ruta_id) REFERENCES rutas (id);

ALTER TABLE bitacora_rutas_ventas_trayectos
    ADD CONSTRAINT FK_BITACORA_RUTAS_VENTAS_TRAYECTOS_ON_VEHICULO FOREIGN KEY (vehiculo_id) REFERENCES vehiculos (id);

ALTER TABLE bitacora_rutas_ventas_trayectos_producto
    ADD CONSTRAINT FK_BITACORA_RUTAS_VENTAS_TRAYECTOS_PRODUCTO_ON_PRODUCTO FOREIGN KEY (producto_id) REFERENCES productos_comerciales (id);

ALTER TABLE clientes
    ADD CONSTRAINT FK_CLIENTES_ON_RUTA_ASOCIADA FOREIGN KEY (ruta_asociada_id) REFERENCES rutas (id);

ALTER TABLE clientes
    ADD CONSTRAINT FK_CLIENTES_ON_TIPOS_CLIENTE FOREIGN KEY (tipos_cliente_id) REFERENCES tipos_clientes (id);

ALTER TABLE cliente_producto
    ADD CONSTRAINT FK_CLIENTE_PRODUCTO_ON_CLIENTE FOREIGN KEY (cliente_id) REFERENCES clientes (id);

ALTER TABLE cliente_producto
    ADD CONSTRAINT FK_CLIENTE_PRODUCTO_ON_PRODUCTO_COMERCIAL FOREIGN KEY (producto_comercial_id) REFERENCES productos_comerciales (id);

ALTER TABLE comisiones_empleado_ruta
    ADD CONSTRAINT FK_COMISIONES_EMPLEADO_RUTA_ON_EMPLEADO FOREIGN KEY (empleado_id) REFERENCES empleados (id);

ALTER TABLE comisiones_empleado_ruta
    ADD CONSTRAINT FK_COMISIONES_EMPLEADO_RUTA_ON_TRAYECTO_ORIGEN_ID_VENTA FOREIGN KEY (trayecto_origen_id_venta) REFERENCES bitacora_rutas_ventas_trayectos_producto (id_venta);

ALTER TABLE empleados
    ADD CONSTRAINT FK_EMPLEADOS_ON_ID_DEPARTAMENTO FOREIGN KEY (id_departamento_id) REFERENCES departamentos (id);

ALTER TABLE empleados
    ADD CONSTRAINT FK_EMPLEADOS_ON_ROLE FOREIGN KEY (role_id) REFERENCES roles (id);

ALTER TABLE empleados
    ADD CONSTRAINT FK_EMPLEADOS_ON_TIPOS_EMPLEADOS_LIST FOREIGN KEY (tipos_empleados_list_id) REFERENCES tipos_empleado (id);

ALTER TABLE inventarios
    ADD CONSTRAINT FK_INVENTARIOS_ON_ALMACEN FOREIGN KEY (almacen_id) REFERENCES almacenes (id);

ALTER TABLE inventarios
    ADD CONSTRAINT FK_INVENTARIOS_ON_PRODUCTO_BASICO_ENTITY FOREIGN KEY (producto_basico_entity_id) REFERENCES productos_basicos (id);

ALTER TABLE inventarios
    ADD CONSTRAINT FK_INVENTARIOS_ON_UNIDADES_MEDIDA_ENTITY FOREIGN KEY (unidades_medida_entity_id) REFERENCES unidades_de_medida (id_unidades_medida);

ALTER TABLE liquidacion
    ADD CONSTRAINT FK_LIQUIDACION_ON_RUTA FOREIGN KEY (ruta_id) REFERENCES rutas (id);

ALTER TABLE liquidacion_partidas
    ADD CONSTRAINT FK_LIQUIDACION_PARTIDAS_ON_CLIENTE FOREIGN KEY (cliente_id) REFERENCES clientes (id);

ALTER TABLE liquidacion_partidas
    ADD CONSTRAINT FK_LIQUIDACION_PARTIDAS_ON_CONCEPTOS_VENTAS FOREIGN KEY (conceptos_ventas_id) REFERENCES liquidacion_conceptos_venta (id);

ALTER TABLE liquidacion_partidas
    ADD CONSTRAINT FK_LIQUIDACION_PARTIDAS_ON_LIQUIDACION_ORIGEN_ID_LIQUIDACION FOREIGN KEY (liquidacion_origen_id_liquidacion) REFERENCES liquidacion (id_liquidacion);

ALTER TABLE liquidacion_partidas
    ADD CONSTRAINT FK_LIQUIDACION_PARTIDAS_ON_PRODUCTO_COMERCIAL FOREIGN KEY (producto_comercial_id) REFERENCES productos_comerciales (id);

ALTER TABLE liquidacion_totalizado_empleado
    ADD CONSTRAINT FK_LIQUIDACION_TOTALIZADO_EMPLEADO_ON_CHOFER_ORIGEN FOREIGN KEY (chofer_origen_id) REFERENCES liquidacion_totalizado_empleado (id);

ALTER TABLE liquidacion_totalizado_empleado
    ADD CONSTRAINT FK_LIQUIDACION_TOTALIZADO_EMPLEADO_ON_EMPLEADO FOREIGN KEY (empleado_id) REFERENCES empleados (id);

ALTER TABLE liquidacion_totalizado_empleado
    ADD CONSTRAINT FK_LIQUIDACION_TOTALIZADO_EMPLEADO_ON_ORIGEN_TOTALIZADO FOREIGN KEY (origen_totalizado_id) REFERENCES liquidacion_totalizado (id);

ALTER TABLE liquidacion_totalizado
    ADD CONSTRAINT FK_LIQUIDACION_TOTALIZADO_ON_ORIGEN_LIQUIDACION_ID_LIQUIDACION FOREIGN KEY (origen_liquidacion_id_liquidacion) REFERENCES liquidacion (id_liquidacion);

ALTER TABLE liquidacion_totalizado
    ADD CONSTRAINT FK_LIQUIDACION_TOTALIZADO_ON_PRODUCTO_COMERCIAL FOREIGN KEY (producto_comercial_id) REFERENCES productos_comerciales (id);

ALTER TABLE medidores
    ADD CONSTRAINT FK_MEDIDORES_ON_SISTEMA_DE_ALMACENAMIENTO FOREIGN KEY (sistema_de_almacenamiento_id) REFERENCES sistemas_almacenamiento (id);

ALTER TABLE medidores
    ADD CONSTRAINT FK_MEDIDORES_ON_UNIDADES_MEDIDA_ID_UNIDADES_MEDIDA FOREIGN KEY (unidades_medida_id_unidades_medida) REFERENCES unidades_de_medida (id_unidades_medida);

ALTER TABLE modulos
    ADD CONSTRAINT FK_MODULOS_ON_PARENTID FOREIGN KEY (parent_id) REFERENCES modulos (id);

ALTER TABLE ordenes_internas_history
    ADD CONSTRAINT FK_ORDENES_INTERNAS_HISTORY_ON_EMPLEADO FOREIGN KEY (empleado_id) REFERENCES empleados (id);

ALTER TABLE ordenes_internas_history
    ADD CONSTRAINT FK_ORDENES_INTERNAS_HISTORY_ON_EMPLEADO_ID_RECIBE FOREIGN KEY (empleado_id_recibe) REFERENCES empleados (id);

ALTER TABLE ordenes
    ADD CONSTRAINT FK_ORDENES_ON_ALMACEN FOREIGN KEY (almacen_id) REFERENCES almacenes (id);

ALTER TABLE ordenes
    ADD CONSTRAINT FK_ORDENES_ON_PROVEEDOR FOREIGN KEY (proveedor_id) REFERENCES proveedores (id);

ALTER TABLE partidas_orden
    ADD CONSTRAINT FK_PARTIDAS_ORDEN_ON_ALMACEN FOREIGN KEY (almacen) REFERENCES almacenes (id);

ALTER TABLE partidas_orden
    ADD CONSTRAINT FK_PARTIDAS_ORDEN_ON_PRECIO_PROVEEDOR_ENTITY FOREIGN KEY (precio_proveedor_entity_id) REFERENCES presentaciones_producto_basico (id);

ALTER TABLE partidas_orden
    ADD CONSTRAINT FK_PARTIDAS_ORDEN_ON_PRODUCTO FOREIGN KEY (producto_id) REFERENCES productos_basicos (id);

ALTER TABLE partidas_orden
    ADD CONSTRAINT FK_PARTIDAS_ORDEN_ON_PROVEEDOR FOREIGN KEY (proveedor_id) REFERENCES proveedores (id);

ALTER TABLE partidas_orden
    ADD CONSTRAINT FK_PARTIDAS_ORDEN_ON_SOLICITUD_COMPRA_ENTITY FOREIGN KEY (solicitud_compra_entity_id) REFERENCES ordenes (id);

ALTER TABLE partidas_orden
    ADD CONSTRAINT FK_PARTIDAS_ORDEN_ON_TIPO_IMPUESTO FOREIGN KEY (tipo_impuesto) REFERENCES tipo_impuesto (id);

ALTER TABLE partidas_orden
    ADD CONSTRAINT FK_PARTIDAS_ORDEN_ON_UNIDADES_MEDIDA_ID_UNIDADES_MEDIDA FOREIGN KEY (unidades_medida_id_unidades_medida) REFERENCES unidades_de_medida (id_unidades_medida);

ALTER TABLE partidas_transferencia_material
    ADD CONSTRAINT FK_PARTIDAS_TRANSFERENCIA_MATERIAL_ON_ALMACEN_DESTINO FOREIGN KEY (almacen_destino_id) REFERENCES almacenes (id);

ALTER TABLE partidas_transferencia_material
    ADD CONSTRAINT FK_PARTIDAS_TRANSFERENCIA_MATERIAL_ON_ALMACEN_ORIGEN FOREIGN KEY (almacen_origen_id) REFERENCES almacenes (id);

ALTER TABLE partidas_transferencia_material
    ADD CONSTRAINT FK_PARTIDAS_TRANSFERENCIA_MATERIAL_ON_EMPLEADOS_ENTITY FOREIGN KEY (empleados_entity_id) REFERENCES empleados (id);

ALTER TABLE partidas_transferencia_material
    ADD CONSTRAINT FK_PARTIDAS_TRANSFERENCIA_MATERIAL_ON_ID_TRANSFERENCIA_PADRE FOREIGN KEY (id_transferencia_padre) REFERENCES transferencias_material (id_transferencia);

ALTER TABLE partidas_transferencia_material
    ADD CONSTRAINT FK_PARTIDAS_TRANSFERENCIA_MATERIAL_ON_PRODUCTO_BASICO_ENTITY FOREIGN KEY (producto_basico_entity_id) REFERENCES productos_basicos (id);

ALTER TABLE partidas_transferencia_material
    ADD CONSTRAINT FK_PARTIDAS_TRANSFERENCIA_MATERIAL_ON_UNIDAD_MEDIDA FOREIGN KEY (unidad_medida_id) REFERENCES unidades_de_medida (id_unidades_medida);

ALTER TABLE partida_orden_interna
    ADD CONSTRAINT FK_PARTIDA_ORDEN_INTERNA_ON_ALMACEN FOREIGN KEY (almacen_id) REFERENCES almacenes (id);

ALTER TABLE partida_orden_interna
    ADD CONSTRAINT FK_PARTIDA_ORDEN_INTERNA_ON_ORDEN_INTERNA FOREIGN KEY (orden_interna_id) REFERENCES ordenes_internas_history (id_orden_interna);

ALTER TABLE partida_orden_interna
    ADD CONSTRAINT FK_PARTIDA_ORDEN_INTERNA_ON_PRODUCTO_BASICO FOREIGN KEY (producto_basico_id) REFERENCES productos_basicos (id);

ALTER TABLE partida_orden_interna
    ADD CONSTRAINT FK_PARTIDA_ORDEN_INTERNA_ON_UNIDADES_MEDIDA FOREIGN KEY (unidades_medida_id) REFERENCES unidades_de_medida (id_unidades_medida);

ALTER TABLE perfiles
    ADD CONSTRAINT FK_PERFILES_ON_MODULE FOREIGN KEY (module_id) REFERENCES modulos (id);

ALTER TABLE perfiles
    ADD CONSTRAINT FK_PERFILES_ON_ROLE FOREIGN KEY (role_id) REFERENCES roles (id);

ALTER TABLE presentaciones_producto_basico
    ADD CONSTRAINT FK_PRESENTACIONES_PRODUCTO_BASICO_ON_PRODUCTO FOREIGN KEY (producto_id) REFERENCES productos_basicos (id);

ALTER TABLE presentaciones_producto_basico
    ADD CONSTRAINT FK_PRESENTACIONES_PRODUCTO_BASICO_ON_PROVEEDOR FOREIGN KEY (proveedor_id) REFERENCES proveedores (id);

ALTER TABLE productos_basicos
    ADD CONSTRAINT FK_PRODUCTOSBASICOS_ON_UNIDADMEDIDADEFAULTIDUNIDADESMEDIDA FOREIGN KEY (unidad_medida_default_id_unidades_medida) REFERENCES unidades_de_medida (id_unidades_medida);

ALTER TABLE productos_comerciales
    ADD CONSTRAINT FK_PRODUCTOSCOMERCIALES_ON_UNIDADMEDIDADEFAULTIDUNIDADESMEDIDA FOREIGN KEY (unidad_medida_default_id_unidades_medida) REFERENCES unidades_de_medida (id_unidades_medida);

ALTER TABLE productos_fuera_especificacion
    ADD CONSTRAINT FK_PRODUCTOSFUERAESPECIFICACION_ON_VENTATRAYECTOENTITYIDVENTA FOREIGN KEY (venta_trayecto_entity_id_venta) REFERENCES bitacora_rutas_ventas_trayectos_producto (id_venta);

ALTER TABLE productos_basicos
    ADD CONSTRAINT FK_PRODUCTOS_BASICOS_ON_CATEGORIA_PRODUCTO FOREIGN KEY (categoria_producto_id) REFERENCES categoria_producto (id);

ALTER TABLE productos_comerciales
    ADD CONSTRAINT FK_PRODUCTOS_COMERCIALES_ON_CATEGORIA_COMERCIAL FOREIGN KEY (categoria_comercial_id) REFERENCES categoria_producto_comercial (id);

ALTER TABLE productos_fuera_especificacion
    ADD CONSTRAINT FK_PRODUCTOS_FUERA_ESPECIFICACION_ON_CONCEPTO FOREIGN KEY (concepto_id) REFERENCES producto_rezagado (id);

ALTER TABLE proveedores
    ADD CONSTRAINT FK_PROVEEDORES_ON_TIPO_PROVEEDOR_ENTITY FOREIGN KEY (tipo_proveedor_entity_id) REFERENCES tipos_proveedores (id);

ALTER TABLE rutas
    ADD CONSTRAINT FK_RUTAS_ON_CHOFER_BASE FOREIGN KEY (chofer_base) REFERENCES empleados (id);

ALTER TABLE rutas
    ADD CONSTRAINT FK_RUTAS_ON_PRODUCTO_BASE FOREIGN KEY (producto_base) REFERENCES productos_comerciales (id);

ALTER TABLE rutas
    ADD CONSTRAINT FK_RUTAS_ON_VEHICULOS FOREIGN KEY (vehiculos_id) REFERENCES vehiculos (id);

ALTER TABLE sistemas_almacenamiento
    ADD CONSTRAINT FK_SISTEMAS_ALMACENAMIENTO_ON_DEPARTAMENTO FOREIGN KEY (departamento_id) REFERENCES departamentos (id);

ALTER TABLE solicitudes_compra_partidas
    ADD CONSTRAINT FK_SOLICITUDESCOMPRAPARTIDAS_ON_UNIDADDEMEDIDAIDUNIDADESMEDIDA FOREIGN KEY (unidad_de_medida_id_unidades_medida) REFERENCES unidades_de_medida (id_unidades_medida);

ALTER TABLE solicitudes_compra
    ADD CONSTRAINT FK_SOLICITUDES_COMPRA_ON_EMPLEADO_SOLICITANTE FOREIGN KEY (empleado_solicitante_id) REFERENCES empleados (id);

ALTER TABLE solicitudes_compra_partidas
    ADD CONSTRAINT FK_SOLICITUDES_COMPRA_PARTIDAS_ON_PADRE FOREIGN KEY (padre_id) REFERENCES solicitudes_compra (id);

ALTER TABLE solicitudes_compra_partidas
    ADD CONSTRAINT FK_SOLICITUDES_COMPRA_PARTIDAS_ON_PRODUCTO_INSUMO FOREIGN KEY (producto_insumo_id) REFERENCES productos_basicos (id);

ALTER TABLE transferencias_material
    ADD CONSTRAINT FK_TRANSFERENCIAS_MATERIAL_ON_EMPLEADOS FOREIGN KEY (empleados_id) REFERENCES empleados (id);

ALTER TABLE vehiculos
    ADD CONSTRAINT FK_VEHICULOS_ON_UNIDAD_MEDIDA FOREIGN KEY (unidad_medida_id) REFERENCES unidades_de_medida (id_unidades_medida);

ALTER TABLE rutas_empleados
    ADD CONSTRAINT fk_rutemp_on_empleados_entity FOREIGN KEY (empleados_id) REFERENCES empleados (id);

ALTER TABLE rutas_empleados
    ADD CONSTRAINT fk_rutemp_on_rutas_entity FOREIGN KEY (rutas_id) REFERENCES rutas (id);