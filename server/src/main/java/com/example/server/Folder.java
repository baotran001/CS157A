package com.example.server;
import java.util.Random;
import java.util.Objects;

public class Folder {
    private String fid;
    private String name;
    private String author;
    private String description;

    public Folder(){
        this.fid = Utility.generateRandomId(15);
    }

    public String getFid(){
        return this.fid;
    }

    public void setFolderid(String setid){
        this.fid = setid;
    }

    public String getName(){
        return this.name;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getAuthor(){
        return this.author;
    }

    public void setAuthor(String author){
        this.author = author;
    }

    public String getDescription(){
        return this.description;
    }

    public void setDescription(String description){
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof Folder)) {
            return false;
        }
        Folder folder = (Folder) o;
        return Objects.equals(fid, folder.fid) && Objects.equals(name, folder.name) && Objects.equals(author, folder.author) && Objects.equals(description, folder.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fid, name, author, description);
    }

    @Override
    public String toString() {
        return "{" +
            " fid='" + getFid() + "'" +
            ", name='" + getName() + "'" +
            ", author='" + getAuthor() + "'" +
            ", description='" + getDescription() + "'" +
            "}";
    }
}
