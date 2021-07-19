package com.sp.socialapp.model;

public class User {

    private String UserName, uid,imageUri,phoneNumber, profession,email,url;


    @Deprecated
    public User(){}

    public User(String UserName, String uid, String imageUri, String phoneNumber, String email) {
        this.UserName = UserName;
        this.uid = uid;
        this.imageUri = imageUri;
        this.phoneNumber = phoneNumber;
        this.email = email;
    }

    public User(String UserName, String uid, String imageUri, String phoneNumber, String profession, String email,String url) {
        this.UserName = UserName;
        this.uid = uid;
        this.imageUri = imageUri;
        this.phoneNumber = phoneNumber;
        this.profession = profession;
        this.email = email;
        this.url = url;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        this.UserName = userName;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
