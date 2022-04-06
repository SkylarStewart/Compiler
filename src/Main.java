import edu.ufl.cise.plc.ILexer;
import edu.ufl.cise.plc.IToken;
import edu.ufl.cise.plc.Lexer;
import edu.ufl.cise.plc.Token;
import edu.ufl.cise.plc.CompilerComponentFactory;
import edu.ufl.cise.plc.LexicalException;
import edu.ufl.cise.plc.PLCException;
import edu.ufl.cise.plc.Parser;
import edu.ufl.cise.plc.ast.ASTNode;
import edu.ufl.cise.plc.ast.Program;
import edu.ufl.cise.plc.CodeGenStringBuilder;

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
/*        System.out.println("hi!");
        String input = """
                int f(int x)
                ^ y+1;
                """;
        Lexer lexer = new Lexer("""
                REDYELLOWGREEN-
                """);


        Parser parser = new Parser(input);
        ASTNode program = parser.parse();
        System.out.println(program);

        System.out.println(lexer.next().getKind());
        System.out.println(lexer.next().getKind());
        System.out.println(lexer.next().getKind());*/

        CodeGenStringBuilder sb = new CodeGenStringBuilder();
        sb.lparen();
        sb.append(5);
        sb.space();
        sb.append("17");
        sb.append('t');
        sb.print();
        int s = 6;



    }
}
