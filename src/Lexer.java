import edu.ufl.cise.plc.ILexer;
import edu.ufl.cise.plc.IToken;
import edu.ufl.cise.plc.LexicalException;

public class Lexer implements ILexer {
String input = "124 5677 testtext";

    private enum State {
        IN_IDENT, //is an identifier
        RES, // is reserved
        HAVE_ZERO, //has zero
        HAVE_DOT, //has dot
        IN_FLOAT, //has float
        IN_NUM, //is a num
        IN_STRING, //parsing as if it were a string
        IN_COMM, //is a comment
        IN_WS, //is whitespace
    }
    @Override
    public IToken next() throws LexicalException {
        return null;
    }

    @Override
    public IToken peek() throws LexicalException {
        return null;
    }
}
