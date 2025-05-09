package main;

import controllers.UserController;
import models.User;
import views.LoginView;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        System.out.println("Welcome to the BTO Management System!");

        // Initialize the system
        if (!CSVReader.initializeSystem()) {
            System.out.println("System initialization failed. Exiting...");
            return;
        }

        Scanner scanner = new Scanner(System.in);
        UserController userController = new UserController();

        while (true) {
            try {
                // Authenticate user
                System.out.println("\nPlease log in to continue.");
                System.out.print("Enter NRIC: ");
                String nric = scanner.nextLine();
                System.out.print("Enter password: ");
                String password = scanner.nextLine();

                User authenticatedUser = userController.login(nric, password);
                if (authenticatedUser == null) {
                    System.out.println("Login failed. Please try again.");
                    continue; // Allow the user to try logging in again
                }

                System.out.println("\nLogin successful!");
                System.out.println("Welcome, " + authenticatedUser.getName());
                System.out.println("Role: " + authenticatedUser.getUserType());

                // Redirect to the appropriate dashboard
                LoginView loginView = new LoginView();
                loginView.redirectToUserDashboard(authenticatedUser);

                System.out.println("You have logged out.");
            } catch (Exception e) {
                System.out.println("An error occurred. Please try again.");
            }
        }
    }
}