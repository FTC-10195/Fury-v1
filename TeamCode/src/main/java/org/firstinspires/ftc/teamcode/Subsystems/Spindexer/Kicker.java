package org.firstinspires.ftc.teamcode.Subsystems.Spindexer;


import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Subsystems.Timer;

@Config
public class Kicker {
    public enum States {
        CHAMBER,
        SHOOTING,
        RETURNING,
        RESTING,
    }

    public static long waitTime = 250;
    public static long minWaitTime = 180;
    public static long kickerReturnPadding = 0;

    public States currentState = States.RESTING;
    Timer timer = new Timer();

    Servo kicker;

    public void initiate(HardwareMap hardwareMap) {
        kicker = hardwareMap.servo.get("kicker");
        kicker.setDirection(Servo.Direction.REVERSE);
    }

    public static double servoShootingPos = 0.85;
    public static double servoRestingPos = 0.48;
    public static double servoChamberPos = .72;
    private double targetPos = servoRestingPos;
    public boolean isReady = true;

    private long calculateWaitTime(double startPos, double endPos) {
        long wait = (long) Math.abs(((endPos - startPos) / (servoShootingPos - servoRestingPos) * waitTime));
        if (wait < minWaitTime){
            wait = minWaitTime;
        }
        return wait;
    }

    public void kick() {
        currentState = States.SHOOTING;
        timer.setWait(calculateWaitTime(targetPos, servoShootingPos));
        isReady = false;
    }

    public void chamber() {
        currentState = States.CHAMBER;
        timer.setWait(calculateWaitTime(servoRestingPos, servoChamberPos));
        isReady = false;
    }

    public States getState() {
        return currentState;
    }

    public void update() {
        switch (currentState) {
            case CHAMBER:
                targetPos = servoChamberPos;
                if (timer.doneWaiting()) {
                    isReady = true;
                }
                break;
            case SHOOTING:
                targetPos = servoShootingPos;
                if (timer.doneWaiting()) {
                    currentState = States.RETURNING;
                    timer.setWait(waitTime + kickerReturnPadding);
                }
                break;
            case RETURNING:
                targetPos = servoRestingPos;
                if (timer.doneWaiting()) {
                    currentState = States.RESTING;
                }
                break;
            case RESTING:
                targetPos = servoRestingPos;
                isReady = true;
                break;
        }
        kicker.setPosition(targetPos);
    }

    public void status(Telemetry telemetry) {
        telemetry.addData("Kicker State", getState());
        telemetry.addData("Kicker Pos", kicker.getPosition());
    }
}
