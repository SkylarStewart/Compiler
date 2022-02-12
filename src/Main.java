import edu.ufl.cise.plc.ILexer;
import edu.ufl.cise.plc.IToken;
import edu.ufl.cise.plc.Lexer;
import edu.ufl.cise.plc.Token;
import edu.ufl.cise.plc.CompilerComponentFactory;
import edu.ufl.cise.plc.LexicalException;
import edu.ufl.cise.plc.PLCException;

import java.util.Arrays;


public class Main {

    static String getASCII(String s) {
        int[] ascii = new int[s.length()];
        for (int i = 0; i != s.length(); i++) {
            ascii[i] = s.charAt(i);
        }
        return Arrays.toString(ascii);
    }

    public static void main(String[] args) throws LexicalException {
        System.out.println("hi!");
        Lexer lexer = new Lexer("""
                
                """);
        IToken newToken = lexer.next();

        while(newToken.getKind() != IToken.Kind.EOF) {
            System.out.println(newToken.getKind());
            System.out.println(newToken.getStringValue());
            System.out.println(getASCII(newToken.getStringValue()));
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

        System.out.println(getASCII("abc\t09\n"));
    }
}
