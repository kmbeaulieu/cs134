package Helpers;

public class AABB {
	private int x; //x pos
	private int y; //y pos
	private int w; //width of AABB
	private int h; //Height of AABB
	
	public AABB(int x,int y,int w,int h){
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
	}
	public AABB(int x,int y){
		this.x = x;
		this.y = y;
	}
	
	public int getX(){
		return x;
	}
	public int getY(){
		return y;
	}
	
	public void updateX(int x){
		this.x =x;
		
	}
	public void updateY(int y){
		this.y = y;
	}
	
	/**
	 * change the width of the AABB
	 * @param w width 
	 */
	public void setW(int w){
		this.w = w;
	}
	/**
	 * change the height of the AABB
	 * @param h height
	 */
	public void setH(int h){
		this.h = h;
	}
	
	/**
	 * See if two AABBs intersect (e.g. are colliding or something is on screen)
	 * @param box1 first box to compare
	 * @param box2 second box to compare
	 * @return boolean if they are intersecting or not
	 */
	public static boolean AABBIntersect(AABB box1, AABB box2){
		// box1 to the right
		 if (box1.x > box2.x + box2.w) {
		 return false;
		 }
		 // box1 to the left
		 if (box1.x + box1.w < box2.x) {
		 return false;
		 }
		 // box1 below
		 if (box1.y > box2.y + box2.h) {
		 return false;
		 }
		 // box1 above
		 if (box1.y + box1.h < box2.y) {
		 return false;
		 }

		return true;
	}
}
