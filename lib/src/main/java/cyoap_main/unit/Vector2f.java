package cyoap_main.unit;

public record Vector2f(float x, float y) {
    public Vector2f add(Vector2f vec) {
        if (vec == null) return this;
        return new Vector2f(x + vec.x(), y + vec.y());
    }

    public Vector2f sub(Vector2f vec) {
        if (vec == null) return this;
        return new Vector2f(x - vec.x(), y - vec.y());
    }

    public Vector2f mul(float s) {
        return new Vector2f(x * s, y * s);
    }

    public Vector2f pow(float t) {
        return new Vector2f((float) Math.pow(x, t), (float) Math.pow(y, t));
    }

    public float mul_scala(Vector2f v) {
        return x * v.x() + y * v.y();
    }

    public float sum() {
        return x + y;
    }
}