package Helpers;

public class AABB {
	private int x; //x pos
	private int y; //y pos
	private int w; //width of AABB
	private int h; //Height of AABB
	private int topOverlap;
	private int bottomOverlap;
	private int leftOverlap;
	private int rightOverlap;
	
//        public AABB(int w, int h){
//            this.w = w;
//            this.h = h;
//            //needs to add x y
//        }
	public AABB(int x,int y,int w,int h){
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
		topOverlap=bottomOverlap=leftOverlap=rightOverlap=0;
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
	
	/**
	 * if box 1 is to the left of box 2
	 * @param box1 AABB to check
	 * @param box2 AABB to check
	 * @return true if to the left, false if not
	 */
	public static boolean AABBisLeftOf(AABB box1, AABB box2){
            // box1 to the right
            
            return box1.x < box2.x + box2.w;
	}
	/**
	 * if box 1 is to the right of box 2
	 * @param box1 AABB to check
	 * @param box2 AABB to check
	 * @return true if to the right, false if not
	 */
	public static boolean AABBisRightOf(AABB box1, AABB box2){
            // box1 to the right
            return box1.x < box2.x + box2.w;
	}
	/**
	 * if box 1 is above box 2
	 * @param box1 AABB to check
	 * @param box2 AABB to check
	 * @return true if box 1 is above, false if not
	 */
	public static boolean AABBisAbove(AABB box1, AABB box2){
            
            
            return box1.y + box1.h < box2.y;
	}
        public static int getOverlap(AABB box1, AABB box2){
            int ov;
            //box1 to right of box2
            if(box1.x>box2.x+box2.w){
                ov=(box1.x+box2.w)-box1.w;
                return ov;
            }
            //box1 left of box2
            if(box1.x+box1.w<box2.x){
                ov=(box1.x+box1.w)-box2.x;
                return ov;
            }
            //box1 above box2
            if(box1.y + box1.h < box2.y){
                ov=(5);
                return ov;
            }
            //box1 below box2
            if(box1.y > box2.y + box2.h){
                ov=(box2.y+box2.h)-box1.y;
                return ov;
            }
            return 0;
        }
        public static boolean AABBIntersectLeftOf(AABB box1,AABB box2, double prevX, double currX){
            return (prevX<currX && AABBIntersect(box1,box2));
        }
        
        public static boolean AABBIntersectAbove(AABB box1, AABB box2, double yvel){
            //falling down and there is an intersect
            // && AABBisBelow(box1,box2)
//            // box1 to the right
//		 if (box1.x > box2.x + box2.w) {
//		 return false;
//		 }
//		 // box1 to the left
//		 if (box1.x + box1.w < box2.x) {
//		 return false;
//		 }
//		 // box1 below
//		 if (box1.y > box2.y + box2.h) {
//		 return false;
//		 }
//		 // box1 above
//		 if (!(box1.y + box1.h < box2.y)) {
//                     if(yvel>.009)return true;
//		 }
            return AABBIntersect(box1, box2) && yvel>0;
           // return  false;
        }
	/**
	 * if box 1 is below box 2
	 * @param box1 AABB to check
	 * @param box2 AABB to check
	 * @return true if box 1 is below, false if not
	 */
	public static boolean AABBisBelow(AABB box1, AABB box2){
            return box1.y > box2.y + box2.h;
	}
}
