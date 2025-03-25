package com.ntu.fdae.group1.bto.views;

public class Menu {
    public static void start() {
        System.out.println("Welcome to the BTO Management System!");
        System.out.println("\t1. Login");
        System.out.println("\t2. Upload user list into the system");
        System.out.println("\t3. Exit");
        System.out.print("Your choice (1-3): ");

        try {
            while (true) {
                // TODO: Can have a utility method to get integer input here
                int choice = Integer.parseInt(System.console().readLine());
                switch (choice) {
                    case 1:
                        System.out.println("Login");
                        break;
                    case 2:
                        System.out.println("Upload user list");
                        break;
                    case 3:
                        System.out.println("Exit");
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                        break;
                }
            }
        } catch (Exception e) {
            start();
        }
    }
}
