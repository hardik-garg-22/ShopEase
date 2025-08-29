package ui;

import dao.OrderDAO;
import models.Order;
import models.Product;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class AdminOrderView extends JFrame {

    private JTable ordersTable;
    private JTable itemsTable;
    private DefaultTableModel ordersModel;
    private DefaultTableModel itemsModel;

    public AdminOrderView() {
        setTitle("ShopEase - Admin Order View");
        setSize(900, 500);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Order Table
        ordersModel = new DefaultTableModel(new Object[]{"Order ID", "User ID", "Total Amount", "Created At"}, 0);
        ordersTable = new JTable(ordersModel);
        loadAllOrders();
        JScrollPane ordersScroll = new JScrollPane(ordersTable);
        ordersScroll.setBorder(BorderFactory.createTitledBorder("All Orders"));

        // Item Table
        itemsModel = new DefaultTableModel(new Object[]{"Product ID", "Name", "Quantity", "Price"}, 0);
        itemsTable = new JTable(itemsModel);
        JScrollPane itemsScroll = new JScrollPane(itemsTable);
        itemsScroll.setBorder(BorderFactory.createTitledBorder("Order Details"));

        // Split Pane
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, ordersScroll, itemsScroll);
        splitPane.setDividerLocation(250);
        add(splitPane, BorderLayout.CENTER);

        // Load order items when a row is selected
        ordersTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = ordersTable.getSelectedRow();
                if (selectedRow >= 0) {
                    int orderId = (int) ordersModel.getValueAt(selectedRow, 0);
                    loadOrderItems(orderId);
                }
            }
        });

        setVisible(true);
    }

    private void loadAllOrders() {
        ordersModel.setRowCount(0);
        List<Order> orders = OrderDAO.getAllOrders();
        for (Order o : orders) {
            ordersModel.addRow(new Object[]{
                    o.getOrderId(), o.getUserId(), o.getTotalAmount(), o.getCreatedAt()
            });
        }
    }

    private void loadOrderItems(int orderId) {
        itemsModel.setRowCount(0);
        List<Product> items = OrderDAO.getOrderItems(orderId);
        for (Product p : items) {
            itemsModel.addRow(new Object[]{
                    p.getProductId(), p.getName(), p.getQuantity(), p.getPrice()
            });
        }
    }
}
