package org.firstinspires.ftc.teamcode.Subsystems;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.hardware.ColorSensor;
@Config
public class ColorSensors {
    public static int greenThresholdGreen = 100;
    public static int purpleThresholdBlue = 100;
    public static LimeLight.BallColors getBallColor(ColorSensor sensor){
        if (sensor.green() > greenThresholdGreen){
            return  LimeLight.BallColors.G;
        }
        if (sensor.blue() > purpleThresholdBlue){
            return LimeLight.BallColors.P;
        }
        return LimeLight.BallColors.NONE;
    }
}
