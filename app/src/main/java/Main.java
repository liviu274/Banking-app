import service.BankService;
import service.InteractiveMenuService;

public class Main {
    public static void main(String[] args) {
        BankService bankService = BankService.getInstance();
        InteractiveMenuService menuService = InteractiveMenuService.getInstance();
        menuService.start();
    }
}