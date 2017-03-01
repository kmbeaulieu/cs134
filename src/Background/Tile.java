package Background;

public class Tile {
	int texture; // the tga file texture int
	
	/**
	 * make a tile with a given texture
	 * @param tex
	 */
	public Tile(int tex){
		texture = tex;
	}
	
	public int getTexture(){
		return texture;
	}
	public void setTexture(int tex){
		texture = tex;
	}
	
}
