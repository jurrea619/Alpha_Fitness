package cs175.homework.alpha_fitness;

/**
 * Created by joshua on 12/2/16.
 */

public class WorkoutSession {

    private int id;
    private int time;
    private int distance;
    private int caloriesBurned;

    public WorkoutSession(){

    }

    public WorkoutSession(int wId, int t, int d, int calBurn){
        id = wId;
        time = t;
        distance = d;
        caloriesBurned = calBurn;
    }

    public void setId(int newId){
        id = newId;
    }

    public int getId(){
        return id;
    }

    public void setTime(int newTime){
        time = newTime;
    }

    public int getTime(){
        return time;
    }

    public void setDistance(int newDistance){
        distance = newDistance;
    }

    public int getDistance(){
        return distance;
    }

    public void setCaloriesBurned(int newCal){
        caloriesBurned = newCal;
    }
}
