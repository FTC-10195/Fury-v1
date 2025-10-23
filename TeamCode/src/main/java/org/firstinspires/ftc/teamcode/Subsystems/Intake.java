package org.firstinspires.ftc.teamcode.Subsystems;



import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
@Config
public class Intake {
    public enum States {
        ON,
        OFF,
        OUTTAKE,
    }


    public States currentState = States.OFF;
    public static double intakePower = .9;
    public static double ejectPower = -.9;


    public void setState(States newStates){
        currentState = newStates;
    }


    DcMotor IntakeMotor;
    public States getState() {
        return currentState;
    }


    public void initiate(HardwareMap hardwaremap){
        IntakeMotor = hardwaremap.dcMotor.get("intake");
        IntakeMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    }
    public void update(){
        switch(currentState){
            case ON:
                IntakeMotor.setPower(intakePower);
                break;
            case OFF:
                IntakeMotor.setPower(0);
                break;
            case OUTTAKE:
                IntakeMotor.setPower(ejectPower);
                break;
        }
    }
}


