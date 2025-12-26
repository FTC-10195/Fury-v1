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
    public static double maxDegrees = 620;
    static double degreesToTicks(double degree){
        return degree/maxDegrees;
    }
    static double ticksToDegrees(double ticks){
        return ticks * maxDegrees;
    }
    public static double intakeStartPos = .15; //"Zero pos of spindexr
    public static double shootingTicks = degreesToTicks(60); //60 degrees / 600 degrees per tick -> .1 ticks
    public static double shootingStartPos = intakeStartPos + shootingTicks;
    public static double maxPos = 1; //When spindexer is at or over max -> reset
    public static double rotateTicks = degreesToTicks(120); //How much the spindexer rotates for 1 slot (120 degrees)
    public static long rotateWaitTime = 400; //How long it takes a spindexer to rotate 1 slot
    public static double kInterpolation = 5;
    States state = States.RESTING;
    Modes mode = Modes.MANUAL;
    Servo rightServo; //Dominant
    Servo leftServo;
    Kicker kicker = new Kicker(); //Child subsystem because kicker is completely dependant on spindexer
    BallDetector ballDetector = new BallDetector();
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
    int rotations = 0; //number of 120 degree rotations

    //If this is true that means we can shoot the first shot in the shooting sequence
    boolean chambered = false;
    private double initialPos = intakeStartPos; //For lerp
    double servoPos = intakeStartPos;
    private long ticksToTime(double ticks){
        return Math.abs((long) (ticks / rotateTicks) * rotateWaitTime);
    }
    public static double interpolateEaseOutExp(
            double startPos,
            double targetPos,
            long startTime,
            long totalTime
    ) {
        long currentTime = System.currentTimeMillis();
        if (currentTime <= startTime) return startPos;
        if (currentTime >= startTime + totalTime) return targetPos;

        // Normalize time to [0, 1]
        double t = (double) (currentTime - startTime) / totalTime;

        // Exponential ease-out
        double factor = 1.0 - Math.pow(2.0, -kInterpolation * t);

        return startPos + (targetPos - startPos) * factor;
    }
    public BallDetector getSensors(){
        return ballDetector;
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

        initialPos = servoPos;

        rotations++;

        targetPos += rotateTicks;
        rotateTimer.setWait(rotateWaitTime);
        rotating = true;



    }
    public void rotateDegree(int degree){
        //Already moving? don't rotate more
        if (rotating) {
            return;
        }

        initialPos = servoPos;

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
        initialPos = servoPos;
        rotateTimer.setWait(ticksToTime(ticksPassed));
        targetPos = intakeStartPos;
        rotating = true;
        rotations = 0;

    }
    public void rotateToShoot(){
        //Already moving? don't rotate more
        if (rotating) {
            return;
        }

        targetPos += shootingTicks;
        initialPos = servoPos;
        rotateTimer.setWait(ticksToTime(shootingTicks));
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
                saveBall();
                //If target
                if (savedBall != LimeLight.BallColors.NONE) {
                    //Shooting
                    rotateToShoot();
                }else{
                   rotate();
                }
                sequence++;
                break;
            case 2:
                //This means the spindexer reset, and if it reset and the code reaches this point
                //That means we can't find our target ball -> give up
                //Might as well say chambered because when we shoot sorted we wont find it anyways
                //HOWEVER, we must move it in position to shoot
                if (rotations == 0){
                    rotateToShoot();
                    sequence++;
                }
                //Finished rotating
                if (!rotating){
                    //If target -> shoot
                    if (savedBall != LimeLight.BallColors.NONE) {
                        //Usually shoot here, just say chambered
                        chambered = true;
                    }//Not target? check again
                    sequence = 1;
                }
                break;
            case 3:
                if (!rotating){
                    sequence = 1;
                    chambered = true;
                }
                break;
        }
    }

    boolean switchToUnsorted = false;
    public void sortedShootingSequence() {
        if (chambered) {
            //FIRE
            shoot();
            sequence = -1;
        }
        if (ballsShot >= 3) {
            ballsShot = 0;
            state = States.RESTING;
            reset();
            return;
        }
        if (switchToUnsorted){
            if (!rotating){
                mode = Modes.UNSORTED;
                sequence = 1;
            }
            return;
        }
        switch (sequence) {
            //-1 and 0 are special cases that is only accessible when chambered
            case -1:
                if (kicker.getState() == Kicker.States.RESTING){
                    reset();
                    sequence++;
                }
                break;
            case 0:
                if (!rotating){
                    sequence++;
                }
                break;
            case 1:
                saveBall();
                //If target -> rotate to shoot
                if (savedBall != LimeLight.BallColors.NONE){
                    rotateToShoot();
                }else{
                    rotate();
                }
                //This means it reset and cant find target ball -> switch to rapid fire
                if (rotations == 0){
                    switchToUnsorted = true;
                    return;
                }
                sequence++;
                break;
            case 2:
                //If ball
                if (!rotating) {
                    if (savedBall != LimeLight.BallColors.NONE) {
                        shoot();
                        sequence++;
                    }else{
                        sequence = 1;
                    }
                }
                break;
            case 3:
                //Done shooting
                if (kicker.getState() == Kicker.States.RETURNING){
                    reset();
                    sequence++;
                }
                break;
            case 4:
                //Done rotating
                if (!rotating){
                    sequence = 1;
                }
                break;
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
        switchToUnsorted = false;
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
        ballDetector.initiate(hardwareMap);
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
        servoPos = interpolateEaseOutExp(initialPos,targetPos,rotateTimer.getStartTime(),rotateTimer.getWaitTime());

        rightServo.setPosition(servoPos);
        leftServo.setPosition(servoPos);

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
        telemetry.addData("SpindexerPosition", rightServo.getPosition());
        telemetry.addData("SpindexerRotating", rotating);
        telemetry.addData("SpindexerState", state);
        telemetry.addData("SpindexerSequence", sequence);
        telemetry.addData("Spindexer rotations",rotations);
        kicker.status(telemetry);
        ballDetector.status(telemetry);
    }

}
