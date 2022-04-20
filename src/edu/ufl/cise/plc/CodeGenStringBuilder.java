package edu.ufl.cise.plc;

public class CodeGenStringBuilder {
    private StringBuilder sb;
    String name;
    int LR;


    public CodeGenStringBuilder() {
        this.sb = new StringBuilder();
        this.name = "";
        this.LR = 0;
    }



    public CodeGenStringBuilder append(Object obj) {
        sb.append(obj);
        return this;
    }
    public CodeGenStringBuilder lparen() {
        sb.append('(');
        return this;
    }
    public CodeGenStringBuilder rparen() {
        sb.append(')');
        return this;
    }
    public CodeGenStringBuilder newline() {
        sb.append('\n');
        return this;
    }

    public CodeGenStringBuilder semicolon() {
        sb.append(';');
        return this;
    }

    public CodeGenStringBuilder space() {
        sb.append(' ');
        return this;
    }

    public CodeGenStringBuilder tab() {
        sb.append('\t');
        return this;
    }

    public CodeGenStringBuilder lbrace() {
        sb.append('{');
        return this;
    }

    public CodeGenStringBuilder rbrace() {
        sb.append('}');
        return this;
    }

    public CodeGenStringBuilder print() {
        System.out.println(sb);
        return this;
    }

    public CodeGenStringBuilder insert(int offset, Object obj) {
        sb.insert(offset, obj);
        return this;
    }

    public CodeGenStringBuilder deleteOne() {
        sb.deleteCharAt(sb.length()-1);
        return this;
    }

    public char getLast() {
        return (sb.charAt(sb.length()-1));
    }

    public void setName(String val) {
        this.name = val;
    }

    public String getName() {
        return this.name;
    }

    public void setLR(int val) {
        this.LR = val;
    }

    public int getLR() {
        return this.LR;
    }

    public StringBuilder returnSB() {
        return sb;
    }

    public int getIndex() {
        return sb.length();
    }



}
