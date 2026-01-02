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

    public static double maxDegrees = 650;
    public static long sortedShootingScanPadding = 100;

    static double degreesToTicks(double degree) {
        return degree / maxDegrees;
    }

    static double ticksToDegrees(double ticks) {
        return ticks * maxDegrees;
    }

    public static double intakeStartPos = .14; //"Zero pos of spindexr
    public static double rotateTicks = degreesToTicks(120);public static long rotateWaitTime = 350; //How long it takes a spindexer to rotate 1 slot

    public static double maxPos = .99; //When spindexer is at or over max -> reset
    public static double rightOffset = 0.00;
    public static double leftOffset = -0.00;
    public static int rapidFireShootNumber = 3;
    States state = States.RESTING;
    Modes mode = Modes.MANUAL;
    Servo rightServo; //Dominant
    Servo leftServo;
    Kicker kicker = new Kicker(); //Child subsystem because kicker is completely dependant on spindexer
    BallDetector ballDetector = new BallDetector();
    Slot[] slots = {new Slot(),new Slot(),new Slot()};
    public static double targetPos = intakeStartPos;
    boolean rotating = false;
    //Timer to determine if the spindexer is rotating or not
    Timer rotateTimer = new Timer();

    int sequence = 1;

    //Ball is what the color sensor reads
    public LimeLight.BallColors savedBall = LimeLight.BallColors.NONE;
    int ballsIntaked = 0;
    int ballsShot = 0;
    int rotations = 0; //number of 120 degree rotations

    //If this is true that means we can shoot the first shot in the shooting sequence
    public boolean chambered = false;
    LimeLight.BallColors[] motif = {LimeLight.BallColors.P, LimeLight.BallColors.P, LimeLight.BallColors.G};

    private long ticksToTime(double ticks) {
        return Math.abs((long) (ticks / rotateTicks) * rotateWaitTime);
    }

    public BallDetector getSensors() {
        return ballDetector;
    }

    public boolean isRotating() {
        return rotating;
    }
    public int getShotsFired(){
        return ballsShot;
    }
    public int getBallsIntaked(){
        return ballsIntaked;
    }

    //Move one slot
    public void rotate() {
        //Already moving? don't rotate more
        if (rotating) {
            return;
        }

        if (targetPos > maxPos || rotations >= 2) {
            reset();
            return;
        }

        rotations++;

        targetPos += rotateTicks;
        rotateTimer.setWait(rotateWaitTime);
        rotating = true;


    }

    public void rotateDegree(double degree) {

        targetPos += degreesToTicks(degree);
        rotateTimer.setWait(ticksToTime(degreesToTicks(degree)));
        rotating = true;

        if (targetPos > maxPos) {
            reset();
        }
    }

    //Move back to starting/zero pos
    public void reset() {
        double ticksPassed = targetPos - intakeStartPos;
        //Calculate how much time to wait based on how many degrees away from zero pos
        rotateTimer.setWait(ticksToTime(ticksPassed));
        targetPos = intakeStartPos;
        chambered = false;
        rotating = true;
        rotations = 0;

    }


    public void shoot() {
        kicker.kick();
        Slot.shoot(slots,targetPos);
        ballsShot++;
    }

    public void saveBall() {
        //Read color sensor and save it
        savedBall = ballDetector.getBallColor();
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
                    slots[Slot.getIntakeSlot(targetPos)].setColor(savedBall);
                    slots[Slot.getIntakeSlot(targetPos)].setId(Slot.getIntakeSlot(targetPos));

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
        if (chambered) {
            setState(States.RESTING);
            sequence = 1;
            return;
        }
        switch (sequence){
            case 1:
                if (mode == Modes.UNSORTED){
                    rotateDegree(60);
                }else{
                    rotateDegree(Slot.rotateToColor(slots,targetPos,motif[ballsShot],60));
                }
                sequence++;
                break;
            case 2:
                if (!rotating){
                   // kicker.chamber();
                    sequence++;
                }
                break;
            case 3:
                if (kicker.isReady) {
                    chambered = true;
                }
                break;
        }
    }

    public void shootingSequence() {
        //AKA unsorted shooting
        if (ballsShot >= rapidFireShootNumber && sequence == 1) {
            ballsShot = 0;
            state = States.RESTING;
            reset();
            return;
        }
        switch (sequence) {
            case 1:
                if (!rotating) {
                    shoot();
                    sequence++;
                    return;
                }
                break;
            case 2:
                //Ball is shot
                if (kicker.getState() == Kicker.States.RESTING) {
                    sequence++;
                    if (ballsShot >= 3){
                        return;
                    }
                    if (mode == Modes.UNSORTED) {
                        rotate();
                    }else{
                        rotateDegree(Slot.rotateToColor(slots, targetPos, motif[ballsShot],120));
                    }
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

    public void setMode(Modes newMode) {
        mode = newMode;
    }
    public void setMotif(LimeLight.BallColors[] motif){
        this.motif = motif;
    }

    public Modes getMode() {
        return mode;
    }

    public Kicker getKicker() {
        return kicker;
    }

    public void initiate(HardwareMap hardwareMap) {
        rightServo = hardwareMap.servo.get("rspin");
        leftServo = hardwareMap.servo.get("lspin");
        ballDetector.initiate(hardwareMap);
        kicker.initiate(hardwareMap);
        rotateTicks = degreesToTicks(120);
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

        rightServo.setPosition(targetPos + rightOffset);
        leftServo.setPosition(targetPos + leftOffset);

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
                shootingSequence();
                break;
        }
    }

    public void status(Telemetry telemetry) {
        telemetry.addData("SpindexerPosition", rightServo.getPosition());
        telemetry.addData("SpindexerRotating", rotating);
        telemetry.addData("Spindexer Chambered",chambered);
        if (ballsShot < 3) {
            telemetry.addData("Spindexer Target Ball", motif[ballsShot]);
        }
        telemetry.addData("SpindexerState", state);
        telemetry.addData("SpindexerSequence", sequence);
        telemetry.addData("Spindexer rotations", rotations);
        telemetry.addData("Spindexer mode", mode);
        for (int i = 0; i < slots.length ; i++){
            telemetry.addData("Slots: " + i, slots[i]);
        }
        kicker.status(telemetry);
        ballDetector.status(telemetry);
    }

}
