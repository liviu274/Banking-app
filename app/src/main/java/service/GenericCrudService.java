package service;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.BankAccount;
import model.Branch;
import model.Card;
import model.CheckingAccount;
import model.Client;
import model.Loan;
import model.SavingsAccount;
import model.Transaction;

public class GenericCrudService<T, ID> implements CrudService<T, ID> {
    private static final String URL = "jdbc:mysql://localhost:3306/banking_app";
    private static final String USER = "bankadmin";
    private static final String PASSWORD = "admin";
    
    private final Class<T> entityClass;
    private static final Map<String, GenericCrudService<?, ?>> instances = new HashMap<>();

    private GenericCrudService(Class<T> clazz) {
        this.entityClass = clazz;
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
        String tableName = entityClass.getSimpleName().toLowerCase();
        StringBuilder createTableSQL = new StringBuilder();
        createTableSQL.append("CREATE TABLE IF NOT EXISTS ").append(tableName).append(" (");
        
        // Get all fields from the class and its superclasses
        List<Field> fields = getAllFields(entityClass);
        String primaryKeyField = getPrimaryKeyFieldName();
        
        // Add columns for each field
        for (Field field : fields) {
            String fieldName = field.getName();
            String sqlType = getSqlType(field.getType());
            
            if (sqlType != null) {
                createTableSQL.append(fieldName).append(" ").append(sqlType);
                
                // Add primary key constraint
                if (fieldName.equals(primaryKeyField)) {
                    createTableSQL.append(" PRIMARY KEY");
                }
                
                createTableSQL.append(", ");
            }
        }
        
        // Remove the last comma and space
        if (createTableSQL.toString().endsWith(", ")) {
            createTableSQL.setLength(createTableSQL.length() - 2);
        }
        
        createTableSQL.append(")");
        
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(createTableSQL.toString());
            System.out.println("Table " + tableName + " created or already exists");
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create table: " + tableName, e);
        }
    }
    
    private List<Field> getAllFields(Class<?> clazz) {
        List<Field> fields = new ArrayList<>();
        Class<?> current = clazz;
        
        // Get fields from the class and all its superclasses
        while (current != null && !current.equals(Object.class)) {
            fields.addAll(Arrays.asList(current.getDeclaredFields()));
            current = current.getSuperclass();
        }
        
        return fields;
    }
    
    private String getSqlType(Class<?> type) {
        if (type.equals(String.class)) {
            return "VARCHAR(255)";
        } else if (type.equals(int.class) || type.equals(Integer.class)) {
            return "INT";
        } else if (type.equals(double.class) || type.equals(Double.class)) {
            return "DOUBLE";
        } else if (type.equals(boolean.class) || type.equals(Boolean.class)) {
            return "BOOLEAN";
        } else if (type.equals(LocalDateTime.class)) {
            return "DATETIME";
        } else if (type.equals(long.class) || type.equals(Long.class)) {
            return "BIGINT";
        } else if (type.equals(float.class) || type.equals(Float.class)) {
            return "FLOAT";
        } else if (Collection.class.isAssignableFrom(type)) {
            // We'll handle collections separately - they need a join table
            return null;
        } else if (!type.isPrimitive() && !type.getName().startsWith("java.")) {
            // This is likely a reference to another entity - store as foreign key
            return "VARCHAR(255)";
        }
        return null;
    }
    
    private String getPrimaryKeyFieldName() {
        // Default primary key field names for each entity
        if (entityClass.equals(Client.class)) return "clientId";
        if (entityClass.equals(BankAccount.class) || 
            entityClass.equals(SavingsAccount.class) || 
            entityClass.equals(CheckingAccount.class)) return "iban";
        if (entityClass.equals(Card.class)) return "cardNumber";
        if (entityClass.equals(Loan.class)) return "loanId";
        if (entityClass.equals(Branch.class)) return "branchId";
        if (entityClass.equals(Transaction.class)) return "transactionId";
        
        // Default to "id" if no specific field is found
        return "id";
    }

    @Override
    public void create(T entity) {
        try {
            String tableName = entityClass.getSimpleName().toLowerCase();
            List<Field> fields = getAllFields(entityClass);
            
            // Build INSERT statement
            StringBuilder columns = new StringBuilder();
            StringBuilder placeholders = new StringBuilder();
            List<Object> values = new ArrayList<>();
            
            for (Field field : fields) {
                field.setAccessible(true);
                String fieldName = field.getName();
                Object value = field.get(entity);
                
                // Skip null values and collections
                if (value == null || Collection.class.isAssignableFrom(field.getType())) {
                    continue;
                }
                
                // Handle reference to another entity - extract its ID
                if (!field.getType().isPrimitive() && !field.getType().getName().startsWith("java.") &&
                    !field.getType().equals(String.class)) {
                    value = extractIdFromEntity(value);
                }
                
                columns.append(fieldName).append(", ");
                placeholders.append("?, ");
                values.add(value);
            }
            
            // Remove the last comma and space
            if (columns.length() > 0) {
                columns.setLength(columns.length() - 2);
                placeholders.setLength(placeholders.length() - 2);
            }
            
            String insertSQL = "INSERT INTO " + tableName + " (" + columns + ") VALUES (" + placeholders + ")";
            
            try (Connection conn = getConnection();
                 PreparedStatement stmt = conn.prepareStatement(insertSQL)) {
                
                // Set parameters
                for (int i = 0; i < values.size(); i++) {
                    stmt.setObject(i + 1, values.get(i));
                }
                
                stmt.executeUpdate();
                System.out.println("Entity created in " + tableName);
            }
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to create entity", e);
        }
    }
    
    private Object extractIdFromEntity(Object entity) throws Exception {
        // Try common ID getter methods
        for (String methodName : new String[]{"getId", "getClientId", "getIban", "getCardNumber", "getLoanId"}) {
            try {
                return entity.getClass().getMethod(methodName).invoke(entity);
            } catch (NoSuchMethodException ignored) {
                // Try next method
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private ID extractId(T entity) throws Exception {
        String primaryKeyField = getPrimaryKeyFieldName();
        
        // Try to get the field directly
        for (Field field : getAllFields(entityClass)) {
            if (field.getName().equals(primaryKeyField)) {
                field.setAccessible(true);
                return (ID) field.get(entity);
            }
        }
        
        // Try common getter methods
        for (String methodName : new String[]{"getId", "getClientId", "getIban", "getCardNumber", "getLoanId", "getTransactionId", "getBranchId"}) {
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
        String primaryKeyField = getPrimaryKeyFieldName();
        String selectSQL = "SELECT * FROM " + tableName + " WHERE " + primaryKeyField + " = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(selectSQL)) {
            stmt.setObject(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                T entity = entityClass.getConstructor().newInstance();
                List<Field> fields = getAllFields(entityClass);
                
                for (Field field : fields) {
                    field.setAccessible(true);
                    String fieldName = field.getName();
                    
                    try {
                        // Skip collections - they would be loaded separately
                        if (Collection.class.isAssignableFrom(field.getType())) {
                            continue;
                        }
                        
                        Object value = rs.getObject(fieldName);
                        
                        // Handle references to other entities
                        if (!field.getType().isPrimitive() && !field.getType().getName().startsWith("java.") &&
                            !field.getType().equals(String.class)) {
                            
                            // For simplicity, just create an empty instance for now
                            // In a real app you'd load the referenced entity
                            value = field.getType().getConstructor().newInstance();
                        }
                        
                        if (value != null) {
                            field.set(entity, value);
                        }
                    } catch (SQLException e) {
                        // Field not found in database - just skip it
                    }
                }
                
                return entity;
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
                T entity = entityClass.getConstructor().newInstance();
                List<Field> fields = getAllFields(entityClass);
                
                for (Field field : fields) {
                    field.setAccessible(true);
                    String fieldName = field.getName();
                    
                    try {
                        // Skip collections - they would be loaded separately
                        if (Collection.class.isAssignableFrom(field.getType())) {
                            continue;
                        }
                        
                        Object value = rs.getObject(fieldName);
                        
                        // Handle references to other entities
                        if (!field.getType().isPrimitive() && !field.getType().getName().startsWith("java.") &&
                            !field.getType().equals(String.class)) {
                            
                            // For simplicity, just create an empty instance for now
                            value = field.getType().getConstructor().newInstance();
                        }
                        
                        if (value != null) {
                            field.set(entity, value);
                        }
                    } catch (SQLException e) {
                        // Field not found in database - just skip it
                    }
                }
                
                results.add(entity);
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
            String primaryKeyField = getPrimaryKeyFieldName();
            List<Field> fields = getAllFields(entityClass);
            
            // Build UPDATE statement
            StringBuilder setClause = new StringBuilder();
            List<Object> values = new ArrayList<>();
            
            for (Field field : fields) {
                field.setAccessible(true);
                String fieldName = field.getName();
                
                // Skip the ID field and collections
                if (fieldName.equals(primaryKeyField) || Collection.class.isAssignableFrom(field.getType())) {
                    continue;
                }
                
                Object value = field.get(entity);
                
                // Skip null values
                if (value == null) {
                    continue;
                }
                
                // Handle reference to another entity - extract its ID
                if (!field.getType().isPrimitive() && !field.getType().getName().startsWith("java.") &&
                    !field.getType().equals(String.class)) {
                    value = extractIdFromEntity(value);
                }
                
                setClause.append(fieldName).append(" = ?, ");
                values.add(value);
            }
            
            // Remove the last comma and space
            if (setClause.length() > 0) {
                setClause.setLength(setClause.length() - 2);
            }
            
            String updateSQL = "UPDATE " + tableName + " SET " + setClause + " WHERE " + primaryKeyField + " = ?";
            
            try (Connection conn = getConnection();
                 PreparedStatement stmt = conn.prepareStatement(updateSQL)) {
                
                // Set parameters
                for (int i = 0; i < values.size(); i++) {
                    stmt.setObject(i + 1, values.get(i));
                }
                
                // Set the ID as the last parameter
                stmt.setObject(values.size() + 1, id);
                
                stmt.executeUpdate();
                System.out.println("Entity updated in " + tableName);
            }
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to update entity", e);
        }
    }

    @Override
    public void delete(ID id) {
        String tableName = entityClass.getSimpleName().toLowerCase();
        String primaryKeyField = getPrimaryKeyFieldName();
        String deleteSQL = "DELETE FROM " + tableName + " WHERE " + primaryKeyField + " = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(deleteSQL)) {
            stmt.setObject(1, id);
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("Entity deleted from " + tableName);
            } else {
                System.out.println("No entity found to delete with ID: " + id);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete entity", e);
        }
    }
}