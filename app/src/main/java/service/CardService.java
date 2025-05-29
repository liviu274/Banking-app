package service;

import java.util.List;

import model.Card;

public class CardService {
    private final GenericCrudService<Card, String> crudService = GenericCrudService.getInstance(Card.class);

    private static CardService instance;

    private CardService() {}

    public static synchronized CardService getInstance() {
        if (instance == null) instance = new CardService();
        return instance;
    }

    public void create(Card card) { crudService.create(card); }
    public Card read(String cardNumber) { return crudService.read(cardNumber); }
    public List<Card> readAll() { 
        return crudService.readAll(); 
    }
    public void update(String cardNumber, Card card) { crudService.update(cardNumber, card); }
    public void delete(String cardNumber) { crudService.delete(cardNumber); }
}
