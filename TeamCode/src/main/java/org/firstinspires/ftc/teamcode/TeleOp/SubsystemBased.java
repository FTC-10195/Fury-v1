package org.firstinspires.ftc.teamcode.TeleOp;

import static org.firstinspires.ftc.teamcode.Subsystems.Conveyor.States.OFF;
import static org.firstinspires.ftc.teamcode.Subsystems.Conveyor.States.ON;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Gamepad;

import org.firstinspires.ftc.teamcode.Subsystems.Conveyor;
import org.firstinspires.ftc.teamcode.Subsystems.Drivetrain;
import org.firstinspires.ftc.teamcode.Subsystems.Flywheel;
import org.firstinspires.ftc.teamcode.Subsystems.Intake;
import org.firstinspires.ftc.teamcode.Subsystems.Trigger;

@TeleOp
public class SubsystemBased extends LinearOpMode {
    public enum States {
        EJECTING,
        INTAKING,
        RESTING,
        SPINNING,
    }
    @Override
    public void runOpMode() throws InterruptedException {
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
        Gamepad previousGamepad1 = new Gamepad();
        while (opModeIsActive()) {
            boolean LB = gamepad1.left_bumper && !previousGamepad1.left_bumper;
            boolean RB = gamepad1.right_bumper && !previousGamepad1.right_bumper;
            boolean X = gamepad1.x && !previousGamepad1.x;
            boolean LT = gamepad1.left_trigger > 0.1 && previousGamepad1.left_trigger <= 0.1;
            boolean RT = gamepad1.right_trigger > 0.1 && previousGamepad1.right_trigger <= 0.1;
            previousGamepad1.copy(gamepad1);
            if (LB){
                switch (conveyor.currentState){
                    case ON:
                        conveyor.setState(OFF);
                        break;
                    case OFF:
                        conveyor.setState(ON);
                        break;
                }
            }
            if (RB){
                switch (intake.getState()){
                    case ON:
                        intake.setState(Intake.States.OFF);
                        break;
                    case OFF:
                        intake.setState(Intake.States.ON);
                        break;
                }
            }
            if (X){
                switch (intake.getState()){
                    case ON:
                    case OFF:
                        intake.setState(Intake.States.OUTTAKE);
                        break;
                    case OUTTAKE:
                        intake.setState(Intake.States.OFF);
                        break;
                }
            }
            if (LT) {
                switch (flywheel.getState()) {
                    case RESTING:
                        flywheel.setState(Flywheel.States.SPINNING);
                        break;
                    case SPINNING:
                        flywheel.setState(Flywheel.States.RESTING);
                        break;
                }
            }

            if (RT) {
                trigger.setState(Trigger.States.SHOOTING);
            }
            drivetrain.update(gamepad1.left_stick_x, gamepad1.left_stick_y, gamepad1.right_stick_x);
            trigger.update();
            flywheel.update();
            intake.update();
            conveyor.update();
        }
    }
}
