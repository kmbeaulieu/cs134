package Helpers;

public class Camera {
	private int x;
	private int y;
	
	/**
	 * create the camera with the top left corner at the given x and y
	 * @param x coordinate
	 * @param y coordinate
	 */
	public Camera(int x, int y){
		this.x=x;
		this.y=y;
	}
	
	//get the value for x and y
	public int getX(){
		return x;
	}
	
	public int getY(){
		return y;
	}
	
	//set the value for x and y
	public void setX(int x) {
		this.x = x;
	}

	public void setY(int y) {
		this.y = y;
	}
	
}
