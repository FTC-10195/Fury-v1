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
import org.firstinspires.ftc.teamcode.Subsystems.Spindexer.Kicker;
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
            return Math.toRadians(heading);
        }
        return Math.toRadians(180 - heading);
    }

    PathChain shootPrescore,
            intakeFirst, intakeFirst2, gateOpen, shoot2, intakeSecond,shoot3;

    public void buildPaths() {

        final Pose startPose = new Pose(57.2836845026299 * redX, 135.4077434731531, Math.toRadians(90)); // Start Pose of our robot.
        final Pose shootPose = new Pose(59.30539192071153 * redX, 120.07646221936741, Math.toRadians(90));
        final Pose intakeFirstPose = new Pose(43 * redX, 85, calculateHeading(180));
        final Pose intakeFirst2Pose = new Pose(19 * redX, intakeFirstPose.getY());
        final Pose openGatePose = new Pose(13.855805597579424 * redX, 77.8124054462935, calculateHeading(180));
        final Pose openGateControl = new Pose(28.976834341906205 * redX, 76.90166414523449, calculateHeading(180));
        final Pose shootPose2 = new Pose(47.31571482602118 * redX, 84.43721633888049, calculateHeading(180));
        final Pose intakeSecondPose = new Pose(11.191660363086233 * redX, 61.03782148260212, calculateHeading(180));
        final Pose intakeSecondPoseControl = new Pose(42.91934568835098 * redX, 47.70953101361574);
        final Pose shootPose3 = new Pose(47.31571482602118 * redX, 84.43721633888049, calculateHeading(180));


        followerHandler.setStartingPose(startPose);
        followerHandler.save();

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
                .setLinearHeadingInterpolation(calculateHeading(90), calculateHeading(180))
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
        intakeSecond = follower.pathBuilder()
                .addPath(
                        new BezierCurve(
                                shootPose2,
                                intakeSecondPoseControl,
                                intakeSecondPose
                        )
                )
                .setGlobalConstantHeadingInterpolation(calculateHeading(180))
                .build();
        shoot3 = follower.pathBuilder()
                .addPath(
                        new BezierLine(
                                intakeSecondPose,
                                shootPose3
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
        lights.setTeamColor(Lights.TeamColors.BLUE);
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
            turret.setPose(follower.getPose());
            turret.setGoal(lights.getTeamColor());
            turret.update();

            flywheel.setTargetVelocity(Flywheel.calculateTargetVelocity(follower.getPose(),turret.getGoal()));
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
                    turret.setState(Turret.States.AIM);
                    path += command.follow(750, shootPrescore);
                    flywheel.setState(Flywheel.States.SPINNING);
                    break;
                case 1:
                    path += command.shoot();
                    if (command.completed()) {
                        flywheel.setState(Flywheel.States.RESTING);
                        turret.setState(Turret.States.MANUAL);
                        turret.setOverride(.5 + (.3 * redX));
                    }
                    break;
                case 2:
                    path += command.follow(1500, intakeFirst);
                    if (spindexer.getKicker().getState() == Kicker.States.RESTING){
                        command.resetSpindexer();
                    }
                    break;
                case 3:
                    path += command.follow(3000,intakeFirst2, .6);
                    command.startIntaking();
                case 4:
                    path += command.follow(2000, gateOpen);
                    if (command.completed()) {
                        flywheel.setState(Flywheel.States.SPINNING);
                    }
                    break;
                case 5:
                    path += command.follow(1800, shoot2);
                    if (command.getTimePassed() > 500 && spindexer.getState() != Spindexer.States.CHAMBER) {
                        command.stopIntaking();
                        spindexer.setMode(Spindexer.Modes.SORTED);
                        spindexer.setState(Spindexer.States.CHAMBER);
                    }
                    break;
                case 6:
                    path += command.shoot();
                    if (command.completed()) {
                        flywheel.setState(Flywheel.States.RESTING);
                    }
                    break;
                case 7:
                    path += command.follow(intakeSecond);
                    break;
                case 8:
                    path += command.intake(3000);
                    if (command.completed()){
                        spindexer.setState(Spindexer.States.CHAMBER);
                        flywheel.setState(Flywheel.States.SPINNING);
                    }
                    break;
                case 9:
                    path += command.follow(1800, shoot3);
                    break;
                case 10:
                    path += command.shoot();
                    break;
            }
        }
    }
}
