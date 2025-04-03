-- Ajuste das restrições de tamanho das colunas
ALTER TABLE categories
    ALTER COLUMN name TYPE VARCHAR(100),
    ALTER COLUMN description TYPE VARCHAR(255);

ALTER TABLE products
    ALTER COLUMN name TYPE VARCHAR(100),
    ALTER COLUMN description TYPE VARCHAR(255),
    ADD CONSTRAINT products_price_check CHECK (price > 0),
    ALTER COLUMN status SET NOT NULL,
    ALTER COLUMN category_id SET NOT NULL;

-- Criação de índices para otimização
CREATE INDEX idx_products_code ON products (code);
CREATE INDEX idx_products_category_id ON products (category_id);

-- Adicionando comentários nas tabelas para documentação
COMMENT ON TABLE categories IS 'Tabela de categorias de produtos';
COMMENT ON COLUMN categories.id IS 'Identificador único da categoria';
COMMENT ON COLUMN categories.name IS 'Nome da categoria (máx. 100 caracteres)';
COMMENT ON COLUMN categories.description IS 'Descrição da categoria (máx. 255 caracteres, opcional)';

COMMENT ON TABLE products IS 'Tabela de produtos';
COMMENT ON COLUMN products.id IS 'Identificador único do produto';
COMMENT ON COLUMN products.name IS 'Nome do produto (máx. 100 caracteres)';
COMMENT ON COLUMN products.description IS 'Descrição do produto (máx. 255 caracteres, opcional)';
COMMENT ON COLUMN products.price IS 'Preço do produto (deve ser maior que zero)';
COMMENT ON COLUMN products.status IS 'Status do produto (ativo/inativo)';
COMMENT ON COLUMN products.code IS 'Código único do produto';
COMMENT ON COLUMN products.category_id IS 'ID da categoria do produto (obrigatório)';
