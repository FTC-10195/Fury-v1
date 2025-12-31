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
    private boolean done = false;
    public boolean completed(){
        return done;
    };
    public Command(Intake intake, Spindexer spindexer, Flywheel flywheel, Lights lights, Follower follower){
        sequence = 0;
        Command.intake = intake;
        Command.spindexer = spindexer;
        Command.flywheel = flywheel;
        Command.lights = lights;
        Command.follower = follower;
    }
    public void reset(){
        sequence = 0;
        done = false;
    }
    public int follow(PathChain path){
        return follow(0,path,1);
    }
    public int follow(long waitTime, PathChain path){
        return follow(waitTime,path,1);
    }
    public int follow(PathChain path, double speed){
        return follow(0,path,speed);
    }
    public int follow(long waitTime, PathChain path, double speed){
        switch (sequence){
            case 0:
                follower.followPath(path,speed,true);
                sequence++;
                sequenceTimer.setWait(waitTime);
                break;
            case 1:
                if (sequenceTimer.doneWaiting()){
                    sequence = 0;
                    done = true;
                    return 1;
                }
                break;
        }
        done = false;
        return 0;
    }
    public int intake(){
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
                    sequence = 0;
                    done = true;
                    return 1;
                }
                break;
        }
        done = false;
        return 0;
    }
    public int shoot(){
        switch (sequence){
            case 0:
                spindexer.setState(Spindexer.States.SHOOTING);
                sequence++;
                break;
            case 1:
                if (!spindexer.isRotating() && spindexer.getState() == Spindexer.States.RESTING){
                    sequence = 0;
                    done = true;
                    return 1;
                }
        }
        done = false;
        return 0;
    }
}

