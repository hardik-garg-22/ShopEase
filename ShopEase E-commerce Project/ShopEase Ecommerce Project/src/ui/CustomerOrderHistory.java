package ui;

import dao.OrderDAO;
import models.Order;
import models.Product;
import models.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class CustomerOrderHistory extends JFrame {

    private JTable orderTable;
    private JTable itemTable;
    private DefaultTableModel orderTableModel;
    private DefaultTableModel itemTableModel;
    private User user;

    public CustomerOrderHistory(User user) {
        this.user = user;

        setTitle("ShopEase - Order History");
        setSize(800, 500);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Orders Table
        orderTableModel = new DefaultTableModel(new Object[]{"Order ID", "Total Amount", "Created At"}, 0);
        orderTable = new JTable(orderTableModel);
        loadOrders();

        JScrollPane orderScrollPane = new JScrollPane(orderTable);
        orderScrollPane.setBorder(BorderFactory.createTitledBorder("Your Orders"));

        // Items Table
        itemTableModel = new DefaultTableModel(new Object[]{"Product ID", "Name", "Quantity", "Price"}, 0);
        itemTable = new JTable(itemTableModel);

        JScrollPane itemScrollPane = new JScrollPane(itemTable);
        itemScrollPane.setBorder(BorderFactory.createTitledBorder("Order Items"));

        // Split Pane
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, orderScrollPane, itemScrollPane);
        splitPane.setDividerLocation(250);
        add(splitPane, BorderLayout.CENTER);

        // Event: Select Order to view items
        orderTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = orderTable.getSelectedRow();
                if (selectedRow >= 0) {
                    int orderId = (int) orderTableModel.getValueAt(selectedRow, 0);
                    loadOrderItems(orderId);
                }
            }
        });

        setVisible(true);
    }

    private void loadOrders() {
        orderTableModel.setRowCount(0);
        List<Order> orders = OrderDAO.getOrdersByUserId(user.getUserId());
        for (Order o : orders) {
            orderTableModel.addRow(new Object[]{
                    o.getOrderId(), o.getTotalAmount(), o.getCreatedAt()
            });
        }
    }

    private void loadOrderItems(int orderId) {
        itemTableModel.setRowCount(0);
        List<Product> items = OrderDAO.getOrderItems(orderId);
        for (Product p : items) {
            itemTableModel.addRow(new Object[]{
                    p.getProductId(), p.getName(), p.getQuantity(), p.getPrice()
            });
        }
    }
}
