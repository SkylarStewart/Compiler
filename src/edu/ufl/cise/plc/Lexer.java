package edu.ufl.cise.plc;
import java.util.HashMap;

public class Lexer implements ILexer {


    //string that is input into the lexer
    String input;

    //keeps track of the location and the length of the input
    int location;
    int length;

    //keeps track of start position/location of token
    int startPos;

    //keeps track of the current line and column of the next character being output
    int line;
    int column;

    int startLine;
    int startColumn;
    int startLocation;

    //keeps track of line incrementation to be reversed in case of a peek() call
    int locchange = 0;
    int linechange = 0;
    int columnchange = 0;
    int multiLineString = 0;

    State state;
    static HashMap<String, IToken.Kind> resWords = new HashMap<>();

    public Lexer(String input1) {
        this.input = input1;
        this.location = 0;
        this.length = input.length();
        this.startPos = 0;
        this.state = State.START;
        this.line = 0;
        this.column = 0;

        //initializes a HashMap with all reserved keywords
        resWords.put("string", IToken.Kind.TYPE);
        resWords.put("int", IToken.Kind.TYPE);
        resWords.put("float", IToken.Kind.TYPE);
        resWords.put("boolean", IToken.Kind.TYPE);
        resWords.put("color", IToken.Kind.TYPE);
        resWords.put("image", IToken.Kind.TYPE);
        resWords.put("void", IToken.Kind.KW_VOID);
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


    //enumerator of all different states that the Lexer can be in at any given time
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
        IN_RARROW, // is right-facing arrow
        IN_LARROW, // is left-facing arrow
        IN_EXC, //is an exclamation point
        IN_EQ, //is an equals sign
        IN_ESC, //is an escape sequence in a string
        IN_MINUS //is a minus
    }
    //throws LexicalException

    //calls the next function
    @Override
    public IToken next() throws LexicalException {
        Token token = findToken();
        this.state = State.START;
        locchange = 0;
        linechange = 0;
        columnchange = 0;
        return token;
    }

    //calls the peek function
    @Override
    public IToken peek() throws LexicalException {
        Token token = findToken();
        this.state = State.START;
/*        location -= locchange;
        line -= linechange;
        column -= columnchange;*/
        line = startLine;
        column = startColumn;
        location = startLocation;


        locchange = 0;
        linechange = 0;
        columnchange = 0;
        return token;
    }

    //contains the entirety of the switch statement that is called in both next() and peek().
    public Token findToken() throws LexicalException {
        startLine = line;
        startColumn = column;
        startLocation = location;
        String tempString = "";
        String tempText = "";


        while (true) {

            //checks for EOF
            if (location == length) {
                //throws an error if the string is invalid
                if (state == State.IN_STRING) {
                    throw new LexicalException("Invalid string");
                }


                Token token = new Token(IToken.Kind.EOF, "End of File", line, column);
                return token;
            }

            char ch = input.charAt(location);

            //main switch statement for every possible Lexer state
            switch (state) {

                //default case, new token
                case START -> {

                    startPos = location;

                    //checks for escape sequences
                    switch (ch) {

                        case ' ', '\t', '\n', '\r' -> {
                            location++;
                            if (ch == '\n') {
                                line++;
                                linechange++;
                                column = 0;
                            } else {
                                column++;
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

                        case '%' -> {
                            Token token = new Token(IToken.Kind.MOD, "%", line, column);
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

                        case '[' -> {
                            Token token = new Token(IToken.Kind.LSQUARE, "[", line, column);
                            location++;
                            locchange++;
                            column++;
                            columnchange++;
                            return token;
                        }

                        case ']' -> {
                            Token token = new Token(IToken.Kind.RSQUARE, "]", line, column);
                            location++;
                            locchange++;
                            column++;
                            columnchange++;
                            return token;
                        }

                        //equality operator statements (can be more than one character long)

                        case '=' -> {
                            if (location == length - 1) {
                                Token token = new Token(IToken.Kind.ASSIGN, "=", line, column);
                                location++;
                                locchange++;
                                column += locchange;
                                columnchange += locchange;
                                return token;
                            }
                            this.state = State.IN_EQ;
                            location++;
                            locchange++;
                        }

                        case '-' -> {
                            if (location == length - 1) {
                                Token token = new Token(IToken.Kind.MINUS, "-", line, column);
                                location++;
                                locchange++;
                                column += locchange;
                                columnchange += locchange;
                                return token;
                            }
                            this.state = State.IN_MINUS;
                            location++;
                            locchange++;
                        }

                        case '!' -> {
                            if (location == length - 1) {
                                Token token = new Token(IToken.Kind.BANG, "!", line, column);
                                location++;
                                locchange++;
                                column += locchange;
                                columnchange += locchange;
                                return token;
                            }
                            this.state = State.IN_EXC;
                            location++;
                            locchange++;
                        }

                        case '>' -> {
                            if (location == length - 1) {
                                Token token = new Token(IToken.Kind.GT, ">", line, column);
                                location++;
                                locchange++;
                                column += locchange;
                                columnchange += locchange;
                                return token;
                            }
                            this.state = State.IN_RARROW;
                            location++;
                            locchange++;
                        }

                        case '<' -> {
                            if (location == length - 1) {
                                Token token = new Token(IToken.Kind.LT, "<", line, column);
                                location++;
                                locchange++;
                                column += locchange;
                                columnchange += locchange;
                                return token;
                            }
                            this.state = State.IN_LARROW;
                            location++;
                            locchange++;
                        }


                        //puts the Lexer into IN_COMM state if a comment is detected.
                        case '#' -> {
                            this.state = State.IN_COMM;
                            location++;
                        }

                        //puts the Lexer into IN_String state if a string is detected.
                        case '"' -> {
                            this.state = State.IN_STRING;
                            tempText = tempText + ch;
                            location++;
                            locchange++;
                        }

                        //puts the Lexer into IN_NUM state if a number is detected.
                        case '1', '2', '3', '4', '5', '6', '7', '8', '9' -> {
                            this.state = State.IN_NUM;
                            location++;
                            locchange++;
                        }
                        case '0' -> {
                            this.state = State.HAVE_ZERO;
                            location++;
                            locchange++;
                        }

                        //puts the Lexer into IN_IDENT state if an identifier start is detected.
                        case 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
                                'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
                                '_', '$' -> {
                            if (location == length - 1) {
                                Token token = new Token(IToken.Kind.IDENT, Character.toString(ch), line, column);
                                location++;
                                locchange++;
                                column += locchange;
                                columnchange += locchange;
                                return token;
                            }
                            this.state = State.IN_IDENT;
                            location++;
                            locchange++;
                        }

                        default -> throw new LexicalException("Invalid character");
                    }

                }

                //identifier logic
                case IN_IDENT -> {

                    switch (ch) {

                        //case if a valid identifier character is detected
                        case 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
                                'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
                                '_', '$', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' -> {
                            if (location == length - 1) {
                                if (resWords.containsKey(input.substring(startPos, location + 1))) {
                                    Token token = new Token(resWords.get(input.substring(startPos, location + 1)), input.substring(startPos, location + 1), line, column);
                                    if (token.getText().equals("true")) {
                                        token.boolValue = true;
                                    }
                                    if (token.getText().equals("false")) {
                                        token.boolValue = false;
                                    }

                                    location++;
                                    locchange++;
                                    column += locchange;
                                    columnchange += locchange;
                                    this.state = State.START;
                                    return token;
                                }

                                Token token = new Token(IToken.Kind.IDENT, input.substring(startPos, location + 1), line, column);
                                location++;
                                locchange++;
                                column += locchange;
                                columnchange += locchange;
                                return token;
                            }
                            location++;
                            locchange++;
                        }

                        //case when a valid identifier character is not detected
                        default -> {
                            String temp = input.substring(startPos, location);
                            if (resWords.containsKey(temp)) {
                                Token token = new Token(resWords.get(temp), temp, line, column);
                                if (token.getText().equals("true")) {
                                    token.boolValue = true;
                                }
                                if (token.getText().equals("false")) {
                                    token.boolValue = false;
                                }
                                column += locchange;
                                columnchange += locchange;
                                this.state = State.START;
                                return token;
                            } else {
                                Token token = new Token(IToken.Kind.IDENT, temp, line, column);
                                column += locchange;
                                columnchange += locchange;
                                this.state = State.START;
                                return token;
                            }

                        }
                    }
                }

                //zero logic
                case HAVE_ZERO -> {
                    switch (ch) {

                        //case when a '.' is detected, changes state to HAVE_DOT
                        case '.' -> {
                            this.state = State.HAVE_DOT;
                            location++;
                            locchange++;
                        }

                        //case if 0 is an integer literal
                        default -> {
                            Token token = new Token(IToken.Kind.INT_LIT, input.substring(startPos, location), line, column);
                            column += locchange;
                            columnchange += locchange;
                            this.state = State.START;
                            return token;
                        }
                    }
                }


                //case if a '.' is detected after an integer literal
                case HAVE_DOT -> {
                    switch (ch) {

                        //case if a valid number is detected, puts into state INT_FLOAT
                        case '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' -> {
                            if (location == length - 1) {
                                Token token = new Token(IToken.Kind.FLOAT_LIT, input.substring(startPos, location + 1), line, column);
                                try {
                                    float val = Float.parseFloat(token.getText());
                                    token.setFloatValue(val);
                                } catch (Exception e) {
                                    throw new LexicalException("Float out of range");
                                }
                                location++;
                                locchange++;
                                column += locchange;
                                columnchange += locchange;
                                this.state = State.START;
                                return token;
                            }
                            location++;
                            locchange++;
                            this.state = State.IN_FLOAT;
                        }

                        //case if the '.' is invalid
                        default -> throw new LexicalException("Number value must follow .");
                    }
                }


                //case if there is a valid float detected
                case IN_FLOAT -> {
                    switch (ch) {

                        //case if a valid float number is detected
                        case '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' -> {
                            if (location == length - 1) {
                                Token token = new Token(IToken.Kind.FLOAT_LIT, input.substring(startPos, location + 1), line, column);
                                try {
                                    float val = Float.parseFloat(token.getText());
                                    token.setFloatValue(val);
                                    if (token.getFloatValue() == Float.POSITIVE_INFINITY) {
                                        throw new Exception("Float out of range");
                                    }
                                } catch (Exception e) {
                                    throw new LexicalException("Float out of range");
                                }
                                location++;
                                locchange++;
                                column += locchange;
                                columnchange += locchange;
                                this.state = State.START;
                                return token;
                            }
                            location++;
                            locchange++;
                        }

                        //case if the float ends (a valid number is no longer detected)
                        default -> {
                            Token token = new Token(IToken.Kind.FLOAT_LIT, input.substring(startPos, location), line, column);
                            try {
                                float val = Float.parseFloat(token.getText());
                                token.setFloatValue(val);
                                if (token.getFloatValue() == Float.POSITIVE_INFINITY) {
                                    throw new Exception("Float out of range");
                                }
                            } catch (Exception e) {
                                throw new LexicalException("Float out of range");
                            }
                            column += locchange;
                            columnchange += locchange;
                            this.state = State.START;
                            return token;
                        }
                    }
                }


                //case if a valid integer literal is detected
                case IN_NUM -> {

                    switch (ch) {

                        //case if another valid number is detected in the integer literal
                        case '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' -> {

                            //returns a num if it is the last value of the file.
                            if (location == length - 1) {
                                Token token = new Token(IToken.Kind.INT_LIT, input.substring(startPos, location + 1), line, column);
                                try {
                                    int val = Integer.parseInt(token.getText());
                                    token.setIntValue(val);
                                } catch (Exception e) {
                                    throw new LexicalException("Integer out of range");
                                }
                                location++;
                                locchange++;
                                column += locchange;
                                columnchange += locchange;
                                this.state = State.START;
                                return token;
                            }


                            location++;
                            locchange++;
                        }

                        //case if a '.' is detected (changes state to HAS_DOT)
                        case '.' -> {
                            if (location == length - 1) {
                                throw new LexicalException("float ends in a dot with no further integers.");
                            }

                            location++;
                            locchange++;
                            this.state = State.HAVE_DOT;
                        }

                        //case if the integer literal ends
                        default -> {
                            Token token = new Token(IToken.Kind.INT_LIT, input.substring(startPos, location), line, column);
                            try {
                                int val = Integer.parseInt(token.getText());
                                token.setIntValue(val);
                            } catch (Exception e) {
                                throw new LexicalException("Integer out of range");
                            }
                            column += locchange;
                            columnchange += locchange;
                            this.state = State.START;
                            return token;
                        }

                    }
                }

                //parses the input if the next token is a string
                case IN_STRING -> {

                    switch (ch) {
                        case '"' -> {
                            tempText = tempText + ch;
                            Token token = new Token(IToken.Kind.STRING_LIT, tempText, line, column);
                            token.setStringValue(tempString);
                            location++;
                            locchange++;
                            column += locchange;
                            columnchange += locchange;
                            line += multiLineString;
                            linechange+= multiLineString;
                            multiLineString = 0;
                            this.state = State.START;
                            return token;
                        }

                        case '\\' -> {
                            this.state = State.IN_ESC;
                            tempText = tempText + ch;
                            //tempString = tempString + ch;
                            location++;
                            locchange++;
                        }

                        case '\n' -> {
                            tempString = tempString + ch;
                            tempText = tempText + ch;
                            location++;
                            locchange++;
                            locchange = locchange-locchange-column;
                            multiLineString++;
                        }

                        default -> {
                            tempString = tempString + ch;
                            tempText = tempText + ch;
                            location++;
                            locchange++;
                        }
                    }
                }

                //parses the input if the next token is a column
                case IN_COMM -> {
                    switch (ch) {
                        case '\n' -> {
                            location++;
                            column = 0;
                            line++;
                            linechange++;
                            this.state = State.START;
                        }

                        default -> {
                            location++;
                            column++;
                        }
                    }
                }

                //case if the character is a GT symbol
                case IN_RARROW -> {
                    switch (ch) {
                        case '=' -> {
                            Token token = new Token(IToken.Kind.GE, ">=", line, column);
                            location++;
                            locchange++;
                            column += locchange;
                            columnchange += locchange;
                            this.state = State.START;
                            return token;
                        }
                        case '>' -> {
                            Token token = new Token(IToken.Kind.RANGLE, ">>", line, column);
                            location++;
                            locchange++;
                            column += locchange;
                            columnchange += locchange;
                            this.state = State.START;
                            return token;
                        }
                        default -> {
                            Token token = new Token(IToken.Kind.GT, ">", line, column);
                            //location++;
                            //locchange++;
                            column += locchange;
                            columnchange += locchange;
                            this.state = State.START;
                            return token;
                        }
                    }
                }

                //case if the character is a LT symbol
                case IN_LARROW -> {
                    switch (ch) {
                        case '=' -> {
                            Token token = new Token(IToken.Kind.LE, "<=", line, column);
                            location++;
                            locchange++;
                            column += locchange;
                            columnchange += locchange;
                            this.state = State.START;
                            return token;
                        }
                        case '<' -> {
                            Token token = new Token(IToken.Kind.LANGLE, "<<", line, column);
                            location++;
                            locchange++;
                            column += locchange;
                            columnchange += locchange;
                            this.state = State.START;
                            return token;
                        }
                        case '-' -> {
                            Token token = new Token(IToken.Kind.LARROW, "<-", line, column);
                            location++;
                            locchange++;
                            column += locchange;
                            columnchange += locchange;
                            this.state = State.START;
                            return token;
                        }

                        default -> {
                            Token token = new Token(IToken.Kind.LT, "<", line, column);
                            column += locchange;
                            columnchange += locchange;
                            this.state = State.START;
                            return token;
                        }
                    }
                }


                //case if the character is an exclamation point
                case IN_EXC -> {
                    switch (ch) {
                        case '=' -> {
                            Token token = new Token(IToken.Kind.NOT_EQUALS, "!=", line, column);
                            location++;
                            locchange++;
                            column += locchange;
                            columnchange += locchange;
                            this.state = State.START;
                            return token;
                        }
                        default -> {
                            Token token = new Token(IToken.Kind.BANG, "!", line, column);
                            column += locchange;
                            columnchange += locchange;
                            this.state = State.START;
                            return token;
                        }
                    }
                }

                //case if the character is an equals sign
                case IN_EQ -> {
                    switch (ch) {
                        case '=' -> {
                            Token token = new Token(IToken.Kind.EQUALS, "==", line, column);
                            location++;
                            locchange++;
                            column += locchange;
                            columnchange += locchange;
                            this.state = State.START;
                            return token;
                        }
                        default -> {
                            Token token = new Token(IToken.Kind.ASSIGN, "=", line, column);
                            column += locchange;
                            columnchange += locchange;
                            this.state = State.START;
                            return token;
                        }
                    }

                }

                //case if the character is an escape sequence
                case IN_ESC -> {
                    switch (ch) {
                        case 'b' -> {
                            tempString = tempString + '\b';
                            tempText = tempText + ch;
                            location++;
                            locchange++;
                            this.state = State.IN_STRING;
                        }

                        case 't' -> {
                            tempString = tempString + '\t';
                            tempText = tempText + ch;
                            location++;
                            locchange++;
                            this.state = State.IN_STRING;
                        }
                        case 'n' -> {
                            tempString = tempString + '\n';
                            tempText = tempText + ch;
                            location++;
                            locchange++;
                            this.state = State.IN_STRING;
                        }
                        case 'f' -> {
                            tempString = tempString + '\f';
                            tempText = tempText + ch;
                            location++;
                            locchange++;
                            this.state = State.IN_STRING;
                        }
                        case 'r' -> {
                            tempString = tempString + '\r';
                            tempText = tempText + ch;
                            location++;
                            locchange++;
                            this.state = State.IN_STRING;
                        }
                        case '"' -> {
                            tempString = tempString + '\"';
                            tempText = tempText + ch;
                            location++;
                            locchange++;
                            this.state = State.IN_STRING;
                        }
                        case '\'' -> {
                            tempString = tempString + '\'';
                            tempText = tempText + ch;
                            location++;
                            locchange++;
                            this.state = State.IN_STRING;
                        }
                        case '\\' -> {
                            tempString = tempString + '\\';
                            tempText = tempText + ch;
                            location++;
                            locchange++;
                            this.state = State.IN_STRING;
                        }

                        default -> throw new LexicalException("invalid backslash");
                    }

                }

                //case if the character is a minus sign
                case IN_MINUS -> {
                    switch (ch) {
                        case '>' -> {
                            Token token = new Token(IToken.Kind.RARROW, "->", line, column);
                            location++;
                            locchange++;
                            column += locchange;
                            columnchange += locchange;
                            this.state = State.START;
                            return token;
                        }
                        default -> {
                            Token token = new Token(IToken.Kind.MINUS, "-", line, column);
                            column += locchange;
                            columnchange += locchange;
                            this.state = State.START;
                            return token;
                        }
                    }

                }

                default -> throw new LexicalException("invalid character");

            }

        }

    }


}