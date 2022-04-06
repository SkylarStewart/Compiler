package edu.ufl.cise.plc;

public class CodeGenStringBuilder {
    private StringBuilder sb;


    public CodeGenStringBuilder() {
        this.sb = new StringBuilder();
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

    public StringBuilder returnSB() {
        return sb;
    }

    public int getIndex() {
        return sb.length();
    }



}
