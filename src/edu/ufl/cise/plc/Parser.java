package edu.ufl.cise.plc;

import edu.ufl.cise.plc.ast.*;
import java.util.List;
public class Parser implements  IParser{


    IToken t;
    Lexer lexer;
    boolean first = true;

    //main parsing function
    @Override
    public ASTNode parse() throws PLCException {
        if (first == true) {
            t = lexer.next();
            first = false;
        }

        if (isKind(IToken.Kind.EOF)) {
            throw new SyntaxException("Empty File");
        }

        return Expr();
    }

    //constructor
    public Parser(String input) {

        lexer = new Lexer(input);
    }

    ASTNode Program() throws PLCException {
        IToken start = t;
        Types.Type returntype = null;
        String name = "";
        List<NameDef> params = null;
        List<ASTNode> decsAndStatements = null;

        ASTNode ast = null;

        if (isKind(IToken.Kind.TYPE, IToken.Kind.KW_VOID)) {
            returntype = Types.Type.toType(t.getText());
            consume();
            match(IToken.Kind.IDENT, "IDENT");
            match(IToken.Kind.LPAREN, "(");

            if (isKind(IToken.Kind.TYPE)) {
                NameDef namedef = NameDef();
                params.add(namedef);
                while (isKind(IToken.Kind.COMMA)) {
                    consume();
                    NameDef namedef2 = NameDef();
                    params.add(namedef2);
                }
            }

            match(IToken.Kind.RPAREN, ")");

            while(isKind(IToken.Kind.TYPE, IToken.Kind.IDENT, IToken.Kind.KW_WRITE, IToken.Kind.RETURN)) {

                if (isKind(IToken.Kind.TYPE)) {
                    // is a declaration
                    Declaration dec = Declaration();
                    decsAndStatements.add(dec);
                }

                if (isKind(IToken.Kind.IDENT,IToken.Kind.KW_WRITE, IToken.Kind.RETURN)) {
                    // is a statement
                    Statement statement = Statement();
                    decsAndStatements.add(statement);
                }

                match(IToken.Kind.SEMI, ";");
            }


        }

        else throw new SyntaxException("invalid expression");

        match(IToken.Kind.EOF, "EOF");

        return new Program(start, returntype, name, params, decsAndStatements);
    }

    NameDef NameDef() throws PLCException {
        NameDef ND = null;
        IToken start = t;
        consume();

        if (isKind(IToken.Kind.LSQUARE)) {
            Dimension dimension = Dimension();

            if (!isKind(IToken.Kind.IDENT)) {
                throw new SyntaxException("invalid expression");
            }

            IToken name = t;
            consume();
            ND = new NameDefWithDim(start, start, name, dimension);
            return ND;
        }

        if (!isKind(IToken.Kind.IDENT)) {
            throw new SyntaxException("invalid expression");
        }

        IToken name = t;
        consume();
        ND = new NameDef(start, start, name);
        return ND;
    }

    Declaration Declaration() throws PLCException {
        IToken start = t;
        Declaration declaration = null;
        NameDef namedef = NameDef();
        declaration = namedef;


        if (isKind(IToken.Kind.ASSIGN, IToken.Kind.LARROW)) {
            IToken op = t;
            consume();
            Expr expr = Expr();

            declaration = new VarDeclaration(start, namedef, op, expr);
        }

        return declaration;
    }

    //expression function
    Expr Expr() throws PLCException{
        Expr expr = null;
        if (isKind(IToken.Kind.KW_IF)) {
            expr = ConditionalExpr();
        }
        else if (isKind(IToken.Kind.BANG,IToken.Kind.MINUS,IToken.Kind.COLOR_OP,IToken.Kind.IMAGE_OP,
                IToken.Kind.BOOLEAN_LIT, IToken.Kind.STRING_LIT, IToken.Kind.INT_LIT, IToken.Kind.FLOAT_LIT,
                IToken.Kind.IDENT, IToken.Kind.LPAREN, IToken.Kind.COLOR_CONST, IToken.Kind.LANGLE, IToken.Kind.KW_CONSOLE)) {
            expr = LogicalOrExpr();
        }
        else throw new SyntaxException("invalid expression.");
        return expr;
    }

    //conditional expression function
     Expr ConditionalExpr() throws PLCException{
         IToken start = t;
        consume();
        match(IToken.Kind.LPAREN, "(");
        Expr condition = Expr();
        match(IToken.Kind.RPAREN, ")");
        Expr truecase = Expr();
        match(IToken.Kind.KW_ELSE, "ELSE");
        Expr falsecase = Expr();
        match(IToken.Kind.KW_FI, "FI");
        ConditionalExpr expr = new ConditionalExpr(start, condition, truecase, falsecase);
        return expr;
    }

    //logical or expression function
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

    //logical and expression function
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

    //comparison expression function
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

    //additive expression function
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

    //multiplicative expression function
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

    //unary expression function
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

    //unary expression extended function
    Expr UnaryExprPostfix() throws PLCException{
        IToken start = t;
        Expr expr = PrimaryExpr();
        if (isKind(IToken.Kind.LSQUARE)) {
            PixelSelector pixelExpr = PixelSelector();
            return new UnaryExprPostfix(t, expr, pixelExpr);
        }
        return expr;
    }

    //primary expression function
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
        else if (isKind(IToken.Kind.COLOR_CONST)) {
            expr = new ColorConstExpr(start);
            consume();
        }
        else if (isKind(IToken.Kind.LANGLE)) {
            consume();
            Expr expr1 = Expr();
            match(IToken.Kind.COMMA, ",");
            Expr expr2 = Expr();
            match(IToken.Kind.COMMA, ",");
            Expr expr3 = Expr();
            match(IToken.Kind.RANGLE, ">>");
            expr = new ColorExpr(start, expr1, expr2, expr3);

        }
        else if (isKind(IToken.Kind.KW_CONSOLE)) {
            expr = new ConsoleExpr(start);
            consume();
        }



        else {
            throw new SyntaxException("Invalid PrimaryExpr");
        }
        return expr;
    }

    //pixel selection function
    PixelSelector PixelSelector() throws PLCException{
        IToken start = t;
        PixelSelector expr = null;
        consume();
        Expr expr1 = Expr();
        match(IToken.Kind.COMMA, ",");
        Expr expr2 = Expr();
        match(IToken.Kind.RSQUARE, "]");
        expr = new PixelSelector(start, expr1, expr2);
        return expr;

    }

    Dimension Dimension() throws PLCException {
        IToken start = t;
        Dimension dim = null;
        consume();
        Expr expr1 = Expr();
        match(IToken.Kind.COMMA, ",");
        Expr expr2 = Expr();
        match(IToken.Kind.RSQUARE, "]");
        dim = new Dimension(start, expr1, expr2);
        return dim;
    }

    Statement Statement() throws PLCException {
        IToken start = t;
        PixelSelector pixel = null;
        Statement statement = null;

        if (isKind(IToken.Kind.IDENT)) {
            consume();
            if (isKind(IToken.Kind.LSQUARE)) {
                pixel = PixelSelector();
            }
            if (isKind(IToken.Kind.ASSIGN)) {
                consume();
                Expr expr = Expr();
                statement = new AssignmentStatement(start,start.getText(),pixel,expr);
            }
            else if (isKind(IToken.Kind.LARROW)) {
                consume();
                Expr expr = Expr();
                statement = new ReadStatement(start,start.getText(),pixel,expr);
            }
            else throw new SyntaxException("The statement could not be built.");
        }

        else if (isKind(IToken.Kind.KW_WRITE)) {
            consume();
            Expr expr1 = Expr();
            match(IToken.Kind.RARROW, "->");
            Expr expr2 = Expr();
            statement = new WriteStatement(start, expr1, expr2);
        }

        else if (isKind(IToken.Kind.RETURN)) {
            consume();
            Expr expr = Expr();
            statement = new ReturnStatement(start, expr);
        }
        else throw new SyntaxException("The statement could not be built.");



        return statement;
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

    void consume() throws LexicalException{
        t = lexer.next();
    }

    void match(IToken.Kind kind, String message) throws PLCException{
        if (t.getKind() == kind) {
            t = lexer.next();
        }
        else {
            throw new SyntaxException("Expected: " + message);
        }
    }

}
