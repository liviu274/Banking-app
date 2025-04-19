package model;

public class Loan {
    private double amount;
    private double interestRate;
    private Client client;

    public Loan(double amount, double interestRate, Client client) {
        this.amount = amount;
        this.interestRate = interestRate;
        this.client = client;
    }

    public double getAmount() { return amount; }
    public double getInterestRate() { return interestRate; }
    public Client getClient() { return client; }

    public void sendNotification() {
        System.out.println("Dear " + client.getFirstName() + client.getLastName() + " we kindly remind you that you still have " +
         this.amount * this.interestRate + " left to pay on your loan");
    }
}
