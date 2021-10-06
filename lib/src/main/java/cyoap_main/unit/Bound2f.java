package cyoap_main.unit;

public class Bound2f {
	public float x, y;
	public float width, height;

	public Bound2f() {
		this(0, 0, 0, 0);
	}
	public Bound2f(float x, float y, float width, float height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
	
	public Bound2f(Bound2f other) {
		this.x = other.x;
		this.y = other.y;
		this.width = other.width;
		this.height = other.height;
	}
	
	public boolean intersect(Bound2f other) {
		if (x + width < other.x)
			return false;
		if (x > other.x + other.width)
			return false;
		if (y + height < other.y)
			return false;
		if (y > other.y + other.height)
			return false;
		return true;
	}
	
	public boolean intersect(Vector2f other) {
		if (x + width < other.x())
			return false;
		if (x > other.x())
			return false;
		if (y + height < other.y())
			return false;
		if (y > other.y())
			return false;
		return true;
	}
}
