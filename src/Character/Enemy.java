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

/**
 *
 * @author Krystle
 */
public class Enemy extends CharacterData{
    private int distanceMoved;
    private boolean goingLeft;
   // private boolean goingRight;
    private boolean seesPlayer;
    public int pewCooldown = 500;
    
    
    public Enemy(double x, double y, int w, int h, AnimationData ad) {
        super(x, y, w, h, ad);
        distanceMoved = 0;
        goingLeft=true;
       // goingRight=false;
        seesPlayer=false;
    }
    public void resetPewCooldown(){
        pewCooldown = 500;
    }
    public void update(float dt){
        currAnimation.update(dt);
        
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
    public void draw(GL2 g, Camera c){
        GameLoop.glDrawSprite(g, currAnimation.getCurFrameImage(),(int) x-c.getX(),(int) y-c.getY(), currAnimation.getcurFrameSize()[0], currAnimation.getcurFrameSize()[1]);
    }
    
}
