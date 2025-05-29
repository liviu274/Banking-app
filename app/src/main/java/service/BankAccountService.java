package service;

import java.util.List;

import model.BankAccount;

public class BankAccountService {
    private final GenericCrudService<BankAccount, String> crudService = GenericCrudService.getInstance(BankAccount.class);

    private static BankAccountService instance;

    private BankAccountService() {}

    public static synchronized BankAccountService getInstance() {
        if (instance == null) instance = new BankAccountService();
        return instance;
    }

    public void create(BankAccount account) { crudService.create(account); }
    public BankAccount read(String iban) { return crudService.read(iban); }
    public List<BankAccount> readAll() { 
        return crudService.readAll(); 
    }
    public void update(String iban, BankAccount account) { crudService.update(iban, account); }
    public void delete(String iban) { crudService.delete(iban); }
}
