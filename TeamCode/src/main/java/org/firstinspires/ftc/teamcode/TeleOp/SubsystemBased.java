package org.firstinspires.ftc.teamcode.TeleOp;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Gamepad;

import org.firstinspires.ftc.teamcode.Subsystems.Drivetrain;
import org.firstinspires.ftc.teamcode.Subsystems.Flywheel;
import org.firstinspires.ftc.teamcode.Subsystems.Intake;
import org.firstinspires.ftc.teamcode.Subsystems.LimeLight;
import org.firstinspires.ftc.teamcode.Subsystems.Spindexer.Spindexer;
import org.firstinspires.ftc.teamcode.Subsystems.TeamColor;

@TeleOp
public class SubsystemBased extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        waitForStart();
        Flywheel flywheel = new Flywheel();
     //   flywheel.initiate(hardwareMap);
        Intake intake = new Intake();
      //  intake.initiate(hardwareMap);
        Drivetrain drivetrain = new Drivetrain();
      //  drivetrain.initiate(hardwareMap);
        TeamColor teamColor = new TeamColor();
      //  teamColor.initiate(hardwareMap);
        teamColor.setColor(TeamColor.Colors.RED);
        Spindexer spindexer = new Spindexer();
        spindexer.initiate(hardwareMap);
        spindexer.setMode(Spindexer.Modes.MANUAL);
        LimeLight limeLight = new LimeLight();
      //  limeLight.initiate(hardwareMap);
        if (isStopRequested()) {
            teamColor.reset();
            return;
        }

        Gamepad previousGamepad1 = new Gamepad();
        while (opModeIsActive()) {
            boolean LB = gamepad1.left_bumper && !previousGamepad1.left_bumper;
            boolean RB = gamepad1.right_bumper && !previousGamepad1.right_bumper;
            boolean X = gamepad1.cross && !previousGamepad1.cross;
            boolean triangle = gamepad1.x && !previousGamepad1.triangle;
            boolean LT = gamepad1.left_trigger > 0.1 && previousGamepad1.left_trigger <= 0.1;
            boolean RT = gamepad1.right_trigger > 0.1 && previousGamepad1.right_trigger <= 0.1;
            boolean circle = gamepad1.circle && !previousGamepad1.circle;
            previousGamepad1.copy(gamepad1);
            if (triangle){
                teamColor.switchColor();
            }
            if (LB){
                spindexer.rotate();
            }
            if (RB){
                spindexer.reset();
            }
            if (X){
                switch (intake.getState()){
                    case ON:
                    case OFF:
                        intake.setState(Intake.States.OUTTAKE);
                        break;
                    case OUTTAKE:
                        intake.setState(Intake.States.OFF);
                        break;
                }
            }
            if (LT) {
                switch (flywheel.getState()) {
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
            limeLight.update(telemetry);

            teamColor.update(telemetry);

            spindexer.status(telemetry);
            telemetry.update();
        }
        teamColor.reset();
    }
}
