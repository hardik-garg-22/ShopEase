package ui;

import dao.OrderDAO;
import dao.ProductDAO;
import models.Product;
import models.User;
import ui.CustomerOrderHistory;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerDashboard extends JFrame {

    private JTable productTable;
    private DefaultTableModel tableModel;
    private List<Product> cart = new ArrayList<>();
    private User user;

    public CustomerDashboard(User user) {
        this.user = user;

        setTitle("ShopEase - Customer Dashboard");
        setSize(800, 450);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Welcome label
        JLabel welcomeLabel = new JLabel("Welcome, " + user.getUsername());
        welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 16));
        add(welcomeLabel, BorderLayout.NORTH);

        // Product Table
        tableModel = new DefaultTableModel(new Object[]{"ID", "Name", "Price", "Quantity"}, 0);
        productTable = new JTable(tableModel);
        refreshProductTable();
        add(new JScrollPane(productTable), BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel();
        JButton addToCartBtn = new JButton("Add to Cart");
        JButton viewCartBtn = new JButton("View Cart");
        JButton checkoutBtn = new JButton("Place Order");
        JButton myOrdersBtn = new JButton("My Orders");
        JButton logoutBtn = new JButton("Logout");

        buttonPanel.add(addToCartBtn);
        buttonPanel.add(viewCartBtn);
        buttonPanel.add(checkoutBtn);
        buttonPanel.add(myOrdersBtn);
        buttonPanel.add(logoutBtn);
        add(buttonPanel, BorderLayout.SOUTH);

        // Action Listeners
        addToCartBtn.addActionListener(e -> addToCart());
        viewCartBtn.addActionListener(e -> viewCart());
        checkoutBtn.addActionListener(e -> placeOrder());
        myOrdersBtn.addActionListener(e -> new CustomerOrdersScreen(user));
        logoutBtn.addActionListener(e -> {
            dispose();
            new LoginScreen().setVisible(true);
        });

        setVisible(true);
    }

    private void refreshProductTable() {
        tableModel.setRowCount(0);
        List<Product> products = ProductDAO.getAllProducts();
        for (Product p : products) {
            tableModel.addRow(new Object[]{
                    p.getProductId(), p.getName(), p.getPrice(), p.getQuantity()
            });
        }
    }

    private void addToCart() {
        int selectedRow = productTable.getSelectedRow();
        if (selectedRow >= 0) {
            int productId = (int) tableModel.getValueAt(selectedRow, 0);
            String name = (String) tableModel.getValueAt(selectedRow, 1);
            double price = (double) tableModel.getValueAt(selectedRow, 2);
            int quantity = (int) tableModel.getValueAt(selectedRow, 3);

            Product p = new Product();
            p.setProductId(productId);
            p.setName(name);
            p.setPrice(price);
            p.setQuantity(1); // Default: 1 item at a time

            cart.add(p);
            JOptionPane.showMessageDialog(this, name + " added to cart!");
        } else {
            JOptionPane.showMessageDialog(this, "Please select a product to add.");
        }
    }

    private void viewCart() {
        if (cart.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Your cart is empty.");
            return;
        }

        StringBuilder cartSummary = new StringBuilder("Your Cart:\n");
        double total = 0;

        for (Product p : cart) {
            cartSummary.append(p.getName())
                    .append(" - ₹").append(p.getPrice()).append("\n");
            total += p.getPrice();
        }

        cartSummary.append("\nTotal: ₹").append(total);
        JOptionPane.showMessageDialog(this, cartSummary.toString());
    }

    private void placeOrder() {
        if (cart.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Cart is empty.");
            return;
        }

        boolean success = OrderDAO.placeOrder(user.getUserId(), cart);
        if (success) {
            JOptionPane.showMessageDialog(this, "Order placed successfully!");
            cart.clear();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to place order. Please try again.");
        }
    }
}
