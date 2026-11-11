package com.example.group404lostfound;

public class LostFoundItem {

    private String name;
    private String description;
    private String category;
    private String date;
    private String location;
    private String type;
    private String photoUri;
    private String status;

    public LostFoundItem(
            String name,
            String description,
            String category,
            String date,
            String location,
            String type,
            String photoUri,
            String status) {

        this.name = name;
        this.description = description;
        this.category = category;
        this.date = date;
        this.location = location;
        this.type = type;
        this.photoUri = photoUri;
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPhotoUri() {
        return photoUri;
    }

    public void setPhotoUri(String photoUri) {
        this.photoUri = photoUri;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}