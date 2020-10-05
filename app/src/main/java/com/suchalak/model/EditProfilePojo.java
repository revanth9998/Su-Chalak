package com.suchalak.model;

import com.google.gson.annotations.SerializedName;

public class EditProfilePojo {


    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getProfile_pic() {
        return profile_pic;
    }

    public void setProfile_pic(String profile_pic) {
        this.profile_pic = profile_pic;
    }

    @SerializedName("firstname")
    private String firstname;

    @SerializedName("lastname")
    private String lastname;

    @SerializedName("dob")
    private String dob;

    @SerializedName("pass")
    private String pass;

    @SerializedName("profile_pic")
    private String profile_pic;


}
