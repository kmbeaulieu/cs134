package Background;

public class Tile {
	public String name;
	public int texture; // the tga file texture int
	public int[] textureSize; //the x/y size of the tile's texture
	public boolean collidable;
        int heightOffset;
        int widthOffset;
        
	
	
	/**
	 * make a tile with a given texture
         * @param n name of the tile (filename usually)
	 * @param tex the texture of the image
	 * @param textureSize the x/y size of the texture
	 */
	public Tile(String n,int tex, int[] textureSize){
		name=n;
		texture = tex;
		this.textureSize = textureSize; 
		collidable = false;
	}
        /**
	 * make a tile with a given texture
         * @param n name
	 * @param tex the texture of the image
	 * @param textureSize the x/y size of the texture
	 * @param col if the given tile is collidable 
	 */
	public Tile(String n,int tex, int[] textureSize, boolean b){
		name=n;
		texture = tex;
		this.textureSize = textureSize; 
		collidable = b;
                heightOffset=0;
                widthOffset=0;
	}
	
        public void setHeightOffset(int h){
            heightOffset = h;
        }
        
        public void setWidthOffset(int w){
            widthOffset = w;
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
	
	public void setCollidable(Boolean b){
		collidable = b;
	}
	public boolean isCollidable(){
		return collidable;
	}
	
}
