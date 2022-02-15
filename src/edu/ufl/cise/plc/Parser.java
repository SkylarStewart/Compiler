package edu.ufl.cise.plc;

import edu.ufl.cise.plc.ast.*;

import java.awt.geom.FlatteningPathIterator;
import java.util.List;

public class Parser implements  IParser{


    IToken t;
    Lexer lexer;

    @Override
    public ASTNode parse() throws PLCException {
        t = lexer.next();
        return Expr();
    }

    public Parser(String input) {

        lexer = new Lexer(input);
        //t = lexer.next();
    }

    Expr Expr() throws PLCException{
        Expr expr = null;
        if (isKind(IToken.Kind.KW_IF)) {
            System.out.println("was conditoinal");
            expr = ConditionalExpr();
        }
        else if (isKind(IToken.Kind.BANG,IToken.Kind.MINUS,IToken.Kind.COLOR_OP,IToken.Kind.IMAGE_OP,
                IToken.Kind.BOOLEAN_LIT, IToken.Kind.STRING_LIT, IToken.Kind.INT_LIT, IToken.Kind.FLOAT_LIT,
                IToken.Kind.IDENT, IToken.Kind.LPAREN)) {
            System.out.println("was logical or");
            expr = LogicalOrExpr();
        }
        return expr;
    }

     Expr ConditionalExpr() throws PLCException{
         IToken start = t;
        consume();
         System.out.println(t.getKind());
        match(IToken.Kind.LPAREN, "(");
         System.out.println("left paren");
        Expr condition = Expr();
         System.out.println("right paren");
        match(IToken.Kind.RPAREN, ")");
        Expr truecase = Expr();
        match(IToken.Kind.KW_ELSE, "ELSE");
        Expr falsecase = Expr();
        match(IToken.Kind.KW_FI, "FI");
        ConditionalExpr expr = new ConditionalExpr(start, condition, truecase, falsecase);
        return expr;
    }

    Expr LogicalOrExpr() throws PLCException{
        IToken start = t;
        Expr expr = LogicalAndExpr();

        while(isKind(IToken.Kind.OR)) {
            IToken op = t;
            consume();
            Expr rightExpr = LogicalAndExpr();
            expr = new BinaryExpr(start, expr, op, rightExpr);
        }
        return expr;
    }

    Expr LogicalAndExpr() throws PLCException{
        IToken start = t;
        Expr expr = ComparisonExpr();

        while(isKind(IToken.Kind.AND)) {
            IToken op = t;
            consume();
            Expr rightExpr = ComparisonExpr();
            expr = new BinaryExpr(start, expr, op, rightExpr);
        }
        return expr;
    }

    Expr ComparisonExpr() throws PLCException{
        IToken start = t;
        Expr expr = AdditiveExpr();

        while(isKind(IToken.Kind.LT,IToken.Kind.GT,IToken.Kind.EQUALS,IToken.Kind.NOT_EQUALS,IToken.Kind.LE,IToken.Kind.GE)) {
            IToken op = t;
            consume();
            Expr rightExpr = AdditiveExpr();
            expr = new BinaryExpr(start, expr, op, rightExpr);
        }
        return expr;
    }

    Expr AdditiveExpr() throws PLCException{
        IToken start = t;
        Expr expr = MultiplicativeExpr();

        while(isKind(IToken.Kind.PLUS, IToken.Kind.MINUS)) {
            IToken op = t;
            consume();
            Expr rightExpr = MultiplicativeExpr();
            expr = new BinaryExpr(start, expr, op, rightExpr);
        }
        return expr;
    }

    Expr MultiplicativeExpr() throws PLCException{
        IToken start = t;
        Expr expr = UnaryExpr();

        while(isKind(IToken.Kind.TIMES, IToken.Kind.DIV, IToken.Kind.MOD)) {
            IToken op = t;
            consume();
            Expr rightExpr = UnaryExpr();
            expr = new BinaryExpr(start, expr, op, rightExpr);
        }
        return expr;
    }

    Expr UnaryExpr() throws PLCException{
        IToken start = t;
        if(isKind(IToken.Kind.BANG,IToken.Kind.MINUS,IToken.Kind.COLOR_OP, IToken.Kind.IMAGE_OP)) {
            IToken op = t;
            consume();
            Expr rightExpr = UnaryExpr();
            return new UnaryExpr(start, op, rightExpr);
        }
        return UnaryExprPostfix();
    }

    Expr UnaryExprPostfix() throws PLCException{
        IToken start = t;
        return PrimaryExpr();
    }

    Expr PrimaryExpr() throws PLCException{
        Expr expr = null;
        IToken start = t;
        if (isKind(IToken.Kind.BOOLEAN_LIT)) {

            expr = new BooleanLitExpr(start);
            consume();
        }
        else if (isKind(IToken.Kind.STRING_LIT)){
            expr = new StringLitExpr(start);
            consume();
        }
        else if (isKind(IToken.Kind.INT_LIT)){
            expr = new IntLitExpr(start);
            consume();
        }
        else if (isKind(IToken.Kind.FLOAT_LIT)){
            expr = new FloatLitExpr(start);
            consume();
        }
        else if (isKind(IToken.Kind.IDENT)){
            expr = new IdentExpr(start);
            consume();
        }
        else if (isKind(IToken.Kind.LPAREN)){
            consume();
            expr = Expr();
            match(IToken.Kind.RPAREN, ")");
        }
        else {
            throw new SyntaxException("Invalid PrimaryExpr");
        }
        return expr;
    }

    Expr PixelSelector() throws PLCException{
        IToken start = t;
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
