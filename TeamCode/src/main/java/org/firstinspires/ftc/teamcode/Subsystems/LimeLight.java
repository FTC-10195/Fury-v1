package org.firstinspires.ftc.teamcode.Subsystems;

import com.acmerobotics.dashboard.config.Config;
import com.pedropathing.ftc.FTCCoordinates;
import com.pedropathing.ftc.InvertedFTCCoordinates;
import com.pedropathing.geometry.PedroCoordinates;
import com.pedropathing.geometry.Pose;
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.robotcore.external.navigation.Pose3D;
import org.firstinspires.ftc.robotcore.external.navigation.Position;
import org.firstinspires.ftc.robotcore.external.navigation.YawPitchRollAngles;

import java.util.ArrayList;

@Config
public class LimeLight {
    public enum BallColors{
        P,
        G,
        NONE
    }
    static BallColors[] motif = {BallColors.P,BallColors.P,BallColors.G};
    public BallColors[] getMotif(){
        return motif;
    }
    public static int blueId = 20;
    public static int redId = 24;
    public static int GPPId = 21;
    public static int PGPId = 22;
    public static int PPGId = 23;

    /*public static double camForward = 0;
    public static double camVertical = 0;
    public static double camHorizontal = 0;
    public static double yaw = 0;
    public static double pitch = 0;
    public static double roll = 0;

     */

    public boolean canRelocalize = false;
    Pose3D calculatedPos = new Pose3D(new Position(DistanceUnit.INCH,0,0,0,0),new YawPitchRollAngles(AngleUnit.RADIANS,0,0,0,0));
    Pose pose = new Pose(72,72);
    Limelight3A limelight;
    public static String ballToString(BallColors color){
        switch (color){
            case P:
                return "P";
            case G:
                return "G";
            case NONE:
                return "NONE";
        }
        return "";
    }
    public static String motifToString(BallColors[] motif){
        return ballToString(motif[0]) + ballToString(motif[1]) + ballToString(motif[2]);
    }

    public void initiate(HardwareMap hardwareMap) {
        limelight = hardwareMap.get(Limelight3A.class, "limelight");
        limelight.pipelineSwitch(0);
        limelight.start();
    }
    public boolean motifId(int id){
        return id == PPGId || id == PGPId || id == GPPId;
    }
    public void setMotif(int id){
        if (id == GPPId){
            motif = new BallColors[]{BallColors.G,BallColors.P,BallColors.P};
        }else if (id == PGPId){
            motif = new BallColors[]{BallColors.P,BallColors.G,BallColors.P};
        }else if (id == PPGId){
            motif = new BallColors[]{BallColors.P,BallColors.P,BallColors.G};
        }
    }


    public void update() {
      LLResult result = limelight.getLatestResult();
        if (result == null || !result.isValid()) {
            return;
        }
        for (int i = 0; i < result.getFiducialResults().size(); i++){
            int id = result.getFiducialResults().get(i).getFiducialId();
            if (motifId(id)){
                setMotif(id);
                return;
            }

            //Should never happen unless somebody is wearing a giant april tag on their shirt
            if (id != redId && id != blueId){
                return;
            }

            //can relocalize
            canRelocalize = true;
            calculatedPos = result.getBotpose();
            relocalize();
        }

    }
    public void relocalize(){
        pose = new Pose(72,72,0);

        Position position = calculatedPos.getPosition().toUnit(DistanceUnit.INCH);
        pose = new Pose(
                position.x,
                position.y,
                calculatedPos.getOrientation().getYaw(),
                InvertedFTCCoordinates.INSTANCE
        );
        pose = pose.getAsCoordinateSystem(PedroCoordinates.INSTANCE);

    }
    public void status(Telemetry telemetry){
        telemetry.addData("LimelightMotif", LimeLight.motifToString(motif));
        telemetry.addData("Limelight Can relocalize", canRelocalize);
        telemetry.addData("Limelight X",pose.getX());
        telemetry.addData("Limelight Y",pose.getY());
        telemetry.addData("Limelight Angle",pose.getHeading());
        telemetry.addData("Limelight getBotPose", calculatedPos.toString());
    }

}
