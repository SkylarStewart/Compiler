package edu.ufl.cise.plc;

import edu.ufl.cise.plc.ast.*;

public class CodeGenVisitor implements ASTVisitor {
    CodeGenVisitor(String packageName){

    }
    @Override
    public Object visitBooleanLitExpr(BooleanLitExpr booleanLitExpr, Object arg) throws Exception{
        //TODO
        return null;
    }

    @Override
    public Object visitStringLitExpr(StringLitExpr stringLitExpr, Object arg) throws Exception{
        CodeGenStringBuilder sb = (CodeGenStringBuilder) arg;
        sb.append("\"").append(stringLitExpr.getValue()).append("\"");
        return sb;
    }

    @Override
    public Object visitIntLitExpr(IntLitExpr intLitExpr, Object arg) throws Exception{
        //TODO
        return null;
    }

    @Override
    public Object visitFloatLitExpr(FloatLitExpr floatLitExpr, Object arg) throws Exception{
        //TODO
        return null;
    }

    @Override
    public Object visitColorConstExpr(ColorConstExpr colorConstExpr, Object arg) throws Exception{
        throw new Exception("NOT IN THIS PROJECT");
    }

    @Override
    public Object visitConsoleExpr(ConsoleExpr consoleExpr, Object arg) throws Exception{
        //TODO
        return null;
    }

    @Override
    public Object visitColorExpr(ColorExpr colorExpr, Object arg) throws Exception{
        throw new Exception("NOT IN THIS PROJECT");
    }

    @Override
    public Object visitUnaryExpr(UnaryExpr unaryExpression, Object arg) throws Exception{
        CodeGenStringBuilder sb = (CodeGenStringBuilder) arg;
        sb.append(unaryExpression.getExpr().getText()).space();
        unaryExpression.visit(this, sb);
        return sb;
    }

    @Override
    public Object visitBinaryExpr(BinaryExpr binaryExpr, Object arg) throws Exception{
        //TODO
        return null;
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
        //TODO
        return null;
    }

    @Override
    public Object visitDimension(Dimension dimension, Object arg) throws Exception{
        throw new Exception("NOT IN THIS PROJECT");
    }
    @Override
    public Object visitPixelSelector(PixelSelector pixelSelector, Object arg) throws Exception{
        throw new Exception("NOT IN THIS PROJECT");
    }
    @Override
    public Object visitAssignmentStatement(AssignmentStatement assignmentStatement, Object arg) throws Exception{
        System.out.println("got here!");
        CodeGenStringBuilder sb = (CodeGenStringBuilder) arg;
        sb.append(assignmentStatement.getName()).space().append('=').space();
        assignmentStatement.getExpr().visit(this, sb);
        return sb;
    }
    @Override
    public Object visitWriteStatement(WriteStatement writeStatement, Object arg) throws Exception{
        //TODO
        return null;
    }
    @Override
    public Object visitReadStatement(ReadStatement readStatement, Object arg) throws Exception{
        //TODO
        return null;
    }
    @Override
    public Object visitProgram(Program program, Object arg) throws Exception{
        CodeGenStringBuilder sb = new CodeGenStringBuilder();
        //appends the package name
        sb.append("package").space().append(packageName).semicolon().newline();
        //TODO
        //various import statements

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
        return sb.returnSB().toString();
    }
    @Override

    public Object visitNameDef(NameDef nameDef, Object arg) throws Exception{
        //TODO
        return null;
    }
    @Override
    public Object visitNameDefWithDim(NameDefWithDim nameDefWithDim, Object arg) throws Exception{
        throw new Exception("NOT IN THIS PROJECT");
    }

    @Override
    public Object visitReturnStatement(ReturnStatement returnStatement, Object arg) throws Exception{
        //TODO
        return null;
    }

    @Override
    public Object visitVarDeclaration(VarDeclaration declaration, Object arg) throws Exception{
        //TODO
        return null;
    }
    @Override
    public Object visitUnaryExprPostfix(UnaryExprPostfix unaryExprPostfix, Object arg) throws Exception{
        throw new Exception("NOT IN THIS PROJECT");
    }

}
