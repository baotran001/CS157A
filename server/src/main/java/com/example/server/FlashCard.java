package com.example.server;
import java.util.Random;
import java.util.Objects;
public class FlashCard {
    // User Schema
    private String flashid;
    private String favorite;
    private String front;
    private String back;
    private String sid;

    public FlashCard() {
        this.flashid = Utility.generateRandomId(15);
        this.favorite = "N";
    }

    public String getSid(){
        return this.sid;
    }

    public void setSid(String sid){
        this.sid = sid;
    }

    public String getFlashid() {
        return this.flashid;
    }

    public void setFlashid(String flashid) {
        this.flashid = flashid;
    }

    public String getFavorite() {
        return this.favorite;
    }

    public void setFavorite(String favorite) {
        this.favorite = favorite;
    }

    public String getFront() {
        return this.front;
    }

    public void setFront(String front) {
        this.front = front;
    }

    public String getBack() {
        return this.back;
    }

    public void setBack(String back) {
        this.back = back;
    }

    public FlashCard flashid(String flashid) {
        setFlashid(flashid);
        return this;
    }

    public FlashCard favorite(String favorite) {
        setFavorite(favorite);
        return this;
    }

    public FlashCard front(String front) {
        setFront(front);
        return this;
    }

    public FlashCard back(String back) {
        setBack(back);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof FlashCard)) {
            return false;
        }
        FlashCard flashCard = (FlashCard) o;
        return Objects.equals(flashid, flashCard.flashid) && Objects.equals(favorite, flashCard.favorite) && Objects.equals(front, flashCard.front) && Objects.equals(back, flashCard.back);
    }

    @Override
    public int hashCode() {
        return Objects.hash(flashid, favorite, front, back);
    }

    @Override
    public String toString() {
        return "{" +
            " flashid='" + getFlashid() + "'" +
            ", favorite='" + getFavorite() + "'" +
            ", front='" + getFront() + "'" +
            ", back='" + getBack() + "'" +
            "}";
    }
    
}
