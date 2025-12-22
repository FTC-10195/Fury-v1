package org.firstinspires.ftc.teamcode.Subsystems.Spindexer;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Subsystems.LimeLight;
import org.firstinspires.ftc.teamcode.Subsystems.Timer;
@Config
public class Spindexer {
    public enum States {
        RESTING,
        INTAKING,
        CHAMBER,
        SHOOTING
    }

    public enum Modes {
        MANUAL,
        SORTED,
        UNSORTED
    }

    public static double intakeStartPos = .05; //"Zero pos of spindexr
    public static double shootingTicks = .1; //60 degrees / 600 degrees per tick -> .1 ticks
    public static double shootingStartPos = intakeStartPos + shootingTicks;
    public static double maxPos = 1; //When spindexer is at or over max -> reset
    public static double rotateTicks = .2; //How much the spindexer rotates for 1 slot (120 degrees)
    public static long rotateWaitTime = 200; //How long it takes a spindexer to rotate 1 slot
    public static double maxDegrees = 600;
    static double degreesToTicks(double degree){
        return degree/maxDegrees;
    }
    static double ticksToDegrees(double ticks){
        return ticks * maxDegrees;
    }
    States state = States.RESTING;
    Modes mode = Modes.MANUAL;
    Servo rightServo; //Dominant
    Servo leftServo;
    Kicker kicker = new Kicker(); //Child subsystem because kicker is completely dependant on spindexer
    CSensor colorSensor = new CSensor();
    public static double targetPos = intakeStartPos;
    boolean rotating = false;
    //Timer to determine if the spindexer is rotating or not
    Timer rotateTimer = new Timer();

    //Sequences are used for intaking and shooting sequence
    Timer sequenceTimer = new Timer();
    int sequence = 1;

    //Ball is what the color sensor reads
    public LimeLight.BallColors savedBall = LimeLight.BallColors.NONE;
    int ballsIntaked = 0;
    int ballsShot = 0;

    //If this is true that means we can shoot the first shot in the shooting sequence
    boolean chambered = false;

    //Move one slot
    public void rotate() {
        //Already moving? don't rotate more
        if (rotating) {
            return;
        }

        targetPos += rotateTicks;
        rotateTimer.setWait(rotateWaitTime);
        rotating = true;

        if (targetPos > maxPos) {
            reset();
        }
    }
    public void rotateDegree(int degree){
        //Already moving? don't rotate more
        if (rotating) {
            return;
        }

        targetPos += degreesToTicks(degree);
        rotateTimer.setWait(Math.abs((long) degree/120 * rotateWaitTime));
        rotating = true;

        if (targetPos > maxPos) {
            reset();
        }
    }

    //Move back to starting/zero pos
    public void reset() {
        double ticksPassed = targetPos - intakeStartPos;
        //Calculate how much time to wait based on how many degrees away from zero pos
        rotateTimer.setWait((long) (ticksPassed / rotateTicks * rotateWaitTime));
        targetPos = intakeStartPos;
        rotating = true;

    }
    public void rotateToShoot(){
        //Already moving? don't rotate more
        if (rotating) {
            return;
        }

        targetPos += shootingTicks;
        rotateTimer.setWait(rotateWaitTime/2);
        rotating = true;

        if (targetPos > maxPos) {
            reset();
        }
    }

    public void shoot() {
        kicker.kick();
        ballsShot++;
    }
    public void saveBall(){
        //Read color sensor and save it
        savedBall = colorSensor.getBallColor();
    }

    public void intakeSequence() {
        //Constantly read for intake
        saveBall();
        if (ballsIntaked >= 3) {
            ballsIntaked = 0;
            state = States.RESTING;
            reset();
            return;
        }
        switch (sequence) {
            case 1:
                //if ball rotate
                if (savedBall != LimeLight.BallColors.NONE) {
                    ballsIntaked++;
                    rotate();
                    sequence++;
                }
                break;
            case 2:
                if (!rotating) {
                    sequence = 1;
                }
                break;
        }
    }

    public void chamberSequence() {
        //If spindexer is chambered then the job is complete, don't do anything
        if (chambered){
            return;
        }
        switch (sequence) {
            case 1:
                //Read what ball is in this slot
                saveBall();
                if (savedBall != LimeLight.BallColors.NONE) {
                    //Shooting
                    rotateDegree(180);
                } else {
                    rotate();
                }
                sequence++;
                break;
            case 2:
                //If we are done rotating
                if (rotating) {
                    return;
                }

                if (savedBall != LimeLight.BallColors.NONE) {
                    chambered = true;
                }
                sequence = 1;
                break;
        }
    }

    public void sortedShootingSequence() {
        if (chambered) {
            //FIRE
            shoot();
        }
        if (ballsShot >= 3 || targetPos >= degreesToTicks(360 + ticksToDegrees(intakeStartPos))) {
            ballsShot = 0;
            state = States.RESTING;
            reset();
            return;
        }
        switch (sequence) {
            case 1:
                if (chambered){
                    rotateDegree(-60);
                    chambered = false;
                }
                if (rotating){
                    return;
                }
                //Read what ball is in this slot
                saveBall();
                if (savedBall != LimeLight.BallColors.NONE) {
                    //Shooting
                    rotateDegree(180);
                } else {
                    rotate();
                }
                sequence++;
                break;
            case 2:
                if (rotating) {
                    return;
                }

                if (savedBall != LimeLight.BallColors.NONE) {
                    shoot();
                    sequence++;
                } else {
                    sequence = 1;
                }
                break;
            case 3:
                //Done shooting?
                if (kicker.getState() == Kicker.States.RETURNING) {
                    rotateDegree(-60);
                    sequence++;
                }
                break;
            case 4:
                if (!rotating) {
                    sequence = 1;
                }
        }
    }

    public void rapidFireSequence() {
        //AKA unsorted shooting
        if (ballsShot >= 3) {
            ballsShot = 0;
            state = States.RESTING;
            reset();
            return;
        }
        switch (sequence) {
            case 1:
                //Rotate to shooting pos
                rotateToShoot();
                sequence++;
                break;
            case 2:
                if (!rotating) {
                    shoot();
                    sequence++;
                }
                break;
            case 3:
                //Ball is shot
                if (kicker.getState() == Kicker.States.RETURNING) {
                    rotate();
                    sequence++;
                }
                break;
            case 4:
                //Spindexer returned
                if (!rotating) {
                    sequence = 2;
                }
                break;
        }


    }

    public void setState(States newState) {
        state = newState;
    }

    public States getState() {
        return state;
    }

    public void setMode(Modes newMode) {
        mode = newMode;
    }

    public Modes getMode() {
        return mode;
    }

    public Kicker getKicker() {
        return kicker;
    }

    public void initiate(HardwareMap hardwareMap) {
        rightServo = hardwareMap.servo.get("rspin");
        rightServo.setDirection(Servo.Direction.REVERSE);
        leftServo = hardwareMap.servo.get("lspin");
        leftServo.setDirection(Servo.Direction.REVERSE);
        colorSensor.initiate(hardwareMap);
        kicker.initiate(hardwareMap);
    }


    public void update() {
        if (state == States.RESTING) {
            //Every sequence ends with RESTING
            //Reset variables
            ballsIntaked = 0;
            ballsShot = 0;
            sequence = 1;
        }
        kicker.update();
        rightServo.setPosition(targetPos);
        leftServo.setPosition(1 - targetPos);

        //Update the rotating variable based on how much time has passed since last rotation
        if (rotating) {
            if (rotateTimer.doneWaiting()) {
                rotating = false;
            } else {
                return;
            }
        }

        //Manual means no automated, only rotate, reset, and shooting avalible
        if (mode == Modes.MANUAL) {
            return;
        }

        //Automatic here


        //Various sequences based on the state of spindexer
        switch (state) {
            case INTAKING:
                intakeSequence();
                break;
            case CHAMBER:
                chamberSequence();
                break;
            case SHOOTING:
                if (mode == Modes.SORTED) {
                    sortedShootingSequence();
                } else if (mode == Modes.UNSORTED) {
                    rapidFireSequence();
                }
                break;
        }
    }

    public void status(Telemetry telemetry) {
        telemetry.addData("SpindexerPosition", targetPos);
        telemetry.addData("SpindexerRotating", rotating);
        telemetry.addData("SpindexerState", state);
        telemetry.addData("SpindexerSequence", sequence);
        kicker.status(telemetry);
        colorSensor.status(telemetry);
    }

}
