package edu.ufl.cise.plc;
import edu.ufl.cise.plc.ILexer;
import edu.ufl.cise.plc.IToken;
import edu.ufl.cise.plc.LexicalException;
import java.util.HashMap;

public class Lexer implements ILexer {

    //string that is input into the lexer
    String input;

    //keeps track of the location and the length of the input
    int location;
    int length;

    //keeps track of the current line and column of the next character being output
    int line;
    int column;

    //keeps track of line incrementation to be reversed in case of a peek() call
    int locchange = 0;
    int linechange = 0;
    int columnchange = 0;

    State state;
    static HashMap<String, IToken.Kind> resWords = new HashMap<>();
    public Lexer(String input) {
        this.input = input;
        this.location = 0;
        this.length = input.length();
        this.state = State.START;
        this.line = 0;
        this.column = 0;
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
    //throws LexicalException

    @Override
    public IToken next()  {
        Token token = findToken();
        this.state = State.START;
        locchange = 0;
        linechange = 0;
        columnchange = 0;
        return token;
    }

    @Override
    public IToken peek()  {
        Token token = findToken();
        this.state = State.START;
        location -= locchange;
        line -= linechange;
        column -= columnchange;


        locchange = 0;
        linechange = 0;
        columnchange = 0;
        return token;
    }

    public Token findToken() {
        int templocation = location;
        int templine = line;
        int tempcolumn = column;

       while(true) {

           if (location == length) {
               Token token = new Token(IToken.Kind.EOF, "End of File", line, column);
               return token;
           }
           char ch = input.charAt(location);

           switch(state) {
               case START -> {

                   int startPos = location;

                   switch(ch) {

                       case ' ', '\t', '\n', '\r' -> {
                           location++;
                           locchange++;
                           if (ch == '\n') {
                               line++;
                               linechange++;
                               column = 0;
                           }
                           else {
                               column++;
                               columnchange++;
                           }
                       }

                       //single character tokens (terminates here)

                       case '&' -> {
                           Token token = new Token(IToken.Kind.AND, "&", line, column);
                           location++;
                           locchange++;
                           column++;
                           columnchange++;
                           return token;
                       }

                       case '/' -> {
                           Token token = new Token(IToken.Kind.DIV, "/", line, column);
                           location++;
                           locchange++;
                           column++;
                           columnchange++;
                           return token;
                       }


                       case ',' -> {
                           Token token = new Token(IToken.Kind.COMMA, ",", line, column);
                           location++;
                           locchange++;
                           column++;
                           columnchange++;
                           return token;
                       }

                       case '(' -> {
                           Token token = new Token(IToken.Kind.LPAREN, "(", line, column);
                           location++;
                           locchange++;
                           column++;
                           columnchange++;
                           return token;
                       }

                       case ')' -> {
                           Token token = new Token(IToken.Kind.RPAREN, ")", line, column);
                           location++;
                           locchange++;
                           column++;
                           columnchange++;
                           return token;
                       }

                       case '-' -> {
                           Token token = new Token(IToken.Kind.MINUS, "-", line, column);
                           location++;
                           locchange++;
                           column++;
                           columnchange++;
                           return token;
                       }

                       case '|' -> {
                           Token token = new Token(IToken.Kind.OR, "|", line, column);
                           location++;
                           locchange++;
                           column++;
                           columnchange++;
                           return token;
                       }

                       case '+' -> {
                           Token token = new Token(IToken.Kind.PLUS, "+", line, column);
                           location++;
                           locchange++;
                           column++;
                           columnchange++;
                           return token;
                       }

                       case '^' -> {
                           Token token = new Token(IToken.Kind.RETURN, "^", line, column);
                           location++;
                           locchange++;
                           column++;
                           columnchange++;
                           return token;
                       }

                       case ';' -> {
                           Token token = new Token(IToken.Kind.SEMI, ";", line, column);
                           location++;
                           locchange++;
                           column++;
                           columnchange++;
                           return token;
                       }

                       case '*' -> {
                           Token token = new Token(IToken.Kind.TIMES, "*", line, column);
                           location++;
                           locchange++;
                           column++;
                           columnchange++;
                           return token;
                       }

                       //equality operator statements
                       case '=' -> {
                           this.state = State.IN_EQ;
                       }

                       case '!' -> {
                           this.state = State.IN_EXC;
                       }

                       case '>' -> {
                           this.state = State.IN_RARROW;
                       }

                       case '<' -> {
                           this.state = State.IN_LARROW;
                       }

                       case '#' -> {
                           this.state = State.IN_COMM;
                       }

                       case'"' -> {
                           this.state = State.IN_STRING;
                       }

                       case '1','2', '3','4','5','6','7','8','9' -> {
                           this.state = State.IN_NUM;
                       }

                       case '0' -> {
                           this.state = State.HAVE_ZERO;
                       }

                       case 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
                               'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
                               '_', '$' -> {
                           this.state = State.IN_IDENT;

                       }

                       default -> throw new IllegalStateException("Lexer bug");


                   }

               }


               case IN_IDENT -> {

               }


               case RES -> {

               }


               case HAVE_ZERO -> {

               }


               case HAVE_DOT -> {

               }


               case IN_FLOAT -> {

               }


               case IN_NUM -> {

               }


               case IN_STRING -> {

               }


               case IN_COMM -> {

               }


               case IN_WS -> {

               }


               case IN_RARROW -> {

               }


               case IN_LARROW -> {

               }


               case IN_EXC   -> {

               }


               case IN_EQ -> {

               }

               default -> throw new IllegalStateException("Lexer bug");

           }

       }

    }


}