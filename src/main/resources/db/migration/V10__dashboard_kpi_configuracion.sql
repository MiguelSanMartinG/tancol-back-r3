CREATE TABLE dashboard_kpi_configuracion
(
    id            UUID                        NOT NULL,
    created_at    TIMESTAMP WITHOUT TIME ZONE,
    updated_at    TIMESTAMP WITHOUT TIME ZONE,
    enabled       BOOLEAN                     NOT NULL,
    deleted       BOOLEAN                     NOT NULL,
    empleado_id   UUID                        NOT NULL,
    configuracion JSONB                       NOT NULL,
    CONSTRAINT pk_dashboard_kpi_configuracion PRIMARY KEY (id),
    CONSTRAINT uk_dashboard_kpi_configuracion_empleado UNIQUE (empleado_id),
    CONSTRAINT fk_dashboard_kpi_configuracion_empleado FOREIGN KEY (empleado_id) REFERENCES empleados (id) ON DELETE CASCADE
);
