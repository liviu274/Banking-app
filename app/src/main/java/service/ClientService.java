package service;

import java.util.List;

import model.Client;

public class ClientService {
    private final GenericCrudService<Client, String> crudService = GenericCrudService.getInstance(Client.class);
    private final LoggingService loggingService = LoggingService.getInstance();

    private static ClientService instance;

    private ClientService() {}

    public static synchronized ClientService getInstance() {
        if (instance == null) instance = new ClientService();
        return instance;
    }

    public void create(Client client) { 
        try {
            crudService.create(client);
            loggingService.logCreate("Client", client.getClientId(), true, 
                    "Client created: " + client.getFirstName() + " " + client.getLastName());
        } catch (Exception e) {
            loggingService.logCreate("Client", client.getClientId(), false, 
                    "Failed to create client: " + e.getMessage());
            throw e;
        }
    }
    
    public Client read(String id) { 
        try {
            Client client = crudService.read(id);
            String details = client != null ? 
                    "Client: " + client.getFirstName() + " " + client.getLastName() :
                    "Client not found";
            loggingService.logRead("Client", id, client != null, details);
            return client;
        } catch (Exception e) {
            loggingService.logRead("Client", id, false, "Error: " + e.getMessage());
            throw e;
        }
    }
    
    public List<Client> readAll() { 
        try {
            List<Client> clients = crudService.readAll(); 
            loggingService.logRead("Client", "ALL", true, 
                    "Retrieved " + clients.size() + " clients");
            return clients;
        } catch (Exception e) {
            loggingService.logRead("Client", "ALL", false, "Error: " + e.getMessage());
            throw e;
        }
    }
    
    public void update(String id, Client client) { 
        try {
            crudService.update(id, client);
            loggingService.logUpdate("Client", id, true, 
                    "Updated client: " + client.getFirstName() + " " + client.getLastName());
        } catch (Exception e) {
            loggingService.logUpdate("Client", id, false, "Error: " + e.getMessage());
            throw e;
        }
    }
    
    public void delete(String id) { 
        try {
            crudService.delete(id);
            loggingService.logDelete("Client", id, true, "Client deleted");
        } catch (Exception e) {
            loggingService.logDelete("Client", id, false, "Error: " + e.getMessage());
            throw e;
        }
    }
}