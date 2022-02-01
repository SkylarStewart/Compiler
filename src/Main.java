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
        Lexer lexer = new Lexer("""
				+ &
				- 	 
				""");
        Token newToken = lexer.findToken();
        Token newToken2 = lexer.findToken();
        Token newToken3 = lexer.findToken();

        System.out.println(newToken.kind);
        System.out.println(newToken.text);
        System.out.println(newToken.sourceLocation.line());
        System.out.println(newToken.sourceLocation.column());

        System.out.println(newToken2.kind);
        System.out.println(newToken2.text);
        System.out.println(newToken2.sourceLocation.line());
        System.out.println(newToken2.sourceLocation.column());

        System.out.println(newToken3.kind);
        System.out.println(newToken3.text);
        System.out.println(newToken3.sourceLocation.line());
        System.out.println(newToken3.sourceLocation.column());



    }
}
