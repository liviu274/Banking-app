package service;

import java.util.List;

import model.BankAccount;

public class BankAccountService {
    private final GenericCrudService<BankAccount, String> crudService = GenericCrudService.getInstance(BankAccount.class);
    private final LoggingService loggingService = LoggingService.getInstance();

    private static BankAccountService instance;

    private BankAccountService() {}

    public static synchronized BankAccountService getInstance() {
        if (instance == null) instance = new BankAccountService();
        return instance;
    }

    public void create(BankAccount account) {
        try {
            crudService.create(account);
            loggingService.logCreate("BankAccount", account.getIban(), true, 
                    "Account created with balance: " + account.getBalance());
        } catch (Exception e) {
            loggingService.logCreate("BankAccount", account.getIban(), false, 
                    "Failed to create account!" + e.getMessage());
            throw e;
        }
    }
    public BankAccount read(String iban) {
        try {
            BankAccount account = crudService.read(iban);
            String details = account != null ? 
                    "Account balance: " + account.getBalance() :
                    "Account not found";
            loggingService.logRead("BankAccount", iban, account != null, details);
            return account;
        } catch (Exception e) {
            loggingService.logRead("BankAccount", iban, false, "Error: " + e.getMessage());
            throw e;
        }
    }
    
    public List<BankAccount> readAll() { 
        try {
            List<BankAccount> accounts = crudService.readAll(); 
            loggingService.logRead("BankAccount", "ALL", true, 
                    "Retrieved " + accounts.size() + " accounts");
            return accounts;
        } catch (Exception e) {
            loggingService.logRead("BankAccount", "ALL", false, "Error: " + e.getMessage());
            throw e;
        }
    }
    
    public void update(String iban, BankAccount account) { 
        try {
            crudService.update(iban, account);
            loggingService.logUpdate("BankAccount", iban, true, 
                    "Updated account with new balance: " + account.getBalance());
        } catch (Exception e) {
            loggingService.logUpdate("BankAccount", iban, false, "Error: " + e.getMessage());
            throw e;
        }
    }
    
    public void delete(String iban) { 
        try {
            crudService.delete(iban);
            loggingService.logDelete("BankAccount", iban, true, "Account deleted");
        } catch (Exception e) {
            loggingService.logDelete("BankAccount", iban, false, "Error: " + e.getMessage());
            throw e;
        }
    }
}
