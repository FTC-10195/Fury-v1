package org.firstinspires.ftc.teamcode.example.java.Subsystems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.rowanmcalpin.nextftc.core.control.controllers.PIDFController;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class FlyWheel {
    public enum States {
        RESTING,
        SPINNING
    }
    public static double targetVelocity = 100; //Ticks Per Second, test number for now

    States currentState = States.RESTING;
    DcMotor flyMotorLeft;
    DcMotor flyMotorRight; //Right dominant

    public void initiate(HardwareMap hardwareMap) {
        flyMotorLeft = hardwareMap.dcMotor.get("flyL");
        flyMotorRight = hardwareMap.dcMotor.get("flyR");
        flyMotorRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    }

    public void setState(States state) {
        currentState = state;
    }

    public States getState() {
        return currentState;
    }
    double previousPosition = 0;
    long previousTime = System.currentTimeMillis();
    public static double kP = .0006; //Idk real small number fs tho
    public static double kI = 0; //EWWWWWW
    public static double kD = 0;
    PIDFController pidfController = new PIDFController(kP,kI,kD);
    double velocity = 0;
    public void update() {
        pidfController.setKP(kP);
        pidfController.setKI(kI);
        pidfController.setKD(kD);
        velocity = (flyMotorRight.getCurrentPosition() - previousPosition)/ (double)((System.currentTimeMillis() - previousTime)/1000); // average rate of change over a small time frame. DIY derivative fr
        double power = 0;
        switch (currentState) {
            case RESTING:
                break;
            case SPINNING:
                power = pidfController.calculate(velocity, targetVelocity);
                break;
        }
        flyMotorRight.setPower(power);
        flyMotorLeft.setPower(-power);
        previousPosition = flyMotorRight.getCurrentPosition();
        previousTime = System.currentTimeMillis();

    }
    public void status(Telemetry telemetry){
        telemetry.addData("FlyVelocity", velocity);
        telemetry.addData("FlyState",currentState);
        telemetry.addData("FlyPos",flyMotorRight.getCurrentPosition());
    }
}
