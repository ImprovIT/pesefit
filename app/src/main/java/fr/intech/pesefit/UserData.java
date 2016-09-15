package fr.intech.pesefit;

/**
 * Created by Alexandre on 15/09/2016.
 */
public class UserData {

    private int _id;
    private long _date;
    private long _duration;
    private int _activity;

    public UserData(){}

    public UserData(long date, long duration, int activity) {
        _date = date;
        _duration = duration;
        _activity = activity;
    }

    public int getId(){
        return _id;
    }

    public int setId(int id){
        return _id = id;
    }

    public long getDate() {
        return _date;
    }

    public void setDate(long date) {
        _date = date;
    }

    public long getDuration() {
        return _duration;
    }

    public void setDuration(long duration) {
        _duration = duration;
    }

    public int getActivity() {
        return _activity;
    }

    public void setActivity(int activity) {
        _activity = activity;
    }
}
