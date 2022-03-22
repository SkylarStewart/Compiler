package edu.ufl.cise.plc;

import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.ufl.cise.plc.IToken.Kind;
import edu.ufl.cise.plc.ast.ASTNode;
import edu.ufl.cise.plc.ast.ASTVisitor;
import edu.ufl.cise.plc.ast.AssignmentStatement;
import edu.ufl.cise.plc.ast.BinaryExpr;
import edu.ufl.cise.plc.ast.BooleanLitExpr;
import edu.ufl.cise.plc.ast.ColorConstExpr;
import edu.ufl.cise.plc.ast.ColorExpr;
import edu.ufl.cise.plc.ast.ConditionalExpr;
import edu.ufl.cise.plc.ast.ConsoleExpr;
import edu.ufl.cise.plc.ast.Declaration;
import edu.ufl.cise.plc.ast.Dimension;
import edu.ufl.cise.plc.ast.Expr;
import edu.ufl.cise.plc.ast.FloatLitExpr;
import edu.ufl.cise.plc.ast.IdentExpr;
import edu.ufl.cise.plc.ast.IntLitExpr;
import edu.ufl.cise.plc.ast.NameDef;
import edu.ufl.cise.plc.ast.NameDefWithDim;
import edu.ufl.cise.plc.ast.PixelSelector;
import edu.ufl.cise.plc.ast.Program;
import edu.ufl.cise.plc.ast.ReadStatement;
import edu.ufl.cise.plc.ast.ReturnStatement;
import edu.ufl.cise.plc.ast.StringLitExpr;
import edu.ufl.cise.plc.ast.Types.Type;
import edu.ufl.cise.plc.ast.UnaryExpr;
import edu.ufl.cise.plc.ast.UnaryExprPostfix;
import edu.ufl.cise.plc.ast.VarDeclaration;
import edu.ufl.cise.plc.ast.WriteStatement;

import javax.print.attribute.PrintServiceAttributeSet;

import static edu.ufl.cise.plc.ast.Types.Type.*;

public class TypeCheckVisitor implements ASTVisitor {

	SymbolTable symbolTable = new SymbolTable();  
	Program root;
	
	record Pair<T0,T1>(T0 t0, T1 t1){};  //may be useful for constructing lookup tables.
	
	private void check(boolean condition, ASTNode node, String message) throws TypeCheckException {
		if (!condition) {
			throw new TypeCheckException(message, node.getSourceLoc());
		}
	}
	
	//The type of a BooleanLitExpr is always BOOLEAN.  
	//Set the type in AST Node for later passes (code generation)
	//Return the type for convenience in this visitor.  
	@Override
	public Object visitBooleanLitExpr(BooleanLitExpr booleanLitExpr, Object arg) throws Exception {
		booleanLitExpr.setType(Type.BOOLEAN);
		return Type.BOOLEAN;
	}

	@Override
	public Object visitStringLitExpr(StringLitExpr stringLitExpr, Object arg) throws Exception {
		stringLitExpr.setType(Type.STRING);
		return Type.STRING;
		//throw new UnsupportedOperationException("Unimplemented visit method.");
	}

	@Override
	public Object visitIntLitExpr(IntLitExpr intLitExpr, Object arg) throws Exception {
		intLitExpr.setType(Type.INT);
		return Type.INT;
		//throw new UnsupportedOperationException("Unimplemented visit method.");
	}

	@Override
	public Object visitFloatLitExpr(FloatLitExpr floatLitExpr, Object arg) throws Exception {
		floatLitExpr.setType(Type.FLOAT);
		return Type.FLOAT;
	}

	@Override
	public Object visitColorConstExpr(ColorConstExpr colorConstExpr, Object arg) throws Exception {
		colorConstExpr.setType(Type.COLOR);
		return Type.COLOR;
		//throw new UnsupportedOperationException("Unimplemented visit method.");
	}

	@Override
	public Object visitConsoleExpr(ConsoleExpr consoleExpr, Object arg) throws Exception {
		consoleExpr.setType(Type.CONSOLE);
		return Type.CONSOLE;
	}
	
	//Visits the child expressions to get their type (and ensure they are correctly typed)
	//then checks the given conditions.
	@Override
	public Object visitColorExpr(ColorExpr colorExpr, Object arg) throws Exception {
		Type redType = (Type) colorExpr.getRed().visit(this, arg);
		Type greenType = (Type) colorExpr.getGreen().visit(this, arg);
		Type blueType = (Type) colorExpr.getBlue().visit(this, arg);
		check(redType == greenType && redType == blueType, colorExpr, "color components must have same type");
		check(redType == Type.INT || redType == Type.FLOAT, colorExpr, "color component type must be int or float");
		Type exprType = (redType == Type.INT) ? Type.COLOR : Type.COLORFLOAT;
		colorExpr.setType(exprType);
		return exprType;
	}	

	
	
	//Maps forms a lookup table that maps an operator expression pair into result type.  
	//This more convenient than a long chain of if-else statements. 
	//Given combinations are legal; if the operator expression pair is not in the map, it is an error. 
	Map<Pair<Kind,Type>, Type> unaryExprs = Map.of(
			new Pair<Kind,Type>(Kind.BANG,BOOLEAN), BOOLEAN,
			new Pair<Kind,Type>(Kind.MINUS, FLOAT), FLOAT,
			new Pair<Kind,Type>(Kind.MINUS, INT),INT,
			new Pair<Kind,Type>(Kind.COLOR_OP,INT), INT,
			new Pair<Kind,Type>(Kind.COLOR_OP,COLOR), INT,
			new Pair<Kind,Type>(Kind.COLOR_OP,IMAGE), IMAGE,
			new Pair<Kind,Type>(Kind.IMAGE_OP,IMAGE), INT
			);
	
	//Visits the child expression to get the type, then uses the above table to determine the result type
	//and check that this node represents a legal combination of operator and expression type. 
	@Override
	public Object visitUnaryExpr(UnaryExpr unaryExpr, Object arg) throws Exception {
		// !, -, getRed, getGreen, getBlue
		Kind op = unaryExpr.getOp().getKind();
		Type exprType = (Type) unaryExpr.getExpr().visit(this, arg);
		//Use the lookup table above to both check for a legal combination of operator and expression, and to get result type.
		Type resultType = unaryExprs.get(new Pair<Kind,Type>(op,exprType));
		check(resultType != null, unaryExpr, "incompatible types for unaryExpr");
		//Save the type of the unary expression in the AST node for use in code generation later. 
		unaryExpr.setType(resultType);
		//return the type for convenience in this visitor.
		return resultType;
	}


	//This method has several cases. Work incrementally and test as you go. 
	@Override
	public Object visitBinaryExpr(BinaryExpr binaryExpr, Object arg) throws Exception {
		Kind op = binaryExpr.getOp().getKind();
		Type leftType = (Type)binaryExpr.getLeft().visit(this, arg);
		Type rightType = (Type)binaryExpr.getRight().visit(this, arg);
		Type resultType = null;

		switch(op) {
			case AND,OR -> {
				if (leftType == BOOLEAN && rightType == BOOLEAN) resultType = BOOLEAN;
				else check(false, binaryExpr, "incompatible types for and/or");
			}

			case EQUALS,NOT_EQUALS -> {
				check(leftType == rightType, binaryExpr, "incompatible types for ==");
				resultType = Type.BOOLEAN;
			}

			case PLUS,MINUS -> {
				if (leftType == INT && rightType == INT) resultType = INT;
				else if(leftType == FLOAT && rightType == FLOAT) resultType = FLOAT;
				else if(leftType == INT && rightType == FLOAT) {
					resultType = FLOAT;
					binaryExpr.getLeft().setCoerceTo(FLOAT);
				}
				else if(leftType == FLOAT && rightType == INT) {
					resultType = FLOAT;
					binaryExpr.getRight().setCoerceTo(FLOAT);
				}
				else if(leftType == COLOR && rightType == COLOR) resultType = COLOR;
				else if(leftType == COLORFLOAT && rightType == COLORFLOAT) resultType = COLORFLOAT;
				else if (leftType == COLORFLOAT && rightType == COLOR) {
					resultType = COLORFLOAT;
					binaryExpr.getRight().setCoerceTo(COLORFLOAT);
				}
				else if (leftType == COLOR && rightType == COLORFLOAT) {
					resultType = COLORFLOAT;
					binaryExpr.getLeft().setCoerceTo(COLORFLOAT);

				}
				else if(leftType == IMAGE && rightType == IMAGE) resultType = IMAGE;
				else check(false, binaryExpr, "incompatible types for plus/minus");

			}

			case TIMES,DIV,MOD -> {

				if (leftType == INT && rightType == INT) resultType = INT;
				else if(leftType == FLOAT && rightType == FLOAT) resultType = FLOAT;
				else if(leftType == INT && rightType == FLOAT) {
					resultType = FLOAT;
					binaryExpr.getLeft().setCoerceTo(FLOAT);
				}
				else if(leftType == FLOAT && rightType == INT) {
					resultType = FLOAT;
					binaryExpr.getRight().setCoerceTo(FLOAT);
				}
				else if(leftType == COLOR && rightType == COLOR) resultType = COLOR;
				else if(leftType == COLORFLOAT && rightType == COLORFLOAT) resultType = COLORFLOAT;
				else if (leftType == COLORFLOAT && rightType == COLOR) {
					resultType = COLORFLOAT;
					binaryExpr.getRight().setCoerceTo(COLORFLOAT);
				}
				else if (leftType == COLOR && rightType == COLORFLOAT) {
					resultType = COLORFLOAT;
					binaryExpr.getLeft().setCoerceTo(COLORFLOAT);

				}
				else if(leftType == IMAGE && rightType == IMAGE) resultType = IMAGE;
				if (leftType == IMAGE && rightType == INT) resultType = IMAGE;
				else if  (leftType == IMAGE && rightType == FLOAT) resultType = IMAGE;
				else if  (leftType == INT && rightType == COLOR) {
					resultType = COLOR;
					binaryExpr.getLeft().setCoerceTo(COLOR);
				}
				else if (leftType == COLOR && rightType == INT) {
					resultType = COLOR;
					binaryExpr.getRight().setCoerceTo(COLOR);
				}
				else if ((leftType == FLOAT && rightType == COLOR) || (leftType == COLOR && rightType == FLOAT)) {
					resultType = COLORFLOAT;
					binaryExpr.getRight().setCoerceTo(COLORFLOAT);
					binaryExpr.getLeft().setCoerceTo(COLORFLOAT);
				}
				else check(false, binaryExpr, "incompatible types for times/division");


			}

			case LT, LE, GT, GE -> {
				if(leftType == INT && rightType == INT) resultType = BOOLEAN;
				else if(leftType == FLOAT && rightType == FLOAT) resultType = BOOLEAN;
				else if(leftType == INT && rightType == FLOAT)
				{
					resultType = BOOLEAN;
					binaryExpr.getLeft().setCoerceTo(FLOAT);
				}
				else if (leftType == FLOAT && rightType == INT) {
					resultType = BOOLEAN;
					binaryExpr.getRight().setCoerceTo(FLOAT);
				}
				else check(false, binaryExpr, "incompatible types for LT/LE/GT/GE");

			}

			default -> {
				throw new Exception("compiler error");
			}
		}

		binaryExpr.setType(resultType);
		return resultType;


		//throw new UnsupportedOperationException("Unimplemented visit method.");
	}

	@Override
	public Object visitIdentExpr(IdentExpr identExpr, Object arg) throws Exception {
		String name = identExpr.getText();
		Declaration dec = symbolTable.search(name);
		check(dec!=null, identExpr, "unidentified identifier " + name);
		check(dec.isInitialized(), identExpr, "not initialized (IdentExpr): " + name);
		identExpr.setDec(dec);
		Type type = dec.getType();
		identExpr.setType(type);
		return type;

		//throw new UnsupportedOperationException("Unimplemented visit method.");
	}

	@Override
	public Object visitConditionalExpr(ConditionalExpr conditionalExpr, Object arg) throws Exception {
		//TODO  implement this method
		throw new UnsupportedOperationException();
	}

	@Override
	public Object visitDimension(Dimension dimension, Object arg) throws Exception {
		dimension.getHeight().visit(this, arg);
		dimension.getWidth().visit(this, arg);
		Expr height = dimension.getHeight();
		Expr width = dimension.getWidth();
		check((height.getType() == INT && width.getType() == INT), dimension, "either height or width were not integers");
		System.out.println("visited dimension");
		return null;
	}

	@Override
	//This method can only be used to check PixelSelector objects on the right hand side of an assignment. 
	//Either modify to pass in context info and add code to handle both cases, or when on left side
	//of assignment, check fields from parent assignment statement.
	public Object visitPixelSelector(PixelSelector pixelSelector, Object arg) throws Exception {
		Type xType = (Type) pixelSelector.getX().visit(this, arg);
		check(xType == Type.INT, pixelSelector.getX(), "only ints as pixel selector components");
		Type yType = (Type) pixelSelector.getY().visit(this, arg);
		check(yType == Type.INT, pixelSelector.getY(), "only ints as pixel selector components");
		return null;
	}

	// int x = a[x,y];

	@Override
	//This method several cases--you don't have to implement them all at once.
	//Work incrementally and systematically, testing as you go.  
	public Object visitAssignmentStatement(AssignmentStatement assignmentStatement, Object arg) throws Exception {
		System.out.println("visited assignmentStatementvisit");
		//TODO:  implement this method
		String name = assignmentStatement.getName();
		Declaration declaration = symbolTable.search(name);
		check(declaration != null, assignmentStatement, "undeclared variable (visitAssignmentStatement)");

		//case 1: the target type is not an IMAGE
		if(declaration.getType() != IMAGE) {
			Type exprType = (Type)assignmentStatement.getExpr().visit(this, arg);

			System.out.println("entered the first case");
			//i. there is no PixelSelector on the left side
			check(assignmentStatement.getSelector() == null, assignmentStatement, "a PixelSelector was detected outside of an image assignment (visitAssignmentStatement)");

			//ii. Expression must be assignment compatible with the target
			check(areAssignCompatible(declaration.getType(), exprType), assignmentStatement, "values were not assignment compatible (visitAssignmentStatement)");

			//coercion for part ii.
			if (declaration.getType() == INT && exprType == FLOAT) {
				assignmentStatement.getExpr().setCoerceTo(INT);
			}
			else if (declaration.getType() == FLOAT && exprType == INT) {
				assignmentStatement.getExpr().setCoerceTo(FLOAT);
			}
			else if (declaration.getType() == INT && exprType == COLOR) {
				assignmentStatement.getExpr().setCoerceTo(INT);
			}
			else if (declaration.getType() == COLOR && exprType == INT) {
				assignmentStatement.getExpr().setCoerceTo(COLOR);
			}


		}

		//case 2: the target type is an IMAGE without a PixelSelector
		if (declaration.getType() == IMAGE && assignmentStatement.getSelector() == null) {
			Type exprType = (Type)assignmentStatement.getExpr().visit(this, arg);

			System.out.println("entered the second case");
			//i. expression must be assignment compatible with target
			//ii. If both the expression and target are IMAGE, they are assignment compatible
			check(areAssignCompatible(declaration.getType(), exprType), assignmentStatement, "values were not assignment compatible (visitAssignmentStatement");

			//iii. The following pairs are assignment compatible. The expression is coerced to match the target variable type.
			if (exprType == INT) {
				assignmentStatement.getExpr().setCoerceTo(COLOR);
			}
			if (exprType == FLOAT) {
				assignmentStatement.getExpr().setCoerceTo(COLORFLOAT);
			}


		}
		System.out.println("got here");

		//case 3: target type is an IMAGE with a pixelSelector
		if (declaration.getType() == IMAGE && assignmentStatement.getSelector() != null) {
			System.out.println("entered the third case");
			//i. Recall from scope rule: expressions appearing in PixelSelector that
			//appear on the left side of an assignment statement are local variables
			//defined in the assignment statement. These variables are implicitly
			//declared to have type INT, and must be an IdentExpr. The names cannot
			//be previously declared as global variable.

			Expr x = assignmentStatement.getSelector().getX();
			Expr y = assignmentStatement.getSelector().getY();

			check(symbolTable.search(x.getText()) == null, assignmentStatement, "X value of the pixelSelector was already defined");
			check(symbolTable.search(y.getText()) == null, assignmentStatement, "Y value of the pixelSelector was already defined");

			IToken xToken = x.getFirstToken();
			IToken yToken = y.getFirstToken();

			Declaration xDec = new NameDef(xToken, "INT", x.getText());
			Declaration yDec = new NameDef(yToken, "INT", y.getText());

			symbolTable.insert(x.getText(), xDec);
			symbolTable.insert(y.getText(), yDec);

			assignmentStatement.getSelector().visit(this, arg);

			assignmentStatement.getExpr().visit(this, arg);

			Type exprType = (Type)assignmentStatement.getExpr().visit(this, arg);

			//checks the type of the RHS
			if (exprType == COLOR || exprType == COLORFLOAT || exprType == FLOAT || exprType == INT) {
				assignmentStatement.getExpr().setCoerceTo(COLOR);
			}
			else{
				check(false, assignmentStatement, "RHS of the pixelSelector is of the wrong type (must be COLOR, COLORFLOAT, FLOAT, or INT");
			}

			symbolTable.delete(x.getText());
			symbolTable.delete(y.getText());

		}

		return null;
		//throw new UnsupportedOperationException("Unimplemented visit method.");
	}


	@Override
	public Object visitWriteStatement(WriteStatement writeStatement, Object arg) throws Exception {
		Type sourceType = (Type) writeStatement.getSource().visit(this, arg);
		Type destType = (Type) writeStatement.getDest().visit(this, arg);
		check(destType == Type.STRING || destType == Type.CONSOLE, writeStatement,
				"illegal destination type for write");
		check(sourceType != Type.CONSOLE, writeStatement, "illegal source type for write");
		return null;
	}

	@Override
	public Object visitReadStatement(ReadStatement readStatement, Object arg) throws Exception {
		//TODO:  implement this method
		throw new UnsupportedOperationException("Unimplemented visit method.");
	}

	public boolean areAssignCompatible(Type targetType, Type rhsType) {
		return (targetType == rhsType
		|| targetType == INT && rhsType == FLOAT
				|| targetType == FLOAT && rhsType == INT
				|| targetType == INT && rhsType == COLOR
				|| targetType == COLOR && rhsType == INT
				|| targetType == IMAGE && rhsType == INT
				|| targetType == IMAGE  && rhsType == FLOAT
				|| targetType == IMAGE  && rhsType == COLOR
				|| targetType == IMAGE && rhsType == COLORFLOAT);


	}

	@Override
	public Object visitVarDeclaration(VarDeclaration declaration, Object arg) throws Exception {
		String name = declaration.getName();
		boolean inserted = symbolTable.insert(name,declaration);
		check(inserted, declaration, "variable " + name + " is already declared");
		Expr init = declaration.getExpr();

		if (declaration.getType() == IMAGE) {
			if (declaration.getNameDef().getDim() == null) {
				check(init!=null, declaration, "image was declared without a supported dimension or initialization");
			}
		}

		if (declaration.getNameDef().getDim() != null) {
			declaration.getNameDef().getDim().visit(this, arg);
		}

		if (init != null) {
			Type initializerType = (Type)init.visit(this,arg);
			check(areAssignCompatible(declaration.getType(), initializerType), declaration, "type of expression and declared type do not match");

			if (declaration.getOp().getKind() == Kind.ASSIGN) {
				if(declaration.getType() == INT && initializerType == FLOAT) {
					declaration.getExpr().setCoerceTo(INT);
				}
				if(declaration.getType() == FLOAT && initializerType == INT) {
					declaration.getExpr().setCoerceTo(FLOAT);
				}
				if(declaration.getType() == INT && initializerType == COLOR) {
					declaration.getExpr().setCoerceTo(INT);
				}
				if(declaration.getType() == COLOR && initializerType == INT) {
					declaration.getExpr().setCoerceTo(COLOR);
				}
				if(declaration.getType() == IMAGE && initializerType == INT) {
					declaration.getExpr().setCoerceTo(COLOR);
				}
				if(declaration.getType() == IMAGE && initializerType == FLOAT) {
					declaration.getExpr().setCoerceTo(COLORFLOAT);
				}
			}

			declaration.setInitialized(true);


		}
		return null;
		//throw new UnsupportedOperationException("Unimplemented visit method.");
	}


	@Override
	public Object visitProgram(Program program, Object arg) throws Exception {
		//Save root of AST so return type can be accessed in return statements
		root = program;

		List<NameDef> params = program.getParams();
		for (NameDef def : params) {
			def.visit(this, arg);
		}

		
		//Check declarations and statements
		List<ASTNode> decsAndStatements = program.getDecsAndStatements();
		for (ASTNode node : decsAndStatements) {
			node.visit(this, arg);
		}
		return program;
	}

	@Override
	public Object visitNameDef(NameDef nameDef, Object arg) throws Exception {
		String name = nameDef.getName();
		boolean inserted = symbolTable.insert(name,nameDef);
		check(inserted, nameDef, "variable " + name + "is already declared" );
		nameDef.setInitialized(true);
		return null;
	}

	@Override
	public Object visitNameDefWithDim(NameDefWithDim nameDefWithDim, Object arg) throws Exception {
		String name = nameDefWithDim.getName();
		boolean inserted = symbolTable.insert(name,nameDefWithDim);
		check(inserted, nameDefWithDim, "variable " + name + "is already declared" );
		nameDefWithDim.getDim().visit(this, arg);
		nameDefWithDim.setInitialized(true);
		return null;
		//throw new UnsupportedOperationException();
	}
 
	@Override
	public Object visitReturnStatement(ReturnStatement returnStatement, Object arg) throws Exception {
		Type returnType = root.getReturnType();  //This is why we save program in visitProgram.
		Type expressionType = (Type) returnStatement.getExpr().visit(this, arg);
		check(returnType == expressionType, returnStatement, "return statement with invalid type");
		return null;
	}

	@Override
	public Object visitUnaryExprPostfix(UnaryExprPostfix unaryExprPostfix, Object arg) throws Exception {
		Type expType = (Type) unaryExprPostfix.getExpr().visit(this, arg);
		check(expType == Type.IMAGE, unaryExprPostfix, "pixel selector can only be applied to image");
		unaryExprPostfix.getSelector().visit(this, arg);
		unaryExprPostfix.setType(Type.INT);
		unaryExprPostfix.setCoerceTo(COLOR);
		return Type.COLOR;
	}

}
