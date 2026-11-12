package org.example;

import java.sql.*;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        // 1. Database connection details
        String url = "jdbc:mysql://localhost:3306/FoodDelivery";
        String username = "root";
        String password = "Mysql Password";

        // 2. SQL query
        String sql = "SELECT restaurant_id, restaurant_name, city, rating FROM Restaurants";

        Scanner scanner = new Scanner(System.in);

        // 3. Establish database connection
        try (Connection connection =
                     DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {

            System.out.println("FoodDelivery database connected successfully");

            // 4. Display restaurants
            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    int id = rs.getInt("restaurant_id");
                    String name = rs.getString("restaurant_name");
                    String city = rs.getString("city");
                    double rating = rs.getDouble("rating");

                    System.out.println(
                            "Restaurant ID: " + id +
                                    " | Name: " + name +
                                    " | City: " + city +
                                    " | Rating: " + rating
                    );
                }
            }

            // 5. Select restaurant
            System.out.println("Enter Restaurant ID:");
            int selectedRestaurantId = scanner.nextInt();

            System.out.println("You selected Restaurant ID: " + selectedRestaurantId);

            // 6. Display menu
            String menuSql = "SELECT item_id, item_name, price FROM Menu_Items WHERE restaurant_id = ?";

            try (PreparedStatement menuPs = connection.prepareStatement(menuSql)) {

                menuPs.setInt(1, selectedRestaurantId);

                try (ResultSet menuRs = menuPs.executeQuery()) {

                    System.out.println("Available Food Items:");

                    int itemCount = 0;

                    while (menuRs.next()) {

                        int itemId = menuRs.getInt("item_id");
                        String itemName = menuRs.getString("item_name");
                        double price = menuRs.getDouble("price");

                        itemCount++;

                        System.out.println(itemId + " | " + itemName + " | " + price);
                    }

                    if (itemCount == 0) {

                        System.out.println("Invalid Restaurant ID. No menu found.");

                    } else {

                        // 7. Select food item
                        System.out.println("Enter Food Item ID:");
                        int selectedItemId = scanner.nextInt();

                        System.out.println("You selected Food Item ID: " + selectedItemId);

                        String itemSql = "SELECT item_id, item_name, price FROM Menu_Items WHERE item_id = ? AND restaurant_id = ?";

                        try (PreparedStatement itemPs = connection.prepareStatement(itemSql)) {

                            itemPs.setInt(1, selectedItemId);
                            itemPs.setInt(2, selectedRestaurantId);

                            try (ResultSet itemRs = itemPs.executeQuery()) {

                                if (itemRs.next()) {

                                    int itemId = itemRs.getInt("item_id");
                                    String itemName = itemRs.getString("item_name");
                                    double price = itemRs.getDouble("price");

                                    System.out.println("Selected Food: " + itemName + " | Price: " + price);

                                    // 8. Enter quantity
                                    System.out.println("Enter Quantity:");
                                    int quantity = scanner.nextInt();

                                    if (quantity <= 0) {

                                        System.out.println("Invalid Quantity. Please enter a quantity greater than zero.");

                                    } else {

                                        double totalBill = price * quantity;

                                        System.out.println("Total Bill: ₹" + totalBill);

                                        // 9. Verify customer
                                        System.out.println("Enter Customer ID:");
                                        int customerId = scanner.nextInt();

                                        String customerSql = "SELECT customer_id, name FROM Customers WHERE customer_id = ?";

                                        try (PreparedStatement customerPs = connection.prepareStatement(customerSql)) {

                                            customerPs.setInt(1, customerId);

                                            try (ResultSet customerRs = customerPs.executeQuery()) {

                                                if (customerRs.next()) {

                                                    String customerName = customerRs.getString("name");

                                                    System.out.println("Customer verified: " + customerName);

                                                    // 10. Start transaction
                                                    try {

                                                        connection.setAutoCommit(false);

                                                        // 11. Insert order into Orders table
                                                        String orderSql = "INSERT INTO Orders " +
                                                                "(customer_id, restaurant_id, total_amount, order_status) " +
                                                                "VALUES (?, ?, ?, ?)";

                                                        int orderId = 0;

                                                        try (PreparedStatement orderPs =
                                                                     connection.prepareStatement(orderSql, Statement.RETURN_GENERATED_KEYS)) {

                                                            orderPs.setInt(1, customerId);
                                                            orderPs.setInt(2, selectedRestaurantId);
                                                            orderPs.setDouble(3, totalBill);
                                                            orderPs.setString(4, "Pending");

                                                            int rowsInserted = orderPs.executeUpdate();

                                                            System.out.println("Order rows inserted: " + rowsInserted);

                                                            // 12. Retrieve generated Order ID
                                                            try (ResultSet generatedKeys = orderPs.getGeneratedKeys()) {

                                                                if (generatedKeys.next()) {

                                                                    orderId = generatedKeys.getInt(1);

                                                                    System.out.println("Generated Order ID: " + orderId);

                                                                } else {

                                                                    throw new SQLException("Order ID was not generated.");
                                                                }
                                                            }
                                                        }

                                                        // 13. Insert food details into Order_Items
                                                        String orderItemSql = "INSERT INTO Order_Items " +
                                                                "(order_id, item_id, quantity, item_price) " +
                                                                "VALUES (?, ?, ?, ?)";

                                                        try (PreparedStatement orderItemPs =
                                                                     connection.prepareStatement(orderItemSql)) {

                                                            orderItemPs.setInt(1, orderId);
                                                            orderItemPs.setInt(2, itemId);
                                                            orderItemPs.setInt(3, quantity);
                                                            orderItemPs.setDouble(4, price);

                                                            int itemRowsInserted = orderItemPs.executeUpdate();

                                                            System.out.println("Order item rows inserted: " + itemRowsInserted);
                                                        }

                                                        // 14. Commit transaction
                                                        connection.commit();

                                                        // 15. Display order confirmation
                                                        System.out.println("\n========== ORDER CONFIRMATION ==========");
                                                        System.out.println("Order ID: " + orderId);
                                                        System.out.println("Customer: " + customerName);
                                                        System.out.println("Food: " + itemName);
                                                        System.out.println("Quantity: " + quantity);
                                                        System.out.println("Price per item: ₹" + price);
                                                        System.out.println("Total Bill: ₹" + totalBill);
                                                        System.out.println("Order Status: Pending");
                                                        System.out.println("Order successfully placed!");


                                                        System.out.println("\nDo you want to view your previous orders?");
                                                        System.out.println("1. Yes");
                                                        System.out.println("2. No");

                                                        int choice = scanner.nextInt();

                                                        if (choice == 1) {

                                                            String historySql = "SELECT o.order_id, r.restaurant_name, " +
                                                                    "m.item_name, oi.quantity, o.total_amount, " +
                                                                    "o.order_status, o.order_date " +
                                                                    "FROM Orders o " +
                                                                    "JOIN Restaurants r ON o.restaurant_id = r.restaurant_id " +
                                                                    "JOIN Order_Items oi ON o.order_id = oi.order_id " +
                                                                    "JOIN Menu_Items m ON oi.item_id = m.item_id " +
                                                                    "WHERE o.customer_id = ? " +
                                                                    "ORDER BY o.order_date DESC";

                                                            try (PreparedStatement historyPs =
                                                                         connection.prepareStatement(historySql)) {

                                                                historyPs.setInt(1, customerId);

                                                                try (ResultSet historyRs = historyPs.executeQuery()) {

                                                                    System.out.println("\n========== YOUR ORDER HISTORY ==========");

                                                                    while (historyRs.next()) {

                                                                        System.out.println("----------------------------------");
                                                                        System.out.println("Order ID: " +
                                                                                historyRs.getInt("order_id"));

                                                                        System.out.println("Restaurant: " +
                                                                                historyRs.getString("restaurant_name"));

                                                                        System.out.println("Food: " +
                                                                                historyRs.getString("item_name"));

                                                                        System.out.println("Quantity: " +
                                                                                historyRs.getInt("quantity"));

                                                                        System.out.println("Total Amount: ₹" +
                                                                                historyRs.getDouble("total_amount"));

                                                                        System.out.println("Status: " +
                                                                                historyRs.getString("order_status"));

                                                                        System.out.println("Order Date: " +
                                                                                historyRs.getTimestamp("order_date"));
                                                                    }
                                                                }
                                                            }

                                                        } else if (choice == 2) {

                                                            System.out.println("Thank you for using FoodDelivery!");

                                                        } else {

                                                            System.out.println("Invalid choice.");
                                                        }


                                                    } catch (SQLException e) {

                                                        connection.rollback();

                                                        System.out.println("Order failed. Changes rolled back.");
                                                        e.printStackTrace();
                                                    }

                                                } else {

                                                    System.out.println("Invalid Customer ID. Order cannot be placed.");
                                                }
                                            }
                                        }
                                    }

                                } else {

                                    System.out.println("Invalid Food Item ID for the selected Restaurant.");
                                }
                            }
                        }
                    }
                }
            }

        } catch (SQLException e) {

            System.out.println("Database operation failed.");
            e.printStackTrace();
        }

        scanner.close();
    }
}