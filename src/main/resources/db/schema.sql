CREATE TABLE payment_transactions (
                                      id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                      transaction_id VARCHAR(50) NOT NULL UNIQUE,
                                      amount DECIMAL(12,2) NOT NULL,
                                      currency VARCHAR(10) NOT NULL,
                                      payment_method VARCHAR(30) NOT NULL,
                                      status VARCHAR(20) NOT NULL,
                                      customer_email VARCHAR(100),
                                      created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                      updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                                          ON UPDATE CURRENT_TIMESTAMP
);