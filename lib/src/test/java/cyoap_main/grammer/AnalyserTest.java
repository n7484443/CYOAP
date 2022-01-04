package cyoap_main.grammer;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AnalyserTest {
    @Test
    public void testAnalyseString() {
        var ins = VariableDataBase.getInstance();
        String str_test = """
                가나다라마바사
                {aaa = 3}
                {a = 3 + 5}
                {func1 = round(4.8)}
                {func2 = ceil(4.8)}
                {func3 = floor(4.8)}
                {func4 = floor(4)}
                {c = "dddddd"}
                {d = true}
                가다ㅏㄷ
                {e = false}
                {f = 6-5.5}
                {t = f == 0.5}
                {if(f == 0.5, alpha = 11, alpha = 15)}
                {if(f == 8.5, beta = 12, beta = 16)}
                {test_alpha = 1}
                {test_alpha += 3}
                """;
        String str_test_2 = """
                {a123123aa = 3}
                """;
        var l = Analyser.getInstance().parser(str_test_2);
        Analyser.getInstance().analyseList(l.getValue());
        assertEquals(3, (int) ins.getValue("a123123aa").getData());

        var t = Analyser.getInstance().parser(str_test);
        var text = t.getKey();
        var func = t.getValue();

        assertEquals("가나다라마바사", text.get(0).strip());
        assertEquals("가다ㅏㄷ", text.get(1).strip());
        assertEquals("aaa = 3", func.get(0).strip());
        assertEquals("a = 3 + 5", func.get(1).strip());

        Analyser.getInstance().analyseList(func);
        assertEquals(3, (int) ins.getValue("aaa").getData());
        assertEquals(8, (int) ins.getValue("a").getData());
        assertEquals(5, (int) ins.getValue("func1").getData());
        assertEquals(5, (int) ins.getValue("func2").getData());
        assertEquals(4, (int) ins.getValue("func3").getData());
        assertEquals(4, (int) ins.getValue("func4").getData());
        assertEquals("dddddd", ins.getValue("c").getData());
        assertTrue((boolean) ins.getValue("d").getData());
        assertFalse((boolean) ins.getValue("e").getData());
        assertEquals(0.5f, (float) ins.getValue("f").getData());

        assertTrue((boolean) ins.getValue("t").getData());

        assertEquals(11, (int) ins.getValue("alpha").getData());
        assertEquals(12, (int) ins.getValue("beta").getData());

        assertEquals(4, (int) ins.getValue("test_alpha").getData());
    }
}