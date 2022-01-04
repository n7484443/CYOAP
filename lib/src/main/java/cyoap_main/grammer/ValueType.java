package cyoap_main.grammer;

public class ValueType {
    public String data;
    public VariableDataBase.types type;

    public ValueType(VariableDataBase.types t) {
        this.type = t;
    }

    public ValueType(VariableDataBase.types t, String data) {
        this.type = t;
        this.data = data;
    }

    public <T> ValueType(T data) {
        this.type = getType(data);
        setData(data);
    }

    public ValueType(ValueType v) {
        if (v == null) {
            this.type = VariableDataBase.types.nulls;
            this.data = "";
        } else {
            this.type = v.type;
            this.data = v.data;
        }
    }

    public ValueType() {
        this.type = VariableDataBase.types.nulls;
    }

    public <T> VariableDataBase.types getType(T data) {
        if (data instanceof Integer)
            return VariableDataBase.types.ints;
        if (data instanceof Float)
            return VariableDataBase.types.floats;
        if (data instanceof String)
            return VariableDataBase.types.strings;
        if (data instanceof Boolean)
            return VariableDataBase.types.booleans;
        if (data instanceof FunctionList.iFunction)
            return VariableDataBase.types.functions;

        return VariableDataBase.types.nulls;
    }

    @SuppressWarnings("unchecked")
    public <T> T getData() {
        if (type.equals(VariableDataBase.types.booleans))
            return (T) (data.equalsIgnoreCase("true") ? Boolean.TRUE : Boolean.FALSE);
        if (type.equals(VariableDataBase.types.strings))
            return (T) (data);
        if (type.equals(VariableDataBase.types.floats))
            return (T) (Float.valueOf(data));
        if (type.equals(VariableDataBase.types.ints))
            return (T) (Integer.valueOf(data));
        if (type.equals(VariableDataBase.types.functions))
            return (T) (FunctionList.getFunction(data));
        return (T) Boolean.FALSE;
    }

    public <T> void setData(T b) {
        if (type.equals(VariableDataBase.types.booleans))
            data = String.valueOf(b);
        else
            data = b.toString();
    }

    public void add(ValueType a) {
        if (a == null) {
            System.err.println("null error!");
            return;
        }
        if (this.type.equals(VariableDataBase.types.strings)) {
            data += a.data;
        } else if (this.type.equals(a.type) && !this.type.equals(VariableDataBase.types.booleans)) {
            float d1 = Float.parseFloat(this.data);
            float d2 = Float.parseFloat(a.data);
            if (this.type.equals(VariableDataBase.types.floats)) {
                this.data = String.valueOf(d1 + d2);
            } else {
                this.data = String.valueOf((int) (d1 + d2));
            }
        } else if (this.type.equals(VariableDataBase.types.floats) && a.type.equals(VariableDataBase.types.ints)) {
            float d1 = Float.parseFloat(this.data);
            float d2 = Float.parseFloat(a.data);
            this.data = String.valueOf(d1 + d2);
        } else {
            System.err.println("type error!");
        }
    }

    public void sub(ValueType a) {
        float d1 = Float.parseFloat(this.data);
        if (a == null) {
            System.err.println("null error!");
            return;
        }
        if (this.type.equals(a.type) && !this.type.equals(VariableDataBase.types.booleans) && !this.type.equals(VariableDataBase.types.strings)) {
            float d2 = Float.parseFloat(a.data);
            if (this.type.equals(VariableDataBase.types.floats)) {
                this.data = String.valueOf(d1 + d2);
            } else {
                this.data = String.valueOf((int) (d1 + d2));
            }
        } else if (this.type.equals(VariableDataBase.types.floats) && a.type.equals(VariableDataBase.types.ints)) {
            float d2 = Float.parseFloat(a.data);
            this.data = String.valueOf(d1 + d2);
        } else {
            System.err.println("type error!");
        }
    }

    public void mul(ValueType a) {
        if (a == null) {
            System.err.println("null error!");
            return;
        }
        if (this.type.equals(a.type) && !this.type.equals(VariableDataBase.types.booleans) && !this.type.equals(VariableDataBase.types.strings)) {
            float d1 = Float.parseFloat(this.data);
            float d2 = Float.parseFloat(a.data);
            if (this.type.equals(VariableDataBase.types.floats)) {
                this.data = String.valueOf(d1 * d2);
            } else {
                this.data = String.valueOf((int) (d1 * d2));
            }
        } else if (this.type.equals(VariableDataBase.types.floats) && a.type.equals(VariableDataBase.types.ints)) {
            float d1 = Float.parseFloat(this.data);
            float d2 = Float.parseFloat(a.data);
            this.data = String.valueOf(d1 * d2);
        } else {
            System.err.println("type error!");
        }
    }

    public void div(ValueType a) {
        if (a == null) {
            System.err.println("null error!");
            return;
        }
        if (this.type.equals(a.type) && !this.type.equals(VariableDataBase.types.booleans) && !this.type.equals(VariableDataBase.types.strings)) {
            if (this.type.equals(VariableDataBase.types.floats)) {
                float d1 = Float.parseFloat(this.data);
                float d2 = Float.parseFloat(a.data);
                this.data = String.valueOf(d1 / d2);
            } else {
                int d1 = Integer.parseInt(this.data);
                int d2 = Integer.parseInt(a.data);
                this.data = String.valueOf(d1 / d2);
            }
        } else if (this.type.equals(VariableDataBase.types.floats) && a.type.equals(VariableDataBase.types.ints)) {
            float d1 = Float.parseFloat(this.data);
            float d2 = Float.parseFloat(a.data);
            this.data = String.valueOf(d1 / d2);
        } else {
            System.err.println("type error!");
        }
    }

    public void set(ValueType a) {
        if (this.type.equals(VariableDataBase.types.nulls)) {
            this.type = a.type;
            this.data = a.data;
        } else if (this.type.equals(a.type)) {
            this.data = a.data;
        } else if (this.type.equals(VariableDataBase.types.floats) && a.type.equals(VariableDataBase.types.ints)) {
            this.data = a.data;
        }
    }

    @Override
    public String toString() {
        return "ValueType{" +
                "data='" + data + '\'' +
                ", type=" + type +
                '}';
    }
}
