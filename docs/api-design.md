# Payment Integration System - API Design

## Overview

This document defines the API contract, payment processing flow, request/response models, and error handling strategy for the Payment Integration System.

---

# Payment Lifecycle

The payment processing lifecycle follows the flow below:

```text
CREATE PAYMENT
       ↓
    PENDING
       ↓
 ┌─────┴─────┐
 ↓           ↓
SUCCESS    FAILED
```

### Status Definitions

| Status  | Description                                                 |
| ------- | ----------------------------------------------------------- |
| PENDING | Payment request has been created and is awaiting processing |
| SUCCESS | Payment has been processed successfully                     |
| FAILED  | Payment processing failed                                   |

---

# API Endpoints

## 1. Create Payment

### Endpoint

```http
POST /api/v1/payments
```

### Description

Creates a new payment request and generates a unique transaction ID.

### Request Body

```json
{
  "amount": 1000.00,
  "currency": "INR",
  "paymentMethod": "UPI",
  "customerEmail": "customer@example.com"
}
```

### Request Field Details

| Field         | Type    | Required | Description                             |
| ------------- | ------- | -------- | --------------------------------------- |
| amount        | Decimal | Yes      | Payment amount                          |
| currency      | String  | Yes      | Currency code (INR, USD, EUR)           |
| paymentMethod | String  | Yes      | Payment method (UPI, CARD, NET_BANKING) |
| customerEmail | String  | Yes      | Customer email address                  |

### Success Response

**HTTP Status:** 201 Created

```json
{
  "transactionId": "TXN-20260615-0001",
  "status": "PENDING",
  "message": "Payment request created successfully"
}
```

---

## 2. Get Payment Status

### Endpoint

```http
GET /api/v1/payments/{transactionId}
```

### Description

Returns payment details and current transaction status.

### Path Variable

| Variable      | Description                           |
| ------------- | ------------------------------------- |
| transactionId | Unique payment transaction identifier |

### Success Response

**HTTP Status:** 200 OK

```json
{
  "transactionId": "TXN-20260615-0001",
  "amount": 1000.00,
  "currency": "INR",
  "paymentMethod": "UPI",
  "status": "SUCCESS"
}
```

---

# DTO Design

## PaymentRequest

```json
{
  "amount": 1000.00,
  "currency": "INR",
  "paymentMethod": "UPI",
  "customerEmail": "customer@example.com"
}
```

### Validation Rules

| Field         | Validation             |
| ------------- | ---------------------- |
| amount        | Must be greater than 0 |
| currency      | Must not be blank      |
| paymentMethod | Must not be blank      |
| customerEmail | Must be a valid email  |

---

## PaymentResponse

```json
{
  "transactionId": "TXN-20260615-0001",
  "status": "PENDING",
  "message": "Payment request created successfully"
}
```

---

# Error Handling Strategy

## Validation Error

**HTTP Status:** 400 Bad Request

```json
{
  "errorCode": "VALIDATION_ERROR",
  "message": "Amount must be greater than zero"
}
```

---

## Payment Not Found

**HTTP Status:** 404 Not Found

```json
{
  "errorCode": "PAYMENT_NOT_FOUND",
  "message": "Transaction does not exist"
}
```

---

## Internal Server Error

**HTTP Status:** 500 Internal Server Error

```json
{
  "errorCode": "INTERNAL_SERVER_ERROR",
  "message": "Unexpected error occurred"
}
```

---

# Business Rules

1. Every payment request must generate a unique transaction ID.
2. Payment amount must be greater than zero.
3. Supported payment methods:

    * UPI
    * CARD
    * NET_BANKING
4. Initial status must always be PENDING.
5. Transaction status can only transition:

    * PENDING → SUCCESS
    * PENDING → FAILED

---

# Future Enhancements

## Payment Gateway Integration

* Razorpay Integration
* Stripe Integration
* PayPal Integration

## Webhook Support

* Gateway callback handling
* Payment status synchronization

## Security

* JWT Authentication
* Role-Based Access Control (RBAC)

## Monitoring

* Actuator Health Checks
* Application Metrics
* Audit Logging

---

# Design Approval Status

Status: Approved for Implementation

Next Story:
Payment API Implementation
