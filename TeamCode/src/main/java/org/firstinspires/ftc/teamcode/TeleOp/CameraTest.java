package org.firstinspires.ftc.teamcode.TeleOp;


import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.Pose;
import com.pedropathing.localization.Localizer;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Gamepad;

import org.firstinspires.ftc.teamcode.Subsystems.Drivetrain;
import org.firstinspires.ftc.teamcode.Subsystems.Webcam;
import org.firstinspires.ftc.teamcode.pedroPathing.Constants;


@TeleOp
public class CameraTest extends LinearOpMode {
    public enum State{
        RESTING,
        INTAKING,
        SPINNING,
        EJECTING

    }
    @Override
    public void runOpMode() throws InterruptedException {
        State currentState = State.RESTING;
        Follower follower;
        Webcam webcam = new Webcam();
        webcam.preInitiate(hardwareMap,telemetry);
        waitForStart();
        if (isStopRequested()) return;
        follower = Constants.createFollower(hardwareMap);
        follower.setStartingPose(new Pose(0,0,0));
        Drivetrain drivetrain = new Drivetrain();
        drivetrain.initiate(hardwareMap);
        Gamepad previousGamepad1 = new Gamepad();
        while (opModeIsActive()) {
            boolean LB = gamepad1.left_bumper && !previousGamepad1.left_bumper;
            boolean RB = gamepad1.right_bumper && !previousGamepad1.right_bumper;
            boolean X = gamepad1.cross && !previousGamepad1.cross;
            boolean LT = gamepad1.left_trigger > 0.1 && previousGamepad1.left_trigger <= 0.1;
            boolean RT = gamepad1.right_trigger > 0.1 && previousGamepad1.right_trigger <= 0.1;

            previousGamepad1.copy(gamepad1);
            follower.update();
            drivetrain.update(gamepad1.left_stick_x, gamepad1.left_stick_y, gamepad1.right_stick_x);
            webcam.update(telemetry);

            telemetry.addData("Pinpoint Pos", follower.getPose());
            telemetry.addData("State", currentState);
            telemetry.update();
        }
    }
}
