/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Character;

import Animation.AnimationData;
import Helpers.Camera;
import Helpers.GameLoop;
import com.jogamp.opengl.GL2;
import java.util.Random;

/**
 *
 * @author Krystle
 */
public class Enemy extends CharacterData{
    private int distanceMoved;
    private boolean goingLeft;
    private boolean canJumpOn;
   // private boolean goingRight;
    private boolean seesPlayer;
    public int pewCooldown = 500;
    private Action currentAction;

    public boolean canJumpOn() {
        return canJumpOn;
    }
    
    public enum Action{
        WALK(5000),SHOOT(5000),JUMP(5000),NOTHING(5000);
        
        //atributes for action
        
        //timer (in seconds) to do the action for 
        double TotalTimeToRun;
        double currentTimeLeft;
        //constructior for the action
        Action(double r){
            this.TotalTimeToRun = r;
            this.currentTimeLeft = r;
        }
        //methods each action has
        double getTimeLeft(){
            return currentTimeLeft;
        }
        
        void updateTimer(double d){
            currentTimeLeft=d;
        }
        
        void resetTimer(){
            currentTimeLeft = TotalTimeToRun;
        }
        
    
    }
    
    public Enemy(double x, double y, int w, int h, AnimationData ad, boolean canJumpOn) {
        super(x, y, w, h, ad);
        distanceMoved = 0;
        goingLeft=true;
       // goingRight=false;
        seesPlayer=false;
        currentAction = Action.NOTHING;
        this.canJumpOn= canJumpOn;
        
    }
    
    
    public void resetPewCooldown(){
        pewCooldown = 500;
    }
    @Override
    public void update(float dt){
        //updates
        currAnimation.update(dt);
       // System.out.println(dt);
        double updatedtime = currentAction.getTimeLeft()-dt;
        currentAction.updateTimer(updatedtime);
        //new action check
        if(currentAction.getTimeLeft()<=0.0){
            //do a new action
            currentAction.resetTimer();
            currentAction = getNewAction();

            
        }
        //do action
        doAction(dt);
        
//        if(distanceMoved<20 && goingLeft){
//            setX(getX()-.5);
//            distanceMoved+=1;
//        }else if(distanceMoved<20 && !goingLeft){
//            setX(getX()+.5);
//            distanceMoved+=1;
//        }else if(distanceMoved>=20){
//            distanceMoved=0;
//            if(goingLeft){
//                goingLeft=false;
//            }else{
//                goingLeft=true;
//            }
//        }
        
        //AI
        
        
    }
    @Override
    public void draw(GL2 g, Camera c){
        GameLoop.glDrawSprite(g, currAnimation.getCurFrameImage(),(int) x-c.getX(),(int) y-c.getY(), currAnimation.getcurFrameSize()[0], currAnimation.getcurFrameSize()[1]);
    }
    
    Action getNewAction(){
        Random r = new Random();
        int roll = r.nextInt(10);
        
        if(roll<5){
            return Action.WALK;
        }
        else if(roll <8){
            return Action.JUMP;
        }
        else if(roll<10){
            return Action.SHOOT;
        }
        else{
            return Action.NOTHING;
        }
    }
    
    private void doAction(float dt) {
        switch(currentAction){
            case WALK:
                //do walk
             //   System.out.println("is walking");
                break;
            case SHOOT:
                //do shoot
             //   System.out.println("is shooting");
                break;

            case JUMP:
                //do jump
            //   jumpPressed=true;
           //     System.out.println("is jumping");
                break;

            case NOTHING:
                //do nothing
            //    System.out.println("is walking");
                break;
        }
    }
    
}
