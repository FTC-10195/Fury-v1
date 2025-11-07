package org.firstinspires.ftc.teamcode.Autonomous;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Gamepad;

import org.firstinspires.ftc.teamcode.Subsystems.Drivetrain;
import org.firstinspires.ftc.teamcode.Subsystems.Flywheel;
import org.firstinspires.ftc.teamcode.Subsystems.Intake;
import org.firstinspires.ftc.teamcode.Subsystems.Kicker;
import org.firstinspires.ftc.teamcode.Subsystems.TeamColor;

@TeleOp
public class TestAutoBlue extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        waitForStart();
        Flywheel flywheel = new Flywheel();
        flywheel.initiate(hardwareMap);
        Intake intake = new Intake();
        intake.initiate(hardwareMap);
        Drivetrain drivetrain = new Drivetrain();
        drivetrain.initiate(hardwareMap);
        Kicker kicker = new Kicker();
        kicker.initiate(hardwareMap);
        TeamColor teamColor = new TeamColor();
        teamColor.initiate(hardwareMap);
        teamColor.setColor(TeamColor.Colors.BLUE);
        if (isStopRequested()) {
            teamColor.save();
            return;
        }
        while (opModeIsActive()) {
            teamColor.update(telemetry);
            telemetry.update();
        }
        teamColor.save();
    }
}
