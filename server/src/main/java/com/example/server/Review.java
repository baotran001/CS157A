package com.example.server;
import java.time.LocalDate;
import java.util.Objects;

public class Review {
    private String rid;
    private int star;
    private String author;
    private java.sql.Date date;
    private String text;

    public Review() {
        this.rid = Utility.generateRandomId(15);
        this.date = java.sql.Date.valueOf(LocalDate.now());
    }

    public String getRid() {
        return this.rid;
    }

    public void setRid(String rid) {
        this.rid = rid;
    }

    public int getStar() {
        return this.star;
    }

    public void setStar(int star) {
        this.star = star;
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
        if (!(o instanceof Review)) {
            return false;
        }
        Review review = (Review) o;
        return Objects.equals(rid, review.rid) && star == review.star && Objects.equals(author, review.author) && Objects.equals(date, review.date) && Objects.equals(text, review.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(rid, star, author, date, text);
    }

    @Override
    public String toString() {
        return "{" +
            " rid='" + getRid() + "'" +
            ", star='" + getStar() + "'" +
            ", author='" + getAuthor() + "'" +
            ", date='" + getDate() + "'" +
            ", text='" + getText() + "'" +
            "}";
    }

}
