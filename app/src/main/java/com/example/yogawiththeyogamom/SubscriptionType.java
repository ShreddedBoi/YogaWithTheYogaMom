package com.example.yogawiththeyogamom;

public class SubscriptionType {
    private String id;
    private String name;
    private String info;
    private String price;
    private float ratedInfo;
    private int imageResource;
    private int basketedCounter;


    public SubscriptionType(String name, String info, String price, float ratedInfo, int imageResource, int basketedCounter) {
        this.name = name;
        this.info = info;
        this.price = price;
        this.ratedInfo = ratedInfo;
        this.imageResource = imageResource;
        this.basketedCounter = basketedCounter;
    }

    public SubscriptionType() {}

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

    public int getBasketedCounter(){return basketedCounter;}

    public String _getId() {
        return id;
    }

    public void setId(String id){
        this.id = id;
    }

}
