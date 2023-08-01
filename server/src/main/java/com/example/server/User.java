package com.example.server;
import java.util.Random;
public class User {

    private static final String ALPHA_NUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    // User Schema
    private String uid;
    private String email;
    private String password;

    public User(){
        this.uid = generateRandomID(15);
    }
    public String getUID(){
        return this.uid;
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
    private String generateRandomID(int length){
        Random random = new Random();
        String randomID = "";

        for( int i = 0; i < length; i++){
            int index = random.nextInt(ALPHA_NUMERIC_STRING.length());
            randomID += ALPHA_NUMERIC_STRING.charAt(index);
        }
        return randomID;
    }
}
