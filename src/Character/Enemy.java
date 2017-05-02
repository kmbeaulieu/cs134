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
import java.util.ArrayList;
import java.util.Random;

/**
 *
 * @author Krystle
 */
public class Enemy extends CharacterData {

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

    private void resetAndChooseAction() {
        currentAction.resetTimer();
        currentAction.setDoneWithAction(false);
        currentAction = getNewAction();
    }

    private void checkIfSeesPlayer() {
        //TODO maybe make later
    }
    
    public enum EnemyType{
        SHYGUY, SHYGUYSHOOTER, 
    }

    public enum ActionsAvailable{
        NEARPLAYER, FARPLAYER;
        
        ArrayList<Action> actAv;
        ArrayList<Double> weights;
        
    }
    
    public enum Action {
        WALK(5000), SHOOT(5000), RUNAWAY(5000), JUMP(5000), NOTHING(5000);

        //atributes for action
        boolean doneWithAction;
        //timer (in MS) to do the action for 
        double TotalTimeToRun;
        double currentTimeLeft;
        int weight;

        //constructior for the action
        Action(double r) {
            this.TotalTimeToRun = r;
            this.currentTimeLeft = r;
            this.doneWithAction = false;
            //weight =w;
            
        }

        boolean isDoneWithAction() {
            return doneWithAction;
        }

        void setDoneWithAction(boolean b) {
            doneWithAction = b;
        }

        //methods each action has
        double getTimeLeft() {
            return currentTimeLeft;
        }

        void updateTimer(double d) {
            currentTimeLeft = d;
        }

        void resetTimer() {
            currentTimeLeft = TotalTimeToRun;
        }
    }

    public Enemy(double x, double y, int w, int h, AnimationData ad, boolean canJumpOn) {
        super(x, y, w, h, ad);
        distanceMoved = 0;
        goingLeft = true;
        seesPlayer = false;
        currentAction = Action.WALK;
        this.canJumpOn = canJumpOn;

    }

    public void resetPewCooldown() {
        pewCooldown = 500;
    }

    @Override
    public void update(float dt) {
        //updates
        currAnimation.update(dt);
        // System.out.println(dt);
        checkIfSeesPlayer();
        if (seesPlayer) {
            //force choose action? 

        }
        //do action
        doAction(dt);
        //new action check for next frame
        if (currentAction.isDoneWithAction() || currentAction.currentTimeLeft <= 0) {
            //reset and do a new action
            resetAndChooseAction();
        }
    }

    @Override
    public void draw(GL2 g, Camera c) {
        GameLoop.glDrawSprite(g, currAnimation.getCurFrameImage(), (int) x - c.getX(), (int) y - c.getY(), currAnimation.getcurFrameSize()[0], currAnimation.getcurFrameSize()[1]);
    }

    Action getNewAction() {
        Random r = new Random();
        int roll = r.nextInt(10);

        if(seesPlayer){
            //choose a weighted action based on if the enemy sees the player
            
        }else{
            if (roll < 5) {
                return Action.WALK;
            } else if (roll < 8) {
                return Action.JUMP;
            } else if (roll < 10) {
                return Action.SHOOT;
            } else {
                return Action.NOTHING;
            }
        }
       return Action.NOTHING;
    }

    private void doAction(float dt) {
        double updatedtime;

        switch (currentAction) {
            case WALK:
                //do walk
                if (distanceMoved < 20 && goingLeft) {
                    setX(getX() - .5);
                    distanceMoved += 1;
                } else if (distanceMoved < 20 && !goingLeft) {
                    setX(getX() + .5);
                    distanceMoved += 1;
                } else if (distanceMoved >= 20) {
                    distanceMoved = 0;
                    //turn around
                    goingLeft = !goingLeft;
                }
                updatedtime = currentAction.getTimeLeft() - dt;
                currentAction.updateTimer(updatedtime);

                System.out.println("is walking, time left: " + currentAction.getTimeLeft());

                break;
            case SHOOT: //do shoot

                updatedtime = currentAction.getTimeLeft() - dt;
                currentAction.updateTimer(updatedtime);
                System.out.println("is shooting");
                break;
            case JUMP: //do jump

                //   jumpPressed=true;
                updatedtime = currentAction.getTimeLeft() - dt;
                currentAction.updateTimer(updatedtime);
                System.out.println("is jumping");
                break;
            case NOTHING: //do nothing

                updatedtime = currentAction.getTimeLeft() - dt;
                currentAction.updateTimer(updatedtime);
                System.out.println("is doing nothing");
                break;
        }
    }

}
