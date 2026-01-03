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
import org.firstinspires.ftc.teamcode.Subsystems.Turret;

@Autonomous
public class Far extends LinearOpMode {
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
            intakeFirst, intakeFirst2, intakeFirst3, intakeFirst4, shoot2, intakeSecond, intakeSecond2,shoot3, leave;

    public void buildPaths() {

        final Pose startPose = new Pose(calculateX(57.089258698941), 9.307110438729195, Math.toRadians(90)); // Start Pose of our robot.
        final Pose shootPose = new Pose(calculateX(57), 11.307110438729195, Math.toRadians(90)); // Start Pose of our robot.

        final Pose intakeFirstControl = new Pose(calculateX(52.653555219364605), 23.11951588502268);
        final Pose intakeFirstPose = new Pose(calculateX(23.540090771558244), 10.444780635400905, calculateHeading(180));
        final Pose intakeFirst2Pose = new Pose(calculateX(9.574886535552192), intakeFirstPose.getY(),calculateHeading(180));
        final Pose shootPose2 = new Pose(calculateX(57), 11.307110438729195, calculateHeading(180));
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
                .setConstantHeadingInterpolation(calculateHeading(90))
                .build();

        intakeFirst = follower.pathBuilder()
                .addPath(
                        new BezierCurve(
                                shootPose,
                                intakeFirstControl,
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
                .setConstantHeadingInterpolation(calculateHeading(180))
                .build();

        intakeFirst3 = follower.pathBuilder()
                .addPath(
                        new BezierLine(
                                intakeFirst2Pose,
                                intakeFirstPose
                        )
                )
                .setConstantHeadingInterpolation(calculateHeading(180))
                .build();
        shoot2 = follower.pathBuilder()
                .addPath(
                        new BezierLine(
                                intakeFirst2Pose,
                                shootPose2
                        )
                )
                .setConstantHeadingInterpolation(calculateHeading(180))
                .build();
        intakeFirst4 = follower.pathBuilder()
                .addPath(
                        new BezierLine(
                                intakeFirstPose,
                                intakeFirst2Pose
                        )
                )
                .setConstantHeadingInterpolation(calculateHeading(180))
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
        turret.setState(Turret.States.MANUAL);
        if (lights.getTeamColor() == Lights.TeamColors.BLUE){
            turret.setOverride(.43);
        }else{
            turret.setOverride(.57);
        }

        //   LimeLight limeLight = new LimeLight();
        //  limeLight.initiate(hardwareMap);
        if (isStopRequested()) {
            lights.save();
            return;
        }
        while (opModeIsActive()) {
            telemetry.addData("Auto path", path);

            limeLight.update();
            spindexer.setMotif(limeLight.getMotif());
            lights.setMotif(limeLight.getMotif());
            followerHandler.update();
            followerHandler.save();
            turret.setPose(follower.getPose());
            turret.setGoal(lights.getTeamColor());
            turret.update();

            flywheel.setTargetVelocity(2000);
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

                    path += command.follow(750,shootPrescore,.6);
                    flywheel.setState(Flywheel.States.SPINNING);
                    break;
                case 1:
                    path += command.shoot();
                    if (command.completed()) {
                        flywheel.setState(Flywheel.States.RESTING);
                        spindexer.setMode(Spindexer.Modes.SORTED);
                    }
                    break;
                case 2:
                    path += command.follow(1500, intakeFirst);
                    if (spindexer.getKicker().getState() == Kicker.States.RESTING){
                        command.resetSpindexer();
                    }
                    break;
                case 3:
                    path += command.follow(4000,intakeFirst2, .5);
                    command.startIntaking();
                    if (command.completed()){
                        command.stopIntaking();
                    }
                case 4:
                    path += command.follow(2000, intakeFirst3);
                    if (command.completed()){
                        command.startIntaking();
                    }
                    break;
                case 5:
                    path += command.follow(3000, intakeFirst2);
                    if (command.completed()){
                        if (lights.getTeamColor() == Lights.TeamColors.BLUE){
                            turret.setOverride(.8);
                        }else{
                            turret.setOverride(.2);
                        }
                    }
                case 6:
                    path += command.follow(1800, shoot2);
                    if (command.getTimePassed() > 500 && spindexer.getState() != Spindexer.States.CHAMBER) {
                        command.stopIntaking();
                        spindexer.setMode(Spindexer.Modes.SORTED);
                        spindexer.setState(Spindexer.States.CHAMBER);
                    }
                    break;
                case 7:
                    path += command.shoot();
                    if (command.completed()) {
                        flywheel.setState(Flywheel.States.RESTING);
                        spindexer.setMode(Spindexer.Modes.SORTED);
                    }
                    break;
                case 8:
                    path += command.follow(1500, intakeFirst);
                    if (spindexer.getKicker().getState() == Kicker.States.RESTING){
                        command.resetSpindexer();
                    }
                    break;
                case 9:
                    path += command.follow(4000,intakeFirst2, .5);
                    command.startIntaking();
                    if (command.completed()){
                        command.stopIntaking();
                    }
                case 10:
                    path += command.follow(2000, intakeFirst3);
                    if (command.completed()){
                        command.startIntaking();
                    }
                case 11:
                    path += command.follow(3000, intakeFirst2);
                    if (command.completed()){
                        if (lights.getTeamColor() == Lights.TeamColors.BLUE){
                            turret.setOverride(.8);
                        }else{
                            turret.setOverride(.2);
                        }
                    }
                case 12:
                    path += command.follow(1800, shoot2);
                    if (command.getTimePassed() > 500 && spindexer.getState() != Spindexer.States.CHAMBER) {
                        command.stopIntaking();
                        spindexer.setMode(Spindexer.Modes.SORTED);
                        spindexer.setState(Spindexer.States.CHAMBER);
                    }
                    break;
            }
        }
    }
}
