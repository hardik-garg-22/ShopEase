package dao;

import models.Order;
import models.Product;
import services.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderDAO {

    public static boolean placeOrder(int userId, List<Product> cart) {
        double total = cart.stream().mapToDouble(p -> p.getPrice() * p.getQuantity()).sum();

        Connection conn = null;
        PreparedStatement orderStmt = null;
        PreparedStatement itemStmt = null;
        PreparedStatement stockUpdateStmt = null;

        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            // ✅ FIX: Change user_id to customer_id
            String orderSql = "INSERT INTO orders (customer_id, total_amount) VALUES (?, ?)";
            orderStmt = conn.prepareStatement(orderSql, Statement.RETURN_GENERATED_KEYS);
            orderStmt.setInt(1, userId);
            orderStmt.setDouble(2, total);
            orderStmt.executeUpdate();

            ResultSet rs = orderStmt.getGeneratedKeys();
            int orderId = 0;
            if (rs.next()) {
                orderId = rs.getInt(1);
            } else {
                throw new SQLException("Failed to retrieve generated order ID.");
            }

            String itemSql = "INSERT INTO order_items (order_id, product_id, quantity, price) VALUES (?, ?, ?, ?)";
            itemStmt = conn.prepareStatement(itemSql);

            String stockSql = "UPDATE products SET quantity = quantity - ? WHERE product_id = ? AND quantity >= ?";
            stockUpdateStmt = conn.prepareStatement(stockSql);

            for (Product p : cart) {
                itemStmt.setInt(1, orderId);
                itemStmt.setInt(2, p.getProductId());
                itemStmt.setInt(3, p.getQuantity());
                itemStmt.setDouble(4, p.getPrice());
                itemStmt.addBatch();

                stockUpdateStmt.setInt(1, p.getQuantity());
                stockUpdateStmt.setInt(2, p.getProductId());
                stockUpdateStmt.setInt(3, p.getQuantity());
                stockUpdateStmt.addBatch();
            }

            itemStmt.executeBatch();
            int[] stockUpdates = stockUpdateStmt.executeBatch();

            for (int result : stockUpdates) {
                if (result == 0) {
                    conn.rollback();
                    System.err.println("Insufficient stock for one or more items.");
                    return false;
                }
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } finally {
            try {
                if (orderStmt != null) orderStmt.close();
                if (itemStmt != null) itemStmt.close();
                if (stockUpdateStmt != null) stockUpdateStmt.close();
                if (conn != null) conn.setAutoCommit(true);
                if (conn != null) conn.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }

        return false;
    }

    public static List<Order> getOrdersByUserId(int userId) {
        List<Order> orders = new ArrayList<>();
        // ✅ FIX: Change user_id to customer_id
        String sql = "SELECT order_id, total_amount, created_at FROM orders WHERE customer_id = ? ORDER BY created_at DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Order o = new Order();
                o.setOrderId(rs.getInt("order_id"));
                o.setUserId(userId);
                o.setTotalAmount(rs.getDouble("total_amount"));
                o.setCreatedAt(rs.getTimestamp("created_at"));
                orders.add(o);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return orders;
    }

    public static List<Product> getOrderItems(int orderId) {
        List<Product> items = new ArrayList<>();
        String sql = "SELECT p.product_id, p.name, oi.quantity, oi.price " +
                "FROM order_items oi JOIN products p ON oi.product_id = p.product_id " +
                "WHERE oi.order_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, orderId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Product p = new Product();
                p.setProductId(rs.getInt("product_id"));
                p.setName(rs.getString("name"));
                p.setQuantity(rs.getInt("quantity"));
                p.setPrice(rs.getDouble("price"));
                items.add(p);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return items;
    }

    public static List<Order> getAllOrders() {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT * FROM orders ORDER BY created_at DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Order o = new Order();
                o.setOrderId(rs.getInt("order_id"));
                // ✅ FIX: Change getInt("user_id") to getInt("customer_id")
                o.setUserId(rs.getInt("customer_id"));
                o.setTotalAmount(rs.getDouble("total_amount"));
                o.setCreatedAt(rs.getTimestamp("created_at"));
                orders.add(o);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return orders;
    }
}
