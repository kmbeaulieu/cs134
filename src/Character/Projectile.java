/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Character;

import Helpers.AABB;
import Helpers.Camera;
import static Helpers.GameLoop.glDrawSprite;
import com.jogamp.opengl.GL2;

/**
 *
 * @author Krystle
 */
public class Projectile {
    
    double x;
    double y;
    double xVel;
    double yVel;
    AABB box;
    int damage;
    int sprite;
    double gravity;
    double speed;
    int w;
    int h; 
    
    //-1 is left, 1 is right
    public int dirX;
    //-1 is up, 1 is down
    public int dirY;
   public Projectile(double x,double y, int spriteTex, int w, int h, int dirX,double speed){
        this.x = x;
        this.y = y;
        damage = 3;
        this.sprite = spriteTex;
        this.w = w;
        this.h = h;
        this.dirX = dirX;
        this.speed = speed;
        xVel = this.speed;
        yVel = -0.2;
        this.box = new AABB((int)x,(int)y,w,h);
        gravity = .0009;
    }
    public Projectile(double x,double y, int spriteTex, int w, int h, int dir){
        this.x = x;
        this.y = y;
        damage = 3;
        this.sprite = spriteTex;
        this.w = w;
        this.h = h;
        speed = 5;
        xVel = speed;
        yVel = -0.2;
        this.box = new AABB((int)x,(int)y,w,h);
        gravity = .0009;
    }
    //for things that shoot in a straight line based off of an angle
    public Projectile(double x,double y, int spriteTex, int w, int h, double theta){
        this.x = x;
        this.y = y;
        damage = 3;
        this.sprite = spriteTex;
        this.w = w;
        this.h = h;
        if(theta>0 && theta<Math.PI){
            //going up
            dirY = -1;
            //if quadrant 1 (top right) or quadrant 4 (bottom right) then going right
            if((theta>0 && theta<(Math.PI/2)) || (theta>(3*Math.PI/2)&& theta<2*Math.PI)){
                dirX = 1;
            }else{
                dirX=-1;
            }
        }else{
            dirY = 1;
        }
       // this.dir = dir;
        speed = .4;
        xVel = speed;
        yVel = speed;
        this.box = new AABB(x,y,w,h);
        gravity = .002;
    }
    public void setGravity(double d){
        gravity = d;
    }

    public int getDamage(){
        return damage;
    }
    public AABB getAABB(){
        return box;
    }
    public int getW(){
        return w;
    }
    public int getH(){
        return h;
    }
    public int getSprite(){
        return sprite;
    }
   
    public double getSpeed(){
        return speed;
    }
    
    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
        this.box.updateX((int)x);
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
        this.box.updateY((int)y);
    }
    
    public double getYVel(){
    	return yVel;
    }
    public void setYVel(double y){
    	yVel = y;
    }
    public void addToYVel(double y){
    	yVel+=y;
    }
    
    public double getXVel(){
    	return xVel;
    }
    public void setXVel(double x){
    	xVel = x;
    }
    public void addToXVel(double x){
    	xVel+=x;
    }
    public double getGravity(){
    	return gravity;
    }
    public int getDirX(){
     return dirX;   
    }
    
    public void draw(GL2 gl, Camera c){
     glDrawSprite(gl, getSprite(),(int)getX() - (int)c.getX(),(int)getY() - (int)c.getY(), getW(), getH());

    }
}
