package org.firstinspires.ftc.teamcode.Subsystems;

public class Timer {
    long timeSnapshot = System.currentTimeMillis();
    long waitTime;
    public Timer(){
        this.waitTime = 0;
        timeSnapshot = System.currentTimeMillis();
    }
    public void setWait(long waitTime){
        this.waitTime = waitTime;
        timeSnapshot = System.currentTimeMillis();
    }
    public boolean doneWaiting(){
        return System.currentTimeMillis() - timeSnapshot > waitTime;
    }
}
