package org.firstinspires.ftc.teamcode.Subsystems.Spindexer;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Subsystems.LimeLight;
import org.firstinspires.ftc.teamcode.Subsystems.Timer;

public class Spindexer {
    public enum States {
        MANUAL,
        RESTING,
        INTAKING,
        CHAMBER,
        SORTED_SHOOTING,
        RAPID_FIRE
    }

    public static double startPos = .05; //"Zero pos of spindexr
    public static double maxPos = .95; //When spindexer is at or over max -> reset
    public static double rotateTicks = .2; //How much the spindexer rotates for 1 slot
    public static long rotateWaitTime = 200; //How long it takes a spindexer to rotate 1 slot
    States state = States.RESTING;
    Servo rightServo; //Dominant
    Servo leftServo;
    Kicker kicker = new Kicker(); //Child subsystem because kicker is completely dependant on spindexer
    CSensor colorSensor = new CSensor();
    public static double targetPos = startPos;
    boolean rotating = false;
    //Timer to determine if the spindexer is rotating or not
    Timer rotateTimer = new Timer();

    //Sequences are used for intaking and shooting sequence
    Timer sequenceTimer = new Timer();
    int sequence = 1;

    //Ball is what the color sensor reads
    public LimeLight.BallColors ball = LimeLight.BallColors.NONE;
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

    //Move back to starting/zero pos
    public void reset() {
        double ticksPassed = targetPos - startPos;
        //Calculate how much time to wait based on how many degrees away from zero pos
        rotateTimer.setWait((long) (ticksPassed / rotateTicks * rotateWaitTime));
        targetPos = startPos;
        rotating = true;

    }

    public void intakeSequence() {
        if (ballsIntaked >= 3 || targetPos >= maxPos) {
            ballsIntaked = 0;
            state = States.RESTING;
            reset();
            return;
        }
        switch (sequence) {
            case 1:
                //if ball rotate
                if (ball != LimeLight.BallColors.NONE) {
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

    public void shoot() {
        kicker.kick();
        ballsShot++;
    }

    public void chamberSequence() {
        if (targetPos >= maxPos) {
            ballsShot = 0;
            state = States.RESTING;
            reset();
            return;
        }
        switch (sequence) {
            case 1:
                //Ball or no ball we rotate to the next slot
                rotate();
                sequence++;
                break;
            case 2:
                //If we are done rotating
                if (!rotating) {
                    //If we have a ball
                    if (ball != LimeLight.BallColors.NONE) {
                        //Chambered!
                        chambered = true;
                        sequence++;
                    } else {
                        //Keep spinning until you find the ball
                        sequence = 1;
                    }
                }
                break;
            case 3:
                //Do nothing, wait until shooting
                break;

        }
    }

    public void sortedShootingSequence() {
        if (chambered) {
            //FIRE
            shoot();
        }
        if (ballsShot >= 3 || targetPos >= maxPos) {
            ballsShot = 0;
            state = States.RESTING;
            reset();
            return;
        }
        switch (sequence) {
            case 1:
                //Ball or no ball we rotate to the next slot
                rotate();
                sequence++;
                break;
            case 2:
                //If we are done rotating
                if (!rotating) {
                    //If we have a ball
                    if (ball != LimeLight.BallColors.NONE) {
                        //Shooting
                        shoot();
                        sequence++;
                    } else {
                        sequence = 1;
                    }
                }
                break;
            case 3:
                //Done shooting?
                if (kicker.getState() == Kicker.States.RETURNING) {
                    sequence = 1;
                }
                break;

        }
    }

    public void rapidFireSequence() {
        //AKA unsorted shooting
        if (ballsShot >= 3 || targetPos >= maxPos) {
            ballsShot = 0;
            state = States.RESTING;
            reset();
            return;
        }
        switch (sequence) {
            case 1:
                shoot();
                sequence++;
                break;
            case 2:
                //Ball is shot
                if (kicker.getState() == Kicker.States.RETURNING) {
                    rotate();
                    sequence++;
                }
                break;
            case 3:
                //Spindexer returned
                if (!rotating) {
                    sequence = 1;
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

    public Kicker getKicker() {
        return kicker;
    }

    public void initiate(HardwareMap hardwareMap) {
        rightServo = hardwareMap.servo.get("rspin");
        leftServo = hardwareMap.servo.get("lspin");
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
        //Read color sensor
        ball = colorSensor.getBallColor();

        //Manual means no automated, only rotate, reset, and shooting avalible
        if (state == States.MANUAL) {
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
            case SORTED_SHOOTING:
                sortedShootingSequence();
                break;
            case RAPID_FIRE:
                rapidFireSequence();
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
