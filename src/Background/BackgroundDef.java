package Background;

public class BackgroundDef {
	private int width = 32;
	private int height = 15;
	private int[] tiles;
	
	/**
	 * Set up your background definition
	 * @param texture the int of the tga file texture
	 * @param numTextures the number of times the texture is used. 
	 */
	public BackgroundDef(int texture, int numTextures){
		
		for(int i = 0; i<numTextures;i++){
			tiles[i]= texture;
		}
		
	}
	
	public BackgroundDef(){
		
	}
	
	public int getTile(int x, int y) {
		return tiles[(y * width) + x];
	}
	
	/**
	 * set a background def with a certain texture of tile.
	 * @param texture to set into the background def
	 */
	public void setTile(int[] textures){
	}
	
	public int getWidth(){
		return width;
	}
	public int getHeight(){
		return height;
	}
	
}
