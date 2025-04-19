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
    private Map<String, Client> clientById = new HashMap<>();
    private TreeMap<String, BankAccount> accountByIban = new TreeMap<>();
    private List<Loan> loans = new ArrayList<>();
    
    private static BankService instance;

    private BankService() {
        // Private constructor to prevent instantiation
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
            Client newClient = new Client(clientId, firstName, lastName, email, phoneNumber);
            clientById.put(clientId, newClient);
            System.out.println("Client " + firstName + " " + lastName + " added successfully.");
        }
    }

    public void createCheckingAccount(String clientID, double overdraft, String iban, double balance)
    {
        Client client = clientById.get(clientID);
        if (client != null)
        {
            BankAccount account = new CheckingAccount(overdraft, iban, balance);
            accountByIban.put(iban, account);
            client.addAccount(account);
            System.out.println("Checking account with iban " + iban + " added successfully");
        }
        else
        {
            System.out.println("ERROR: createCheckingAccount -> Client with ID " + clientID + " not found!");
        }
    }

    public void createSavingsAccount(String clientID, double interestRate, String iban, double balance)
    {
        Client client = clientById.get(clientID);
        if (client != null)
        {
            BankAccount account = new SavingsAccount(interestRate, iban, balance);
            accountByIban.put(iban, account);
            client.addAccount(account);
            System.out.println("Savings account with iban " + iban + " added successfully");
        }
        else
        {
            System.out.println("ERROR: createSavingsAccount -> Client with ID " + clientID + " not found!");
        }
    }

    public void deposit(String iban, double amount)
    {
        BankAccount account = accountByIban.get(iban);
        if (account != null)
        {
            account.deposit(amount);
            System.out.println("Amount " + amount + " was successfully deposited into account: " + iban);
        }
        else
        {
            System.out.println("ERROR: deposit -> account " + iban + " was not found!");
        }
    }

    public void withdraw(String iban, double amount)
    {
        BankAccount account = accountByIban.get(iban);
        if (account != null)
        {
            if(account.withdraw(amount))
                System.out.println("Amount " + amount + " was withdrawed from account: " + iban);
            else
                System.out.println("Insufficient funds!");
        }
        else
        {
            System.out.println("ERROR: withdraw -> account " + iban + " was not found!");
        }
    }

    public void transfer(String ibanSender, String ibanReciever, double amount)
    {
        BankAccount accountSender = accountByIban.get(ibanSender);
        BankAccount accountReciever = accountByIban.get(ibanReciever);

        if (accountReciever != null && accountSender != null)
        {
            if (accountSender.withdraw(amount))
            {
                accountReciever.deposit(amount);
                System.out.println("Transfer succeeded");
            }
            else
            {
                System.out.println("Insufficient funds");
            }
        }
        else
        {
            System.out.println("ERROR: transfer -> one of the given accounts was not found");
        }
    }

    public void printAccountBalance(String iban)
    {
        BankAccount account = accountByIban.get(iban);
        if (account != null)
        {
            System.out.println("Account balance: " + account.getBalance());
        }
        else
            System.out.println("Account not found!");
    }

    public void printTransactionHistory(String iban)
    {
        BankAccount account = accountByIban.get(iban);
        if (account != null)
        {
            for (Transaction t : account.getTransactions()) {
                System.out.println(t.getType() + " of " + t.getAmount() + " at " + t.getTimestamp());
            }
        }
    }

    public void offerLoan(double amount, double interestRate, String clientID)
    {
        Client client = clientById.get(clientID);
        if (client != null)
        {
            Loan loan = new Loan(amount, interestRate, client);
            List<BankAccount> clientAccounts = client.getAccounts();
            if (clientAccounts != null && clientAccounts.size() != 0)
            {
                System.out.println("Client " + client.getFirstName() + client.getLastName() + " loaned " + amount + " with an interest rate of " + interestRate);
                BankAccount acc = clientAccounts.get(0);
                acc.deposit(amount);
            }
            else
            {
                System.out.println("Client doesn't have any active accounts");
            }
        }
    }

    public void linkCard(String cardNumber, String type, String iban)
    {
        BankAccount acc = accountByIban.get(iban);
        if (acc != null)
        {
            if (type.compareTo("CREDIT") == 0 || type.compareTo("DEBIT") == 0)
            {
                Card card = new Card(cardNumber, type, acc);
                System.out.println("Card with number " + cardNumber + " of type " + type + " linked to account " + iban + " successfully.");
            }
            else
            {
                System.out.println("ERRO: card type not valid");
            }
        }
        else
        {
            System.out.println("Account not found!");
        }
    }
}
