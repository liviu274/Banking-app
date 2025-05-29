package service;

import java.util.List;

import model.Card;

public class CardService {
    private final GenericCrudService<Card, String> crudService = GenericCrudService.getInstance(Card.class);
    private final LoggingService loggingService = LoggingService.getInstance();

    private static CardService instance;

    private CardService() {}

    public static synchronized CardService getInstance() {
        if (instance == null) instance = new CardService();
        return instance;
    }

    public void create(Card card) {
        try {
            crudService.create(card);
            loggingService.logCreate("Card", card.getCardNumber(), true, 
                    "Card created of type: " + card.getType());
        } catch (Exception e) {
            loggingService.logCreate("Card", card.getCardNumber(), false, 
                    "Failed to create card: " + e.getMessage());
            throw e;
        }
    }
    
    public Card read(String cardNumber) {
        try {
            Card card = crudService.read(cardNumber);
            String details = card != null ? 
                    "Card type: " + card.getType() :
                    "Card not found";
            loggingService.logRead("Card", cardNumber, card != null, details);
            return card;
        } catch (Exception e) {
            loggingService.logRead("Card", cardNumber, false, "Error: " + e.getMessage());
            throw e;
        }
    }
    
    public List<Card> readAll() { 
        try {
            List<Card> cards = crudService.readAll(); 
            loggingService.logRead("Card", "ALL", true, 
                    "Retrieved " + cards.size() + " cards");
            return cards;
        } catch (Exception e) {
            loggingService.logRead("Card", "ALL", false, "Error: " + e.getMessage());
            throw e;
        }
    }
    
    public void update(String cardNumber, Card card) { 
        try {
            crudService.update(cardNumber, card);
            loggingService.logUpdate("Card", cardNumber, true, 
                    "Updated card type: " + card.getType());
        } catch (Exception e) {
            loggingService.logUpdate("Card", cardNumber, false, "Error: " + e.getMessage());
            throw e;
        }
    }
    
    public void delete(String cardNumber) { 
        try {
            crudService.delete(cardNumber);
            loggingService.logDelete("Card", cardNumber, true, "Card deleted");
        } catch (Exception e) {
            loggingService.logDelete("Card", cardNumber, false, "Error: " + e.getMessage());
            throw e;
        }
    }
}