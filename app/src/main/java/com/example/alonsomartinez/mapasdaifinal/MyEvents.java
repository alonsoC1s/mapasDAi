package com.example.alonsomartinez.mapasdaifinal;


import com.google.firebase.database.IgnoreExtraProperties;



@IgnoreExtraProperties
public class MyEvents {
    public Double eventLatitude;
    public Double eventLongitude;
    public String eventName;
    public String eventContent;
    public String eventID;



    public MyEvents(){
        //Default empty constructor
    }

    public MyEvents(String name,String content, Double lat, Double lng, String eventID){
        this.eventName = name;
        this.eventContent = content;
        this.eventLatitude = lat;
        this.eventLongitude = lng;
        this.eventID = eventID;

    }


    public String getEventName() {return eventName;}
    ////
    /////
    public Double getEventLatitude() {return eventLatitude;}
    public Double getEventLongitude() {return eventLongitude;}
    /////
    public String getEventContent() {return eventContent;}


    public String getEventID(){return eventID;}




}