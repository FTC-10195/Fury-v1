package org.firstinspires.ftc.teamcode.example.java.Subsystems;

public class TeamColor {
    public enum Color{
        RED,
        BLUE
    }
    Color color;
    public TeamColor(Color color){
        this.color = color;
    }
    public void setColor(Color color){
        this.color = color;
    }
    public Color getColor(){
        return color;
    }

}
