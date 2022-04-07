package com.example.firebase;

public class ModelClass {
    private String emailtext;
    private  String nametext;
    private String numbertext;
    private String passwordtext;
    private String userid;

    public ModelClass(String emailtext, String nametext, String numbertext, String passwordtext, String userid) {
        this.emailtext = emailtext;
        this.nametext = nametext;
        this.numbertext = numbertext;
        this.passwordtext = passwordtext;
        this.userid = userid;
    }

    public ModelClass() {
    }

    public String getEmailtext() {
        return emailtext;
    }

    public void setEmailtext(String emailtext) {
        this.emailtext = emailtext;
    }

    public String getNametext() {
        return nametext;
    }

    public void setNametext(String nametext) {
        this.nametext = nametext;
    }

    public String getNumbertext() {
        return numbertext;
    }

    public void setNumbertext(String numbertext) {
        this.numbertext = numbertext;
    }

    public String getPasswordtext() {
        return passwordtext;
    }

    public void setPasswordtext(String passwordtext) {
        this.passwordtext = passwordtext;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }
}