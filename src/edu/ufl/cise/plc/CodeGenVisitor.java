package edu.ufl.cise.plc;

import edu.ufl.cise.plc.ast.*;
import java.lang.StringBuilder;
import java.util.List;
import java.util.Locale;

import edu.ufl.cise.plc.ast.Types;
import edu.ufl.cise.plc.ast.Types.Type;
import edu.ufl.cise.plc.runtime.ColorTuple;

public class CodeGenVisitor implements ASTVisitor {

    String packageName;

    String importStatements = "";

    CodeGenVisitor(String packageName){
        this.packageName = packageName;
    }

    public String getTypeName(Type type) throws Exception{
        switch(type) {
            case BOOLEAN -> {
                return "boolean";
            }
            case FLOAT -> {
                return "float";
            }
            case INT -> {
                return "int";
            }
            case STRING -> {
                return "String";
            }
            case VOID -> {
                return "Void";
            }
            default -> {
                throw new Exception("Unsupported Type");
            }
        }
    }

    public String getBoxedType(Type type) throws Exception{
        switch(type) {
            case BOOLEAN -> {
                return "Boolean";
            }
            case FLOAT -> {
                return "Float";
            }
            case INT -> {
                return "Integer";
            }
            case STRING -> {
                return "String";
            }
            case VOID -> {
                return "Void";
            }
            default -> {
                throw new Exception("Unsupported Type");
            }
        }
    }

    @Override
    public Object visitBooleanLitExpr(BooleanLitExpr booleanLitExpr, Object arg) throws Exception{
        CodeGenStringBuilder sb = (CodeGenStringBuilder) arg;
        sb.append(booleanLitExpr.getText());
        return sb;
    }

    @Override
    public Object visitStringLitExpr(StringLitExpr stringLitExpr, Object arg) throws Exception{
        CodeGenStringBuilder sb = (CodeGenStringBuilder) arg;

        if(stringLitExpr.getText().contains("\n")) {
            sb.append("\"\"\"").newline().append(stringLitExpr.getValue()).newline().append("\"\"\"");
        }
        else {
            sb.append("\"").append(stringLitExpr.getValue()).append("\"");
        }

        return sb;
    }

    @Override
    public Object visitIntLitExpr(IntLitExpr intLitExpr, Object arg) throws Exception{
        CodeGenStringBuilder sb = (CodeGenStringBuilder) arg;
        if(intLitExpr.getCoerceTo() != null && intLitExpr.getCoerceTo() != Type.INT) {
            sb.lparen().append(getTypeName(intLitExpr.getCoerceTo())).rparen().space();
        }
        sb.append(intLitExpr.getValue());
        return sb;
    }

    @Override
    public Object visitFloatLitExpr(FloatLitExpr floatLitExpr, Object arg) throws Exception{
        CodeGenStringBuilder sb = (CodeGenStringBuilder) arg;
        if(floatLitExpr.getCoerceTo() != null && floatLitExpr.getCoerceTo() != Type.FLOAT) {
            sb.lparen().append(getTypeName(floatLitExpr.getCoerceTo())).rparen().space();
        }
        sb.append(floatLitExpr.getValue()).append('f');
        return sb;
    }

    @Override
    public Object visitColorConstExpr(ColorConstExpr colorConstExpr, Object arg) throws Exception{
        //TODO: Edit
        throw new Exception("NOT IN THIS PROJECT");
    }

    @Override
    public Object visitConsoleExpr(ConsoleExpr consoleExpr, Object arg) throws Exception{
        //TODO: add color types that are read from the console
        CodeGenStringBuilder sb = (CodeGenStringBuilder) arg;
        if (!(importStatements.contains("import edu.ufl.cise.plc.runtime.ConsoleIO;\n"))) {
            importStatements = importStatements + "import edu.ufl.cise.plc.runtime.ConsoleIO;\n";
        }
        if(consoleExpr.getType() == Type.IMAGE)
        {
            throw new Exception("not implemented yet");
        }
        String boxedType = getBoxedType(consoleExpr.getCoerceTo());
        sb.lparen().append(boxedType).rparen().newline();
        String upperType = getTypeName(consoleExpr.getCoerceTo()).toUpperCase(Locale.ROOT);
        sb.append("ConsoleIO.readValueFromConsole(\"");
        sb.append(upperType);
        String promptType = getBoxedType(consoleExpr.getCoerceTo()).toLowerCase(Locale.ROOT);
        sb.append("\", ");
        sb.append("\"Enter ");
        sb.append(promptType);
        sb.append(": \")");

        return sb;
    }

    @Override
    public Object visitColorExpr(ColorExpr colorExpr, Object arg) throws Exception{
        //TODO: Edit
        CodeGenStringBuilder sb = (CodeGenStringBuilder) arg;
        if (!(importStatements.contains("import edu.ufl.cise.plc.runtime.ColorTuple;\n"))) {
            importStatements = importStatements + "import edu.ufl.cise.plc.runtime.ColorTuple;\n";
        }
        if (colorExpr.getRed().getType() == Type.INT && colorExpr.getBlue().getType() == Type.INT && colorExpr.getGreen().getType() == Type.INT) {
            sb.append("new ColorTuple(");
            colorExpr.getRed().visit(this, sb);
            sb.append(", ");
            colorExpr.getGreen().visit(this, sb);
            sb.append(", ");
            colorExpr.getBlue().visit(this, sb);
            sb.rparen();
        }
        else{
           throw new Exception("Red, Green, or Blue were not integers");
        }

        return sb;
    }

    @Override
    public Object visitUnaryExpr(UnaryExpr unaryExpression, Object arg) throws Exception{
        //TODO: add color
        CodeGenStringBuilder sb = (CodeGenStringBuilder) arg;
        sb.lparen();
        sb.append(unaryExpression.getOp().getText()).space();
        unaryExpression.getExpr().visit(this, arg);
        sb.rparen();
        return sb;
    }

    @Override
    public Object visitBinaryExpr(BinaryExpr binaryExpr, Object arg) throws Exception{
        //TODO: Edit
        CodeGenStringBuilder sb = (CodeGenStringBuilder) arg;
        sb.lparen();

        if (binaryExpr.getRight().getType() == Type.STRING && binaryExpr.getOp().getKind() == IToken.Kind.EQUALS) {
            binaryExpr.getLeft().visit(this, sb);
            sb.append(".equals(");
            binaryExpr.getRight().visit(this, sb);
            sb.rparen();
        }
        else if (binaryExpr.getRight().getType() == Type.STRING && binaryExpr.getOp().getKind() == IToken.Kind.NOT_EQUALS) {
            sb.append('!');
            binaryExpr.getLeft().visit(this, sb);
            sb.append(".equals(");
            binaryExpr.getRight().visit(this, sb);
            sb.rparen();
        }
        else {
            binaryExpr.getLeft().visit(this, sb);
            sb.space().append(binaryExpr.getOp().getText()).space();
            binaryExpr.getRight().visit(this, sb);
        }
        sb.rparen();
        return sb;
    }

    @Override
    public Object visitIdentExpr(IdentExpr identExpr, Object arg) throws Exception{
        CodeGenStringBuilder sb = (CodeGenStringBuilder) arg;
        if( identExpr.getCoerceTo() != null &&  identExpr.getCoerceTo() != identExpr.getType()) {
            sb.lparen().append(getTypeName(identExpr.getCoerceTo())).rparen().space();
        }
        sb.append(identExpr.getText());
        return sb;
    }

    @Override
    public Object visitConditionalExpr(ConditionalExpr conditionalExpr, Object arg) throws Exception{
        //TODO: Edit
        CodeGenStringBuilder sb = (CodeGenStringBuilder) arg;
        sb.lparen().lparen();
        conditionalExpr.getCondition().visit(this, sb);
        sb.rparen().append(" ? ");
        conditionalExpr.getTrueCase().visit(this, sb);
        sb.append(" : ");
        conditionalExpr.getFalseCase().visit(this, sb);
        sb.rparen();
        return sb;
    }

    @Override
    public Object visitDimension(Dimension dimension, Object arg) throws Exception{
        //TODO: Edit
        throw new Exception("NOT IN THIS PROJECT");
    }
    @Override
    public Object visitPixelSelector(PixelSelector pixelSelector, Object arg) throws Exception{
        //TODO: Edit
        throw new Exception("NOT IN THIS PROJECT");
    }
    @Override
    public Object visitAssignmentStatement(AssignmentStatement assignmentStatement, Object arg) throws Exception{
        //TODO: Edit
        CodeGenStringBuilder sb = (CodeGenStringBuilder) arg;
        sb.append(assignmentStatement.getName()).space().append('=').space();

        if(assignmentStatement.getExpr().getCoerceTo() != null && assignmentStatement.getExpr().getType() != assignmentStatement.getTargetDec().getType())
        {
            sb.lparen().append(getTypeName(assignmentStatement.getExpr().getCoerceTo())).rparen().space();
        }

        sb.lparen();
        assignmentStatement.getExpr().visit(this, sb);
        sb.rparen();
        return sb;
    }
    @Override
    public Object visitWriteStatement(WriteStatement writeStatement, Object arg) throws Exception{
        //TODO
        CodeGenStringBuilder sb = (CodeGenStringBuilder) arg;
        if (!(importStatements.contains("import edu.ufl.cise.plc.runtime.ConsoleIO;\n"))) {
            importStatements = importStatements + "import edu.ufl.cise.plc.runtime.ConsoleIO;\n";
        }
        sb.append("ConsoleIO.console.println(");
        writeStatement.getSource().visit(this, arg);
        sb.append(")");

        return null;
    }
    @Override
    public Object visitReadStatement(ReadStatement readStatement, Object arg) throws Exception{
        //TODO
        CodeGenStringBuilder sb = (CodeGenStringBuilder) arg;
        sb.append(readStatement.getName());
        sb.append(" = ");
        readStatement.getSource().visit(this, arg);
        return null;
    }
    @Override
    public Object visitProgram(Program program, Object arg) throws Exception{
        CodeGenStringBuilder sb = new CodeGenStringBuilder();
        //appends the package name
        sb.append("package").space().append(packageName).semicolon().newline();
        //TODO
        //various import statements
        int index = sb.getIndex();
        //class name & declaration
        String className = program.getName();
        sb.append("public class ").append(className).lbrace().newline();

        //class append
        sb.append("public static ").append(getTypeName(program.getReturnType())).append(" apply( ");

        //checking parameters
        List<NameDef> params = program.getParams();
        int i = 0;
        for (NameDef def : params) {
            i++;
            def.visit(this, sb);
            if(i != params.size()) {
                sb.append(",");
            }
        }

        sb.rparen().lbrace().newline();

        //Check declarations and statements
        List<ASTNode> decsAndStatements = program.getDecsAndStatements();
        for (ASTNode node : decsAndStatements) {
            node.visit(this, sb);
            sb.semicolon().newline();
        }

        sb.space().rbrace().newline().rbrace();
        //insert import statements at correct index here???
        sb.insert(index, importStatements);
        return sb.returnSB().toString();
    }
    @Override

    public Object visitNameDef(NameDef nameDef, Object arg) throws Exception{
        CodeGenStringBuilder sb  = (CodeGenStringBuilder) arg;
        sb.append(getTypeName(nameDef.getType())).space().append(nameDef.getName());
        return sb;
    }
    @Override
    public Object visitNameDefWithDim(NameDefWithDim nameDefWithDim, Object arg) throws Exception{
        throw new Exception("NOT IN THIS PROJECT");
    }

    @Override
    public Object visitReturnStatement(ReturnStatement returnStatement, Object arg) throws Exception{
        CodeGenStringBuilder sb = (CodeGenStringBuilder) arg;
        Expr expr = returnStatement.getExpr();
        sb.append("return ");
        expr.visit(this, sb);
        return sb;
    }

    @Override
    public Object visitVarDeclaration(VarDeclaration declaration, Object arg) throws Exception{
        //TODO: Edit
        CodeGenStringBuilder sb = (CodeGenStringBuilder) arg;
        declaration.getNameDef().visit(this, sb);
        if(declaration.getExpr() != null)
        {
            sb.append(" = ");

            if(declaration.getExpr().getCoerceTo() != null && declaration.getExpr().getType() != declaration.getType()) {
                sb.lparen().append(getTypeName(declaration.getExpr().getCoerceTo())).rparen().space();
            }

            declaration.getExpr().visit(this, sb);
        }
        return sb;
    }
    @Override
    public Object visitUnaryExprPostfix(UnaryExprPostfix unaryExprPostfix, Object arg) throws Exception{
        //TODO: Edit
        throw new Exception("NOT IN THIS PROJECT");
    }

}
