package com.example.server;
import java.util.Random;
import java.util.Objects;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;

public class Sets {
    private String sid;
    private String name;
    private String author;
    private java.sql.Date date;
    private String description;

    public Sets(){
        this.sid = Utility.generateRandomId(15);
    }

    public String getSid(){
        return this.sid;
    }

    public void setSetid(String setid){
        this.sid = setid;
    }

    public String getName(){
        return this.name;
    }

    public void setSetname(String name){
        this.name = name;
    }

    public String getAuthor(){
        return this.author;
    }

    public void setSetauthor(String author){
        this.author = author;
    }

    public java.sql.Date getDate(){
        return this.date;
    }

    public void setSetdate(java.sql.Date date){
        this.date = date;
    }

    public String getDescription(){
        return this.description;
    }

    public void setSetdescription(String description){
        this.description = description;
    }

    public Sets setid(String setid) {
        setSetid(setid);
        return this;
    }

    public Sets name(String name) {
        setSetname(name);
        return this;
    }

    public Sets author(String author) {
        setSetauthor(author);
        return this;
    }

    public Sets date(java.sql.Date date) {
        setSetdate(date);
        return this;
    }

    public Sets description(String description) {
        setSetdescription(description);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof Sets)) {
            return false;
        }
        Sets sets = (Sets) o;
        return Objects.equals(sid, sets.sid) && Objects.equals(name, sets.name) && Objects.equals(author, sets.author) && Objects.equals(date, sets.date) && Objects.equals(description, sets.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sid, name, author, date, description);
    }

    @Override
    public String toString() {
        return "{" +
            " sid='" + getSid() + "'" +
            ", name='" + getName() + "'" +
            ", author='" + getAuthor() + "'" +
            ", date='" + getDate() + "'" +
            ", description='" + getDescription() + "'" +
            "}";
    }
}
