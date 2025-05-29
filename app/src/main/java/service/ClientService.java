package service;

import java.util.List;

import model.Client;

public class ClientService {
    private final GenericCrudService<Client, String> crudService = GenericCrudService.getInstance(Client.class);

    private static ClientService instance;

    private ClientService() {}

    public static synchronized ClientService getInstance() {
        if (instance == null) instance = new ClientService();
        return instance;
    }

    public void create(Client client) { crudService.create(client); }
    public Client read(String id) { return crudService.read(id); }
    public List<Client> readAll() { 
        return crudService.readAll(); 
    }
    public void update(String id, Client client) { crudService.update(id, client); }
    public void delete(String id) { crudService.delete(id); }
}
