package service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LoggingService {
    private static final String LOG_FILE = "banking_operations.csv";
    private static LoggingService instance;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    private LoggingService() {
        // Initialize the CSV file with headers if it doesn't exist
        File file = new File(LOG_FILE);
        if (!file.exists()) {
            try (PrintWriter writer = new PrintWriter(new FileWriter(file, true))) {
                writer.println("Timestamp,Operation,EntityType,EntityId,Status,Details");
            } catch (IOException e) {
                System.err.println("Failed to initialize log file: " + e.getMessage());
            }
        }
    }
    
    public static synchronized LoggingService getInstance() {
        if (instance == null) {
            instance = new LoggingService();
        }
        return instance;
    }
    
    public void logOperation(String operation, String entityType, String entityId, boolean success, String details) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(LOG_FILE, true))) {
            StringBuilder logEntry = new StringBuilder();
            
            // Timestamp
            logEntry.append(LocalDateTime.now().format(formatter)).append(",");
            
            // Operation type (CREATE, READ, UPDATE, DELETE)
            logEntry.append(operation).append(",");
            
            // Entity type (Client, Account, Card, Loan)
            logEntry.append(entityType).append(",");
            
            // Entity ID
            logEntry.append(entityId).append(",");
            
            // Success or failure
            logEntry.append(success ? "SUCCESS" : "FAILURE").append(",");
            
            // Additional details (escape commas in details)
            logEntry.append("\"").append(details.replace("\"", "\"\"")).append("\"");
            
            writer.println(logEntry);
        } catch (IOException e) {
            System.err.println("Failed to log operation: " + e.getMessage());
        }
    }
    
    // Convenience methods for each CRUD operation
    public void logCreate(String entityType, String entityId, boolean success, String details) {
        logOperation("CREATE", entityType, entityId, success, details);
    }
    
    public void logRead(String entityType, String entityId, boolean success, String details) {
        logOperation("READ", entityType, entityId, success, details);
    }
    
    public void logUpdate(String entityType, String entityId, boolean success, String details) {
        logOperation("UPDATE", entityType, entityId, success, details);
    }
    
    public void logDelete(String entityType, String entityId, boolean success, String details) {
        logOperation("DELETE", entityType, entityId, success, details);
    }
}