package org.firstinspires.ftc.teamcode.TeleOp;

import static org.firstinspires.ftc.teamcode.Subsystems.Conveyor.States.EJECT;
import static org.firstinspires.ftc.teamcode.Subsystems.Conveyor.States.OFF;
import static org.firstinspires.ftc.teamcode.Subsystems.Conveyor.States.ON;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Gamepad;

import org.firstinspires.ftc.teamcode.Subsystems.Conveyor;
import org.firstinspires.ftc.teamcode.Subsystems.Drivetrain;
import org.firstinspires.ftc.teamcode.Subsystems.Flywheel;
import org.firstinspires.ftc.teamcode.Subsystems.Intake;
import org.firstinspires.ftc.teamcode.Subsystems.Kicker;
import org.firstinspires.ftc.teamcode.Subsystems.Trigger;

@TeleOp
public class DriveOnly extends LinearOpMode {
    public enum State{
        RESTING,
        INTAKING,
        SPINNING,
        EJECTING

    }
    @Override
    public void runOpMode() throws InterruptedException {
        State currentState = State.RESTING;
        waitForStart();
        if (isStopRequested()) return;

        Conveyor conveyor = new Conveyor();
        conveyor.initiate(hardwareMap);
        Flywheel flywheel = new Flywheel();
        flywheel.initiate(hardwareMap);
        Trigger trigger = new Trigger();
        trigger.initiate(hardwareMap);
        Intake intake = new Intake();
        intake.initiate(hardwareMap);
        Drivetrain drivetrain = new Drivetrain();
        drivetrain.initiate(hardwareMap);
        Kicker kicker = new Kicker();
        kicker.initiate(hardwareMap);
        Gamepad previousGamepad1 = new Gamepad();
        while (opModeIsActive()) {
            boolean LB = gamepad1.left_bumper && !previousGamepad1.left_bumper;
            boolean RB = gamepad1.right_bumper && !previousGamepad1.right_bumper;
            boolean X = gamepad1.cross && !previousGamepad1.cross;
            boolean LT = gamepad1.left_trigger > 0.1 && previousGamepad1.left_trigger <= 0.1;
            boolean RT = gamepad1.right_trigger > 0.1 && previousGamepad1.right_trigger <= 0.1;
            previousGamepad1.copy(gamepad1);

            switch (currentState) {
                case RESTING:
                    if (RT) {
                        currentState = State.SPINNING;
                        flywheel.setState(Flywheel.States.SPINNING);
                    } else if (LT) {
                        currentState = State.INTAKING;
                    }
                    break;


                case SPINNING:
                    if (RT || LT) {
                        kicker.setState(Kicker.ServoState.SHOOTING);
                    }
                    if (gamepad1.right_trigger > 0.1 && flywheel.IsReady){
                        trigger.setState(Trigger.States.SHOOTING);
                    } else {
                        trigger.setState(Trigger.States.RESTING);
                    }
                    if (kicker.getCurrentServoState() == Kicker.ServoState.SHOOTING){
                        conveyor.setState(ON);
                    } else {
                        conveyor.setState(OFF);
                    }
                    break;


                case INTAKING:
                    if (LT) {
                        currentState = State.RESTING;
                    } else if (X) {
                        currentState = State.EJECTING;
                    }
                    break;
                case EJECTING:
                    if (LT){
                        currentState = State.INTAKING;
                    }
                    if (X) {
                        currentState = State.RESTING;
                    }
                    break;

            }
            if (LB) {
                currentState = State.RESTING;
            }

            switch (currentState) {
                case RESTING:
                    flywheel.setState(Flywheel.States.RESTING);
                    intake.setState(Intake.States.OFF);
                    conveyor.setState(Conveyor.States.OFF);
                    trigger.setState(Trigger.States.RESTING);
                    break;
                case SPINNING:
                    conveyor.setState(Conveyor.States.ON);
                    intake.setState(Intake.States.OFF);
                    break;
                case INTAKING:
                    flywheel.setState(Flywheel.States.RESTING);
                    conveyor.setState(Conveyor.States.ON);
                    intake.setState(Intake.States.ON);
                    trigger.setState(Trigger.States.RESTING);
                    break;
                case EJECTING:
                    flywheel.setState(Flywheel.States.RESTING);
                    conveyor.setState(ON);
                    intake.setState(Intake.States.OUTTAKE);
                    trigger.setState(Trigger.States.RESTING);
                    break;

            }


            //Overrides
            if (gamepad1.square){
                conveyor.setState(EJECT);
                intake.setState(Intake.States.OUTTAKE);
            }
            if (gamepad1.circle){
                kicker.setState(Kicker.ServoState.SHOOTING);
            }
            if (gamepad1.triangle){
                trigger.setState(Trigger.States.SHOOTING);
                conveyor.setState(ON);
                if (flywheel.getState() == Flywheel.States.RESTING){
                    flywheel.setState(Flywheel.States.SPINNING);
                }
            }

            drivetrain.update(gamepad1.left_stick_x, gamepad1.left_stick_y, gamepad1.right_stick_x);
            telemetry.addData("State", currentState);
            telemetry.update();
        }
    }
}
