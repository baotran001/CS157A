package com.example.server;
import java.util.Random;
public class User {
    // User Schema
    private String username;
    private String email;
    private String password;

    public User(){
    }
    
    public String getUsername(){
        return this.username;
    }
    public void setUsername(String username){
        this.username = username;
    }
    public String getEmail(){
        return this.email;
    }
    public void setEmail(String email){
        this.email = email;
    }
    public String getPassword(){
        return this.password;
    }
    public void setPassword(String pass){
        this.password = pass;
    }
}