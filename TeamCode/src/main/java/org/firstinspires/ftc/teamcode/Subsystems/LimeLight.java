package org.firstinspires.ftc.teamcode.Subsystems;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;

@Config
public class LimeLight {
    public enum BallColors{
        P,
        G,
        NONE
    }
    BallColors[] motif = {BallColors.P,BallColors.P,BallColors.G};
    public BallColors[] getMotif(){
        return motif;
    }
    public boolean canSeeTeamColor = false;
    public static int blueId = 20;
    public static int redId = 24;
    public static int GPPId = 21;
    public static int PGPId = 22;
    public static int PPGId = 23;

    Limelight3A limelight;
    Lights.TeamColors teamColor = Lights.TeamColors.RED;

    public void initiate(HardwareMap hardwareMap) {
        limelight = hardwareMap.get(Limelight3A.class, "limelight");
        limelight.pipelineSwitch(8);
        limelight.start();
    }
    public void setTeamColor(Lights.TeamColors newColor){
        teamColor = newColor;
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
    public boolean colorId(int id){
        return (teamColor.equals(Lights.TeamColors.RED) && id == redId) || (teamColor.equals(Lights.TeamColors.BLUE) && id == blueId);
    }


    public void update(Telemetry telemetry) {
     /*  LLResult result = limelight.getLatestResult();
        canSeeTeamColor = false;
        if (result == null || result.isValid()) {
            return;
        }
        for (int i = 0; i < result.getDetectorResults().size(); i++){
            int id = result.getDetectorResults().get(i).getClassId();
            telemetry.addData("ID " + i, id);
            if (motifId(id)){
                setMotif(id);
                return;
            }
            canSeeTeamColor = colorId(id);

        }
        */
        telemetry.addData("LimelightMotif", motif);
        telemetry.addData("Limelight teamColor", teamColor);
        telemetry.addData("Limelight can see team color", canSeeTeamColor);
    }

}
