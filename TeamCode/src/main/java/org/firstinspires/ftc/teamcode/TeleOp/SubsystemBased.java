package org.firstinspires.ftc.teamcode.TeleOp;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Gamepad;

import org.firstinspires.ftc.teamcode.Subsystems.Drivetrain;
import org.firstinspires.ftc.teamcode.Subsystems.Flywheel;
import org.firstinspires.ftc.teamcode.Subsystems.Intake;
import org.firstinspires.ftc.teamcode.Subsystems.Lights;
import org.firstinspires.ftc.teamcode.Subsystems.Spindexer.Spindexer;

@TeleOp
public class SubsystemBased extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        waitForStart();
        Flywheel flywheel = new Flywheel();
        flywheel.initiate(hardwareMap);
        Intake intake = new Intake();
        intake.initiate(hardwareMap);
        Drivetrain drivetrain = new Drivetrain();
        drivetrain.initiate(hardwareMap);
        Lights lights = new Lights();
        lights.initiate(hardwareMap);
        lights.setTeamColor(Lights.TeamColors.RED);

        Spindexer spindexer = new Spindexer();
        spindexer.initiate(hardwareMap);
        spindexer.setMode(Spindexer.Modes.UNSORTED);
     //   LimeLight limeLight = new LimeLight();
      //  limeLight.initiate(hardwareMap);
        if (isStopRequested()) {
            lights.reset();
            return;
        }

        Gamepad previousGamepad1 = new Gamepad();
        while (opModeIsActive()) {
            boolean LB = gamepad1.left_bumper && !previousGamepad1.left_bumper;
            boolean RB = gamepad1.right_bumper && !previousGamepad1.right_bumper;
            boolean X = gamepad1.cross && !previousGamepad1.cross;
            boolean triangle = gamepad1.triangle && !previousGamepad1.triangle;
            boolean LT = gamepad1.left_trigger > 0.1 && previousGamepad1.left_trigger <= 0.1;
            boolean RT = gamepad1.right_trigger > 0.1 && previousGamepad1.right_trigger <= 0.1;
            boolean circle = gamepad1.circle && !previousGamepad1.circle;
            boolean square = gamepad1.square && !previousGamepad1.square;
            previousGamepad1.copy(gamepad1);
            if (triangle){
                lights.switchTeamColor();
            }
            if (LB){
                //spindexer.rotateDegree(-60);
                spindexer.getKicker().kick();
            }
            if (RB){
                spindexer.rotate();
            }
            if (RT){
                spindexer.rotateDegree(60);
            }
            if (LT){
                spindexer.reset();
                spindexer.setState(Spindexer.States.RESTING);
            }
            if (X){
                spindexer.setState(Spindexer.States.SHOOTING);
            }
            if (circle){
                switch (lights.getMode()){
                    case TEAM:
                        lights.setMode(Lights.Mode.MOTIF);
                        break;
                    case MOTIF:
                        lights.setMode(Lights.Mode.INTAKING);
                        break;
                    case INTAKING:
                        lights.setMode(Lights.Mode.TEAM);
                        break;
                }
            }
            if (square){
                switch (flywheel.getState()){
                    case RESTING:
                        flywheel.setState(Flywheel.States.SPINNING);
                        break;
                    case SPINNING:
                        flywheel.setState(Flywheel.States.RESTING);
                        break;
                }
            }


            drivetrain.update(gamepad1.left_stick_x, gamepad1.left_stick_y, gamepad1.right_stick_x);
            flywheel.update();
            intake.update();
            spindexer.update();
        //    limeLight.update(telemetry);

            lights.update(telemetry);

            spindexer.status(telemetry);
            flywheel.status(telemetry);
            telemetry.update();
        }
        lights.reset();
    }
}
