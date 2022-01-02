package cyoap_main.grammer;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AnalyserTest {
    @Test
    public void testAnalyseString() {
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
                """;
        var t = Analyser.getInstance().parser(str_test);
        var text = t.getKey();
        var func = t.getValue();

        assertEquals("가나다라마바사", text.get(0).strip());
        assertEquals("가다ㅏㄷ", text.get(1).strip());
        assertEquals("aaa = 3", func.get(0).strip());
        assertEquals("a = 3 + 5", func.get(1).strip());

        Analyser.getInstance().analyse(func);
        assertEquals(3, (int) VariableDataBase.getInstance().getValue("aaa").getData());
        assertEquals(8, (int) VariableDataBase.getInstance().getValue("a").getData());
        assertEquals(5, (int) VariableDataBase.getInstance().getValue("func1").getData());
        assertEquals(5, (int) VariableDataBase.getInstance().getValue("func2").getData());
        assertEquals(4, (int) VariableDataBase.getInstance().getValue("func3").getData());
        assertEquals(4, (int) VariableDataBase.getInstance().getValue("func4").getData());
        assertEquals("dddddd", VariableDataBase.getInstance().getValue("c").getData());
        assertTrue((boolean) VariableDataBase.getInstance().getValue("d").getData());
        assertFalse((boolean) VariableDataBase.getInstance().getValue("e").getData());
        assertEquals(8.5f, (float) VariableDataBase.getInstance().getValue("f").getData());
    }
}