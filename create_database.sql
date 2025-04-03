-- Script para criar o banco de dados simplesdental
DO
$do$
BEGIN
   IF NOT EXISTS (
      SELECT FROM pg_database WHERE datname = 'simplesdental'
   ) THEN
      CREATE DATABASE simplesdental
      WITH
      OWNER = postgres
      ENCODING = 'UTF8'
      LC_COLLATE = 'en_US.utf8'
      LC_CTYPE = 'en_US.utf8'
      TEMPLATE template0;
   END IF;
END
$do$;
