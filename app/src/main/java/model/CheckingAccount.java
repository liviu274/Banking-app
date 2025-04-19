package model;

public class CheckingAccount extends BankAccount{
    private double overdraft;

    public CheckingAccount(double overdraft, String iban, double balance) {
        super(iban, balance);
        this.overdraft = overdraft;
    }

    @Override
    public boolean withdraw(double amount) {
        if (this.balance + this.overdraft > amount)
        {
            balance -= amount;
            transactions.add(new Transaction("Withdraw", amount));
            return true;
        }
        else
            return false;
    }
    public double getOverdraft() {
        return overdraft;
    }
    public void updateOverdraft(double newOverdraft) {
        this.overdraft = newOverdraft;
    }
}
