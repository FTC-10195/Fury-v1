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
public class RobotBased extends LinearOpMode {
    public enum States{
        RESTING,
        INTAKING,
        PREPARING_TO_FIRE,
        SHOOTING
    }
    @Override
    public void runOpMode() throws InterruptedException {
        States state = States.RESTING;
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
            previousGamepad1.copy(gamepad1);
            if (triangle){
                lights.switchTeamColor();
            }
            if (RB){
                state = States.RESTING;
            }
            switch (state){
                case RESTING:
                    intake.setState(Intake.States.OFF);
                    flywheel.setState(Flywheel.States.RESTING);
                    if (spindexer.getMode() == Spindexer.Modes.SORTED){
                        lights.setMode(Lights.Mode.MOTIF);
                    }else{
                        lights.setMode(Lights.Mode.TEAM);
                    }
                    if (LT){
                        state = States.INTAKING;
                        spindexer.setState(Spindexer.States.INTAKING);
                    }
                    if (RT){
                        state = States.PREPARING_TO_FIRE;
                        flywheel.setState(Flywheel.States.SPINNING);
                        if (spindexer.getMode() == Spindexer.Modes.SORTED){
                            spindexer.setState(Spindexer.States.CHAMBER);
                        }
                    }
                    break;
                case INTAKING:
                    intake.setState(Intake.States.ON);
                    flywheel.setState(Flywheel.States.RESTING);
                    lights.setMode(Lights.Mode.INTAKING);
                    if (LT || spindexer.getState() == Spindexer.States.RESTING){
                        state = States.RESTING;
                    }
                    break;
                case PREPARING_TO_FIRE:
                    if (spindexer.getMode() == Spindexer.Modes.SORTED){
                        lights.setMode(Lights.Mode.MOTIF);
                    }else{
                        lights.setMode(Lights.Mode.TEAM);
                    }
                    intake.setState(Intake.States.OFF);
                    if (flywheel.isReady && spindexer.getState() == Spindexer.States.RESTING){
                        gamepad1.rumble(1,10,100);
                        if (RT){
                            state = States.SHOOTING;
                            spindexer.setState(Spindexer.States.SHOOTING);
                        }
                    }
                    break;
                case SHOOTING:
                    intake.setState(Intake.States.OFF);
                    if (RT || spindexer.getState() == Spindexer.States.RESTING){
                        state = States.RESTING;
                    }
                    break;
            }

            //Overrides
            if (gamepad1.cross){
                intake.setState(Intake.States.OUTTAKE);
            }



            drivetrain.update(gamepad1.left_stick_x, gamepad1.left_stick_y, gamepad1.right_stick_x);
           flywheel.update();
           intake.update();
            spindexer.update();
        //    limeLight.update(telemetry);


            telemetry.addData("State",state);
            lights.update(telemetry);

            spindexer.status(telemetry);
            flywheel.status(telemetry);
            telemetry.update();
        }
        lights.reset();
    }
}
