package cyoap_main.grammer;

public record ParsingUnit(int type, String data) {
    public ParsingUnit changeUnitType(int new_type) {
        return new ParsingUnit(new_type, data);
    }

    public ParsingUnit addUnitData(char new_data) {
        return new ParsingUnit(type, data + new_data);
    }
}
