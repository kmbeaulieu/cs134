package Character;
import Animation.AnimationData;
import Helpers.AABB;
import Helpers.Camera;
import Helpers.GameLoop;
import com.jogamp.opengl.GL2;

public class Item {

        public int type;//0=coin, 1=doors....
	public AABB itemBox;
	int x;
	int y;
	int sprite;
	int initXSpeed;
	public AnimationData data;
	
	public Item(int x,int y, AnimationData ad, int type){
		this.x=x;
		this.y=y;
		itemBox = new AABB(x,y);
		data = ad;
		initXSpeed = 0;
                this.type =type;
	}

    public AABB getItemBox() {
        return itemBox;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getSprite() {
        return sprite;
    }
    
    public void update(long deltaTimeMS) {
                data.update(deltaTimeMS);
    }
         public void draw(GL2 gl, Camera c) {
        GameLoop.glDrawSprite(gl, data.getCurFrameImage() , (int)(x-c.getX()), (int)(y-c.getY()), data.def.frames[data.getCurFrame()].frameSize[0], data.def.frames[data.getCurFrame()].frameSize[1]);
    }
}
