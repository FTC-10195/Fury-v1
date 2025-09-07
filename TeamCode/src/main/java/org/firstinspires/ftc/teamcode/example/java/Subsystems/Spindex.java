package org.firstinspires.ftc.teamcode.example.java.Subsystems;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.example.java.MainTeleOp;

public class Spindex {
    public enum BallColor {
        P,
        G,
        NONE
    }
    Servo spindex;
    //Im not sure what the spindex will look like
    //But so I will number them
    BallColor[] dials = {BallColor.NONE, BallColor.NONE, BallColor.NONE};
    BallColor[] targetDials = {BallColor.NONE, BallColor.NONE, BallColor.NONE};

    public static double dial1ShootPos = 0.3;
    public static double dial2ShootPos = 0.5;
    public static double dial3ShootPos = 0.7;
    public static double rotateTime = 400;
    int shotCounter = 0;
    int currentDial = 0;
    public boolean readyToFire = false;
    double timeSnapshot = System.currentTimeMillis();
    public void initiate(HardwareMap hardwareMap){
        spindex = hardwareMap.servo.get("spin");
    }
    public void setDial(int dial, BallColor color){
        dials[dial] = color;
    }
    public BallColor[] getDials(){
        return dials;
    }
    public int qauntity(BallColor targetColor){
        int n = 0;
        for (int i = 0; i < dials.length; i++){
            if (dials[i].equals(targetColor)){
                n++;
            }
        }
        return n;
    }
    public boolean isFull(){
        return  qauntity(BallColor.NONE) == 0;
    }
    public boolean isEmpty(){
        return  qauntity(BallColor.NONE) == 3;
    }
    public void setTargetDials(Webcam.Sequence sequence) {
        switch (sequence) {
            case GPP:
                targetDials = new BallColor[]{BallColor.G, BallColor.P, BallColor.P};
                break;
            case PGP:
                targetDials = new BallColor[]{BallColor.P, BallColor.G, BallColor.P};
                break;
            case PPG:
                targetDials = new BallColor[]{BallColor.P, BallColor.P, BallColor.G};
                break;
            case NONE:
                targetDials = new BallColor[]{BallColor.NONE, BallColor.NONE, BallColor.NONE};
                break;
        }
    }
    public void shoot(){
        dials[currentDial] = BallColor.NONE;
        shotCounter += 1;
        if (shotCounter == 3){
            shotCounter = 0;
        }
    }

    public void rotate(int targetDial) {
        //If the target dial isn't the current dial then
        if (targetDial != currentDial){
            currentDial = targetDial;
            timeSnapshot = System.currentTimeMillis();
        }
        int displacement = targetDial - currentDial;
        //Ex, 1 rotation is 1 rotate time, 2 is 2 rotate time, etc
        double totalTime = Math.abs(displacement * rotateTime);
        if (System.currentTimeMillis() - timeSnapshot > totalTime) {
            readyToFire = true;
        } else {
            readyToFire = false;
        }
    }

    public void update() {
        //Lets calculate target dial!!!

        //LOGIC EXPLAINED
        /* DIAL# CURRENT  WANT
        FIRE COUNTER = 0
        0 P /G/
        1 P P
        2 G P
        Target = 2

        FIRE COUNTER = 1
        0 P G
        1 P /P/
        2 NONE P
        Target = 0

        FIRE COUNTER = 2
        0 NONE G
        1 P P
        2 NONE /P/
        Target = 1


         */
        int targetDial = 0;
        for (int i = 0; i < targetDials.length; i++) {
            if ((dials[i]).equals(targetDials[currentDial + shotCounter])) {
                targetDial = i;
            }
        }
        rotate(targetDial);
        switch (currentDial){
            case 0:
                spindex.setPosition(dial1ShootPos);
                break;
            case 1:
                spindex.setPosition(dial2ShootPos);
                break;
            case 2:
                spindex.setPosition(dial3ShootPos);
                break;
        }
    }

}
