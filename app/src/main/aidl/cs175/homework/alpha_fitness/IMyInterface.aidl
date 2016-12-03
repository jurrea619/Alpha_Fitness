// IMyInterface.aidl
package cs175.homework.alpha_fitness;

// Declare any non-default types here with import statements

interface IMyInterface {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */

    void startTimer();

    void resetTimer();

    void addStoredTime(long timeInMillis);

    long getStartTime();

    long getTimeUpdate();

    void setTimeUpdate(long timeInMillis);
    }
