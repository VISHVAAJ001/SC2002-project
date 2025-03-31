package com.ntu.fdae.group1.bto.views;
import java.util.Scanner;

public abstract class BaseUI {
    protected Scanner scanner = new Scanner(System.in);

    public void displayMessage(String message) {
        System.out.println(message);
    }

    public String promptForInput(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }

    public int promptForInt(String prompt) {
        System.out.print(prompt);
        return Integer.parseInt(scanner.nextLine());
    }

    protected void clearConsole() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    protected void displayError(String message) {
        System.out.println("Error: " + message);
    }
}
