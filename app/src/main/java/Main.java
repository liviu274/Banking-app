import service.BankService;
import service.InteractiveMenuService;

public class Main {
    public static void main(String[] args) {
        BankService bankService = BankService.getInstance();
        InteractiveMenuService menuService = InteractiveMenuService.getInstance();
        menuService.start();

        // bankService.addClient("C001", "John", "Doe", "john.doe@example.com", "1234567890");
        // bankService.addClient("C002", "Jane", "Smith", "jane.smith@example.com", "0987654321");
        // bankService.addClient("C003", "Alice", "Johnson", "alice.johnson@example.com", "1122334455");
        // bankService.addClient("C004", "Bob", "Brown", "bob.brown@example.com", "5566778899");

        // bankService.createCheckingAccount("C001", 500.0, "IBAN001", 1000.0);
        // bankService.createCheckingAccount("C002", 300.0, "IBAN002", 1500.0);

        // bankService.createSavingsAccount("C003", 0.02, "IBAN003", 2000.0);
        // bankService.createSavingsAccount("C004", 0.03, "IBAN004", 2500.0);

        // bankService.deposit("IBAN001", 200.0);
        // bankService.withdraw("IBAN001", 100.0);

        // bankService.deposit("IBAN002", 300.0);
        // bankService.withdraw("IBAN002", 150.0);

        // bankService.deposit("IBAN003", 400.0);
        // bankService.withdraw("IBAN003", 200.0);

        // bankService.deposit("IBAN004", 500.0);
        // bankService.withdraw("IBAN004", 250000.0); // Insufficient funds

        // bankService.transfer("IBAN001", "IBAN002", 500.0); // success
        // bankService.transfer("IBAN002", "IBAN001", 50000.0); // Insufficient funds

        // System.out.println("Account 1:");
        // bankService.printAccountBalance("IBAN001");
        // bankService.printTransactionHistory("IBAN001");
        // System.out.println("Account 2:");
        // bankService.printTransactionHistory("IBAN002");
        // System.out.println("Checking if error message works");
        // bankService.printAccountBalance("invalidiban");

        // bankService.offerLoan(1000.0, 10.0, "C001");
        // bankService.printAccountBalance("IBAN001");
        // bankService.printTransactionHistory("IBAN001");

        // bankService.linkCard("1234567812345678", "DEBIT", "IBAN001");
    }
}