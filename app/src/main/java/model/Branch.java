package model;

import java.util.HashSet;
import java.util.Set;

public class Branch {
    private String name;
    private Set<Client> clients;

    public Branch(String name) {
        this.name = name;
        this.clients = new HashSet<>();
    }

    public String getName() { return name; }
    public Set<Client> getClients() { return clients; }

    public void addClient(Client client) {
        clients.add(client);
    }
}
