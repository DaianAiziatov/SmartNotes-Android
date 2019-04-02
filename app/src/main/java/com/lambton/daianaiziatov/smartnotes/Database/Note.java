package com.lambton.daianaiziatov.smartnotes.Database;

import android.os.Parcel;
import android.os.Parcelable;

import com.lambton.daianaiziatov.smartnotes.StringArrayConverter;

import java.sql.Date;
import java.util.ArrayList;

public class Note implements Parcelable {

    private String noteId = "";
    private String details = "";
    private Date date;
    private double locationLatitude = 0.0;
    private double locationLongitude = 0.0;
    private ArrayList<String> recordings = new ArrayList<>();

    public Note() {
    }

    protected Note(Parcel in) {
        noteId = in.readString();
        details = in.readString();
        date = new Date(in.readLong());
        locationLatitude = in.readDouble();
        locationLongitude = in.readDouble();
        recordings = in.createStringArrayList();
    }

    public static final Creator<Note> CREATOR = new Creator<Note>() {
        @Override
        public Note createFromParcel(Parcel in) {
            return new Note(in);
        }

        @Override
        public Note[] newArray(int size) {
            return new Note[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(noteId);
        dest.writeString(details);
        dest.writeLong(getDateAsLong());
        dest.writeDouble(locationLatitude);
        dest.writeDouble(locationLongitude);
        dest.writeStringList(recordings);
    }

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
