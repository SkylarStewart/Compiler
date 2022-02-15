package edu.ufl.cise.plc;

import edu.ufl.cise.plc.ast.ASTNode;
import edu.ufl.cise.plc.ast.ConditionalExpr;
import edu.ufl.cise.plc.ast.Expr;
import edu.ufl.cise.plc.ast.IntLitExpr;

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

    Expr Expr() throws PLCException{
        Expr expr = null;
        if (isKind(IToken.Kind.KW_IF)) {
            expr = ConditionalExpr();
        }
        else if (isKind(IToken.Kind.BANG,IToken.Kind.MINUS,IToken.Kind.COLOR_OP,IToken.Kind.IMAGE_OP)) {
            expr = LogicalOrExpr();
        }
        if (isKind(IToken.Kind.INT_LIT)) {
            IntLitExpr expr2 = new IntLitExpr(t);
            expr = expr2;
        }

        return expr;
    }

     Expr ConditionalExpr() throws PLCException{
        IToken startToken = t;
        consume();
        match(IToken.Kind.LPAREN, "(");
        Expr condition = Expr();
        match(IToken.Kind.RPAREN, ")");
        Expr truecase = Expr();
        match(IToken.Kind.KW_ELSE, "ELSE");
        Expr falsecase = Expr();
        match(IToken.Kind.KW_FI, "FI");
        ConditionalExpr expr = new ConditionalExpr(startToken, condition, truecase, falsecase);
        return expr;
    }

    Expr LogicalOrExpr() {
        return null;
    }

    Expr LogicalAndExpr() {
        return null;
    }

    Expr ComparisonExpr() {
        return null;
    }

    Expr AdditiveExpr() {
        return null;
    }

    Expr MultiplicativeExpr() {
        return null;
    }

    Expr UnaryExpr() {
        return null;
    }

    Expr UnaryExprPostfix() {
        return null;
    }

    Expr PrimaryExpr() {
        return null;
    }

    Expr PixelSelector() {
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
