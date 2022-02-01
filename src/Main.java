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
        IToken newToken = lexer.peek();
        IToken newToken2 = lexer.findToken();
        IToken newToken3 = lexer.findToken();
        IToken newToken4 = lexer.findToken();

        System.out.println(newToken.getKind());
        System.out.println(newToken.getText());
        System.out.println(newToken.getSourceLocation().line());
        System.out.println(newToken.getSourceLocation().column());

        System.out.println(newToken2.getKind());
        System.out.println(newToken2.getText());
        System.out.println(newToken2.getSourceLocation().line());
        System.out.println(newToken2.getSourceLocation().column());

        System.out.println(newToken3.getKind());
        System.out.println(newToken3.getText());
        System.out.println(newToken3.getSourceLocation().line());
        System.out.println(newToken3.getSourceLocation().column());

        System.out.println(newToken4.getKind());
        System.out.println(newToken4.getText());
        System.out.println(newToken4.getSourceLocation().line());
        System.out.println(newToken4.getSourceLocation().column());



    }
}
