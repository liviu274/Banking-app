package service;

import java.util.List;

import model.Loan;

public class LoanService {
    private final GenericCrudService<Loan, String> crudService = GenericCrudService.getInstance(Loan.class);

    private static LoanService instance;

    private LoanService() {}

    public static synchronized LoanService getInstance() {
        if (instance == null) instance = new LoanService();
        return instance;
    }

    public void create(Loan loan) { crudService.create(loan); }
    public Loan read(String id) { return crudService.read(id); }
    public List<Loan> readAll() { 
        return crudService.readAll(); 
    }
    public void update(String id, Loan loan) { crudService.update(id, loan); }
    public void delete(String id) { crudService.delete(id); }
}
