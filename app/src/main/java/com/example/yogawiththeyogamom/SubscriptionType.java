package com.example.yogawiththeyogamom;

public class SubscriptionType {
    private String name;
    private String info;
    private String price;
    private float ratedInfo;
    private final int imageResource;


    public SubscriptionType(String name, String info, String price, float ratedInfo, int imageResource) {
        this.name = name;
        this.info = info;
        this.price = price;
        this.ratedInfo = ratedInfo;
        this.imageResource = imageResource;
    }

    public String getName() {
        return name;
    }

    public String getInfo() {
        return info;
    }

    public String getPrice() {
        return price;
    }

    public float getRatedInfo() {
        return ratedInfo;
    }

    public int getImageResource(){
        return imageResource;
    }
}
