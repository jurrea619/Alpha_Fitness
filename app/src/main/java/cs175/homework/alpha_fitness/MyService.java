package cs175.homework.alpha_fitness;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.SystemClock;

public class MyService extends Service {

    IMyInterface.Stub mBinder;
    WatchTime watchTime;

    public MyService() {
    }

    @Override
    public void onCreate(){
        super.onCreate();
        watchTime = new WatchTime();
        mBinder = new IMyInterface.Stub(){

            public void startTimer() {
                watchTime.setStartTime(SystemClock.uptimeMillis());
            };

            public void resetTimer() {
                watchTime.resetWatchTime();
            };

            public void addStoredTime(long timeInMillis){
                watchTime.addStoredTime(timeInMillis);
            };

            public long getStartTime(){
                return watchTime.getStartTime();
            };

            public long getTimeUpdate(){
                return watchTime.getTimeUpdate();
            };

            public void setTimeUpdate(long timeInMillis){
                watchTime.setTimeUpdate(watchTime.getStoredTime() + timeInMillis);
            };
        };
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
}
