package edu.ufl.cise.plc;

//This class eliminates hard coded dependencies on the actual Lexer class.  You can call your lexer whatever you
//want as long as it implements the ILexer interface and you have provided an appropriate body for the getLexer method.
import edu.ufl.cise.plc.ast.ASTVisitor;

public class CompilerComponentFactory {

	//This method will be invoked to get an instance of your lexer.
	public static ILexer getLexer(String input) {
		return new Lexer(input);
	}

	public static IParser getParser(String input) {
		return new Parser(input);
	}

	public static TypeCheckVisitor getTypeChecker() {
		return new TypeCheckVisitor();
	}

	public static ASTVisitor getCodeGenerator(String packageName) {
		return new CodeGenVisitor(packageName);
	}

}