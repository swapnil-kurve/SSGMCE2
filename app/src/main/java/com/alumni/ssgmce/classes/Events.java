package com.alumni.ssgmce.classes;

/**
 * Created by USer on 10-10-2016.
 */

public class Events {
    String Eventid,Event,EventDescription,EventScheduleId,DayNo,EventDate,StatrTime,EndTime,EventEndDate,NeedToRegister;

    public Events(String eventid, String event, String eventDescription, String eventScheduleId, String dayNo, String eventDate, String statrTime, String endTime, String eventEndDate, String needToRegister) {
        Eventid = eventid;
        Event = event;
        EventDescription = eventDescription;
        EventScheduleId = eventScheduleId;
        DayNo = dayNo;
        EventDate = eventDate;
        StatrTime = statrTime;
        EndTime = endTime;
        EventEndDate = eventEndDate;
        NeedToRegister = needToRegister;
    }

    public String getEventid() {
        return Eventid;
    }

    public String getEvent() {
        return Event;
    }

    public String getEventDescription() {
        return EventDescription;
    }

    public String getEventScheduleId() {
        return EventScheduleId;
    }

    public String getDayNo() {
        return DayNo;
    }

    public String getEventDate() {
        return EventDate;
    }

    public String getStatrTime() {
        return StatrTime;
    }

    public String getEndTime() {
        return EndTime;
    }

    public String getEventEndDate() {
        return EventEndDate;
    }

    public String getNeedToRegister() {
        return NeedToRegister;
    }
}
