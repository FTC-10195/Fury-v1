package org.firstinspires.ftc.teamcode.Autonomous;

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
public class JankBigT extends LinearOpMode {

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
            if (elapsed < 1000) {
                drivetrain.update(0, 0.5, 0);
            } else {
                drivetrain.update(0, 0, 0);
                conveyor.setState(ON);
                flywheel.setState(Flywheel.States.SPINNING);
            }
                if (flywheel.IsReady && elapsed < 3000) {
                    trigger.setState(Trigger.States.SHOOTING);
                    kicker.setState(Kicker.ServoState.SHOOTING);
                }
                if (kicker.getCurrentServoState() == Kicker.ServoState.RESTING){
                    kicker.setState(Kicker.ServoState.SHOOTING);
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
