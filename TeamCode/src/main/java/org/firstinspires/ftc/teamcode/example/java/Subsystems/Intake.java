package org.firstinspires.ftc.teamcode.example.java.Subsystems;

import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class Intake {
    public enum States {
        RESTING,
        INTAKING,
        OUTTAKING
    }

    States currentState = States.RESTING;

    public void setState(States newState) {
        currentState = newState;
    }

    public States getState() {
        return currentState;
    }

    DcMotor intakeMotor;
    ColorSensor colorSensor;
    public static double minPurpleR = 0;
    public static double maxPurpleR = 50;
    public static double minPurpleG = 0;
    public static double maxPurpleG = 50;
    public static double minPurpleB = 0;
    public static double maxPurpleB = 50;

    public static double minGreenR = 0;
    public static double maxGreenR = 50;
    public static double minGreenG = 0;
    public static double maxGreenG = 50;
    public static double minGreenB = 0;
    public static double maxGreenB = 50;
    public static double differenceThreshold = 1000;
    public static double outtakeTime = 400;
    int previousDistance;
    int intakeNumber = 0;
    double timeSnapshot = System.currentTimeMillis();

    public void outtake() {
        if (currentState != States.OUTTAKING) {
            timeSnapshot = System.currentTimeMillis();
        }
        currentState = States.OUTTAKING;
    }

    public void initiate(HardwareMap hardwareMap) {
        intakeMotor = hardwareMap.dcMotor.get("intake");
        colorSensor = hardwareMap.colorSensor.get("color");
    }

    public void update(Spindex spindex) {
        switch (currentState) {
            case RESTING:
                break;
            case INTAKING:
                intakeMotor.setPower(1);
                int difference = colorSensor.alpha() - previousDistance;
                //Every time we intake a ball, we check it's color and either spit it out or add it to the spindex and spin the spnndex
                //Essentially a rising edge detector
                if (difference > differenceThreshold) {
                    //First, is it green
                    if (colorSensor.green() > minGreenG && colorSensor.green() < maxGreenG) {
                        //Okay its green
                        if (spindex.qauntity(Spindex.BallColor.G) >= 1) {
                            outtake();
                        } else {
                            spindex.setDial(intakeNumber, Spindex.BallColor.G);
                            intakeNumber += 1;

                        }
                    } else if (colorSensor.red() > minPurpleR && colorSensor.red() < maxPurpleR) {
                        //Okay its purple
                        if (spindex.qauntity(Spindex.BallColor.P) >= 2) {
                            outtake();
                        } else {
                            spindex.setDial(intakeNumber, Spindex.BallColor.P);
                            intakeNumber += 1;
                        }
                    }
                }
                break;
            case OUTTAKING:
                intakeMotor.setPower(-1);
                if (System.currentTimeMillis() - timeSnapshot > outtakeTime) {
                    setState(States.INTAKING);
                }
                break;
        }
        if (intakeNumber > 2){
            intakeNumber = 0;
        }
        previousDistance = colorSensor.alpha();
    }

}
