package Character;

import java.util.ArrayList;

import Animation.AnimationData;
import Helpers.AABB;
import Helpers.Camera;
import Helpers.GameLoop;
import com.jogamp.opengl.GL2;

public class CharacterData {

    double x;
    double y;
    double prevX;
    double prevY;
    boolean isWalking;
    AnimationData currAnimation;
    public double yvelocity;
    boolean isGrounded;
    public double gravity = .0009;
    int health;
    boolean jumpPressed;
    protected AABB box;
    int sprite;
    int w;
    int h;
    public double jumpvel;
    boolean isDead;
    ArrayList<Item> projectiles;

    /**
     * create a new character in the game. can be user or AI
     * @param x pos
     * @param y pos
     * @param sprite texture for the character
     * @param w width 
     * @param h height
     * @param ad the animation data the character starts out with
     */
    public CharacterData(double x, double y, int w, int h, AnimationData ad) {
        this.x = x;
        currAnimation = ad;
        prevX=this.x;
        this.y = y;
        prevY = this.y;
        this.sprite = currAnimation.getCurFrameImage();
        this.w = w;
        this.h = h;
        isWalking = false;
        jumpPressed = false;
        yvelocity = 0.0;
        isGrounded = true;
        this.box = new AABB((int)x,(int)y);
        jumpvel = -.3;
        isDead = false;
        health = 10;
    }

    public double getPrevX(){
        return prevX;
    }
    public void setPrevX(double x){
        prevX=x;
    }
    
    public double getPrevY(){
        return prevY;
    }
    public void setPrevY(double y){
        prevY=y;
    }
    public void setDeath(boolean b) {
        isDead = b;
    }

    public boolean isDead() {
        return isDead;
    }

    public void setX(double x) {
        this.x = x;
        box.updateX((int)this.x);
    }

    public void setY(double y) {
        this.y = y;
        box.updateY((int)this.y);
    }

    public int getW() {
        return w;
    }

    public int getH() {
        return h;
    }

    public int getSprite() {
        return sprite;
    }

    public AABB getAABB() {
        return box;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int h) {
        health = h;
    }

   public void update(float deltaTime) {
        currAnimation.update(deltaTime);
    }
   
   public AnimationData getCurrentAnimation(){
       return currAnimation;
   }

    public void draw(GL2 gl, Camera c) {
        GameLoop.glDrawSprite(gl, currAnimation.getCurFrameImage() , (int)(x-c.getX()), (int)(y-c.getY()), currAnimation.def.frames[currAnimation.getCurFrame()].frameSize[0], currAnimation.def.frames[currAnimation.getCurFrame()].frameSize[1]);
    }

    //for the game loop to use
    public double getX() {
        return x;
    }
    public double getY() {
        return y;
    }

    public double addToX(double i) {

        return (x + i);

    }

    public double addtoY(double i) {
        return (y + i);
    }

    public boolean isGrounded() {
        return isGrounded;
    }

    public void setGrounded(boolean b) {
        isGrounded = b;
    }
    public void setCurrentAnimation(AnimationData ad){
        currAnimation = ad;
    }

    public boolean isJumping() {
        return jumpPressed;
    }

    public void setJump(boolean b) {
        jumpPressed = b;
    }

    public boolean isWalking() {
        return isWalking;
    }

    public void setWalking(boolean b) {
        isWalking = b;
    }

    public double updateyvelocity() {
        return yvelocity;
    }

}
