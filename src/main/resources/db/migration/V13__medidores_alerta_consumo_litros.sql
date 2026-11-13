-- Consumo diario en litros a partir del cual se avisa al capturar la lectura del medidor.
-- Es opcional: un medidor sin valor no muestra la alerta. La alerta no impide guardar.
ALTER TABLE medidores
    ADD COLUMN IF NOT EXISTS alerta_consumo_litros DECIMAL(19, 2);
