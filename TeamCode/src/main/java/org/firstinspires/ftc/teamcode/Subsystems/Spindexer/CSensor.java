package org.firstinspires.ftc.teamcode.Subsystems.Spindexer;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Subsystems.LimeLight;

@Config
public class CSensor {
    //Will tune thresholds more, hopefully it's this simple (GREEN VS BLUE) but it might not be
    public static int greenThresholdGreen = 100;
    public static int greenThresholdBlue = 0;
    public static int greenThresholdRed = 0;
    public static int purpleThresholdBlue = 100;
    public static int purpleThresholdGreen = 0;
    public static int getPurpleThresholdRed = 0;
    ColorSensor sensor1;
    ColorSensor sensor2;
    public void initiate(HardwareMap hardwareMap){
        sensor1 = hardwareMap.colorSensor.get("color");
        sensor2 = hardwareMap.colorSensor.get("color2");
    }
    public LimeLight.BallColors getBallColor(){
        if (sensor1.green() > greenThresholdGreen){
            return  LimeLight.BallColors.G;
        }
        if (sensor2.green() > greenThresholdGreen){
            return LimeLight.BallColors.G;
        }
        if (sensor1.blue() > purpleThresholdBlue){
            return LimeLight.BallColors.P;
        }
        if (sensor2.blue() > purpleThresholdBlue){
            return LimeLight.BallColors.P;
        }
        return LimeLight.BallColors.NONE;
    }
    public void status(Telemetry telemetry){
        telemetry.addData("Color Sensor Ball", getBallColor());
        telemetry.addData("Color Sensor1 Reading: ","\n Red: " + sensor1.red() + "\n" +
                "Green: " + sensor1.green() + "\n" +
                "Blue: " + sensor1.blue() + "\n" +
                "Alpha: " + sensor1.alpha());
        telemetry.addData("Color Sensor2 Reading: ","\n Red: " + sensor2.red() + "\n" +
                "Green: " + sensor2.green() + "\n" +
                "Blue: " + sensor2.blue() + "\n" +
                "Alpha: " + sensor2.alpha());
    }
}
