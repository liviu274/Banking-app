package service;

import java.util.List;

import model.Loan;

public class LoanService {
    private final GenericCrudService<Loan, String> crudService = GenericCrudService.getInstance(Loan.class);
    private final LoggingService loggingService = LoggingService.getInstance();

    private static LoanService instance;

    private LoanService() {}

    public static synchronized LoanService getInstance() {
        if (instance == null) instance = new LoanService();
        return instance;
    }

    public void create(Loan loan) {
        try {
            crudService.create(loan);
            loggingService.logCreate("Loan", loan.getId(), true, 
                    "Loan created with amount: " + loan.getAmount() + ", interest rate: " + loan.getInterestRate());
        } catch (Exception e) {
            loggingService.logCreate("Loan", loan.getId(), false, 
                    "Failed to create loan: " + e.getMessage());
            throw e;
        }
    }
    
    public Loan read(String id) {
        try {
            Loan loan = crudService.read(id);
            String details = loan != null ? 
                    "Loan amount: " + loan.getAmount() + ", interest rate: " + loan.getInterestRate() :
                    "Loan not found";
            loggingService.logRead("Loan", id, loan != null, details);
            return loan;
        } catch (Exception e) {
            loggingService.logRead("Loan", id, false, "Error: " + e.getMessage());
            throw e;
        }
    }
    
    public List<Loan> readAll() { 
        try {
            List<Loan> loans = crudService.readAll(); 
            loggingService.logRead("Loan", "ALL", true, 
                    "Retrieved " + loans.size() + " loans");
            return loans;
        } catch (Exception e) {
            loggingService.logRead("Loan", "ALL", false, "Error: " + e.getMessage());
            throw e;
        }
    }
    
    public void update(String id, Loan loan) { 
        try {
            crudService.update(id, loan);
            loggingService.logUpdate("Loan", id, true, 
                    "Updated loan amount: " + loan.getAmount());
        } catch (Exception e) {
            loggingService.logUpdate("Loan", id, false, "Error: " + e.getMessage());
            throw e;
        }
    }
    
    public void delete(String id) { 
        try {
            crudService.delete(id);
            loggingService.logDelete("Loan", id, true, "Loan deleted");
        } catch (Exception e) {
            loggingService.logDelete("Loan", id, false, "Error: " + e.getMessage());
            throw e;
        }
    }
}