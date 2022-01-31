import edu.ufl.cise.plc.IToken;

public class Token implements IToken {

    public int line;
    public int column;

    public Kind kind;
    public String text;
    public SourceLocation sourceLocation;
    public int intValue;
    public float floatValue;
    public boolean boolValue;
    public String stringValue;

    public Kind getKind() {
        return null;
    }


    public String getText() {
        return null;
    }


    public SourceLocation getSourceLocation() {
        return null;
    }


    public int getIntValue() {
        return 0;
    }


    public float getFloatValue() {
        return 0;
    }


    public boolean getBooleanValue() {
        return false;
    }


    public String getStringValue() {
        return null;
    }
}
