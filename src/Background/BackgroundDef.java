package Background;

public class BackgroundDef {
	private int width;
	private int height;
	private Tile[] tiles;
	private Tile[][] layout;
	
	/**
	 * Create a definition of a background. (like the layer of hills or trees or something)
	 * @param tiles an array of tiles to use
	 * @param width how many tiles across
	 * @param height how many tiles tall
	 */
	public BackgroundDef(Tile[] tiles, int width, int height){
		this.tiles=tiles;
		
		
	}
	
	/**
	 * Use this when you have a predefined layout instead of going by tile
	 * @param layout the tile set for the background layer
	 */
	public BackgroundDef(Tile[][] layout){
		this.layout=layout;
		this.height = layout.length;
		int[][] test = new int[][]{{1,1,1},{2,2,2}};
		System.out.println(test.length);
		
	}
	/**
	 * set a predefined layout. Is tiles[] needed then? To keep it like framedef probably..
	 * @param layout the grid for the "level" to set up
	 */
	public void setLayout(Tile[][] layout){
		this.layout = layout;
	}
	
	/**
	 * get a tile at a certain layout position
	 * @param x how many tiles in the layout to go in x dir
	 * @param y how many tiles in the layout to go y dir
	 * @return the Tile found
	 */
	public Tile getTile(int x, int y) {
		return tiles[(y * width) + x];
	}
	
	/**
	 * Set a spot on the layout to a certain tile. 
	 * This is good if you have a small amount of tiles. Otherwise it might be best to use a predefined layout.
	 * @param posX What tile's x pos to put Tile t in
	 * @param posY what tile's y pos to put Tile t in
	 * @param t the Tile to put in
	 */
	public void setTile(int posX, int posY, Tile t){
		layout[posY][posX] = t;
	}
	
	public int getWidth(){
		return width;
	}
	public int getHeight(){
		return height;
	}
	
}
