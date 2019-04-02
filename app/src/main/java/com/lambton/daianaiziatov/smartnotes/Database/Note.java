package com.lambton.daianaiziatov.smartnotes.Database;

import com.lambton.daianaiziatov.smartnotes.StringArrayConverter;

import java.sql.Date;
import java.util.ArrayList;

public class Note {

    private String noteId;
    private String details;
    private Date date;
    private double locationLatitude;
    private double locationLongitude;
    private ArrayList<String> recordings;

    public String getNoteId() {
        return noteId;
    }

    public void setNoteId(String noteId) {
        this.noteId = noteId;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public long getDateAsLong() {
        return this.date.getTime();
    }

    public void setDateFromLong(long date) {
        this.date = new Date(date);
    }

    public double getLocationLatitude() {
        return locationLatitude;
    }

    public void setLocationLatitude(double locationLatitude) {
        this.locationLatitude = locationLatitude;
    }

    public double getLocationLongitude() {
        return locationLongitude;
    }

    public void setLocationLongitude(double locationLongitude) {
        this.locationLongitude = locationLongitude;
    }

    public ArrayList<String> getRecordings() {
        return recordings;
    }

    public void setRecordings(ArrayList<String> recordings) {
        this.recordings = recordings;
    }

    public String getRecordingsAsString() {
        return StringArrayConverter.convertArrayToString(this.recordings);
    }

    public void setRecordingsFromString(String recordings) {
        this.recordings = StringArrayConverter.convertStringToArray(recordings);
    }
}
