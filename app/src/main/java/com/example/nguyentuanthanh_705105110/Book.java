package com.example.nguyentuanthanh_705105110;

public class Book {
    private int id;
    private String code;
    private String title;
    private String category;
    private String author;
    private String entryDate;
    private double price;
    private String imageUrl;

    // Constructor đầy đủ
    public Book(int id, String code, String title, String category, String author, String entryDate, double price, String imageUrl) {
        this.id = id;
        this.code = code;
        this.title = title;
        this.category = category;
        this.author = author;
        this.entryDate = entryDate;
        this.price = price;
        this.imageUrl = imageUrl;
    }

    // Constructor không có ID (cho việc thêm mới)
    public Book(String code, String title, String category, String author, String entryDate, double price, String imageUrl) {
        this.code = code;
        this.title = title;
        this.category = category;
        this.author = author;
        this.entryDate = entryDate;
        this.price = price;
        this.imageUrl = imageUrl;
    }

    // Getters
    public int getId() { return id; }
    public String getCode() { return code; }
    public String getTitle() { return title; }
    public String getCategory() { return category; }
    public String getAuthor() { return author; }
    public String getEntryDate() { return entryDate; }
    public double getPrice() { return price; }
    public String getImageUrl() { return imageUrl; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setCode(String code) { this.code = code; }
    public void setTitle(String title) { this.title = title; }
    public void setCategory(String category) { this.category = category; }
    public void setAuthor(String author) { this.author = author; }
    public void setEntryDate(String entryDate) { this.entryDate = entryDate; }
    public void setPrice(double price) { this.price = price; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    @Override
    public String toString() {
        return "Book{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", title='" + title + '\'' +
                ", category='" + category + '\'' +
                ", author='" + author + '\'' +
                ", entryDate='" + entryDate + '\'' +
                ", price=" + price +
                ", imageUrl='" + imageUrl + '\'' +
                '}';
    }
}