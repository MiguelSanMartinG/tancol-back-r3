-- Crear la tabla para almacenar la secuencia
CREATE TABLE IF NOT EXISTS secuencia
(
    año              VARCHAR(2) PRIMARY KEY,
    ultima_secuencia INT
);

-- Crear la función para generar el ID
CREATE OR REPLACE FUNCTION generar_id()
    RETURNS TEXT AS
$$
DECLARE
    año_actual      VARCHAR(2);
    nueva_secuencia INT;
    nuevo_id        TEXT;
BEGIN
    -- Obtener los últimos 2 dígitos del año actual
    año_actual := TO_CHAR(CURRENT_DATE, 'YY');

    -- Bloquear la fila correspondiente al año actual para evitar concurrencia
    LOCK TABLE secuencia IN ROW EXCLUSIVE MODE;

    -- Verificar si ya existe una secuencia para el año actual
    IF EXISTS (SELECT 1 FROM secuencia WHERE año = año_actual) THEN
        -- Incrementar la secuencia
        UPDATE secuencia
        SET ultima_secuencia = ultima_secuencia + 1
        WHERE año = año_actual
        RETURNING ultima_secuencia INTO nueva_secuencia;
    ELSE
        -- Si no existe, crear una nueva fila con secuencia inicial 1
        INSERT INTO secuencia (año, ultima_secuencia)
        VALUES (año_actual, 1)
        RETURNING ultima_secuencia INTO nueva_secuencia;
    END IF;

    -- Formatear el ID: SC-<año>-<secuencia>
    nuevo_id := 'SC-' || año_actual || '-' || LPAD(nueva_secuencia::TEXT, 2, '0');

    -- Retornar el nuevo ID
    RETURN nuevo_id;
END;
$$ LANGUAGE plpgsql;