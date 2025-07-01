
# IconDenim Backend

This is the backend project for the IconDenim system, built with Java Spring Boot.

## Table of Contents
- [Introduction](#introduction)
- [Project Structure](#project-structure)
- [Installation & Running](#installation--running)
- [Main Features](#main-features)
- [Environment Configuration](#environment-configuration)
- [Contact](#contact)

## Introduction
This project provides APIs for the IconDenim e-commerce system, supporting product management, orders, users, vouchers, cart, Redis cache, JWT authentication, RabbitMQ message queue, SMTP email sending, and more.

## Project Structure
```
backend/
├── src/main/java/com/store/backend/
│   ├── address/         # Address management
│   ├── cart/            # Cart management
│   ├── category/        # Category management
│   ├── color/           # Color management
│   ├── common/          # Common classes
│   ├── config/          # Spring, Redis, Security config
│   ├── exception/       # Exception handling
│   ├── guest/           # Guest cart
│   ├── image/           # Image management
│   ├── order/           # Order management
│   ├── product/         # Product management
│   ├── rabbitmq/        # RabbitMQ integration
│   ├── redis/           # Redis integration
│   ├── security/        # JWT security
│   ├── size/            # Size management
│   ├── smtp/            # Email sending
│   ├── user/            # User management
│   ├── variant/         # Product variant management
│   └── voucher/         # Voucher management
├── src/main/resources/
│   ├── application.properties
│   ├── static/
│   └── templates/
├── pom.xml
└── ...
```

## Installation & Running
1. **Requirements:**
   - Java 17 or higher
   - Maven 3.6+
   - PostgreSQL, Redis, RabbitMQ
2. **Install:**
   ```bash
   git clone <repo-url>
   cd backend
   mvn clean install
   ```
3. **Run the application:**
   ```bash
   ./mvnw spring-boot:run
   ```

## Main Features
- User registration/login, JWT authentication
- Product, category, variant, image management
- Cart management (user & guest)
- Order placement and management
- Voucher and discount application
- Email verification, order notification
- Redis cache, RabbitMQ integration

## Environment Configuration
- Create `src/main/resources/application.properties` to configure DB, Redis, SMTP, RabbitMQ etc based on `src/main/resources/application-sample.properties`.

## Contact
- Email: tienhai2808@gmail.com
- Facebook: https://www.facebook.com/hai.tan.288
