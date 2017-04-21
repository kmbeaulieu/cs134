package Character;
import Helpers.AABB;

public class Item {

	AABB itemBox;
	int x;
	int y;
	int sprite;
	int initXSpeed;
	
	
	public Item(int d,int e, int s){
		this.x=d;
		this.y=e;
		itemBox = new AABB(d,e);
		sprite = s;
		initXSpeed = 1;
	}
}
