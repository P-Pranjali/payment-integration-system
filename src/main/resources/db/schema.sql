# Database Design

## Objective

Create the database schema required for storing payment transactions.

## Database

payment_system_db

## Table: payment_transactions

| Column         | Type          | Constraints                 |
| -------------- | ------------- | --------------------------- |
| id             | BIGINT        | Primary Key, Auto Increment |
| transaction_id | VARCHAR(50)   | Unique, Not Null            |
| amount         | DECIMAL(12,2) | Not Null                    |
| currency       | VARCHAR(10)   | Not Null                    |
| payment_method | VARCHAR(30)   | Not Null                    |
| status         | VARCHAR(20)   | Not Null                    |
| customer_email | VARCHAR(100)  | Nullable                    |
| created_at     | TIMESTAMP     | Default Current Timestamp   |
| updated_at     | TIMESTAMP     | Auto Updated                |

## Status Values

* PENDING
* SUCCESS
* FAILED

## Purpose

Store payment requests and track their processing status.
