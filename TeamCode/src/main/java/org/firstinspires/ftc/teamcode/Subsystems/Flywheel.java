package org.firstinspires.ftc.teamcode.Subsystems;


import com.acmerobotics.dashboard.config.Config;
import com.pedropathing.control.PIDFCoefficients;
import com.pedropathing.control.PIDFController;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;

@Config
public class Flywheel {
    public enum States {
        SPINNING,
        RESTING,
    }
    public boolean isReady = false;
    public static long waitTime = 500;
    public static double targetVelocity = 1600;
    public static double kP = 0.0012;
    public static double kI = 0;
    public static double kD = 0;
    public static double kF = 0.00041;
    public static double tolerance = 100;
    public static double maxPower = .92;
    public double currentVelocity = 0.0000;
    public static double rMod = 1;
    public static double lMod = -1;

    Timer overideTimer = new Timer();

    PIDFController pidfController = new PIDFController(new PIDFCoefficients(kP,kI,kD,kF));



    public States getState() {
        return currentState;

    }


    public States currentState = States.SPINNING;
    DcMotorEx flywheel;
    DcMotorEx flywheel2;
    public void initiate(HardwareMap hardwareMap) {
        flywheel = hardwareMap.get(DcMotorEx.class, "flywheel");
        flywheel2 = hardwareMap.get(DcMotorEx.class, "fly2");
        flywheel.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        flywheel.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        flywheel2.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
    }


    public void setState(States newState) {
        if (newState == States.SPINNING && currentState != States.SPINNING) {
            overideTimer.setWait(waitTime);
        }
        currentState = newState;
    }
    double prevPos = 0;
    double posDifference = 0;
    long timeSnapshot = System.currentTimeMillis();
    double timeDifference = 0;
    double power = 0;

    public void update() {
        pidfController.setCoefficients((new PIDFCoefficients(kP,kI,kD,0)));
        pidfController.setTargetPosition(targetVelocity);
        pidfController.updatePosition(currentVelocity);

        flywheel.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        posDifference = flywheel.getCurrentPosition() - prevPos;
        timeDifference = (double) ((System.currentTimeMillis() - timeSnapshot) / 1000.00); //Convert to seconds

        timeSnapshot = System.currentTimeMillis();
        prevPos = flywheel.getCurrentPosition();

        currentVelocity = posDifference / (timeDifference) * lMod;


      switch (getState()){
          case RESTING:
              power = 0;
              isReady = false;
              break;
          case SPINNING:
            //  power = maxPower;
              power = pidfController.run() + (kF * targetVelocity);
              if (overideTimer.doneWaiting() || Math.abs(targetVelocity - currentVelocity) < tolerance)
              break;
      }

        if (Math.abs(power) > maxPower){
            power = Math.signum(power) * maxPower;
        }
        flywheel.setPower(power * rMod);
        flywheel2.setPower(power * lMod);
    }
    public void status (Telemetry telemetry) {
        telemetry.addData("Calculated velocity", currentVelocity);
        telemetry.addData("Calculated VelocityError", targetVelocity - currentVelocity);
        telemetry.addData("Current Pos", flywheel.getCurrentPosition());
        telemetry.addData("Prev Pos", prevPos);
        telemetry.addData("Pos diff", posDifference);
        telemetry.addData("Flywheel power", power);
        telemetry.addData("flywheelReady",isReady);
    }
}
