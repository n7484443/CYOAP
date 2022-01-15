package cyoap_main.unit;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class Bound2f {
	public float x, y, width, height;

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
		return !(y > other.y + other.height);
	}

	public boolean intersect(Vector2f other) {
		if (x + width < other.x())
			return false;
		if (x > other.x())
			return false;
		if (y + height < other.y())
			return false;
		return !(y > other.y());
	}

	public Bound2f setPosition(float x, float y) {
		this.x = x;
		this.y = y;
		return this;
	}

	public Bound2f mul(float scale) {
		this.x *= scale;
		this.y *= scale;
		this.width *= scale;
		this.height *= scale;
		return this;
	}
}
