package models;

public class Admin extends User {

    public Admin() {
        super(0, "admin", "admin"); // Placeholder values, will be overwritten later
    }


    public void login() {
        System.out.println("Admin logged in");
    }


    public void logout() {
        System.out.println("Admin logged out");
    }
}
