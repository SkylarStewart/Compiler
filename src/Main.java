import edu.ufl.cise.plc.ILexer;
import edu.ufl.cise.plc.IToken;
import edu.ufl.cise.plc.Lexer;
import edu.ufl.cise.plc.Token;
import edu.ufl.cise.plc.CompilerComponentFactory;
import edu.ufl.cise.plc.LexicalException;
import edu.ufl.cise.plc.PLCException;
import edu.ufl.cise.plc.Parser;
import edu.ufl.cise.plc.ast.ASTNode;

import java.util.Arrays;


public class Main {

    static String getASCII(String s) {
        int[] ascii = new int[s.length()];
        for (int i = 0; i != s.length(); i++) {
            ascii[i] = s.charAt(i);
        }
        return Arrays.toString(ascii);
    }

    public static void main(String[] args) throws PLCException {
        System.out.println("hi!");
        String input = """
				3 * (4 + 5)
				""";
        Lexer lexer = new Lexer("""
                hi
                """);

        Parser parser = new Parser(input);
        ASTNode node = parser.parse();
        System.out.println(node.getText());
        System.out.println(node.getSourceLoc());
        IToken newToken = lexer.next();

        while(newToken.getKind() != IToken.Kind.EOF) {
            System.out.println(newToken.getKind());
            System.out.println(newToken.getStringValue());
            System.out.print(newToken.getSourceLocation().line());
            System.out.print(" ");
            System.out.println(newToken.getSourceLocation().column());
            newToken = lexer.next();
        }


        //"This is a string"    "This is a string"   "This is a string"
        String newString = "\n \f \r";
        System.out.println(newString);

        System.out.println(getASCII("abc\t09\n"));
    }
}
