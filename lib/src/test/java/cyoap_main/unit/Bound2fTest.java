package cyoap_main.unit;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class Bound2fTest {
    @Test
    public void test() {
        var bound2f_1 = new Bound2f(3, 4, 5, 6);
        var bound2f_2 = new Bound2f(6, 7, 1, 2);
        assertTrue(bound2f_1.intersect(bound2f_2));
        assertTrue(bound2f_2.intersect(bound2f_1));
        bound2f_2.setPosition(9, 11);
        assertFalse(bound2f_1.intersect(bound2f_2));
        assertFalse(bound2f_2.intersect(bound2f_1));
        var vector2f = new Vector2f(3.5f, 4.5f);
        assertTrue(bound2f_1.intersect(vector2f));
    }
}