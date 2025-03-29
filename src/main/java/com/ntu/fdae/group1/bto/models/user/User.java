package com.ntu.fdae.group1.bto.models.user;

/**
 * User class is an abstract class that represents a user in the system.
 * 
 */
public abstract class User {
    private String userId;
    private String password;
    private int age;
    private String maritalStatus;
    private String name;
    public String getName() {
        return name;
    }


    public User(String userId, String password, int age, String maritalStatus) {
        this.userId = userId;
        this.password = password;
        this.age = age;
        this.maritalStatus = maritalStatus;
    }

    public String getUserId() {
        return userId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getAge() {
        return age;
    }

    public void setMaritalStatus(String maritalStatus) {
        this.maritalStatus = maritalStatus;
    }

    public String getMaritalStatus() {
        return maritalStatus;
    }

    public void setAge(int age) {
        this.age = age;
    }

}
