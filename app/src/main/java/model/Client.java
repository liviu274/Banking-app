package model;

import java.util.ArrayList;
import java.util.List;

public class Client {
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String clientId;
    private List<BankAccount> accounts;

    public Client(String firstName, String lastName, String email, String phoneNumber, String clientId) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.clientId = clientId;
        this.accounts = new ArrayList<>(); // Initialize the accounts list
    }

    public Client() {
        this.accounts = new ArrayList<>();
    }

    public String getFirstName() {
        return firstName;
    }
    public String getLastName() {
        return lastName;
    }
    public String getEmail() {
        return email;
    }
    public String getPhoneNumber() {
        return phoneNumber;
    }
    public List<BankAccount> getAccounts() {
        return accounts;
    }
    public String getClientId() {
        return clientId;
    }

    public void addAccount(BankAccount account) {
        this.accounts.add(account);
    }
}
