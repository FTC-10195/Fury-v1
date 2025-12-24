package org.firstinspires.ftc.teamcode.Subsystems.Spindexer;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.Subsystems.LimeLight;

@Config
public class Sensors {
    //Will tune thresholds more, hopefully it's this simple (GREEN VS BLUE) but it might not be
    public static int greenThresholdGreen = 100;
    public static int greenThresholdBlue = 0;
    public static int greenThresholdRed = 0;
    public static int purpleThresholdBlue = 100;
    public static int purpleThresholdGreen = 0;
    public static int getPurpleThresholdRed = 0;
    public static double distanceThresholdINCHES = 6;
    ColorSensor colorSensor;
    DistanceSensor distanceSensor;
    public void initiate(HardwareMap hardwareMap){
        colorSensor = hardwareMap.colorSensor.get("color");
        distanceSensor = hardwareMap.get(DistanceSensor.class,"dis");
    }
    public LimeLight.BallColors getBallColor(){
        if (colorSensor.green() > greenThresholdGreen){
            return  LimeLight.BallColors.G;
        }
        if (colorSensor.blue() > purpleThresholdBlue){
            return LimeLight.BallColors.P;
        }
        //Close, unknwon color, assume purple
        if (distanceSensor.getDistance(DistanceUnit.INCH) < distanceThresholdINCHES){
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
        telemetry.addData("Distance",distanceSensor.getDistance(DistanceUnit.INCH));
    }
}
