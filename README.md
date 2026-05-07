# Kiryana Management System

A desktop application for small Pakistani village kiryana (general) stores to replace handwritten stock and customer credit registers with a simple digital system. The shopkeeper can manage products, track inventory, process cash and credit sales, and manage customer khata — all from one screen.

---

## Group Members

| Full Name | CMS / Student ID | Section |
|-----------|-----------------|---------|
| [Waqas ur Rehman Ghumro] | [023-25-0121] | [BSCS-II Section (D)] |

---

## Demo Video

[Click here to watch the demo video]([PASTE YOUR YOUTUBE LINK HERE])

---

## GitHub Repository

[KiryanaMgmtSystem](https://github.com/waqas2712/KiryanaMgmtSystem)

---

## Purpose

Village shopkeepers in Pakistan record stock and customer debt (khata) using handwritten registers. This is slow, error-prone, and gives no way to track history. This system solves that by providing:

- A secure login for the shopkeeper
- A Point of Sale screen to sell products by pieces, weight, or rupee amount
- Automatic stock deduction after every sale
- Customer credit (khata) tracking with full payment history
- Inventory management with low stock alerts
- A live dashboard showing pending debts, recent sales, and low stock items

---

## Main Modules and Class Groups

### Model Classes — represent real world objects

| Class | What it models |
|-------|---------------|
| `Product` | A product the shop sells (name, unit, price, cost) |
| `Inventory` | Stock level and low stock threshold for one product |
| `Sale` | One complete sale transaction (CASH or CREDIT) |
| `SaleItem` | One product line inside a sale |
| `Receipt` | Temporary in-memory bill built before checkout |
| `Debt` | A customer outstanding khata record |
| `DebtTransaction` | One payment or credit entry in payment history |
| `User` | Shopkeeper login credentials |

### DAO Classes — database access, one class per table

| Class | Database Table |
|-------|---------------|
| `DBConnection` | Provides JDBC connection to MySQL |
| `ProductDAO` | product |
| `InventoryDAO` | inventory |
| `SaleDAO` | sales |
| `SaleItemDAO` | item_per_sale |
| `DebtDAO` | debt |
| `DebtTransactionDAO` | debt_tracking |

### Service Classes — business logic

| Class | Responsibility |
|-------|---------------|
| `AuthService` | Checks login credentials against the database |
| `SaleService` | Processes complete cash and credit sales |
| `InventoryService` | Manages products and stock levels |
| `DebtService` | Records repayments and updates debt balances |

### GUI Classes — Java Swing interface

| Class | Screen |
|-------|--------|
| `LoginFrame` | Login window — first screen |
| `DashboardFrame` | Main window with tab navigation and summary |
| `SellPanel` | Point of Sale — search products, build receipt, checkout |
| `RestockPanel` | Inventory — add, update, restock, delete products |
| `DebtPanel` | Khata — view debts, payment history, record payments |

---

## OOP Concepts Applied

| Concept | Where and How |
|---------|--------------|
| **Encapsulation** | All model classes have private fields with public getters and setters. `Debt.applyPayment()` manages its own balance and status internally |
| **Inheritance** | `LoginFrame` and `DashboardFrame` extend `JFrame`. `SellPanel`, `RestockPanel`, `DebtPanel` extend `JPanel` |
| **Abstraction** | GUI never writes SQL. It only calls service methods. DAOs hide all SQL from the rest of the application |
| **Composition** | `SaleService` has five DAO objects inside it. `Receipt` has an `ArrayList` of `SaleItem`. `Inventory` has a `Product` object inside it |
| **Collections** | `ArrayList` used throughout for product lists, receipt items, debt lists, and transaction history |
| **Exception Handling** | All JDBC calls are inside try-catch-finally blocks. DB failure at startup shows a user-friendly popup |
| **Database Persistence** | MySQL with 7 tables connected via JDBC |

---

## How to Compile and Run

### Requirements

- **JDK 8 or above**
- **MySQL** installed and running
- **`mysql-connector-java.jar`** in the same folder as the `.java` files
  - Download: https://dev.mysql.com/downloads/connector/j/
  - Choose Platform Independent, extract the ZIP, get the `.jar` file

---

### Step 1 — Set up the database

Open MySQL and run:

```sql
source kiryana_db.sql
```

This creates `kiryana_db` database with all 7 tables and a default login:
- **Username:** `admin`
- **Password:** `admin123`

---

### Step 2 — Set your MySQL credentials

Open `DBConnection.java` and update:

```java
private static final String URL     = "jdbc:mysql://localhost:3306/kiryana_db";
private static final String DB_USER = "root";
private static final String DB_PASS = "";
```

---

### Step 3 — Compile

Open command line in the project folder.

**Windows:**
```
javac -cp .;mysql-connector-java.jar *.java
```

**Mac / Linux:**
```
javac -cp .:mysql-connector-java.jar *.java
```

---

### Step 4 — Run

**Windows:**
```
java -cp .;mysql-connector-java.jar Main
```

**Mac / Linux:**
```
java -cp .:mysql-connector-java.jar Main
```

---

### Step 5 — Login

- **Username:** `admin`
- **Password:** `admin123`

---

## Database Tables

| Table | Purpose |
|-------|---------|
| `users` | Shopkeeper login |
| `product` | All products |
| `inventory` | Stock quantity and threshold per product |
| `sales` | Every sale (CASH or CREDIT) |
| `item_per_sale` | Product lines inside each sale |
| `debt` | Customer khata with balance and status |
| `debt_tracking` | Full payment history per debt |

---

## Academic Integrity

This project was built individually as part of the OOP Spring 2026 semester project.
All code was written and is fully understood by the submitting student.
