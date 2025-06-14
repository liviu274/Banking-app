package service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import model.BankAccount;
import model.Card;
import model.CheckingAccount;
import model.Client;
import model.Loan;
import model.SavingsAccount;
import model.Transaction;

// Singleton
public class BankService {
    // Keep in-memory structures for quick access
    private Map<String, Client> clientById = new HashMap<>();
    private TreeMap<String, BankAccount> accountByIban = new TreeMap<>();
    private List<Loan> loans = new ArrayList<>();
    
    // Database services
    private final ClientService clientService;
    private final BankAccountService accountService;
    private final CardService cardService;
    private final LoanService loanService;
    
    private static BankService instance;

    private BankService() {
        // Initialize database services
        clientService = ClientService.getInstance();
        accountService = BankAccountService.getInstance();
        cardService = CardService.getInstance();
        loanService = LoanService.getInstance();

        initializeFromDatabase();
    }

    // Initialize in-memory data from database
    private void initializeFromDatabase() {
        // Load clients
        List<Client> clients = clientService.readAll();
        for (Client client : clients) {
            clientById.put(client.getClientId(), client);
        }
        
        // Load accounts
        List<BankAccount> accounts = accountService.readAll();
        for (BankAccount account : accounts) {
            accountByIban.put(account.getIban(), account);
        }
    }

    public static BankService getInstance() {
        if (instance == null) {
            instance = new BankService();
        }
        return instance;
    }

    public void addClient(String clientId, String firstName, String lastName, String email, String phoneNumber) {
        if (clientById.containsKey(clientId)) {
            System.out.println("Client with ID " + clientId + " already exists.");
        } else {
            Client newClient = new Client(firstName, lastName, email, phoneNumber, clientId);
            clientById.put(clientId, newClient);
            // Persist to database
            clientService.create(newClient);
            System.out.println("Client " + firstName + " " + lastName + " added successfully.");
        }
    }

    public void viewClientDetails(String clientID){
        Client c = clientById.get(clientID);
        if (c != null) {
            System.out.println("Client ID: " + c.getClientId());
            System.out.println("Name: " + c.getFirstName() + " " + c.getLastName());
            System.out.println("Email: " + c.getEmail());
            System.out.println("Phone: " + c.getPhoneNumber());            
        } else {
            System.out.println("Client not found!");
        }
    }

    public void createCheckingAccount(String clientID, double overdraft, String iban, double balance) {
        Client client = clientById.get(clientID);
        if (client != null) {
            BankAccount account = new CheckingAccount(overdraft, iban, balance);
            accountByIban.put(iban, account);
            client.addAccount(account);
            // Persist to database
            accountService.create(account);
            clientService.update(clientID, client);
            System.out.println("Checking account with iban " + iban + " added successfully");
        } else {
            System.out.println("ERROR: createCheckingAccount -> Client with ID " + clientID + " not found!");
        }
    }

    public void createSavingsAccount(String clientID, double interestRate, String iban, double balance) {
        Client client = clientById.get(clientID);
        if (client != null) {
            BankAccount account = new SavingsAccount(interestRate, iban, balance);
            accountByIban.put(iban, account);
            client.addAccount(account);
            // Persist to database
            accountService.create(account);
            clientService.update(clientID, client);
            System.out.println("Savings account with iban " + iban + " added successfully");
        } else {
            System.out.println("ERROR: createSavingsAccount -> Client with ID " + clientID + " not found!");
        }
    }

    public void deposit(String iban, double amount) {
        BankAccount account = accountByIban.get(iban);
        if (account != null) {
            account.deposit(amount);
            // Update account in database
            accountService.update(iban, account);
            System.out.println("Amount " + amount + " was successfully deposited into account: " + iban);
        } else {
            System.out.println("ERROR: deposit -> account " + iban + " was not found!");
        }
    }

    public void withdraw(String iban, double amount) {
        BankAccount account = accountByIban.get(iban);
        if (account != null) {
            if(account.withdraw(amount)) {
                // Update account in database
                accountService.update(iban, account);
                System.out.println("Amount " + amount + " was withdrawn from account: " + iban);
            } else {
                System.out.println("Insufficient funds!");
            }
        } else {
            System.out.println("ERROR: withdraw -> account " + iban + " was not found!");
        }
    }

    public void transfer(String ibanSender, String ibanReciever, double amount) {
        BankAccount accountSender = accountByIban.get(ibanSender);
        BankAccount accountReciever = accountByIban.get(ibanReciever);

        if (accountReciever != null && accountSender != null) {
            if (accountSender.withdraw(amount)) {
                accountReciever.deposit(amount);
                // Update both accounts in database
                accountService.update(ibanSender, accountSender);
                accountService.update(ibanReciever, accountReciever);
                System.out.println("Transfer succeeded");
            } else {
                System.out.println("Insufficient funds");
            }
        } else {
            System.out.println("ERROR: transfer -> one of the given accounts was not found");
        }
    }

    public void printAccountBalance(String iban) {
        BankAccount account = accountByIban.get(iban);
        if (account != null) {
            System.out.println("Account balance: " + account.getBalance());
        } else {
            System.out.println("Account not found!");
        }
    }

    public void printTransactionHistory(String iban) {
        BankAccount account = accountByIban.get(iban);
        if (account != null) {
            for (Transaction t : account.getTransactions()) {
                System.out.println(t.getType() + " of " + t.getAmount() + " at " + t.getTimestamp());
            }
        }
    }

    public void offerLoan(double amount, double interestRate, String clientID) {
        Client client = clientById.get(clientID);
        if (client != null) {
            String loanID = "LOAN" + System.currentTimeMillis();
            Loan loan = new Loan(loanID , amount, interestRate, client);
            loans.add(loan);
            // Persist loan to database
            loanService.create(loan);
            
            List<BankAccount> clientAccounts = client.getAccounts();
            if (clientAccounts != null && clientAccounts.size() != 0) {
                System.out.println("Client " + client.getFirstName() + client.getLastName() + " loaned " + amount + " with an interest rate of " + interestRate);
                BankAccount acc = clientAccounts.get(0);
                acc.deposit(amount);
                // Update the account in database
                accountService.update(acc.getIban(), acc);
            } else {
                System.out.println("Client doesn't have any active accounts");
            }
        }
    }

    public void linkCard(String cardNumber, String type, String iban) {
        BankAccount acc = accountByIban.get(iban);
        if (acc != null) {
            if (type.compareTo("CREDIT") == 0 || type.compareTo("DEBIT") == 0) {
                Card card = new Card(cardNumber, type, acc);
                // Persist card to database
                cardService.create(card);
                System.out.println("Card with number " + cardNumber + " of type " + type + " linked to account " + iban + " successfully.");
            } else {
                System.out.println("ERROR: card type not valid");
            }
        } else {
            System.out.println("Account not found!");
        }
    }
}
