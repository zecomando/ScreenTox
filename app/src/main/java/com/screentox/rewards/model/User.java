package com.screentox.rewards.model;

import com.google.gson.annotations.SerializedName;

public class User {
    private int id;
    private String name;
    private String username;
    private String email;
    private String password;
    private String deviceInfo;
    private int status;
    private int referralsMade;
    private int invitedBy;
    private String timezone;

    @SerializedName("balance_points")
    private float balancePoints;

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getDeviceInfo() { return deviceInfo; }
    public void setDeviceInfo(String deviceInfo) { this.deviceInfo = deviceInfo; }

    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }

    public int getReferralsMade() { return referralsMade; }
    public void setReferralsMade(int referralsMade) { this.referralsMade = referralsMade; }

    public int getInvitedBy() { return invitedBy; }
    public void setInvitedBy(int invitedBy) { this.invitedBy = invitedBy; }

    public String getTimezone() { return timezone; }
    public void setTimezone(String timezone) { this.timezone = timezone; }

    public float getBalancePoints() { return balancePoints; }
    public void setBalancePoints(float balancePoints) { this.balancePoints = balancePoints; }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", balancePoints=" + balancePoints +
                '}';
    }
}
