package com.libertymutual.goforcode.sparkapp.models;

import org.javalite.activejdbc.Model;

public class User extends Model {

    public User() {
    }

    public User(String email, String password, String firstName, String lastName) {
        setEmail(email);
        setPassword(password);
        setFirstName(firstName);
        setLastName(lastName);
    }

    public String getEmail() {
        return getString("email");
    }

    public void setEmail(String email) {
        set("email", email);
    }

    public String getPassword() {
        return getString("password");
    }

    public void setPassword(String password) {
        set("password", password);
    }

    public String getFirstName() {
        return getString("first_name");
    }

    public void setFirstName(String firstName) {
        set("first_name", firstName);
    }

    public String getLastName() {
        return getString("last_name");
    }

    public void setLastName(String lastName) {
        set("last_name", lastName);
    }

    public int hashCode() {
        return getInteger("id");
    }

    public boolean equals(Object obj) {
        if (obj == null) { return false; }
        User user = (User) obj;
        if (user.getInteger("id") == getInteger("id")) {
            return true;
        }
        return false;
    }
}
