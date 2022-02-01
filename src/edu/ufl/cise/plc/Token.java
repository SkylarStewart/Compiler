package edu.ufl.cise.plc;
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

    public Token() {

    }

    @Override
    public Kind getKind() {
        return kind;
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public SourceLocation getSourceLocation() {
        return sourceLocation;
    }

    @Override
    public int getIntValue() {
        return intValue;
    }

    @Override
    public float getFloatValue() {
        return floatValue;
    }

    @Override
    public boolean getBooleanValue() {
        return boolValue;
    }

    @Override
    public String getStringValue() {
        return stringValue;
    }
}
