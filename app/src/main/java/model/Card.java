package model;

public class Card {
    private String cardNumber;
    private String type; // Credit or Debit
    private BankAccount linkedAccount;

    public Card(String cardNumber, String type, BankAccount linkedAccount) {
        this.cardNumber = cardNumber;
        this.type = type;
        this.linkedAccount = linkedAccount;
    }

    public Card() {
        
    }

    public String getCardNumber() { return cardNumber; }
    public String getType() { return type; }
    public BankAccount getLinkedAccount() { return linkedAccount; }
    
}
