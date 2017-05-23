/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Character;

import Animation.AnimationData;
import Helpers.Camera;
import Helpers.GameLoop;
import Helpers.WeightedAction;
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
    public boolean canShoot;
    // private boolean goingRight;
    private boolean seesPlayer;
    private final double CLOSE_TO_PLAYER_DISTANCE = 100;
    public int projTex;
    public int[] projTexSize;
    public double projSpeed;
    public int pewCooldown = 1000;

    private Action currentAction;

    private void resetAndChooseAction() {
        currentAction.resetTimer();
        currentAction.setDoneWithAction(false);
        currentAction = getNewAction();
    }

    private void checkIfSeesPlayer(double playerx, double playery) {
        //TODO maybe make later
        seesPlayer = (Math.abs(playerx - x) < CLOSE_TO_PLAYER_DISTANCE || Math.abs(playery - y) < CLOSE_TO_PLAYER_DISTANCE);

    }

    public int getProjTex() {
        //return the projectile texture of this enemy;
        return projTex;
    }

    private void setProjSpeed(double s) {
        projSpeed = s;
    }

    /**
     * set the texture info this enemy uses for projectiles
     *
     * @param tex texture
     * @param wh width/height array
     */
    public void setProjectileInfo(int tex, int[] wh) {
        projTex = tex;
        projTexSize = wh;

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
        currentAction = Action.NOTHING;
        this.canJumpOn = canJumpOn;
        seesPlayer = false;
        canShoot = false;
        projSpeed = 1;
    }

    public boolean canJumpOn() {
        return canJumpOn;
    }

    public void resetPewCooldown() {
        pewCooldown = 1000;
    }

    @Override
    public void update(float dt) {
        //update animation
        currAnimation.update(dt);
        box.setW(currAnimation.getcurFrameSize()[0]);
        box.setH(currAnimation.getcurFrameSize()[1]);

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
        if (roll < 6) {
            return Action.WALK;
        } else if (roll < 7) {
            return Action.JUMP;
        } else if (roll < 9) {
            return Action.SHOOT;
        } else {
            return Action.NOTHING;
        }
    }

    private void doAction(float dt) {
        double updatedtime;
        switch (currentAction) {
            case WALK:
                //do walk ATM is just back and forth
                if (distanceMoved < 35 && goingLeft) {
                    setX(getX() - .5);
                    distanceMoved += 1;
                } else if (distanceMoved < 35 && !goingLeft) {
                    setX(getX() + .5);
                    distanceMoved += 1;
                } else if (distanceMoved >= 35) {
                    distanceMoved = 0;
                    //turn around
                    goingLeft = !goingLeft;
                }
                updatedtime = currentAction.getTimeLeft() - dt;
                currentAction.updateTimer(updatedtime);
                //  System.out.println("WALKING");
                break;
            case SHOOT: //do shoot
                if (canShoot) {
                    if (pewCooldown <= 0) {
                        resetPewCooldown();
                        double halfheight = (y + h / 2);
                        if (goingLeft) {
                            projectiles.add(new Projectile(x - 3, halfheight, projTex, projTexSize[0], projTexSize[1], -1, 1));
                        } else {
                            projectiles.add(new Projectile((x + w + 3), halfheight, projTex, projTexSize[0], projTexSize[1], 1, 1));
                        }
                    }
                    pewCooldown -= dt;
                    updatedtime = currentAction.getTimeLeft() - dt;

                } else {
                    updatedtime = 0;

                }
                currentAction.updateTimer(updatedtime);
                //     System.out.println("SHOOTING");
                break;
            case JUMP: //do jump

                yvelocity = jumpvel;
                isGrounded = false;
                updatedtime = 0;
                // updatedtime = currentAction.getTimeLeft() - dt;
                currentAction.updateTimer(updatedtime);
                //      System.out.println("JUMPING");
                break;
            case NOTHING: //do nothing

                updatedtime = currentAction.getTimeLeft() - dt;
                currentAction.updateTimer(updatedtime);
                //       System.out.println("is doing nothing");
                break;
        }
    }
}
