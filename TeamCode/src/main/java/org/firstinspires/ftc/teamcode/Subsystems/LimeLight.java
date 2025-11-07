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

    Limelight3A limelight;

    public void initiate(HardwareMap hardwareMap) {
        limelight = hardwareMap.get(Limelight3A.class, "limelight");
        limelight.pipelineSwitch(8);
        limelight.start();
    }

    public void update() {
        LLResult result = limelight.getLatestResult();
        if (result != null && result.isValid()) {
            //if april tag id = something motif = ...
        }else{

        }
    }

}
