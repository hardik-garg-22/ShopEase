package ui;

import javax.swing.*;
import dao.UserDAO;
import models.User;

public class LoginScreen extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;

    public LoginScreen() {
        setTitle("ShopEase - Login");
        setSize(300, 200);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);

        JLabel userLabel = new JLabel("Username:");
        userLabel.setBounds(20, 20, 80, 25);
        add(userLabel);

        usernameField = new JTextField();
        usernameField.setBounds(110, 20, 150, 25);
        add(usernameField);

        JLabel passLabel = new JLabel("Password:");
        passLabel.setBounds(20, 60, 80, 25);
        add(passLabel);

        passwordField = new JPasswordField();
        passwordField.setBounds(110, 60, 150, 25);
        add(passwordField);

        JButton loginButton = new JButton("Login");
        loginButton.setBounds(90, 110, 100, 30);
        add(loginButton);

        loginButton.addActionListener(e -> {
            String user = usernameField.getText();
            String pass = new String(passwordField.getPassword());

            User u = UserDAO.authenticate(user, pass);
            if (u != null) {
                JOptionPane.showMessageDialog(this, "Login Successful!");
                dispose();

                if (u.getRole().equalsIgnoreCase("admin")) {
                    new AdminDashboard(u).setVisible(true);
                } else if (u.getRole().equalsIgnoreCase("customer")) {
                    new CustomerDashboard(u).setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(this, "Unknown role: " + u.getRole());
                }
            } else {
                JOptionPane.showMessageDialog(this, "Invalid credentials!");
            }
        });
    }
}
