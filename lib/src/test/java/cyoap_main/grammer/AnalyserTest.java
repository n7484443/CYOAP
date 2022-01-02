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
                {f = 3 + 5.5}
                {if(f == 4, alpha = 4, alpha = 3)}
                """;
        var t = Analyser.getInstance().parser(str_test);
        var text = t.getKey();
        var func = t.getValue();

        assertEquals("가나다라마바사", text.get(0).strip());
        assertEquals("가다ㅏㄷ", text.get(1).strip());
        assertEquals("aaa = 3", func.get(0).strip());
        assertEquals("a = 3 + 5", func.get(1).strip());

        Analyser.getInstance().analyse(func);
        assertEquals(3, (int) ins.getValue("aaa").getData());
        assertEquals(8, (int) ins.getValue("a").getData());
        assertEquals(5, (int) ins.getValue("func1").getData());
        assertEquals(5, (int) ins.getValue("func2").getData());
        assertEquals(4, (int) ins.getValue("func3").getData());
        assertEquals(4, (int) ins.getValue("func4").getData());
        assertEquals("dddddd", ins.getValue("c").getData());
        assertTrue((boolean) ins.getValue("d").getData());
        assertFalse((boolean) ins.getValue("e").getData());
        assertEquals(8.5f, (float) ins.getValue("f").getData());
        assertEquals(4, (int) ins.getValue("alpha").getData());
    }
}