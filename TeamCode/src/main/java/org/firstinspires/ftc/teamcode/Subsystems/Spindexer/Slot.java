package org.firstinspires.ftc.teamcode.Subsystems.Spindexer;

import com.acmerobotics.dashboard.config.Config;

import org.firstinspires.ftc.teamcode.Subsystems.LimeLight;
@Config
public class Slot {
    LimeLight.BallColors color = LimeLight.BallColors.NONE;
    public static double tolerance = .01;
    static double[] slot0Pos = {Spindexer.intakeStartPos, Spindexer.intakeStartPos + Spindexer.degreesToTicks(360)};
    static double[] slot1Pos = {Spindexer.intakeStartPos + Spindexer.degreesToTicks(120), Spindexer.intakeStartPos + Spindexer.degreesToTicks(480)};
    static double[] slot2Pos = {Spindexer.intakeStartPos + Spindexer.degreesToTicks(240), Spindexer.intakeStartPos + Spindexer.degreesToTicks(600)};

    public static int getSlot(double pos){
        for (int i = 0; i < 1; i++){
            if (Math.abs(slot0Pos[i] - pos) < tolerance){
                return 0;
            }
            if (Math.abs(slot1Pos[i] - pos) < tolerance){
                return 1;
            }
            if (Math.abs(slot2Pos[i] - pos) < tolerance){
                return 2;
            }
        }
        return 0;
    }
}
