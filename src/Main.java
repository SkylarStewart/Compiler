import edu.ufl.cise.plc.ILexer;
import edu.ufl.cise.plc.IToken;
import edu.ufl.cise.plc.Lexer;
import edu.ufl.cise.plc.Token;
import edu.ufl.cise.plc.CompilerComponentFactory;
import edu.ufl.cise.plc.LexicalException;
import edu.ufl.cise.plc.PLCException;
public class Main {

    public static void main(String[] args) throws LexicalException {
        System.out.println("hi!");
        Lexer lexer = new Lexer("""
                11.23
                113.21
                11111111111111111111111111111111111111111111111111111111111111111111.123
                """);
        IToken newToken = lexer.next();

        while(newToken.getKind() != IToken.Kind.EOF) {
            System.out.println(newToken.getKind());
            System.out.println(newToken.getText());
            System.out.println(newToken.getStringValue());
            System.out.println(newToken.getFloatValue());
            System.out.print(newToken.getSourceLocation().line());
            System.out.print(" ");
            System.out.println(newToken.getSourceLocation().column());
            newToken = lexer.next();
        }


        //"This is a string"    "This is a string"   "This is a string"
        String newString = "\n \f \r";
        System.out.println(newString);

    }
}
