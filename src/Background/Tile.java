package Background;

public class Tile {
	public String name;
	public int texture; // the tga file texture int
	public int[] textureSize; //the x/y size of the tile's texture
	//public boolean collidable;
	
	
	/**
	 * make a tile with a given texture
	 * @param tex the texture of the image
	 * @param textureSize the x/y size of the texture
	 * @param col if the given tile is collidable TODO add this
	 */
	public Tile(String n,int tex, int[] textureSize){
		name=n;
		texture = tex;
		this.textureSize = textureSize; 
		//collidable = col;
	}
	
	public int getTexture(){
		return texture;
	}
	public void setTexture(int tex){
		texture = tex;
	}
	public String getName(){
		return name;
	}
	
//	public boolean isCollideable(){
//		return collidable;
//	}
//	
}
