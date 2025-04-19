package model;

public class SavingsAccount extends BankAccount {
    private double interestRate;

    public SavingsAccount(double balance, String iban, double interestRate) {
        super(iban, balance);
        this.interestRate = interestRate;
    }

    public double getInterestRate() { return interestRate; }
}
