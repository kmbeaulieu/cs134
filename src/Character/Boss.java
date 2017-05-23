package Character;

import Animation.AnimationData;
import Helpers.Camera;
import Helpers.GameLoop;
import com.jogamp.opengl.GL2;

public class Boss extends CharacterData {

    public int attackTimer = 3000;
    public int doingattackTimer = 2000;
    private boolean goingLeft;
    private int distanceMoved = 0;
    

    public Boss(double x, double y, int w, int h, AnimationData ad) {
        super(x, y, w, h, ad);
        box.setW(ad.getcurFrameSize()[0]);
        box.setH(ad.getcurFrameSize()[1]);
        goingLeft = false;
        health = 13;
    }

    public void reset(){
        box.setH(0);
        box.setW(0);
    }
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
    }

    public void move(Camera c) {
        if (distanceMoved < 50 && goingLeft) {
            if (getX() - 1 >= 32) {
                setX(getX() - 1);
                distanceMoved += 1;
            } else {
                goingLeft = !goingLeft;
            }
        } else if (distanceMoved < 50 && !goingLeft) {
            if (getX() + 1 + w <= 18 * 16) {
                setX(getX() + 1);
                distanceMoved += 1;
            } else {
                goingLeft = !goingLeft;
            }
        } else if (distanceMoved >= 50) {
            distanceMoved = 0;
            //turn around
            goingLeft = !goingLeft;
        }
    }

    public void walk(Camera c) {

        if (distanceMoved < 150 && goingLeft) {
            if (getX() - 1 >= 32) {
                setX(getX() - 1);
                distanceMoved += 1;
            } else {
                goingLeft = !goingLeft;
            }
        } else if (distanceMoved < 150 && !goingLeft) {
            if (getX() + 1 + w <= 18 * 16) {
                setX(getX() + 1);
                distanceMoved += 1;
            } else {
                goingLeft = !goingLeft;
            }
        } else if (distanceMoved >= 150) {
            distanceMoved = 0;
            //turn around
            goingLeft = !goingLeft;
        }
//                updatedtime = currentAction.getTimeLeft() - dt;
//                currentAction.updateTimer(updatedtime);
        //  System.out.println("WALKING");
    }

    @Override
    public void draw(GL2 gl, Camera c) {
        if (goingLeft) {
            GameLoop.glDrawSprite(gl, currAnimation.getCurFrameImage(), (int) (x - c.getX()), (int) (y - c.getY()), currAnimation.def.frames[currAnimation.getCurFrame()].frameSize[0] * -1, currAnimation.def.frames[currAnimation.getCurFrame()].frameSize[1]);
        } else {
            GameLoop.glDrawSprite(gl, currAnimation.getCurFrameImage(), (int) (x - c.getX()), (int) (y - c.getY()), currAnimation.def.frames[currAnimation.getCurFrame()].frameSize[0], currAnimation.def.frames[currAnimation.getCurFrame()].frameSize[1]);
        }
    }
}
