package com.screentox.rewards.model;

public class UnlockDevice {
    private int id;
    private String timestamp;
    private int userId;

    // Construtor
    public UnlockDevice(int userId) {
        this.userId = userId;
        this.timestamp = getCurrentTimestamp();
    }

    // Getter e Setter
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    // Método para obter o timestamp atual
    private String getCurrentTimestamp() {
        return java.time.LocalDateTime.now().toString();
    }
}
