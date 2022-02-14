package edu.ufl.cise.plc;

import edu.ufl.cise.plc.ast.ASTNode;
import edu.ufl.cise.plc.ast.ConditionalExpr;
import edu.ufl.cise.plc.ast.Expr;

import java.util.List;

public class Parser implements  IParser{


    IToken t;
    Lexer lexer;

    @Override
    public ASTNode parse() throws PLCException {

        return null;
    }

    public Parser(String input) throws LexicalException{

        lexer = new Lexer(input);
        t = lexer.next();
    }

    ASTNode Expr() throws PLCException{
        ASTNode expr = null;
        if (isKind(IToken.Kind.KW_IF)) {
            expr = ConditionalExpr();
        }
        else if (isKind(IToken.Kind.BANG,IToken.Kind.MINUS,IToken.Kind.COLOR_OP,IToken.Kind.IMAGE_OP)) {
            expr = LogicalOrExpr();
        }
        return expr;
    }

    ASTNode ConditionalExpr() throws PLCException{
        IToken startToken = t;
        consume();
        match(IToken.Kind.LPAREN, "(");
        ASTNode condition = Expr();
        match(IToken.Kind.RPAREN, ")");
        ASTNode truecase = Expr();
        match(IToken.Kind.KW_ELSE, "ELSE");
        ASTNode falsecase = Expr();
        match(IToken.Kind.KW_FI, "FI");
        ConditionalExpr expr = new ConditionalExpr(startToken, condition, truecase, falsecase);
    }

    ASTNode LogicalOrExpr() {
        return null;
    }

    ASTNode LogicalAndExpr() {
        return null;
    }

    ASTNode ComparisonExpr() {
        return null;
    }

    ASTNode AdditiveExpr() {
        return null;
    }

    ASTNode MultiplicativeExpr() {
        return null;
    }

    ASTNode UnaryExpr() {
        return null;
    }

    ASTNode UnaryExprPostfix() {
        return null;
    }

    ASTNode PrimaryExpr() {
        return null;
    }

    ASTNode PixelSelector() {
        return null;
    }

    //returns a boolean describing whether the end of the series of tokens has been reached.
    boolean isEnd() {
        if (t.getKind() == IToken.Kind.EOF) {
            return true;
        }
        else {
            return false;
        }
    }

    //returns the kind of a single token
    boolean isKind(IToken.Kind kind) {
        return t.getKind() == kind;
    }

    //returns the kind of multiple tokens
    boolean isKind(IToken.Kind... kinds) {
        for (IToken.Kind k : kinds) {
            if (k == t.getKind()) {
                return true;
            }
        }
        return false;
    }

    IToken consume() throws LexicalException{
        t = lexer.next();
        return t;
    }

    void match(IToken.Kind kind, String message) throws PLCException{
        if (t.getKind() == kind) {
            t = lexer.next();
        }
        else {
            throw new PLCException("Expected: " + message);
        }
    }

}
