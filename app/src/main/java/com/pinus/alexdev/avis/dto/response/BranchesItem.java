package com.pinus.alexdev.avis.dto.response;

public class BranchesItem {
    private String address;
    private String phone;
    private String contact;
    private String name;
    private String id;
    private String logoUrl;

    public String getAddress () {
        return address;
    }

    public void setAddress (String address) {
        this.address = address;
    }

    public String getPhone () {
        return phone;
    }

    public void setPhone (String phone) {
        this.phone = phone;
    }

    public String getContact () {
        return contact;
    }

    public void setContact (String contact) {
        this.contact = contact;
    }

    public String getName () {
        return name;
    }

    public void setName (String name) {
        this.name = name;
    }

    public String getId () {
        return id;
    }

    public void setId (String id) {
        this.id = id;
    }

    public String getLogoUrl () {
        return logoUrl;
    }

    public void setLogoUrl (String logoUrl) {
        this.logoUrl = logoUrl;
    }

    @Override
    public String toString() {
        return
                "BranchesItem{" +
                        "name = '" + name + '\'' +
                        ",id = '" + id + '\'' +
                        "}";
    }
}