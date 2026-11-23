-- Foto del empleado (nombre de la imagen subida por POST /upload); se usa como foto de perfil del sistema.
ALTER TABLE empleados ADD COLUMN IF NOT EXISTS id_imagen VARCHAR(300);
