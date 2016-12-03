package cs175.homework.alpha_fitness;

/**
 * Created by joshua on 12/1/16.
 */

public class WatchTime {

    //Time Elements
    private long mStartTime;
    private long mTimeUpdate;
    private long mStoredTime;

    public WatchTime(){
        mStartTime = 0L;
        mTimeUpdate = 0L;
        mStoredTime = 0L;
    }

    public void resetWatchTime(){
        mStartTime = 0L;
        mStoredTime = 0L;
        mTimeUpdate = 0L;
    }

    public void setStartTime(long startTime){
        mStartTime = startTime;
    }

    public long getStartTime(){
        return mStartTime;
    }

    public void setTimeUpdate(long time){
        mTimeUpdate = time;
    }

    public long getTimeUpdate(){
        return mTimeUpdate;
    }

    public void addStoredTime(long timeinMillis){
        mStoredTime += timeinMillis;
    }

    public long getStoredTime(){
        return mStoredTime;
    }
}
