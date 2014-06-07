package com.ram.mygov;

public class Event {

    public String eventName;
    public String eventInfo;
    public String eventLoc;
    public String eventDate;
    public String eventTime;
    public String eventID;

    public Event(String name, String info, String loc, String date, String time, String id) {

        eventName = name;
        eventInfo = info;
        eventLoc = loc;
        eventDate = date;
        eventTime = time;
        eventID = id;

    }

}
