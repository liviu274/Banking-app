package model;

public class Loan {
    private String loanId;
    private double amount;
    private double interestRate;
    private Client client;

    public Loan(String loanId, double amount, double interestRate, Client client) {
        this.loanId = loanId;
        this.amount = amount;
        this.interestRate = interestRate;
        this.client = client;
    }

        public Loan()
    {
        
    }
    
    // Add getter for ID (this is what GenericCrudService is looking for)
    public String getId() {
        return loanId;
    }
    
    // Other getters
    public double getAmount() {
        return amount;
    }
    
    public double getInterestRate() {
        return interestRate;
    }
    
    public Client getClient() {
        return client;
    }

    public void sendNotification() {
        System.out.println("Dear " + client.getFirstName() + client.getLastName() + " we kindly remind you that you still have " +
         this.amount * this.interestRate + " left to pay on your loan");
    }
}
