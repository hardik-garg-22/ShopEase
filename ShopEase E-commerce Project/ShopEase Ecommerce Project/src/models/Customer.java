package models;

public class Customer extends User {

    public Customer() {
        super(0, "customer", "customer"); // Temporary/default values
    }


    public void login() {
        System.out.println("Customer logged in");
    }


    public void logout() {
        System.out.println("Customer logged out");
    }
}
