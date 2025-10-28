package org.firstinspires.ftc.teamcode.Autonomous;

import static org.firstinspires.ftc.teamcode.Subsystems.Conveyor.States.OFF;
import static org.firstinspires.ftc.teamcode.Subsystems.Conveyor.States.ON;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.Subsystems.Conveyor;
import org.firstinspires.ftc.teamcode.Subsystems.Drivetrain;
import org.firstinspires.ftc.teamcode.Subsystems.Flywheel;
import org.firstinspires.ftc.teamcode.Subsystems.Intake;
import org.firstinspires.ftc.teamcode.Subsystems.Kicker;
import org.firstinspires.ftc.teamcode.Subsystems.Trigger;

@Autonomous
public class JankBlueSmallT extends LinearOpMode {

    @Override
    public void runOpMode() throws InterruptedException {
        waitForStart();
        if (isStopRequested()) return;
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

        while (opModeIsActive()) {
            long elapsed = System.currentTimeMillis() - startTime;

            if (elapsed < 2000) {
                drivetrain.update(-0.1, -0.7, 0);
                conveyor.setState(OFF);
                flywheel.setState(Flywheel.States.RESTING);
                trigger.setState(Trigger.States.RESTING);
                kicker.setState(Kicker.ServoState.RESTING);
            }else if (elapsed > 2000 && elapsed < 2500) {
                drivetrain.update(0, 0, -0.4);
                flywheel.setState(Flywheel.States.SPINNING);
                conveyor.setState(ON);
            } else if (elapsed > 2500 && elapsed < 3000){
                drivetrain.update(0, -0.4, 0);
            }
            else if (flywheel.IsReady && elapsed < 5000) {
                trigger.setState(Trigger.States.SHOOTING);
                kicker.setState(Kicker.ServoState.SHOOTING);
            }

            if (kicker.getCurrentServoState() == Kicker.ServoState.RESTING && elapsed > 5000){
                kicker.setState(Kicker.ServoState.SHOOTING);
            }
            if (elapsed > 3000){
                drivetrain.update(0, 0, 0);
            }
            if (elapsed > 10000)  {
                drivetrain.update(-0.5, 0, 0);
            }
            conveyor.update();
            flywheel.update();
            intake.update();
            trigger.update();
            telemetry.update();
            kicker.update();
        }
    }
}
