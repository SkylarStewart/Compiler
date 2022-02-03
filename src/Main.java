import edu.ufl.cise.plc.ILexer;
import edu.ufl.cise.plc.IToken;
import edu.ufl.cise.plc.Lexer;
import edu.ufl.cise.plc.Token;
import edu.ufl.cise.plc.CompilerComponentFactory;
import edu.ufl.cise.plc.LexicalException;
import edu.ufl.cise.plc.PLCException;
public class Main {

    public static void main(String[] args) throws LexicalException {
        System.out.println("Hi!");
        Lexer lexer = new Lexer("""
                #comment
                "string""string also"
                """);
        IToken newToken = lexer.next();
        IToken newToken2 = lexer.next();
        IToken newToken3 = lexer.next();
        IToken newToken4 = lexer.next();
        IToken newToken5 = lexer.next();
        IToken newToken6 = lexer.next();

        System.out.println(newToken.getKind());
        System.out.println(newToken.getText());
        System.out.print(newToken.getSourceLocation().line());
        System.out.print(" ");
        System.out.println(newToken.getSourceLocation().column());

        System.out.println(newToken2.getKind());
        System.out.println(newToken2.getText());
        System.out.print(newToken2.getSourceLocation().line());
        System.out.print(" ");
        System.out.println(newToken2.getSourceLocation().column());

        System.out.println(newToken3.getKind());
        System.out.println(newToken3.getText());
        System.out.print(newToken3.getSourceLocation().line());
        System.out.print(" ");
        System.out.println(newToken3.getSourceLocation().column());

        System.out.println(newToken4.getKind());
        System.out.println(newToken4.getText());
        System.out.print(newToken4.getSourceLocation().line());
        System.out.print(" ");
        System.out.println(newToken4.getSourceLocation().column());

        System.out.println(newToken5.getKind());
        System.out.println(newToken5.getText());
        System.out.print(newToken5.getSourceLocation().line());
        System.out.print(" ");
        System.out.println(newToken5.getSourceLocation().column());

        System.out.println(newToken6.getKind());
        System.out.println(newToken6.getText());
        System.out.print(newToken6.getSourceLocation().line());
        System.out.print(" ");
        System.out.println(newToken6.getSourceLocation().column());


        //"This is a string"    "This is a string"   "This is a string"


        String newString = "\n \f \r";
        System.out.println(newString);

    }
}
