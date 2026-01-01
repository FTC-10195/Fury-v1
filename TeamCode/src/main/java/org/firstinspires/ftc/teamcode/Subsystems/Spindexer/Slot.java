package org.firstinspires.ftc.teamcode.Subsystems.Spindexer;

import com.acmerobotics.dashboard.config.Config;

import org.firstinspires.ftc.teamcode.Subsystems.LimeLight;
@Config
public class Slot {
    LimeLight.BallColors color = LimeLight.BallColors.NONE;
    public static double slotOffset = 120; //degrees
    int id = -1;
    public void setId(int id){
        this.id = id;
    }
    public int getId(){
        return id;
    }

    public static int getIntakeSlot(double pos){
        pos = pos - Spindexer.intakeStartPos;
        pos = pos / Spindexer.degreesToTicks(slotOffset);
        return (int) Math.round(pos);
    }
    public static int getShootSlot(double pos){
        pos = pos - Spindexer.degreesToTicks(180);
        return getIntakeSlot(pos);
    }
    public static double getIntakePos(int id){
        double pos = Spindexer.degreesToTicks(id * slotOffset);
        pos = pos + Spindexer.intakeStartPos;
        return pos;
    }
    public static double getShootPos(int id){
        return getIntakePos(id) + Spindexer.degreesToTicks(180);
    }
    public static double targetPosToShootingPos(double targetPos){
        int angle = (int) Math.round(Spindexer.ticksToDegrees(targetPos - Spindexer.intakeStartPos));
        //No remainder means its an intaking pos, remainder means its a shooting pos
        if (angle % 120 == 0){
            return  targetPos + Spindexer.degreesToTicks(180);
        }else{
            return  targetPos;
        }
    }
    public static int findColor(Slot[] slots, double targetPos, LimeLight.BallColors targetColor){
        int candidate1 = -1;
        int candidate2 = -1;
        for (int i = 0; i < slots.length; i++){
            if (targetColor == slots[i].getColor()){
                if (candidate1 == -1){
                    candidate1 = slots[i].getId();
                }else{
                    candidate2 = slots[i].getId();
                }
            }
        }
        if (candidate1 == -1 || candidate2 == -1){
            return candidate1;
        }
        //At this point if there are
        double distance1 = Math.abs(targetPosToShootingPos(targetPos) - getShootPos(candidate1));
        double distance2 = Math.abs(targetPosToShootingPos(targetPos) - getShootPos(candidate2));
        if (distance1 < distance2){
            return candidate1;
        }else{
            return candidate2;
        }


    }
    public static Slot findSlotFromId(Slot[] slots, int id){
        for (int i = 0; i < slots.length; i++){
            if (id == slots[i].getId()){
                return slots[i];
            }
        }
        //No ball of target found
        return slots[0];
    }
    public static void shoot(Slot[] slots, double pos){
        int id = getShootSlot(pos);
        Slot slot = findSlotFromId(slots, id);
        slot.shoot();
    }
    public void shoot(){
        color = LimeLight.BallColors.NONE;
        id = -1;
    }
    public static double rotateToColor(Slot[] slots, double targetPos, LimeLight.BallColors targetColor,double movementWhenBallNotFound){
        //get id of the slot with the color we want
        int id = Slot.findColor(slots,targetPos,targetColor);
        double pos;
        if (id == -1){
            //Cant find color then modify target.
            pos = targetPos + Spindexer.degreesToTicks(movementWhenBallNotFound);
        }else{
            //Calculating what position this slot is in ticks
            pos = Slot.getShootPos(id);
        }

        //Calculating difference from current pos, then rotate to it
        return Spindexer.ticksToDegrees(pos - targetPos);
    }
    public LimeLight.BallColors getColor(){
        return color;
    }
    public void setColor(LimeLight.BallColors ballColor){
        color = ballColor;
    }
    @Override
    public String toString(){
        return "\n Slot: " + id + "\n" +
                "Color: " + color + "\n" +
                "Intake pos: " + getIntakePos(id) + "\n" +
                "Shoot pos: " + getShootPos(id) + "\n";

    }

}
