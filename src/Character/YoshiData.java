package Character;

import Animation.AnimationData;
import Helpers.AABB;
import Helpers.Camera;
import Helpers.GameLoop;
import static Helpers.GameLoop.glDrawSprite;
import com.jogamp.opengl.GL2;

public class YoshiData extends CharacterData {

    boolean isTongueOut;
    boolean goingLeft;
    boolean goingRight;
    boolean isThrowing;
    boolean canFlutter;
    double jumpOffEnemyYVel;
    int speed;
    public int numEggs;
    public int projectileTimer;
    public int aimTex;
    public int[] aimSize;
    public boolean isAiming;
    public double aimX;
    public double aimY;
    //double in radians
    public double theta;
    public double prevTheta;
    public int score;

    /**
     * Make a yoshi at a certain coordinate. The default speed is 5.
     *
     * @param x sprite x coordinate.
     * @param y sprite y coordinate.
     * @param w sprite width
     * @param h sprite height
     * @param aimTex texture for aiming
     * @param d starting animation
     * @param aimSize int[] for tex size for aiming
     */
    public YoshiData(double x, double y, int w, int h, AnimationData d,int aimTex,int[] aimSize) {
        super(x, y, w, h, d);
        isTongueOut = false;
        this.speed = 2;
        this.goingRight = true;
        this.goingLeft=false;
        numEggs = 5;
        projectileTimer = 500;
        health = 1;
        jumpOffEnemyYVel=-.1;
        canFlutter=true;
        isAiming=false;
        //not aiming
        aimX = x+10;
        aimY = y+10;
        //in radians
        theta = 0;
        prevTheta = 0;
        this.aimTex = aimTex;
        this.aimSize = aimSize;
        score=0;
    }

    @Override
    public void setGrounded(boolean b){
        super.setGrounded(b);
        //auto set the flutter for yoshi every time you touch the ground
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

    @Override
    public AnimationData getCurrentAnimation() {
        return super.getCurrentAnimation(); 
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime); 
        if(isAiming){
            if(goingLeft){
                //aimer will be on the left side, starting at bottom
                //save point
                prevTheta = theta;
                theta-=Math.PI/128;
                System.out.println("yoshi x = "+x+" yoshi y = " +y+" aimx "+aimX+" aimy "+aimY);
                aimX = x-20 + ((60) * Math.cos(theta));
                aimY = (60*Math.sin(theta));
                if(theta<(Math.PI/2) || theta>(3*(Math.PI)/2)){
                    theta = Math.PI;
                }//if angle is greater than straight up
                //x = rcostheta
                
            }else if(goingRight){
                //aimer will be on the right side, starting at bottom
                //save point
                prevTheta = theta;
                //go up in pi/12 increments
                theta+=Math.PI/128;
                System.out.println("yoshi x = "+x+" yoshi y = " +y+" aimx "+aimX+" aimy "+aimY);
                aimX = x+20 + ((60) * Math.cos(theta));
                aimY = (60*Math.sin(theta));
                //fix the angle
                if(theta<0 || theta>(Math.PI/2)){
                    theta = 0;
                }//if angle is greater than straight up
               
            }
        }
    }
    
    public void resetAim(){
        theta = 0;
        prevTheta=0;
    }

    @Override
    public double updateyvelocity() {
        return super.updateyvelocity(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void draw(GL2 gl, Camera c) {
        super.draw(gl, c); 
        if(isAiming){
            GameLoop.glDrawSprite(gl, aimTex , (int)(aimX-c.getX()), (int)(y-aimY-c.getY()),aimSize[0], aimSize[1]);
        }
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
