package cyoap_main.unit;

public record Vector2f(float x, float y) {
    public Vector2f add(Vector2f vec) {
        return new Vector2f(x + vec.x(), y + vec.y());
    }

    public Vector2f sub(Vector2f vec) {
        return new Vector2f(x - vec.x(), y - vec.y());
    }

    public Vector2f mul(float s) {
        return new Vector2f(x * s, y * s);
    }

    public float mul_scala(Vector2f v) {
        return x * v.x() + y * v.y();
    }
}