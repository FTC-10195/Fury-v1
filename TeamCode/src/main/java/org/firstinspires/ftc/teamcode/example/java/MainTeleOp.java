package org.firstinspires.ftc.teamcode.example.java;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Gamepad;

import org.firstinspires.ftc.teamcode.example.java.Subsystems.FlyWheel;
import org.firstinspires.ftc.teamcode.example.java.Subsystems.Intake;
import org.firstinspires.ftc.teamcode.example.java.Subsystems.Spindex;
import org.firstinspires.ftc.teamcode.example.java.Subsystems.TeamColor;
import org.firstinspires.ftc.teamcode.example.java.Subsystems.Trigger;
import org.firstinspires.ftc.teamcode.example.java.Subsystems.Turret;
import org.firstinspires.ftc.teamcode.example.java.Subsystems.Webcam;

@TeleOp
public class MainTeleOp extends LinearOpMode {
    public enum States {
        RESTING,
        INTAKING,
        AIMING
    }

    @Override
    public void runOpMode() throws InterruptedException {
        States state = MainTeleOp.States.RESTING;
        TeamColor teamColor = new TeamColor(TeamColor.Color.RED);
        Webcam webcam = new Webcam();
        Intake intake = new Intake();
        FlyWheel flyWheel = new FlyWheel();
        Trigger trigger = new Trigger();
        Spindex spindex = new Spindex();
        Turret turret = new Turret();

        waitForStart();

        webcam.initiate(hardwareMap, teamColor.getColor());
        intake.initiate(hardwareMap);
        flyWheel.initiate(hardwareMap);
        trigger.initiate(hardwareMap);
        spindex.initiate(hardwareMap);
        turret.initiate(hardwareMap);
        Gamepad previousGamepad1 = new Gamepad();
        if (isStopRequested()) return;
        while (opModeIsActive()) {
            boolean LB = (gamepad1.left_bumper && !previousGamepad1.left_bumper);
            boolean RB = (gamepad1.right_bumper && !previousGamepad1.right_bumper);
            boolean RT = (gamepad1.right_trigger >= .1 && previousGamepad1.right_trigger < .1);
            previousGamepad1.copy(gamepad1);

            if (LB) {
                switch (state) {
                    case RESTING:
                        state = States.INTAKING;
                        intake.setState(Intake.States.INTAKING);
                        break;
                    case INTAKING:
                        state = States.RESTING;
                        break;
                }
            }
            if (RB) {
                state = States.RESTING;
            }
            if (RT) {
                switch (state) {
                    case RESTING:
                        state = States.AIMING;
                        break;
                    case INTAKING:
                        intake.outtake();
                        break;
                    case AIMING:
                        if (turret.readyToFire && spindex.readyToFire) {
                            spindex.shoot();
                            trigger.shoot();
                        }
                        break;
                }
            }

            switch (state) {
                case RESTING:
                    intake.setState(Intake.States.RESTING);
                    turret.setState(Turret.States.RESTING);
                    flyWheel.setState(FlyWheel.States.RESTING);
                    break;
                case INTAKING:
                    break;
                case AIMING:
                    turret.setState(Turret.States.ROTATING);
                    flyWheel.setState(FlyWheel.States.SPINNING);
                    if (spindex.isEmpty()) {
                        state = States.RESTING;
                    }
                    break;
            }
            webcam.update();
            intake.update(spindex);
            flyWheel.update();
            trigger.update();
            spindex.update();
            turret.update(webcam);
            telemetry.update();
        }
    }
}
