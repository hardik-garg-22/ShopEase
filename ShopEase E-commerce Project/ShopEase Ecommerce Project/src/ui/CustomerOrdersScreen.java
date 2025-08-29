package ui;

import dao.OrderDAO;
import models.Order;
import models.Product;
import models.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class CustomerOrdersScreen extends JFrame {

    private JTable orderTable;
    private JTable itemTable;
    private DefaultTableModel orderModel;
    private DefaultTableModel itemModel;
    private User user;

    public CustomerOrdersScreen(User user) {
        this.user = user;
        setTitle("ShopEase - My Orders");
        setSize(800, 500);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        // Order Table
        orderModel = new DefaultTableModel(new Object[]{"Order ID", "Total Amount", "Date"}, 0);
        orderTable = new JTable(orderModel);
        JScrollPane orderPane = new JScrollPane(orderTable);
        orderPane.setBorder(BorderFactory.createTitledBorder("Orders"));

        // Item Table
        itemModel = new DefaultTableModel(new Object[]{"Product Name", "Quantity", "Price"}, 0);
        itemTable = new JTable(itemModel);
        JScrollPane itemPane = new JScrollPane(itemTable);
        itemPane.setBorder(BorderFactory.createTitledBorder("Items"));

        // Split Pane
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, orderPane, itemPane);
        splitPane.setDividerLocation(250);
        add(splitPane, BorderLayout.CENTER);

        // Load orders
        loadOrders();

        // Event Listener for Order Selection
        orderTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = orderTable.getSelectedRow();
                if (row >= 0) {
                    int orderId = (int) orderModel.getValueAt(row, 0);
                    showOrderItems(orderId);
                }
            }
        });

        setVisible(true);
    }

    private void loadOrders() {
        List<Order> orders = OrderDAO.getOrdersByUserId(user.getUserId());
        for (Order order : orders) {
            orderModel.addRow(new Object[]{
                    order.getOrderId(),
                    order.getTotalAmount(),
                    order.getCreatedAt()  // ✅ Fixed method call
            });
        }
    }

    private void showOrderItems(int orderId) {
        itemModel.setRowCount(0);
        List<Product> items = OrderDAO.getOrderItems(orderId);
        for (Product p : items) {
            itemModel.addRow(new Object[]{p.getName(), p.getQuantity(), p.getPrice()});
        }
    }
}
