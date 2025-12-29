package org.firstinspires.ftc.teamcode.Autonomous.Commands;

import com.acmerobotics.dashboard.config.Config;
import com.pedropathing.follower.Follower;
import com.pedropathing.paths.PathChain;

import org.firstinspires.ftc.teamcode.Subsystems.Flywheel;
import org.firstinspires.ftc.teamcode.Subsystems.Intake;
import org.firstinspires.ftc.teamcode.Subsystems.Lights;
import org.firstinspires.ftc.teamcode.Subsystems.Spindexer.Spindexer;
import org.firstinspires.ftc.teamcode.Subsystems.Timer;
@Config
public class Command {
    static Timer sequenceTimer = new Timer();
    public static long intakeTime = 4000;
    static int sequence = 0;

    static Intake intake;
    static Spindexer spindexer;
    static Flywheel flywheel;
    static Lights lights;
    static Follower follower;
    public Command(Intake intake, Spindexer spindexer, Flywheel flywheel, Lights lights, Follower follower){
        sequence = 0;
        Command.intake = intake;
        Command.spindexer = spindexer;
        Command.flywheel = flywheel;
        Command.lights = lights;
        Command.follower = follower;
    }
    public static void reset(){
        sequence = 0;
    }
    public int follow(int currentPath, PathChain path){
        return follow(currentPath,0,path,1);
    }
    public int follow(int currentPath, long waitTime, PathChain path){
        return follow(currentPath,waitTime,path,1);
    }
    public int follow(int currentPath, long waitTime, PathChain path, double speed){
        switch (sequence){
            case 0:
                follower.followPath(path,speed,true);
                sequence++;
                sequenceTimer.setWait(waitTime);
                break;
            case 1:
                if (sequenceTimer.doneWaiting()){
                    sequence = 0;
                    return currentPath + 1;
                }
                break;
        }
        return currentPath;
    }
    public int intake(int path){
        switch (sequence){
            case 0:
                intake.setState(Intake.States.ON);
                spindexer.setState(Spindexer.States.INTAKING);
                sequenceTimer.setWait(intakeTime);
                sequence++;
                break;
            case 1:
                if (sequenceTimer.doneWaiting()){
                    intake.setState(Intake.States.OFF);
                    spindexer.setState(Spindexer.States.RESTING);
                    spindexer.reset();
                    sequence++;
                }
                break;
            case 2:
                sequence = 0;
                return path + 1;
        }
        return path;
    }
    public int shoot(int path){
        switch (sequence){
            case 0:
                spindexer.setState(Spindexer.States.SHOOTING);
                sequence++;
                break;
            case 1:
                if (!spindexer.isRotating() && spindexer.getState() == Spindexer.States.RESTING){
                    sequence = 0;
                    return path + 1;
                }
        }
        return path;
    }
}

