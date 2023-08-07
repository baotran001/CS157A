package com.example.server;
import java.time.LocalDate;
import java.util.Objects;

public class Comment {
    private String cid;
    private String author;
    private java.sql.Date date;
    private String text;

    public Comment() {
        this.cid = Utility.generateRandomId(15);
        this.date = java.sql.Date.valueOf(LocalDate.now());
    }

    public String getCid() {
        return this.cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getAuthor() {
        return this.author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public java.sql.Date getDate() {
        return this.date;
    }

    public void setDate(java.sql.Date date) {
        this.date = date;
    }

    public String getText() {
        return this.text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof Comment)) {
            return false;
        }
        Comment comment = (Comment) o;
        return Objects.equals(cid, comment.cid) && Objects.equals(author, comment.author) && Objects.equals(date, comment.date) && Objects.equals(text, comment.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cid, author, date, text);
    }

    @Override
    public String toString() {
        return "{" +
            " cid='" + getCid() + "'" +
            ", author='" + getAuthor() + "'" +
            ", date='" + getDate() + "'" +
            ", text='" + getText() + "'" +
            "}";
    }
}
