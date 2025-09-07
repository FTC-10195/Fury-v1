package org.firstinspires.ftc.teamcode.example.java.Subsystems;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;

public class Turret {
    public enum States {
        RESTING,
        ROTATING
    }

    States currentState = States.RESTING;
    Servo horzServo;
    Servo vertServo;
    double absolutEncoder = 0;
   public boolean readyToFire = false;
    public static double camXOffsetInches = 5; //Position of camera relative to turret
    public static double camYOffsetInches = 0;

    public void initiate(HardwareMap hardwareMap) {
        horzServo = hardwareMap.servo.get("turret");
        vertServo = hardwareMap.servo.get("vert");
    }

    public void setState(States newState) {
        currentState = newState;
    }

    public States getState() {
        return currentState;
    }

    public static double normalizeRadians(double angle) {
        return Math.atan2(Math.sin(angle), Math.cos(angle));
    }
    /*
     * @param cameraWorldX        camera's x position in world
            * @param cameraWorldY        camera's y position in world
            * @param cameraWorldHeading  camera heading in world (radians)
     * @param cameraRelX          camera x position relative to robot center (robot frame)
     * @param cameraRelY          camera y position relative to robot center (robot frame)
     * @param cameraRelHeading    camera heading relative to robot (radians)

     */
    public static Pose2D computeRobotPose(double cameraWorldX, double cameraWorldY,
                                          double cameraWorldHeading,
                                          double cameraRelX, double cameraRelY,
                                          double cameraRelHeading) {
        //THANK YOU GPT
        // Step 1: Robot heading
        double robotHeading = normalizeRadians(cameraWorldHeading - cameraRelHeading);

        // Step 2: Rotate camera-relative offset into world frame
        double cosH = Math.cos(robotHeading);
        double sinH = Math.sin(robotHeading);

        double camOffsetWorldX = cosH * cameraRelX - sinH * cameraRelY;
        double camOffsetWorldY = sinH * cameraRelX + cosH * cameraRelY;

        // Step 3: Robot position = camera position - rotated offset
        double robotWorldX = cameraWorldX - camOffsetWorldX;
        double robotWorldY = cameraWorldY - camOffsetWorldY;

        return new Pose2D(DistanceUnit.INCH, robotWorldX, robotWorldY, AngleUnit.RADIANS, robotHeading);
    }


    public void update(Webcam webcam) {
        //TurretPos
        //Some conversion from heading to servo position will have to be done
        switch (currentState) {
            case RESTING:
                readyToFire = false;
                break;
            case ROTATING:
                Pose2D robotPose = computeRobotPose(webcam.cameraXPos, webcam.cameraYPos, webcam.absoluteEncoderPos, camXOffsetInches, camYOffsetInches, webcam.goalHeading);
                double distanceX = robotPose.getX(DistanceUnit.INCH) - webcam.goalX;
                double distanceY = robotPose.getY(DistanceUnit.INCH) - webcam.goalY;
                double turretHeading = Math.atan2(distanceX, distanceY);
                //Some calculation converting radians to turret pos
                horzServo.setPosition(turretHeading);
                double turretDistance = Math.sqrt(Math.pow(distanceX, 2) + Math.pow(distanceY, 2));
                //Some calculation to convert magnitude to vertical raising
                //This will fs have to change because if its too close its better to shoot in a higher arc
                vertServo.setPosition(turretDistance);

                //Some absolute encoder logic
                if (absolutEncoder == turretHeading) {
                    readyToFire = true;
                }
                break;
        }

    }
}
