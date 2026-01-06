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
import org.firstinspires.ftc.teamcode.Subsystems.LimeLight;
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
    LimeLight limeLight = new LimeLight();
    FollowerHandler followerHandler = new FollowerHandler();
    Command command;
    private int path = 0;

    private double calculateHeading(double heading) {
        if (lights.getTeamColor() == Lights.TeamColors.BLUE) {
            return Math.toRadians(heading);
        }
        return Math.toRadians(180 - heading);
    }
    private double calculateX(double x){
        if (lights.getTeamColor() == Lights.TeamColors.BLUE){
            return x;
        }
        return 144 - x;
    }

    PathChain shootPrescore,
            intakeFirst, intakeFirst2, gateOpen, shoot2, intakeSecond, intakeSecond2,shoot3, leave;

    public void buildPaths() {

        final Pose startPose = new Pose(calculateX(57.2836845026299), 135.4077434731531, Math.toRadians(90)); // Start Pose of our robot.
        final Pose shootPose = new Pose(calculateX(59.30539192071153), 120.07646221936741, Math.toRadians(90));
        final Pose intakeFirstPose = new Pose(calculateX(43), 85, calculateHeading(180));
        final Pose intakeFirst2Pose = new Pose(calculateX(20), intakeFirstPose.getY(),calculateHeading(180));
        final Pose openGatePose = new Pose(calculateX(13.855805597579424), 79.55521936459908, calculateHeading(180));
        final Pose openGateControl = new Pose(calculateX(39.19468608169441), 79.70499243570346, calculateHeading(180));
        final Pose shootPose2 = new Pose(calculateX(50.54415658093797), 86.70499243570347, calculateHeading(180));
        final Pose intakeSecondPose = new Pose(calculateX(45.75141830559758), 59.03782148260212, calculateHeading(180));
        final Pose intakeSecond2Pose = new Pose(calculateX(10.452344931921337),58.98940998487141,calculateHeading(180));
        final Pose shootPose3 = new Pose(calculateX(50.54415658093797), 86.70499243570347, calculateHeading(180));
        final Pose shootPose3Control = new Pose(calculateX(28.323279122541603),38.34190620272315);
        final Pose leavePose = new Pose(calculateX(43.355049167927376),71.45537065052949,calculateHeading(180));

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
                        new BezierLine(
                                shootPose2,
                                intakeSecondPose
                        )
                )
                .setGlobalConstantHeadingInterpolation(calculateHeading(180))
                .build();
        intakeSecond2 = follower.pathBuilder()
                .addPath(
                        new BezierLine(
                                intakeSecondPose,
                                intakeSecond2Pose
                        )
                )
                .setGlobalConstantHeadingInterpolation(calculateHeading(180))
                .build();
        shoot3 = follower.pathBuilder()
                .addPath(
                        new BezierCurve(
                                intakeSecond2Pose,
                                shootPose3Control,
                                shootPose3
                        )
                )
                .setGlobalConstantHeadingInterpolation(calculateHeading(180))
                .build();
        leave = follower.pathBuilder()
                .addPath(
                        new BezierLine(
                                shootPose3,
                                leavePose
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
        limeLight.initiate(hardwareMap);
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
            boolean options = gamepad1.options && !previousGamepad1.options;
            boolean rb = gamepad1.right_bumper && !previousGamepad1.right_bumper;
            previousGamepad1.copy(gamepad1);

            if (options) {
                lights.switchTeamColor();
                buildPaths();
            }
            if (rb){
                buildPaths();
            }

            spindexer.update();
            lights.update(telemetry);
            telemetry.update();

        }

        waitForStart();


        //   LimeLight limeLight = new LimeLight();
        //  limeLight.initiate(hardwareMap);
        if (isStopRequested()) {
            lights.save();
            return;
        }
        while (opModeIsActive()) {
            telemetry.addData("Auto path", path);

            limeLight.update(telemetry);
            spindexer.setMotif(limeLight.getMotif());
            lights.setMotif(limeLight.getMotif());
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
                    }
                    break;
                case 2:
                    path += command.follow(1500, intakeFirst);
                    if (spindexer.getKicker().getState() == Kicker.States.RESTING){
                        command.resetSpindexer();
                    }
                    break;
                case 3:
                    path += command.follow(4000,intakeFirst2, .45);
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
                    path += command.follow(1500,intakeSecond);
                    if (spindexer.getKicker().getState() == Kicker.States.RESTING){
                        command.resetSpindexer();
                    }
                    break;
                case 8:
                    path += command.follow(4000,intakeSecond2,.45);
                    command.startIntaking();
                    if (command.completed()){
                        command.stopIntaking();
                        spindexer.setState(Spindexer.States.CHAMBER);
                        flywheel.setState(Flywheel.States.SPINNING);
                    }
                    break;
                case 9:
                    path += command.follow(1800, shoot3);
                    break;
                case 10:
                    path += command.shoot();
                    if (command.completed()){
                        flywheel.setState(Flywheel.States.RESTING);
                    }
                    break;
                case 11:
                    path += command.follow(leave);
                    break;
            }
        }
    }
}
