# FoodDelivery - Java JDBC Project

A console-based food ordering application built with Java 17, JDBC, MySQL, and Maven.

## Features
- Display available restaurants
- Select a restaurant and view its menu
- Select a food item and enter a quantity
- Calculate the bill using the menu price stored in MySQL
- Verify the customer ID
- Save an order and its item details in MySQL
- Use a JDBC transaction with commit and rollback
- Display order confirmation
- View previous orders for a customer
- Validate invalid restaurant, food item, quantity, and customer selections

## Technologies
- Java 17
- JDBC
- MySQL
- Maven
- MySQL Connector/J 9.5.0
- IntelliJ IDEA

## Database setup
1. Install and start MySQL.
2. Create a database named `FoodDelivery`.
3. Create the tables used by the application: `Customers`, `Restaurants`, `Menu_Items`, `Orders`, and `Order_Items`.
4. Insert sample customer, restaurant, and menu data.

The application expects the table and column names shown in the Java SQL queries. Make sure your database has matching tables and sample records before running it.

## Configure database connection
Open `src/main/java/org/example/Main.java` and update the connection settings for your local MySQL installation:

```java
String url = "jdbc:mysql://localhost:3306/FoodDelivery";
String username = "root";
String password = "YOUR_MYSQL_PASSWORD";
```

Replace the placeholder with your own local MySQL password. Do not commit real passwords, API keys, or other secrets to GitHub.

## Run the project
1. Clone or download this repository.
2. Open the project in IntelliJ IDEA as a Maven project.
3. Make sure Java 17 and MySQL are installed and running.
4. Configure the database connection as described above.
5. Allow Maven to download the MySQL Connector/J dependency.
6. Run the `Main` class.

## Project scope
This is a learning project demonstrating Java database connectivity and basic order processing. It supports one food item per order and does not include online payments, user authentication, an admin dashboard, or a graphical interface.
