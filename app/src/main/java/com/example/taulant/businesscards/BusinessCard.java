package com.example.taulant.businesscards;



/**
 * Created by taulant on 23/3/17.
 */

public class BusinessCard {
    String name;
    String phone;
    String email;
    String job;
    String company;
    byte[] image;
    String id;

    public BusinessCard(String name, String phone, String email, String job, String company, byte[] image, String id){//, String id){
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.job = job;
        this.company = company;
        this.image = image;
        this.id = id;
    }

    public String getName(){return name;}
    public String getPhone(){return phone;}
    public String getEmail(){return email;}
    public String getJob(){return job;}
    public String getCompany(){return company;}
    public byte[] getImage(){return image;}
    public String getId(){return id;}
}
