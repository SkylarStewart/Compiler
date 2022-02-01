package edu.ufl.cise.plc;
import edu.ufl.cise.plc.ILexer;
import edu.ufl.cise.plc.IToken;
import edu.ufl.cise.plc.LexicalException;
import java.util.HashMap;

public class Lexer implements ILexer {
    String input = "124 5677 testtext";
    int location;
    int length;
    State state;
    static HashMap<String, IToken.Kind> resWords = new HashMap<>();
    public Lexer(String input) {
        this.input = input;
        this.location = 0;
        this.length = input.length();
        resWords.put("string", IToken.Kind.TYPE);
        resWords.put("int", IToken.Kind.TYPE);
        resWords.put("float", IToken.Kind.TYPE);
        resWords.put("boolean", IToken.Kind.TYPE);
        resWords.put("color", IToken.Kind.TYPE);
        resWords.put("image", IToken.Kind.TYPE);
        resWords.put("void", IToken.Kind.TYPE);
        resWords.put("getWidth", IToken.Kind.IMAGE_OP);
        resWords.put("getHeight", IToken.Kind.IMAGE_OP);
        resWords.put("getRed", IToken.Kind.COLOR_OP);
        resWords.put("getGreen", IToken.Kind.COLOR_OP);
        resWords.put("getBlue", IToken.Kind.COLOR_OP);
        resWords.put("BLACK", IToken.Kind.COLOR_CONST);
        resWords.put("BLUE", IToken.Kind.COLOR_CONST);
        resWords.put("CYAN", IToken.Kind.COLOR_CONST);
        resWords.put("DARK_GRAY", IToken.Kind.COLOR_CONST);
        resWords.put("GRAY", IToken.Kind.COLOR_CONST);
        resWords.put("GREEN", IToken.Kind.COLOR_CONST);
        resWords.put("LIGHT_GRAY", IToken.Kind.COLOR_CONST);
        resWords.put("MAGENTA", IToken.Kind.COLOR_CONST);
        resWords.put("ORANGE", IToken.Kind.COLOR_CONST);
        resWords.put("PINK", IToken.Kind.COLOR_CONST);
        resWords.put("RED", IToken.Kind.COLOR_CONST);
        resWords.put("WHITE", IToken.Kind.COLOR_CONST);
        resWords.put("YELLOW", IToken.Kind.COLOR_CONST);
        resWords.put("true", IToken.Kind.BOOLEAN_LIT);
        resWords.put("false", IToken.Kind.BOOLEAN_LIT);
        resWords.put("if", IToken.Kind.KW_IF);
        resWords.put("else", IToken.Kind.KW_ELSE);
        resWords.put("fi", IToken.Kind.KW_FI);
        resWords.put("write", IToken.Kind.KW_WRITE);
        resWords.put("console", IToken.Kind.KW_CONSOLE);
    }



    private enum State {
        START, //start
        IN_IDENT, //is an identifier
        RES, // is reserved
        HAVE_ZERO, //has zero
        HAVE_DOT, //has dot
        IN_FLOAT, //has float
        IN_NUM, //is a num
        IN_STRING, //parsing as if it were a string
        IN_COMM, //is a comment
        IN_WS, //is whitespace
        IN_RARROW, // is right-facing arrow
        IN_LARROW, // is left-facing arrow
        IN_EXC, //is an exclamation point
        IN_EQ, //is an equals sign
    }
    @Override
    public IToken next() throws LexicalException {
        return null;
    }

    @Override
    public IToken peek() throws LexicalException {
        return null;
    }

    public Token findToken() {

        return null;
    }
}