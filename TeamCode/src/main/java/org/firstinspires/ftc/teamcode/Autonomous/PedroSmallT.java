package org.firstinspires.ftc.teamcode.Autonomous;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.Path;
import com.pedropathing.paths.PathChain;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.Subsystems.Conveyor;
import org.firstinspires.ftc.teamcode.Subsystems.Drivetrain;
import org.firstinspires.ftc.teamcode.Subsystems.Flywheel;
import org.firstinspires.ftc.teamcode.Subsystems.Intake;
import org.firstinspires.ftc.teamcode.Subsystems.Kicker;
import org.firstinspires.ftc.teamcode.Subsystems.Trigger;
import org.firstinspires.ftc.teamcode.pedroPathing.Constants;

@Autonomous
public class PedroSmallT extends LinearOpMode {
    private Follower follower;
    private long pathTimer = System.currentTimeMillis();

    private int pathState;
    private final Pose startPose = new Pose(55.850, 8.075, Math.toRadians(90)); // Start Pose of our robot.
    private final Pose scorePose = new Pose(32.299065420560744, 120.22429906542055, Math.toRadians(145)); // Scoring Pose of our robot. It is facing the goal at a 135 degree angle.
    private final Pose parkPose = new Pose(19.06542056074766, 104.74766355140187, Math.toRadians(145)); // Highest (First Set) of Artifacts from the Spike Mark.
    private Path scorePreload;
    private PathChain park1;

    public void buildPaths() {
        /* This is our scorePreload path. We are using a BezierLine, which is a straight line. */
        scorePreload = new Path(new BezierLine(startPose, scorePose));
        scorePreload.setLinearHeadingInterpolation(startPose.getHeading(), scorePose.getHeading());

    /* Here is an example for Constant Interpolation
    scorePreload.setConstantInterpolation(startPose.getHeading()); */

        /* This is our grabPickup1 PathChain. We are using a single path with a BezierLine, which is a straight line. */
        park1 = follower.pathBuilder()
                .addPath(new BezierLine(scorePose, parkPose))
                .setLinearHeadingInterpolation(scorePose.getHeading(), parkPose.getHeading())
                .build();


    }
    public void setPathState(int pState) {
        pathState = pState;
        pathTimer = System.currentTimeMillis();
    }
    @Override
    public void runOpMode() throws InterruptedException {
        Conveyor conveyor = new Conveyor();
        conveyor.initiate(hardwareMap);
        Flywheel flywheel = new Flywheel();
        flywheel.initiate(hardwareMap);
        Trigger trigger = new Trigger();
        trigger.initiate(hardwareMap);
        Kicker kicker = new Kicker();
        kicker.initiate(hardwareMap);
        Intake intake = new Intake();
        intake.initiate(hardwareMap);
        Drivetrain drivetrain = new Drivetrain();
        drivetrain.initiate(hardwareMap);
        long startTime = System.currentTimeMillis();
        follower = Constants.createFollower(hardwareMap);
        buildPaths();
        follower.setStartingPose(startPose);
        waitForStart();
        setPathState(0);
        if (isStopRequested()) return;



        while (opModeIsActive()) {
            follower.update();
            long timePassed = System.currentTimeMillis() - pathTimer;
            switch (pathState){
                case 0:
                    follower.followPath(scorePreload);
                    setPathState(1);
                    break;
                case 1:
                    if (timePassed > 1000){
                        flywheel.setState(Flywheel.States.SPINNING);
                        setPathState(pathState+1);
                    }
                    break;
                case 2:
                    if (flywheel.IsReady && timePassed > 2000){
                        kicker.setState(Kicker.ServoState.SHOOTING);
                        trigger.setState(Trigger.States.SHOOTING);
                        setPathState(pathState+1);
                    }
                    break;
                case 3:
                    if (timePassed > 700){
                        kicker.setState(Kicker.ServoState.SHOOTING);
                        setPathState(pathState+1);
                    }
                case 4:
                    if (timePassed > 700){
                        kicker.setState(Kicker.ServoState.SHOOTING);
                        flywheel.setState(Flywheel.States.RESTING);
                        trigger.setState(Trigger.States.RESTING);
                        kicker.setState(Kicker.ServoState.RESTING);
                        follower.followPath(park1);
                        setPathState(pathState+1);
                    }


            }
            // Feedback to Driver Hub for debugging
            telemetry.addData("path state", pathState);
            telemetry.addData("x", follower.getPose().getX());
            telemetry.addData("y", follower.getPose().getY());
            telemetry.addData("heading", follower.getPose().getHeading());
            telemetry.update();

            conveyor.update();
            flywheel.update();
            intake.update();
            trigger.update();
            kicker.update();
        }
    }
}
