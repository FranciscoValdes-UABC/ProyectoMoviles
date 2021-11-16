package com.example.proyectomoviles;

public class Lectures {
    private int id;
    private String userId;
    private String NFC;
    private String date;
    private String name;
    private String image;

    public Lectures(int id, String userId, String NFC, String date, String name, String image) {
        this.id = id;
        this.userId = userId;
        this.NFC = NFC;
        this.date = date;
        this.name = name;
        this.image = image;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNFC() {
        return NFC;
    }

    public void setNFC(String NFC) {
        this.NFC = NFC;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
