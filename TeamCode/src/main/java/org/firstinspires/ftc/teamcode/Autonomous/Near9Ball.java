package org.firstinspires.ftc.teamcode.Autonomous;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierCurve;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import com.qualcomm.robotcore.hardware.Gamepad;

import org.firstinspires.ftc.teamcode.Autonomous.Commands.Command;
import org.firstinspires.ftc.teamcode.Subsystems.Flywheel;
import org.firstinspires.ftc.teamcode.Subsystems.FollowerHandler;
import org.firstinspires.ftc.teamcode.Subsystems.Intake;
import org.firstinspires.ftc.teamcode.Subsystems.Lights;
import org.firstinspires.ftc.teamcode.Subsystems.Spindexer.Spindexer;
import org.firstinspires.ftc.teamcode.Subsystems.Timer;
import org.firstinspires.ftc.teamcode.Subsystems.Turret;
import org.firstinspires.ftc.teamcode.pedroPathing.Constants;

@Autonomous
public class Near9Ball extends LinearOpMode {
    private Follower follower;
    private Flywheel flywheel = new Flywheel();
    private Intake intake = new Intake();
    private Lights lights = new Lights();
    private Spindexer spindexer = new Spindexer();
    private Turret turret = new Turret();
    private Timer pathTimer = new Timer();
    FollowerHandler followerHandler = new FollowerHandler();
    Command command;
    private int path = 0;
    private int redX = 1;

    private double calculateHeading(double heading) {
        if (redX == 1) {
            return  Math.toRadians(heading);
        }
        return Math.toRadians(180 - heading);
    }

    PathChain shootPrescore,
            intakeFirst, intakeFirst2, gateOpen, shoot2;

    public void buildPaths() {

        final Pose startPose = new Pose(57.2836845026299 * redX, 135.4077434731531, Math.toRadians(90)); // Start Pose of our robot.
        final Pose shootPose = new Pose(59.30539192071153 * redX, 120.07646221936741, Math.toRadians(90));
        final Pose intakeFirstPose = new Pose(43 * redX, 85, calculateHeading(180));
        final Pose intakeFirst2Pose = new Pose(23 * redX, intakeFirstPose.getY());
        final Pose openGatePose = new Pose(12.855805597579424 * redX, 65.394856278366106, calculateHeading(180));
        final Pose openGateControl = new Pose(43.615260968229954 * redX, 74.06959152798788, calculateHeading(180));
        final Pose shootPose2 = new Pose(45.31571482602118 * redX, 83.43721633888049, calculateHeading(180));

        follower.setStartingPose(startPose);

        shootPrescore = follower.pathBuilder()
                .addPath(
                        new BezierLine(
                                startPose,
                                shootPose
                        )
                )
                .setGlobalConstantHeadingInterpolation(calculateHeading(90))
                .build();
        intakeFirst = follower.pathBuilder()
                .addPath(
                        new BezierLine(
                                shootPose,
                                intakeFirstPose
                        )
                )
                .setLinearHeadingInterpolation(calculateHeading(90),calculateHeading(180))
                .build();
        intakeFirst2 = follower.pathBuilder()
                .addPath(
                        new BezierLine(
                                intakeFirstPose,
                                intakeFirst2Pose
                        )
                )
                .setGlobalConstantHeadingInterpolation(calculateHeading(180))
                .build();
        gateOpen = follower.pathBuilder()
                .addPath(
                        new BezierCurve(
                                intakeFirst2Pose,
                                openGateControl,
                                openGatePose
                        )
                )
                .setGlobalConstantHeadingInterpolation(calculateHeading(180))
                .build();
        shoot2 = follower.pathBuilder()
                .addPath(
                        new BezierLine(
                                openGatePose,
                                shootPose2
                        )
                )
                .setGlobalConstantHeadingInterpolation(calculateHeading(180))
                .build();
    }

    @Override
    public void runOpMode() throws InterruptedException {
        Gamepad previousGamepad1 = new Gamepad();
        followerHandler.initiate(hardwareMap);
        follower = followerHandler.getFollower();

        flywheel.initiate(hardwareMap);
        intake.initiate(hardwareMap);
        lights.initiate(hardwareMap);
        lights.setTeamColor(Lights.TeamColors.RED);
        spindexer.initiate(hardwareMap);
        spindexer.setMode(Spindexer.Modes.UNSORTED);
        turret.initiate(hardwareMap);
        turret.setState(Turret.States.AIM);

        command = new Command(intake, spindexer, flywheel, lights, follower);

        //Chamber
        boolean ready = false;
        spindexer.reset();
        while (!opModeIsActive() && !isStopRequested()) {
            if (!spindexer.isRotating() && !ready) {
                ready = true;
                spindexer.rotateDegree(60);
            }
            boolean triangle = gamepad1.triangle && !previousGamepad1.triangle;
            previousGamepad1.copy(gamepad1);

            if (triangle) {
                redX = redX * -1;
                lights.switchTeamColor();
            }

            spindexer.update();
            lights.update(telemetry);

        }

        waitForStart();
        buildPaths();

        //   LimeLight limeLight = new LimeLight();
        //  limeLight.initiate(hardwareMap);
        if (isStopRequested()) {
            lights.save();
            return;
        }
        while (opModeIsActive()) {
            telemetry.addData("Auto path", path);

            followerHandler.update();
            followerHandler.save();
            flywheel.update();
            intake.update();
            lights.update(telemetry);
            lights.save();
            spindexer.update();


            flywheel.status(telemetry);
            spindexer.status(telemetry);


            telemetry.update();

            switch (path) {
                case 0:
                    path += command.follow(750, shootPrescore);
                    flywheel.setState(Flywheel.States.SPINNING);
                    break;
                case 1:
                    path += command.shoot();
                    if (command.completed()){
                        flywheel.setState(Flywheel.States.RESTING);
                    }
                    break;
                case 2:
                    path += command.follow(2000, intakeFirst);
                    break;
                case 3:
                    path += command.follow(intakeFirst2,.55);
                    break;
                case 4:
                    path += command.intake();
                    break;
                case 5:
                    path += command.follow(2000,gateOpen,.8);
                    if (command.completed()){
                        spindexer.rotateDegree(60);
                        flywheel.setState(Flywheel.States.SPINNING);
                    }
                    break;
                case 6:
                    path += command.follow(2000,shoot2);
                    break;
                case 7:
                    path += command.shoot();
                    break;
            }
        }
    }
}
