package org.firstinspires.ftc.teamcode.example.java.Subsystems;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.HardwareMap;

@Config
public class Webcam{
    public enum Sequence{
        PPG,
        PGP,
        GPP,
        NONE
    }
    //Vision portal info added later, etc
    CRServo servo;
    public static double goalPosX = 0;
    public static double goalPosY = 0;
    public static double kP = .0001;

    double goalHeading; //Theta
    double goalDistance; //d
    public double goalX = 0;
    public double goalY = 0;
    double cameraXPos; // Where the camera is on the field
    double cameraYPos;
    //Whatever the position of the goal is, depends on team color
    double absoluteEncoderPos; //Logic tbd

    Sequence targetSequence = Sequence.NONE;
    public Sequence getSequence(){
        return targetSequence;
    }
    public double getGoalHeading(){
        return goalHeading;
    }
    public double getGoalDistance(){
        return goalDistance;
    }
    public void initiate(HardwareMap hardwareMap, TeamColor.Color color) {
        servo = hardwareMap.crservo.get("cam");
        if (color.equals(TeamColor.Color.RED)){
            //RED Goal
        }else if (color.equals(TeamColor.Color.BLUE)){
            //BLUE Goal
        }
    }
    public void update() {
        if (targetSequence.equals(Sequence.NONE)) {
            //Scan Obelisk Code
        }
        //Scan Goal, set goal heading
        //It is a CR servo, some PID logic will be used to rotate
        //Rotate until the center of the tag is center of the camera on x-axis
        double xDisplacement = 100;
        servo.setPower(xDisplacement * kP);
        //Absolute encoder records heading
    }
}
