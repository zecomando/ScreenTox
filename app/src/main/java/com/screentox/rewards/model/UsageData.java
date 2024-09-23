package com.screentox.rewards.model;

public class UsageData {
    private int unlocks;
    private long totalUsage;
    private String appUsage;

    // Construtor padrão
    public UsageData() {}

    public UsageData(int unlocks, long totalUsage, String appUsage) {
        this.unlocks = unlocks;
        this.totalUsage = totalUsage;
        this.appUsage = appUsage;
    }

    public int getUnlocks() {
        return unlocks;
    }

    public void setUnlocks(int unlocks) {
        this.unlocks = unlocks;
    }

    public long getTotalUsage() {
        return totalUsage;
    }

    public void setTotalUsage(long totalUsage) {
        this.totalUsage = totalUsage;
    }

    public String getAppUsage() {
        return appUsage;
    }

    public void setAppUsage(String appUsage) {
        this.appUsage = appUsage;
    }
}
