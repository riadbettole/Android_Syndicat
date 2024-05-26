package com.example.android_syndicat;

public class Item {
    private String itemName;
    private String itemImage;
    private String idUser;

    public Item(){

    }

    public Item(String itemName, String itemImage, String idUser){
        this.itemName = itemName;
        this.itemImage = itemImage;
        this.idUser = idUser;
    }

    public String getItemImage(){
        return itemImage;
    }

    public String getItemidUser(){
        return idUser;
    }
    public String getItemName(){
        return itemName;
    }
    public void setItemName(String itemName){
        this.itemName = itemName;
    }
}
