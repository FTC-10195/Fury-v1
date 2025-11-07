package org.firstinspires.ftc.teamcode.Subsystems;

import com.acmerobotics.dashboard.config.Config;
import com.bylazar.lights.RGBIndicator;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;

@Config
public class TeamColor {
    public enum Colors {
        RED,
        BLUE,
        NONE
    }

    static TeamColor.Colors savedColor;

    public void save() {
        savedColor = color;
    }

    public void load() {
        if (savedColor != null) {
            color = savedColor;
        }
    }

    public void reset() {
        savedColor = null;
    }

    Servo rgbIndicator;
    public static double blue = 0.3;
    public static double red = 0.2;

    public void initiate(HardwareMap hardwareMap) {
        rgbIndicator = hardwareMap.get(Servo.class, "rgb");
    }

    Colors color = Colors.RED;

    public void setColor(Colors newColor) {
        color = newColor;
    }

    public Colors getColor() {
        return color;
    }

    public void switchColor() {
        switch (color) {
            case RED:
                color = Colors.BLUE;
                break;
            case BLUE:
                color = Colors.RED;
                break;
        }
    }

    public void update(Telemetry telemetry) {
        load();
        switch (color) {
            case BLUE:
                rgbIndicator.setPosition(blue);
                break;
            case RED:
                rgbIndicator.setPosition(red);
                break;
            case NONE:
                rgbIndicator.setPosition(0);
        }
        telemetry.addData("TeamColor", color);
    }
}
