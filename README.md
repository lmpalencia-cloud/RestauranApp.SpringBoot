# 🍽️ Restaurant Management System

Sistema de gestión de restaurante basado en Spring Boot, diseñado para
administrar usuarios, productos, mesas, pedidos y flujo de trabajo del
personal.

## 📂 Estructura del Proyecto

    restaurant-management/
    ├─ pom.xml
    ├─ src/main/java/com/simonyluismario/restaurante/
    │  ├─ RestaurantApplication.java
    │  ├─ config/
    │  │  └─ SecurityConfig.java
    │  ├─ dataloader/
    │  │  └─ DataLoader.java
    │  ├─ controllers/
    │  │  ├─ AuthController.java
    │  │  ├─ AdminController.java
    │  │  └─ WorkerController.java
    │  ├─ models/
    │  │  ├─ User.java
    │  │  ├─ Role.java
    │  │  ├─ Product.java
    │  │  ├─ TableEntity.java
    │  │  ├─ OrderEntity.java
    │  │  └─ OrderItemm.java
    │  ├─ repositories/
    │  │  ├─ UserRepository.java
    │  │  ├─ ProductRepository.java
    │  │  ├─ TableRepository.java
    │  │  └─ OrderRepository.java
    │  ├─ services/
    │  │  ├─ UserService.java
    │  │  ├─ ProductService.java
    │  │  ├─ OrderService.java
    │  │  ├─ EmailService.java
    │  │  └─ DataInitializer.java
    │  └─ dto/
    │     └─ ResetPasswordToken.java
    ├─ src/main/resources/
    │  ├─ application.properties
    │  ├─ static/
    │  │  ├─ css/style.css
    │  │  └─ js/app.js
    │  └─ templates/
    │     ├─ login.html
    │     ├─ register.html
    │     ├─ olvide.html
    │     ├─ reset_password.html
    │     ├─ admin/employees.html
    │     ├─ admin/menu.html
    │     ├─ admin/product_form.html
    │     ├─ worker/order_view.html
    │     ├─ worker/table_products.html
    │     └─ worker/workspace.html

## 🚀 Descripción General del Sistema

Este proyecto permite gestionar de forma eficiente las operaciones
internas de un restaurante.

## 🛠️ Tecnologías Utilizadas

-   Java 21
-   Spring Boot 4
-   Spring Security
-   Spring Data JPA
-   Thymeleaf
-   Maven

## 🧑‍💻 Autores

-   **Simon Andrés Espinosa Arteaga**
-   **Luis Mario Palencia de Hoyos**

## 📄 Licencia

MIT License
