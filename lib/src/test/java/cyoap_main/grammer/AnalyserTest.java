package cyoap_main.grammer;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AnalyserTest {
    @Test
    public void testAnalyseString() {
        var ins = VariableDataBase.getInstance();
        String str_test = """
                aaa = 3
                a = 3 + 5
                func1 = round(4.8)
                func2 = ceil(4.8)
                func3 = floor(4.8)
                func4 = floor(4)
                c = "abcdefg가나다라마바사"
                d = true
                e = false
                f = 6-5.5
                comp1 = f == 0.5
                comp2 = f >= 0.5
                comp3 = f > 0.5
                if(f == 0.5, alpha = 11, alpha = 15)
                if(f == 8.5, beta = 12, beta = 16)
                test_alpha = 1
                test_alpha += 3
                """;
        String str_test_2 = """
                a123123aa = 3
                """;
        var l = Analyser.getInstance().parser(str_test_2);
        Analyser.getInstance().analyseList(l);
        assertEquals(3, (int) ins.getValue("a123123aa").getData());

        var func = Analyser.getInstance().parser(str_test);

        assertEquals("aaa = 3", func.get(0).strip());
        assertEquals("a = 3 + 5", func.get(1).strip());

        Analyser.getInstance().analyseList(func);
        assertEquals(3, (int) ins.getValue("aaa").getData());
        assertEquals(8, (int) ins.getValue("a").getData());
        assertEquals(5, (int) ins.getValue("func1").getData());
        assertEquals(5, (int) ins.getValue("func2").getData());
        assertEquals(4, (int) ins.getValue("func3").getData());
        assertEquals(4, (int) ins.getValue("func4").getData());
        assertEquals("abcdefg가나다라마바사", ins.getValue("c").getData());
        assertTrue((boolean) ins.getValue("d").getData());
        assertFalse((boolean) ins.getValue("e").getData());
        assertEquals(0.5f, (float) ins.getValue("f").getData());

        assertTrue((boolean) ins.getValue("comp1").getData());
        assertTrue((boolean) ins.getValue("comp2").getData());
        assertFalse((boolean) ins.getValue("comp3").getData());

        assertEquals(11, (int) ins.getValue("alpha").getData());
        assertEquals(12, (int) ins.getValue("beta").getData());

        assertEquals(4, (int) ins.getValue("test_alpha").getData());
    }
}