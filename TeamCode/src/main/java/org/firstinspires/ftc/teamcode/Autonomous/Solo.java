package org.firstinspires.ftc.teamcode.Autonomous;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierCurve;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import com.qualcomm.robotcore.hardware.Gamepad;

import org.firstinspires.ftc.teamcode.Subsystems.Flywheel;
import org.firstinspires.ftc.teamcode.Subsystems.Intake;
import org.firstinspires.ftc.teamcode.Subsystems.Lights;
import org.firstinspires.ftc.teamcode.Subsystems.Spindexer.Spindexer;
import org.firstinspires.ftc.teamcode.Subsystems.Timer;
import org.firstinspires.ftc.teamcode.pedroPathing.Constants;

@Autonomous
public class Solo extends LinearOpMode {
    private Follower follower;
    private Flywheel flywheel = new Flywheel();
    private Intake intake = new Intake();
    private Lights lights = new Lights();
    private Spindexer spindexer = new Spindexer();
    private Timer sequenceTimer = new Timer();
    private int sequence = 0;
    private int redX = 1;
    private double redHeading = Math.toRadians(90);
    private double calculateRedHeading(double heading){
        if (redX == 1){
            return 0;
        }
        return 180 - (heading*2);
    }
    PathChain shootPrescore,
    intakeFirst, intakeFirst2;
    public void buildPaths() {

        final Pose startPose = new Pose(57.2836845026299 * redX, 135.4077434731531, Math.toRadians(90)); // Start Pose of our robot.
        final Pose shootPose = new Pose(59.30539192071153 * redX, 120.07646221936741,Math.toRadians(90));
        final Pose intakeFirstPose = new Pose(43 * redX, 82,Math.toRadians(180) + calculateRedHeading(180));
        final Pose intakeFirst2Pose = new Pose(intakeFirstPose.getX() - (20 * redX),intakeFirstPose.getY());
        follower.setStartingPose(startPose);

        shootPrescore = follower.pathBuilder()
                .addPath(
                        new BezierLine(
                                startPose,
                                shootPose
                        )
                )
                .setGlobalConstantHeadingInterpolation(Math.toRadians(90))
                .build();
        intakeFirst = follower.pathBuilder()
                .addPath(
                        new BezierLine(
                                shootPose,
                                intakeFirstPose
                        )
                )
                .setConstantHeadingInterpolation(Math.toRadians(180) + calculateRedHeading(180))
                .build();
        intakeFirst2 = follower.pathBuilder()
                .addPath(
                        new BezierLine(
                                intakeFirstPose,
                                intakeFirst2Pose
                        )
                )
                .setGlobalConstantHeadingInterpolation(Math.toRadians(180) + calculateRedHeading(180))
                .build();
    }

    @Override
    public void runOpMode() throws InterruptedException {
        Gamepad previousGamepad1 = new Gamepad();

        follower = Constants.createFollower(hardwareMap);

        flywheel.initiate(hardwareMap);
        intake.initiate(hardwareMap);
        lights.initiate(hardwareMap);
        lights.setTeamColor(Lights.TeamColors.RED);
        spindexer.initiate(hardwareMap);
        spindexer.setMode(Spindexer.Modes.UNSORTED);

        //Chamber
        boolean ready = false;
        spindexer.reset();
        while (!opModeIsActive() && !isStopRequested()){
            if (!spindexer.isRotating() && !ready){
                ready = true;
                spindexer.rotateDegree(60);
            }
            boolean triangle = gamepad1.triangle && !previousGamepad1.triangle;
            previousGamepad1.copy(gamepad1);

            if (triangle){
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
            telemetry.addData("Auto Sequence", sequence);

            follower.update();
            flywheel.update();
            intake.update();
            lights.update(telemetry);
            spindexer.update();

            flywheel.status(telemetry);
            spindexer.status(telemetry);


            telemetry.update();

            switch (sequence){
                case 0:
                    follower.followPath(shootPrescore);
                    flywheel.setState(Flywheel.States.SPINNING);
                    sequenceTimer.setWait(250);
                    sequence++;
                    break;
                case 1:
                        if (sequenceTimer.doneWaiting()){
                            spindexer.setState(Spindexer.States.SHOOTING);
                            sequence++;
                        }
                    break;
                case 2:
                    if (!spindexer.isRotating() && spindexer.getState() == Spindexer.States.RESTING){
                        follower.followPath(intakeFirst,1,true);
                        sequenceTimer.setWait(2000);
                        sequence++;
                    }
                    break;
                case 3:
                    if (sequenceTimer.doneWaiting()){
                        sequence++;
                        follower.followPath(intakeFirst2,.55,true);
                        intake.setState(Intake.States.ON);
                        spindexer.setState(Spindexer.States.INTAKING);
                        sequenceTimer.setWait(4000);

                    }
                    break;
                case 4:
                    if (sequenceTimer.doneWaiting()){
                        intake.setState(Intake.States.OFF);
                        spindexer.setState(Spindexer.States.RESTING);
                        spindexer.reset();
                    }
                    break;
            }
        }
    }
}
