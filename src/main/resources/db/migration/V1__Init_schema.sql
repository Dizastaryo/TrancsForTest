-- Создание таблицы для транзакций
CREATE TABLE transactions (
                              id SERIAL PRIMARY KEY,
                              date DATE NOT NULL,
                              amount DOUBLE PRECISION NOT NULL,
                              operation VARCHAR(255) NOT NULL,
                              details TEXT,
                              currency VARCHAR(3) NOT NULL,
                              limit_exceeded BOOLEAN NOT NULL DEFAULT FALSE
);

-- Создание таблицы для лимитов
CREATE TABLE limits (
                        id SERIAL PRIMARY KEY,
                        amount DOUBLE PRECISION NOT NULL,
                        currency VARCHAR(3) NOT NULL DEFAULT 'USD',
                        created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Создание таблицы для курсов обмена
CREATE TABLE exchange_rate (
                               id SERIAL PRIMARY KEY,
                               currency_pair VARCHAR(10) NOT NULL,
                               closing_rate DOUBLE PRECISION NOT NULL,
                               local_date DATE NOT NULL
);

-- Создание таблицы для аккаунтов
CREATE TABLE accounts (
                          id SERIAL PRIMARY KEY,
                          balance DOUBLE PRECISION NOT NULL DEFAULT 10000.0
);



