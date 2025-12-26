package org.firstinspires.ftc.teamcode.Subsystems.Spindexer;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.Subsystems.LimeLight;

@Config
public class BallDetector {

    //Will tune thresholds more, hopefully it's this simple (GREEN VS BLUE) but it might not be
    public static int greenThresholdGreen = 1000;
    public static int greenThresholdBlue = 0;
    public static int greenThresholdRed = 0;
    public static int purpleThresholdBlue = 1000;
    public static int purpleThresholdGreen = 0;
    public static int getPurpleThresholdRed = 0;
    public static double distanceThresholdINCHES = 0;
    ColorSensor colorSensor;
    ColorSensor colorSensor2;
    public boolean active = true;
    public void initiate(HardwareMap hardwareMap){
        colorSensor = hardwareMap.colorSensor.get("color");
        colorSensor2 = hardwareMap.colorSensor.get("color2");
    }
    public LimeLight.BallColors getBallColor(){
        if (!active){
            return LimeLight.BallColors.NONE;
        }
        if (colorSensor.green() > greenThresholdGreen || colorSensor2.green() > greenThresholdGreen){
            return  LimeLight.BallColors.G;
        }
        if (colorSensor.blue() > purpleThresholdBlue || colorSensor2.blue() > purpleThresholdBlue){
            return LimeLight.BallColors.P;
        }
        return LimeLight.BallColors.NONE;
    }
    public void status(Telemetry telemetry){
        telemetry.addData("Color Sensor Ball", getBallColor());
        telemetry.addData("Color Sensor1 Reading: ","\n Red: " + colorSensor.red() + "\n" +
                "Green: " + colorSensor.green() + "\n" +
                "Blue: " + colorSensor.blue() + "\n" +
                "Alpha: " + colorSensor.alpha());
        telemetry.addData("Color Sensor2 Reading: ","\n Red: " + colorSensor2.red() + "\n" +
                "Green: " + colorSensor2.green() + "\n" +
                "Blue: " + colorSensor2.blue() + "\n" +
                "Alpha: " + colorSensor2.alpha());
    }
}
