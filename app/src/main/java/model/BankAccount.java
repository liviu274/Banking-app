package model;

import java.util.ArrayList;
import java.util.List;

public class BankAccount {
    protected String iban;
    protected double balance;
    protected List<Transaction> transactions;

    public BankAccount(String iban, double balance) {
        this.iban = iban;
        this.balance = balance;
        this.transactions = new ArrayList<>();
    }

    public BankAccount() {
        this.transactions = new ArrayList<>();
    }

    public String getIban() { return iban; }
    public double getBalance() { return balance; }
    public List<Transaction> getTransactions() { return transactions; }

    public void deposit(double amount) {
        this.balance += amount;
        this.transactions.add(new Transaction("Deposit", amount));
    }

    public boolean withdraw(double amount) {
        if (this.balance > amount)
        {
            balance -= amount;
            transactions.add(new Transaction("Withdraw", amount));
            return true;
        }
        else
            return false;
    }
}
