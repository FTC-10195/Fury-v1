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
    public static int purpleThresholdBlue = 100;
    ColorSensor sensor;
    public void initiate(HardwareMap hardwareMap){
        sensor = hardwareMap.colorSensor.get("color");
    }
    public LimeLight.BallColors getBallColor(){
        if (sensor.green() > greenThresholdGreen){
            return  LimeLight.BallColors.G;
        }
        if (sensor.blue() > purpleThresholdBlue){
            return LimeLight.BallColors.P;
        }
        return LimeLight.BallColors.NONE;
    }
    public void status(Telemetry telemetry){
        telemetry.addData("Color Sensor Ball", getBallColor());
        telemetry.addData("Color Sensor Reading: ","\n Red: " + sensor.red() + "\n" +
                "Green: " + sensor.green() + "\n" +
                "Blue: " + sensor.blue() + "\n" +
                "Alpha: " + sensor.alpha());
    }
}
