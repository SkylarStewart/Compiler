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

    public Object visitStringLitExpr(StringLitExpr stringLitExpr, Object arg) throws Exception{
        //TODO
        return null;
    }

    public Object visitIntLitExpr(IntLitExpr intLitExpr, Object arg) throws Exception{
        //TODO
        return null;
    }

    public Object visitFloatLitExpr(FloatLitExpr floatLitExpr, Object arg) throws Exception{
        //TODO
        return null;
    }

    public Object visitColorConstExpr(ColorConstExpr colorConstExpr, Object arg) throws Exception{
        throw new Exception("NOT IN THIS PROJECT");
    }

    public Object visitConsoleExpr(ConsoleExpr consoleExpr, Object arg) throws Exception{
        //TODO
        return null;
    }

    public Object visitColorExpr(ColorExpr colorExpr, Object arg) throws Exception{
        throw new Exception("NOT IN THIS PROJECT");
    }

    public Object visitUnaryExpr(UnaryExpr unaryExpression, Object arg) throws Exception{
        //TODO
        return null;
    }

    public Object visitBinaryExpr(BinaryExpr binaryExpr, Object arg) throws Exception{
        //TODO
        return null;
    }

    public Object visitIdentExpr(IdentExpr identExpr, Object arg) throws Exception{
        //TODO
        return null;
    }

    public Object visitConditionalExpr(ConditionalExpr conditionalExpr, Object arg) throws Exception{
        //TODO
        return null;
    }

    public Object visitDimension(Dimension dimension, Object arg) throws Exception{
        throw new Exception("NOT IN THIS PROJECT");
    }
    @Override
    public Object visitPixelSelector(PixelSelector pixelSelector, Object arg) throws Exception{
        throw new Exception("NOT IN THIS PROJECT");
    }
    @Override
    public Object visitAssignmentStatement(AssignmentStatement assignmentStatement, Object arg) throws Exception{
        //TODO
        return null;
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
        //TODO
        return null;
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
