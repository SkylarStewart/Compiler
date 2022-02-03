package edu.ufl.cise.plc;

public class Token implements IToken {

    public Kind kind;
    public String text;
    public SourceLocation sourceLocation;
    public int intValue;
    public float floatValue;
    public boolean boolValue;
    public String stringValue;

    public Token(Kind kind, String text, int line, int column) {
        this.kind = kind;
        this.sourceLocation = new SourceLocation(line, column);
        this.text = text;
    }

    //mutators

    public void setIntValue(int val){
        this.intValue = val;
    }

    public void setFloatValue(float val){
        this.floatValue = val;
    }

    public void setBoolValue(boolean val){
        this.boolValue = val;
    }

    public void setStringValue(String val){
        this.stringValue = val;
    }

    //no need to create mutators for Kind, Text, and sourceLocation because they are initialized in constructor
    //getters

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
