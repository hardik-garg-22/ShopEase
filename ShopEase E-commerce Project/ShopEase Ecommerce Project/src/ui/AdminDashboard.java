package ui;

import dao.ProductDAO;
import models.Product;
import models.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class AdminDashboard extends JFrame {

    private JTable productTable;
    private DefaultTableModel tableModel;
    private JTextField nameField, priceField, quantityField;

    public AdminDashboard(User user) {
        setTitle("ShopEase - Admin Dashboard");
        setSize(800, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Table Setup
        tableModel = new DefaultTableModel(new Object[]{"ID", "Name", "Price", "Quantity"}, 0);
        productTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(productTable);
        add(scrollPane, BorderLayout.CENTER);

        // Input Panel
        JPanel inputPanel = new JPanel(new GridLayout(2, 4, 10, 10));
        nameField = new JTextField();
        priceField = new JTextField();
        quantityField = new JTextField();

        inputPanel.add(new JLabel("Name:"));
        inputPanel.add(nameField);
        inputPanel.add(new JLabel("Price:"));
        inputPanel.add(priceField);
        inputPanel.add(new JLabel("Quantity:"));
        inputPanel.add(quantityField);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton addBtn = new JButton("Add");
        JButton updateBtn = new JButton("Update");
        JButton deleteBtn = new JButton("Delete");
        JButton refreshBtn = new JButton("Refresh");
        JButton logoutBtn = new JButton("Logout");

        buttonPanel.add(addBtn);
        buttonPanel.add(updateBtn);
        buttonPanel.add(deleteBtn);
        buttonPanel.add(refreshBtn);
        buttonPanel.add(logoutBtn);

        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.add(inputPanel, BorderLayout.CENTER);
        southPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(southPanel, BorderLayout.SOUTH);

        // Button Actions
        addBtn.addActionListener(e -> addProduct());
        updateBtn.addActionListener(e -> updateProduct());
        deleteBtn.addActionListener(e -> deleteProduct());
        refreshBtn.addActionListener(e -> refreshTable());
        logoutBtn.addActionListener(e -> {
            dispose();
            new LoginScreen().setVisible(true);
        });

        // Row selection
        productTable.getSelectionModel().addListSelectionListener(e -> {
            int selectedRow = productTable.getSelectedRow();
            if (selectedRow >= 0) {
                nameField.setText(tableModel.getValueAt(selectedRow, 1).toString());
                priceField.setText(tableModel.getValueAt(selectedRow, 2).toString());
                quantityField.setText(tableModel.getValueAt(selectedRow, 3).toString());
            }
        });

        refreshTable();
        setVisible(true);
    }

    private void addProduct() {
        try {
            String name = nameField.getText().trim();
            double price = Double.parseDouble(priceField.getText().trim());
            int quantity = Integer.parseInt(quantityField.getText().trim());

            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Product name is required.");
                return;
            }

            Product product = new Product();
            product.setName(name);
            product.setPrice(price);
            product.setQuantity(quantity);
            ProductDAO.addProduct(product);

            JOptionPane.showMessageDialog(this, "Product added!");
            clearInputs();
            refreshTable();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid price or quantity.");
        }
    }

    private void updateProduct() {
        int selectedRow = productTable.getSelectedRow();
        if (selectedRow >= 0) {
            try {
                int productId = (int) tableModel.getValueAt(selectedRow, 0);
                String name = nameField.getText().trim();
                double price = Double.parseDouble(priceField.getText().trim());
                int quantity = Integer.parseInt(quantityField.getText().trim());

                Product product = new Product();
                product.setProductId(productId);
                product.setName(name);
                product.setPrice(price);
                product.setQuantity(quantity);

                ProductDAO.updateProduct(product);
                JOptionPane.showMessageDialog(this, "Product updated!");
                clearInputs();
                refreshTable();

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid price or quantity.");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Select a product to update.");
        }
    }

    private void deleteProduct() {
        int selectedRow = productTable.getSelectedRow();
        if (selectedRow >= 0) {
            int productId = (int) tableModel.getValueAt(selectedRow, 0);
            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this product?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                ProductDAO.deleteProduct(productId);
                JOptionPane.showMessageDialog(this, "Product deleted.");
                clearInputs();
                refreshTable();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Select a product to delete.");
        }
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        List<Product> products = ProductDAO.getAllProducts();
        for (Product p : products) {
            tableModel.addRow(new Object[]{p.getProductId(), p.getName(), p.getPrice(), p.getQuantity()});
        }
    }

    private void clearInputs() {
        nameField.setText("");
        priceField.setText("");
        quantityField.setText("");
    }
}
