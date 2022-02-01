import edu.ufl.cise.plc.ILexer;
import edu.ufl.cise.plc.IToken;
import edu.ufl.cise.plc.Lexer;
import edu.ufl.cise.plc.Token;
import edu.ufl.cise.plc.CompilerComponentFactory;
import edu.ufl.cise.plc.LexicalException;
import edu.ufl.cise.plc.PLCException;
public class Main {

    public static void main(String[] args) {
        System.out.println("Hi!");
        Lexer lexer = new Lexer("&");
        Token newToken = lexer.findToken();

        System.out.println(newToken.kind);
    }
}
