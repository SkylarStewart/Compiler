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
                "test
                amazing test
                "
                "test
                "
                "test2
                "
                "test3" "test4"
                "test5
                "
                + == != #comment
                11111.33344458 4590059 CYAN void skylar
                "testtext
                
                "
                     skylar
                """);
        IToken newToken = lexer.next();

        while(newToken.getKind() != IToken.Kind.EOF) {
            System.out.println(newToken.getKind());
            System.out.println(newToken.getText());
           // System.out.println(newToken.getStringValue());
           // System.out.println(newToken.getFloatValue());
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
