package com.screentox.rewards.model;

public class UnlockScreen {
    private int userId;
    private String type; // 'News', 'Ad', 'Bet', 'Survey', 'Quiz'
    private String action; // 'No action', 'Unlock Screen', 'Click Ad', 'Answer Survey', 'Make Bet'

    public UnlockScreen(int userId, String type, String action) {
        this.userId = userId;
        this.type = type;
        this.action = action;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }
}
