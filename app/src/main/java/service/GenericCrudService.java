package service;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GenericCrudService<T, ID> implements CrudService<T, ID> {
    private static final String URL = "jdbc:mysql://localhost:3306/banking_app";
    private static final String USER = "bankadmin";
    private static final String PASSWORD = "admin";
    
    private final Class<T> entityClass;
    private static final Map<String, GenericCrudService<?, ?>> instances = new HashMap<>();

    private GenericCrudService(Class<T> clazz) {
        this.entityClass = clazz;
        // Ensure table exists for this entity
        createTableIfNotExists();
    }

    @SuppressWarnings("unchecked")
    public static synchronized <T, ID> GenericCrudService<T, ID> getInstance(Class<T> clazz) {
        String key = clazz.getName();
        if (!instances.containsKey(key)) {
            instances.put(key, new GenericCrudService<>(clazz));
        }
        return (GenericCrudService<T, ID>) instances.get(key);
    }
    
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
    
    private void createTableIfNotExists() {
        // This is a simplified version - in a real application, you would
        // define table schema properly with annotations or configuration
        String tableName = entityClass.getSimpleName().toLowerCase();
        String createTableSQL = "CREATE TABLE IF NOT EXISTS " + tableName + " ("
                + "id VARCHAR(255) PRIMARY KEY"
                + ")";
                
        try (Connection conn = getConnection();
            Statement stmt = conn.createStatement()) {
            stmt.execute(createTableSQL);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create table: " + tableName, e);
        }
    }

    @Override
    public void create(T entity) {
        try {
            String tableName = entityClass.getSimpleName().toLowerCase();
            ID id = extractId(entity);
            
            // Use INSERT IGNORE to silently skip duplicates
            String insertSQL = "INSERT IGNORE INTO " + tableName + " (id) VALUES (?)";
            
            try (Connection conn = getConnection();
                 PreparedStatement stmt = conn.prepareStatement(insertSQL)) {
                stmt.setObject(1, id);
                stmt.executeUpdate();
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to create entity", e);
        }
    }

    @SuppressWarnings("unchecked")
    private ID extractId(T entity) throws Exception {
        // Try common ID getter methods
        for (String methodName : new String[]{"getClientId", "getId", "getIban", "getCardNumber"}) {
            try {
                return (ID) entity.getClass().getMethod(methodName).invoke(entity);
            } catch (NoSuchMethodException ignored) {
                // Try next method
            }
        }
        throw new RuntimeException("No ID getter method found in " + entityClass.getName());
    }

    @Override
    public T read(ID id) {
        String tableName = entityClass.getSimpleName().toLowerCase();
        String selectSQL = "SELECT * FROM " + tableName + " WHERE id = ?";
        
        try (Connection conn = getConnection();
            PreparedStatement stmt = conn.prepareStatement(selectSQL)) {
            stmt.setObject(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                // This is simplified - you would need to map all columns to object properties
                return entityClass.getConstructor().newInstance();
            }
            return null;
        } catch (Exception e) {
            throw new RuntimeException("Failed to read entity", e);
        }
    }

    @Override
    public List<T> readAll() {
        List<T> results = new ArrayList<>();
        String tableName = entityClass.getSimpleName().toLowerCase();
        String selectAllSQL = "SELECT * FROM " + tableName;
        
        try (Connection conn = getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(selectAllSQL)) {
            
            while (rs.next()) {
                // This is simplified - you would need to map all columns to object properties
                results.add(entityClass.getConstructor().newInstance());
            }
            return results;
        } catch (Exception e) {
            throw new RuntimeException("Failed to read all entities", e);
        }
    }

    @Override
    public void update(ID id, T entity) {
        try {
            String tableName = entityClass.getSimpleName().toLowerCase();
            // Basic implementation - would need to be enhanced for real use
            String updateSQL = "UPDATE " + tableName + " SET id = ? WHERE id = ?";
            
            try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(updateSQL)) {
                stmt.setObject(1, extractId(entity));
                stmt.setObject(2, id);
                stmt.executeUpdate();
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to update entity", e);
        }
    }

    @Override
    public void delete(ID id) {
        String tableName = entityClass.getSimpleName().toLowerCase();
        String deleteSQL = "DELETE FROM " + tableName + " WHERE id = ?";
        
        try (Connection conn = getConnection();
            PreparedStatement stmt = conn.prepareStatement(deleteSQL)) {
            stmt.setObject(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete entity", e);
        }
    }
}
