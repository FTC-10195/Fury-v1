package org.firstinspires.ftc.teamcode.Subsystems;

import com.acmerobotics.dashboard.config.Config;
import com.pedropathing.control.PIDFCoefficients;
import com.pedropathing.follower.Follower;
import com.pedropathing.follower.FollowerConstants;
import com.pedropathing.geometry.Pose;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.pedroPathing.Constants;
@Config
public class FollowerHandler {
    public static double brakeDTranslational = 0.1;
    public static double brakePHeading = 4;
    public static double brakePTranslational = 0.6;

    public static double pathingDTranslational  = 0.1;
    public static double pathingPHeading = 3;
    public static double pathingPTranslational = 0.3;

    public static double mass = 12;
    public static Pose defaultPose = new Pose(72,72,0);
    public static Pose pose;
    Follower follower;
    public void initiate(HardwareMap hardwareMap){
        follower = Constants.createFollower(hardwareMap);
        setPathMode();
        if (pose == null){
            pose = defaultPose;
        }
        follower.setStartingPose(pose);
    }
    public void save(){
        pose = follower.getPose();
    }
    public void load(){
        if (pose == null){
            pose = defaultPose;
        }
        follower.setPose(pose);
    }

    //For relocalization
    public void setPose(Pose newPose){
        pose = newPose;
        load();
    }
    public Follower getFollower(){
        return follower;
    }
    public void setBrakeMode(){
        follower.setConstants( new FollowerConstants()
                .forwardZeroPowerAcceleration(-72.417)
                .lateralZeroPowerAcceleration(-83.947)
                .headingPIDFCoefficients(new PIDFCoefficients(brakePHeading,0,0,0.01))
                .translationalPIDFCoefficients(new PIDFCoefficients(brakePTranslational,0,brakeDTranslational,0.01))
                .mass(mass));
        follower.updateConstants();
    }
    public void setPathMode(){
        follower.setConstants( new FollowerConstants()
                .forwardZeroPowerAcceleration(-72.417)
                .lateralZeroPowerAcceleration(-83.947)
                .headingPIDFCoefficients(new PIDFCoefficients(pathingPHeading,0,0,0.01))
                .translationalPIDFCoefficients(new PIDFCoefficients(pathingPTranslational,0,pathingDTranslational,0.01))
                .mass(mass));
        follower.updateConstants();
    }
    public void update(){
        follower.update();
    }
    public void status(Telemetry telemetry){
        telemetry.addData("FollowerHandlerPose",pose);
    }
}
