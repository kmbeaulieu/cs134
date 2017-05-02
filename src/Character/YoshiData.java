package Character;

import Animation.AnimationData;
import Helpers.AABB;

public class YoshiData extends CharacterData {

    boolean isTongueOut;
    boolean goingLeft;
    boolean goingRight;
    boolean isThrowing;
    boolean canFlutter;
    double jumpOffEnemyYVel;
    int speed;
    int numEggs;
    public int projectileTimer;

    /**
     * Make a yoshi at a certain coordinate. The default speed is 5.
     *
     * @param x sprite x coordinate.
     * @param y sprite y coordinate.
     * @param sprite sprite texture
     * @param w sprite width
     * @param h sprite height
     * @param d starting animation
     */
    public YoshiData(double x, double y, int w, int h, AnimationData d) {
        super(x, y, w, h, d);
        isTongueOut = false;
        this.speed = 2;
        this.goingRight = true;
        numEggs = 0;
        projectileTimer = 500;
        health = 1;
        jumpOffEnemyYVel=-.1;
        canFlutter=true;

    }

    @Override
    public void setGrounded(boolean b){
        super.setGrounded(b);
        //auto set the flutter for yoshi every time you touch the ground
        canFlutter=true;
    }
    public boolean canFlutter(){
        return canFlutter;
    }

    public void resetTimer() {
        projectileTimer = 500;
    }

	public boolean isTongueOut(){
		return isTongueOut;
	}
	public boolean isGoingRight(){
		return goingRight;
	}
	public boolean isGoingLeft(){
		return goingLeft;
	}
    public void setTongueOut(boolean b) {
        // TODO Auto-generated method stub
        isTongueOut = b;
    }

    public void setX(int i) {
        this.x = i;
        this.box.updateX(i);
    }

    public void setY(int i) {
        this.y = i;
        this.box.updateY(i);
    }

    /**
     * move yoshi to the right, also update its left/right status
     */
    public void moveYoshiRight() {
        this.x = this.x + speed;
        this.box.updateX((int)this.x);
        this.goingRight = true;
        this.goingLeft = false;
    }

    /**
     * move yoshi to the left, also update its left/right status
     */
    public void moveYoshiLeft() {
        this.x = this.x - speed;
        this.box.updateX((int)this.x);
        this.goingRight = false;
        this.goingLeft = true;
    }

    public int getSpeed() {
        return speed;
    }

    public void fall() {
        setY(this.y + (int)this.gravity);
    }

    public void setFlutter(boolean b) {
        canFlutter = b;
    }
}
