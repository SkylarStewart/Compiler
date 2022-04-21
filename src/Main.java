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
import edu.ufl.cise.plc.runtime.ConsoleIO;
import edu.ufl.cise.plc.runtime.ImageOps;


import java.awt.image.BufferedImage;
import java.util.Arrays;

import java.awt.image.BufferedImage;
import edu.ufl.cise.plc.runtime.FileURLIO;
import edu.ufl.cise.plc.runtime.ImageOps;
import edu.ufl.cise.plc.runtime.ColorTuple;
import java.awt.Color;
import edu.ufl.cise.plc.runtime.ConsoleIO;
import edu.ufl.cise.plc.runtime.ColorTupleFloat;

public class Main {




    static String getASCII(String s) {
        int[] ascii = new int[s.length()];
        for (int i = 0; i != s.length(); i++) {
            ascii[i] = s.charAt(i);
        }
        return Arrays.toString(ascii);
    }

    public static void main(String[] args) throws PLCException {


    }







    public class a{
        public static BufferedImage apply( int width,int height){
            BufferedImage f = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            float x = (float) (float) width;
            float y = (float) (float) height;
            for( int g = 0; g<f.getWidth(); g++)
                for (int h = 0; h < f.getHeight(); h++)
                    ImageOps.setColor(f, g, h,new ColorTuple((new ColorTupleFloat((((float) g / x) * (float) 255), 0.0f, (((float) h / y) * (float) 255)))));
            return f;
        }
    }



}
