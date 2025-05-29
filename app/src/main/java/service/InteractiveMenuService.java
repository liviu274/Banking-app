package service;

import java.util.Scanner;

public class InteractiveMenuService {
    private final BankService bankService;
    private final Scanner scanner;
    private static InteractiveMenuService instance;

    private InteractiveMenuService() {
        this.bankService = BankService.getInstance();
        this.scanner = new Scanner(System.in);
    }
    
    public static InteractiveMenuService getInstance() {
        if (instance == null) {
            instance = new InteractiveMenuService();
        }
        return instance;
    }

        private int getIntInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                int value = Integer.parseInt(scanner.nextLine());
                return value;
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
    }
    
    private double getDoubleInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                double value = Double.parseDouble(scanner.nextLine());
                return value;
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
    }
    
    public void start() {
        boolean exit = false;
        while (!exit) {
            displayMainMenu();
            int choice = getIntInput("Enter your choice: ");
            
            switch (choice) {
                case 1 -> clientManagementMenu();
                case 2 -> accountManagementMenu();
                case 3 -> transactionMenu();
                case 4 -> cardManagementMenu();
                case 5 -> loanManagementMenu();
                case 0 -> exit = true;
                default -> System.out.println("Invalid option. Please try again.");
            }
        }
        scanner.close();
        System.out.println("Thank you for using our banking application!");
    }
    
    private void displayMainMenu() {
        System.out.println("\n===== BANKING APPLICATION =====");
        System.out.println("1. Client Management");
        System.out.println("2. Account Management");
        System.out.println("3. Transactions");
        System.out.println("4. Card Management");
        System.out.println("5. Loan Management");
        System.out.println("0. Exit");
        System.out.println("==============================");
    }
    
    // CLIENT MANAGEMENT
    private void clientManagementMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("\n===== CLIENT MANAGEMENT =====");
            System.out.println("1. Add New Client");
            System.out.println("2. View Client Details");
            System.out.println("0. Back to Main Menu");
            System.out.println("===========================");
            
            int choice = getIntInput("Enter your choice: ");
            switch (choice) {
                case 1 -> addClient();
                case 2 -> viewClientDetails();
                case 0 -> back = true;
                default -> System.out.println("Invalid option. Please try again.");
            }
        }
    }
    
    private void addClient() {
        System.out.println("\n----- ADD NEW CLIENT -----");
        String clientId = getStringInput("Enter client ID: ");
        String firstName = getStringInput("Enter first name: ");
        String lastName = getStringInput("Enter last name: ");
        String email = getStringInput("Enter email: ");
        String phoneNumber = getStringInput("Enter phone number: ");
        
        bankService.addClient(clientId, firstName, lastName, email, phoneNumber);
    }
    
    private void viewClientDetails() {
        System.out.println("\n----- VIEW CLIENT DETAILS -----");
        String clientId = getStringInput("Enter client ID: ");
        // Implement this method in BankService to retrieve client details
        bankService.viewClientDetails(clientId);
        // System.out.println("This feature is not implemented yet.");
    }
    
    // ACCOUNT MANAGEMENT
    private void accountManagementMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("\n===== ACCOUNT MANAGEMENT =====");
            System.out.println("1. Create Checking Account");
            System.out.println("2. Create Savings Account");
            System.out.println("3. View Account Balance");
            System.out.println("0. Back to Main Menu");
            System.out.println("=============================");
            
            int choice = getIntInput("Enter your choice: ");
            switch (choice) {
                case 1 -> createCheckingAccount();
                case 2 -> createSavingsAccount();
                case 3 -> viewAccountBalance();
                case 0 -> back = true;
                default -> System.out.println("Invalid option. Please try again.");
            }
        }
    }
    
    private void createCheckingAccount() {
        System.out.println("\n----- CREATE CHECKING ACCOUNT -----");
        String clientId = getStringInput("Enter client ID: ");
        double overdraft = getDoubleInput("Enter overdraft limit: ");
        String iban = getStringInput("Enter IBAN: ");
        double balance = getDoubleInput("Enter initial balance: ");
        
        bankService.createCheckingAccount(clientId, overdraft, iban, balance);
    }
    
    private void createSavingsAccount() {
        System.out.println("\n----- CREATE SAVINGS ACCOUNT -----");
        String clientId = getStringInput("Enter client ID: ");
        double interestRate = getDoubleInput("Enter interest rate (e.g., 0.05 for 5%): ");
        String iban = getStringInput("Enter IBAN: ");
        double balance = getDoubleInput("Enter initial balance: ");
        
        bankService.createSavingsAccount(clientId, interestRate, iban, balance);
    }
    
    private void viewAccountBalance() {
        System.out.println("\n----- VIEW ACCOUNT BALANCE -----");
        String iban = getStringInput("Enter IBAN: ");
        bankService.printAccountBalance(iban);
    }
    
    private void viewTransactionHistory() {
        System.out.println("\n----- VIEW TRANSACTION HISTORY -----");
        String iban = getStringInput("Enter IBAN: ");
        bankService.printTransactionHistory(iban);
    }
    
    // TRANSACTION MANAGEMENT
    private void transactionMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("\n===== TRANSACTIONS =====");
            System.out.println("1. Deposit");
            System.out.println("2. Withdraw");
            System.out.println("3. Transfer");
            System.out.println("0. Back to Main Menu");
            System.out.println("======================");
            
            int choice = getIntInput("Enter your choice: ");
            switch (choice) {
                case 1 -> deposit();
                case 2 -> withdraw();
                case 3 -> transfer();
                case 0 -> back = true;
                default -> System.out.println("Invalid option. Please try again.");
            }
        }
    }
    
    private void deposit() {
        System.out.println("\n----- DEPOSIT -----");
        String iban = getStringInput("Enter IBAN: ");
        double amount = getDoubleInput("Enter amount to deposit: ");
        bankService.deposit(iban, amount);
    }
    
    private void withdraw() {
        System.out.println("\n----- WITHDRAW -----");
        String iban = getStringInput("Enter IBAN: ");
        double amount = getDoubleInput("Enter amount to withdraw: ");
        bankService.withdraw(iban, amount);
    }
    
    private void transfer() {
        System.out.println("\n----- TRANSFER -----");
        String sourceIban = getStringInput("Enter source IBAN: ");
        String destinationIban = getStringInput("Enter destination IBAN: ");
        double amount = getDoubleInput("Enter amount to transfer: ");
        bankService.transfer(sourceIban, destinationIban, amount);
    }
    
    // CARD MANAGEMENT
    private void cardManagementMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("\n===== CARD MANAGEMENT =====");
            System.out.println("1. Link Card to Account");
            System.out.println("0. Back to Main Menu");
            System.out.println("=========================");
            
            int choice = getIntInput("Enter your choice: ");
            switch (choice) {
                case 1 -> linkCard();
                case 0 -> back = true;
                default -> System.out.println("Invalid option. Please try again.");
            }
        }
    }
    
    private void linkCard() {
        System.out.println("\n----- LINK CARD -----");
        String cardNumber = getStringInput("Enter card number: ");
        
        System.out.println("Card Types:");
        System.out.println("1. DEBIT");
        System.out.println("2. CREDIT");
        int typeChoice = getIntInput("Select card type (1 or 2): ");
        String cardType = switch (typeChoice) {
            case 1 -> "DEBIT";
            case 2 -> "CREDIT";
            default -> {
                System.out.println("Invalid choice. Using DEBIT by default.");
                yield "DEBIT";
            }
        };
        
        String iban = getStringInput("Enter IBAN to link the card to: ");
        bankService.linkCard(cardNumber, cardType, iban);
    }
    
    // LOAN MANAGEMENT
    private void loanManagementMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("\n===== LOAN MANAGEMENT =====");
            System.out.println("1. Apply for Loan");
            System.out.println("0. Back to Main Menu");
            System.out.println("=========================");
            
            int choice = getIntInput("Enter your choice: ");
            switch (choice) {
                case 1 -> applyForLoan();
                case 0 -> back = true;
                default -> System.out.println("Invalid option. Please try again.");
            }
        }
    }
    
    private void applyForLoan() {
        System.out.println("\n----- APPLY FOR LOAN -----");
        String clientId = getStringInput("Enter client ID: ");
        double amount = getDoubleInput("Enter loan amount: ");
        double interestRate = getDoubleInput("Enter interest rate (e.g., 0.05 for 5%): ");
        
        bankService.offerLoan(amount, interestRate, clientId);
    }
    
    // UTILITY METHODS
    private String getStringInput(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }
    

}
