package com.example.debttracker;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "debts")
public class Debt {
    @PrimaryKey(autoGenerate = true)
    private int id;
    
    private String personName;
    private double amount;
    private String description;
    private String date;
    private boolean isOwedToMe; // true if someone owes me, false if I owe someone
    private boolean isPaid;

    public Debt(String personName, double amount, String description, String date, boolean isOwedToMe) {
        this.personName = personName;
        this.amount = amount;
        this.description = description;
        this.date = date;
        this.isOwedToMe = isOwedToMe;
        this.isPaid = false;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPersonName() {
        return personName;
    }

    public void setPersonName(String personName) {
        this.personName = personName;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public boolean isOwedToMe() {
        return isOwedToMe;
    }

    public void setOwedToMe(boolean owedToMe) {
        isOwedToMe = owedToMe;
    }

    public boolean isPaid() {
        return isPaid;
    }

    public void setPaid(boolean paid) {
        isPaid = paid;
    }
}

