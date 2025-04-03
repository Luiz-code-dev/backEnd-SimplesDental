-- Adiciona as novas colunas
ALTER TABLE users ADD COLUMN first_name VARCHAR(255);
ALTER TABLE users ADD COLUMN last_name VARCHAR(255);

-- Copia o nome atual para first_name
UPDATE users SET first_name = name;

-- Define last_name como 'User' para registros existentes
UPDATE users SET last_name = 'User';

-- Remove a coluna name
ALTER TABLE users DROP COLUMN name;

-- Adiciona as constraints NOT NULL
ALTER TABLE users ALTER COLUMN first_name SET NOT NULL;
ALTER TABLE users ALTER COLUMN last_name SET NOT NULL;
