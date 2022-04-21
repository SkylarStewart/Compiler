package edu.ufl.cise.plc;

import edu.ufl.cise.plc.ast.*;
import java.lang.StringBuilder;
import java.util.List;
import java.util.Locale;
import edu.ufl.cise.plc.ast.Types;
import edu.ufl.cise.plc.ast.Types.Type;
import edu.ufl.cise.plc.runtime.ColorTuple;
import java.awt.Color;
import java.awt.image.BufferedImage;


import edu.ufl.cise.plc.runtime.ImageEqual;
import edu.ufl.cise.plc.runtime.ImageOps;
import java.util.ArrayList;
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
                return "void";
            }
            case IMAGE -> {
                return "BufferedImage";
            }
            case COLOR -> {
                return "ColorTuple";
            }
            case COLORFLOAT -> {
                return "ColorTupleFloat";
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
            case IMAGE -> {
                return "BufferedImage";
            }
            case COLOR -> {
                return "Color";
            }
            case COLORFLOAT -> {
                return "ColorFloat";
            }
            default -> {
                throw new Exception("Unsupported Type");
            }
        }
    }

    //NOTE: COMPLETE!
    @Override
    public Object visitBooleanLitExpr(BooleanLitExpr booleanLitExpr, Object arg) throws Exception{
        CodeGenStringBuilder sb = (CodeGenStringBuilder) arg;
        sb.append(booleanLitExpr.getText());
        return sb;
    }

    //NOTE: COMPLETE!
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

    //NOTE: COMPLETE!
    @Override
    public Object visitIntLitExpr(IntLitExpr intLitExpr, Object arg) throws Exception{
        CodeGenStringBuilder sb = (CodeGenStringBuilder) arg;
        if(intLitExpr.getCoerceTo() != null && intLitExpr.getCoerceTo() != Type.INT && intLitExpr.getCoerceTo() != Type.COLOR && intLitExpr.getCoerceTo() != Type.COLORFLOAT) {
            sb.lparen().append(getTypeName(intLitExpr.getCoerceTo())).rparen().space();
        }
        sb.append(intLitExpr.getValue());
        return sb;
    }

    //NOTE: COMPLETE!
    @Override
    public Object visitFloatLitExpr(FloatLitExpr floatLitExpr, Object arg) throws Exception{
        CodeGenStringBuilder sb = (CodeGenStringBuilder) arg;
        if(floatLitExpr.getCoerceTo() != null && floatLitExpr.getCoerceTo() != Type.FLOAT && floatLitExpr.getCoerceTo() != Type.COLOR && floatLitExpr.getCoerceTo() != Type.COLORFLOAT) {
            sb.lparen().append(getTypeName(floatLitExpr.getCoerceTo())).rparen().space();
        }
        sb.append(floatLitExpr.getValue()).append('f');
        return sb;
    }

    //NOTE: COMPLETE!
    @Override
    public Object visitColorConstExpr(ColorConstExpr colorConstExpr, Object arg) throws Exception{
        CodeGenStringBuilder sb = (CodeGenStringBuilder) arg;
        if (!(importStatements.contains("import java.awt.Color;\n"))) {
            importStatements = importStatements + "import java.awt.Color;\n";
        }
        if (!(importStatements.contains("import edu.ufl.cise.plc.runtime.ColorTuple;\n"))) {
            importStatements = importStatements + "import edu.ufl.cise.plc.runtime.ColorTuple;\n";
        }

        sb.append("ColorTuple.unpack(Color.").append(colorConstExpr.getText()).append(".getRGB()").rparen();

        return sb;

    }

    @Override
    public Object visitConsoleExpr(ConsoleExpr consoleExpr, Object arg) throws Exception{
        //TODO: add color types that are read from the console
        CodeGenStringBuilder sb = (CodeGenStringBuilder) arg;
        if (!(importStatements.contains("import edu.ufl.cise.plc.runtime.ConsoleIO;\n"))) {
            importStatements = importStatements + "import edu.ufl.cise.plc.runtime.ConsoleIO;\n";
        }

        if(consoleExpr.getCoerceTo() == Type.COLOR)
        {
            sb.append("(ColorTuple) ");
            sb.append("ConsoleIO.readValueFromConsole(\"COLOR\"");
            sb.append(", \"Enter RGB Values:\")");

        }
        else {

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
        }
        return sb;
    }

    @Override
    public Object visitColorExpr(ColorExpr colorExpr, Object arg) throws Exception{
        CodeGenStringBuilder sb = (CodeGenStringBuilder) arg;
        if (!(importStatements.contains("import edu.ufl.cise.plc.runtime.ColorTuple;\n"))) {
            importStatements = importStatements + "import edu.ufl.cise.plc.runtime.ColorTuple;\n";
        }
        if (!(importStatements.contains("import edu.ufl.cise.plc.runtime.ColorTupleFloat;\n"))) {
            importStatements = importStatements + "import edu.ufl.cise.plc.runtime.ColorTupleFloat;\n";
        }
        //if (colorExpr.getRed().getType() == Type.INT && colorExpr.getBlue().getType() == Type.INT && colorExpr.getGreen().getType() == Type.INT) {
            sb.append("new ColorTuple(");

            if(colorExpr.getType() == Type.COLORFLOAT) {
                sb.append("new ColorTupleFloat(");
            }
            colorExpr.getRed().visit(this, sb);
            sb.append(", ");
            colorExpr.getGreen().visit(this, sb);
            sb.append(", ");
            colorExpr.getBlue().visit(this, sb);
            sb.rparen();

        if(colorExpr.getType() == Type.COLORFLOAT) {
            sb.rparen();
        }

        //}
        //else{
           //throw new Exception("Red, Green, or Blue were not integers");
        //}

        return sb;
    }

    //NOTE: COMPLETE!
    @Override
    public Object visitUnaryExpr(UnaryExpr unaryExpression, Object arg) throws Exception{
        System.out.println("was a unary expression");
        //TODO: add color
        CodeGenStringBuilder sb = (CodeGenStringBuilder) arg;

        //runs if op is getRed, getGreen, or getBlue
        if(unaryExpression.getOp().getKind() == IToken.Kind.COLOR_OP) {
            if (!(importStatements.contains("import edu.ufl.cise.plc.runtime.ColorTuple;\n"))) {
                importStatements = importStatements + "import edu.ufl.cise.plc.runtime.ColorTuple;\n";
            }

            if(unaryExpression.getExpr().getType() == Type.INT || unaryExpression.getExpr().getType() == Type.COLOR)
            {


                sb.append("ColorTuple.").append(unaryExpression.getOp().getText()).lparen();
                unaryExpression.getExpr().visit(this, sb);
                sb.rparen();
            }

            else if(unaryExpression.getExpr().getType() == Type.IMAGE)
            {
                if (!(importStatements.contains("import edu.ufl.cise.plc.runtime.ImageOps;\n"))) {
                    importStatements = importStatements + "import edu.ufl.cise.plc.runtime.ImageOps;\n";
                }

                sb.append("ImageOps.");
                if (unaryExpression.getOp().getText().equals("getRed")) {

                    sb.append("extractRed");
                }
                if (unaryExpression.getOp().getText().equals("getGreen")) {
                    sb.append("extractGreen");
                }
                if (unaryExpression.getOp().getText().equals("getBlue")) {
                    sb.append("extractBlue");
                }
                sb.lparen();
                unaryExpression.getExpr().visit(this, sb);
                sb.rparen();
            }
        }

        else {
            sb.lparen();
            sb.append(unaryExpression.getOp().getText()).space();
            unaryExpression.getExpr().visit(this, sb);
            sb.rparen();
        }

        return sb;
    }


    //NOTE: COMPLETE!
    @Override
    public Object visitBinaryExpr(BinaryExpr binaryExpr, Object arg) throws Exception{
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
        else if(binaryExpr.getLeft().getType() == Type.IMAGE && binaryExpr.getOp().getKind() == IToken.Kind.EQUALS)
        {
            if (!(importStatements.contains("import edu.ufl.cise.plc.runtime.ImageEqual;\n"))) {
                importStatements = importStatements + "import edu.ufl.cise.plc.runtime.ImageEqual;\n";
            }

            sb.append("ImageEqual.equals(");
            binaryExpr.getLeft().visit(this, sb);
            sb.append(", ");
            binaryExpr.getRight().visit(this, sb);
            sb.rparen();
        }
        else if(binaryExpr.getLeft().getType() == Type.IMAGE && binaryExpr.getOp().getKind() == IToken.Kind.EQUALS)
        {
            if (!(importStatements.contains("import edu.ufl.cise.plc.runtime.ImageEqual;\n"))) {
                importStatements = importStatements + "import edu.ufl.cise.plc.runtime.ImageEqual;\n";
            }

            sb.append("!ImageEqual.equals(");
            binaryExpr.getLeft().visit(this, sb);
            sb.append(", ");
            binaryExpr.getRight().visit(this, sb);
            sb.rparen();
        }

        else if(binaryExpr.getType() == Type.COLOR || binaryExpr.getType() == Type.COLORFLOAT)
        {
            if (!(importStatements.contains("import edu.ufl.cise.plc.runtime.ImageOps;\n"))) {
                importStatements = importStatements + "import edu.ufl.cise.plc.runtime.ImageOps;\n";
            }
            if (!(importStatements.contains("import edu.ufl.cise.plc.runtime.ColorTuple;\n"))) {
                importStatements = importStatements + "import edu.ufl.cise.plc.runtime.ColorTuple;\n";
            }
            //binaryTupleOp(OP op, ColorTuple left, ColorTuple right)

            sb.append("ImageOps.binaryTupleOp(").append("ImageOps.OP.").append(binaryExpr.getOp().getKind().toString()).append(", ");
            binaryExpr.getLeft().visit(this, sb);
            sb.append(", ");

           if (binaryExpr.getRight().getType() == Type.INT && binaryExpr.getCoerceTo() != Type.COLOR) {
                sb.append("ColorTuple.unpack(");
                binaryExpr.getRight().visit(this, sb);
                sb.rparen();
            }
            else {
                binaryExpr.getRight().visit(this, sb);
            }

            //binaryExpr.getRight().visit(this, sb);
            sb.rparen();

            //ImageOps.setColor(teal, x, y, ((ImageOps.binaryTupleOp(ImageOps.OP.PLUS, ColorTuple.unpack(blue.getRGB(x, y)), ColorTuple.unpack(green.getRGB(x, y))))));
            //ImageOps.setColor(teal, x, y, ((ImageOps.binaryTupleOp(ImageOps.OP.PLUS, ColorTuple.unpack(blue.getRGB(x, y)), ColorTuple.unpack(ColorTuple.unpack(green.getRGB(x, y)))))));


        }
        else if (binaryExpr.getType() == Type.IMAGE) {
            if (!(importStatements.contains("import edu.ufl.cise.plc.runtime.ImageOps;\n"))) {
                importStatements = importStatements + "import edu.ufl.cise.plc.runtime.ImageOps;\n";
            }
            if (binaryExpr.getLeft().getType() == Type.IMAGE && binaryExpr.getRight().getType() == Type.IMAGE) {
                sb.append("ImageOps.binaryImageImageOp(").append("ImageOps.OP.").append(binaryExpr.getOp().getKind().toString()).append(",");
                binaryExpr.getLeft().visit(this, sb);
                sb.append(", ");
                binaryExpr.getRight().visit(this, sb);
                sb.rparen();


            }
            if (binaryExpr.getLeft().getType() == Type.IMAGE && (binaryExpr.getRight().getType() == Type.INT || binaryExpr.getRight().getType() == Type.FLOAT)) {
                System.out.println("got to right case");
                sb.append("ImageOps.binaryImageScalarOp(").append("ImageOps.OP.").append(binaryExpr.getOp().getKind().toString()).append(",");
                binaryExpr.getLeft().visit(this, sb);
                sb.append(", ");
                binaryExpr.getRight().visit(this, sb);
                sb.rparen();
            }
        }
        else {
            binaryExpr.getLeft().visit(this, sb);
            sb.space().append(binaryExpr.getOp().getText()).space();
            binaryExpr.getRight().visit(this, sb);
        }
        sb.rparen();
        return sb;
    }

    //NOTE: COMPLETE!
    @Override
    public Object visitIdentExpr(IdentExpr identExpr, Object arg) throws Exception{
        CodeGenStringBuilder sb = (CodeGenStringBuilder) arg;
        if( identExpr.getCoerceTo() != null &&  identExpr.getCoerceTo() != identExpr.getType() && identExpr.getCoerceTo()!= Type.COLOR && identExpr.getCoerceTo() != Type.COLORFLOAT) {
            sb.lparen().append(getTypeName(identExpr.getCoerceTo())).rparen().space();
        }
        sb.append(identExpr.getText());
        return sb;
    }

    //NOTE: COMPLETE!
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
        CodeGenStringBuilder sb = (CodeGenStringBuilder) arg;
        dimension.getWidth().visit(this, sb);
        sb.append(", ");
        dimension.getHeight().visit(this, sb);

        return sb;
    }
    @Override
    public Object visitPixelSelector(PixelSelector pixelSelector, Object arg) throws Exception{
        //TODO: Edit
        CodeGenStringBuilder sb = (CodeGenStringBuilder) arg;
        int val = sb.getLR();
        String name = sb.getName();

        // if val is '1' the pixel selector is on the left-hand side.
        if (val == 1) {
            if (!(importStatements.contains("import edu.ufl.cise.plc.runtime.ImageOps;\n"))) {
                importStatements = importStatements + "import edu.ufl.cise.plc.runtime.ImageOps;\n";
            }
            ArrayList<String> returnArray = new ArrayList<String>();

            //we will return an array of two Strings: the X and the Y.
            CodeGenStringBuilder newSB = new CodeGenStringBuilder();
            pixelSelector.getX().visit(this, newSB);

            CodeGenStringBuilder newSB2 = new CodeGenStringBuilder();
            pixelSelector.getY().visit(this, newSB2);
            returnArray.add(newSB.returnSB().toString());
            returnArray.add(newSB2.returnSB().toString());
            return returnArray;

   /*         sb.append("for( int ");
            pixelSelector.getX().visit(this, sb);
            sb.append(" = 0; ");
            pixelSelector.getX().visit(this, sb);
            sb.append("<");
            sb.append(name).append(".getWidth(); ");
            pixelSelector.getX().visit(this, sb);
            sb.append("++)");

            sb.newline().tab();
            sb.append("for (int ");
            pixelSelector.getY().visit(this, sb);
            sb.append(" = 0; ");
            pixelSelector.getY().visit(this, sb);
            sb.append(" < ");
            sb.append(name).append(".getHeight(); ");
            pixelSelector.getY().visit(this, sb);
            sb.append("++)");
            sb.newline().tab();

            sb.append("ImageOps.setColor(").append(name).append(", ");
            pixelSelector.getX().visit(this, sb);
            sb.append(", ");
            pixelSelector.getY().visit(this, sb);
            sb.append(", ");*/


        }

        //if the val is '2' the pixel selector is on the right-hand side
        else if (val == 2) {
            pixelSelector.getX().visit(this, sb);
            sb.append(", ");
            pixelSelector.getY().visit(this, sb);
            return sb;
        }
        return sb;
    }

    //ImageOps.setColor(f, g, h, (new ColorTuple((((float) g / x) * (float) 255), 0.0f, (((float) h / y) * (float) 255))));
    //ImageOps.setColor(f, g, h, new ColorTuple((new ColorTupleFloat((((float) g / x) * (float) 255), 0.0f, (((float) h / y) * (float) 255)))));

    //TODO: PRETTY BIG W.I.P.
    @Override
    public Object visitAssignmentStatement(AssignmentStatement assignmentStatement, Object arg) throws Exception{
        CodeGenStringBuilder sb = (CodeGenStringBuilder) arg;
        System.out.println("visited assignmentStatement");

        if (!(importStatements.contains("import edu.ufl.cise.plc.runtime.ColorTuple;\n"))) {
            importStatements = importStatements + "import edu.ufl.cise.plc.runtime.ColorTuple;\n";
        }

        //makes the X & Y
        String X = "";
        String Y = "";
        if (assignmentStatement.getSelector() != null) {
            sb.setLR(1);
            ArrayList<String> XandY = (ArrayList<String>) assignmentStatement.getSelector().visit(this, sb);
            X = XandY.get(0);
            Y = XandY.get(1);

            System.out.println("X: " + X);
            System.out.println("Y: " + Y);
        }
        else {
            X = "tempX";
            Y = "tempY";
        }

        //code that runs if it's an image
        if(assignmentStatement.getTargetDec().getType() == Type.IMAGE) {

            //MAJOR CASE 1: has DIM
            //the only RHS values can be: INT, COLOR, IDENT
            if (assignmentStatement.getTargetDec().getDim() != null) {
                if (!(importStatements.contains("import edu.ufl.cise.plc.runtime.ImageOps;\n"))) {
                    importStatements = importStatements + "import edu.ufl.cise.plc.runtime.ImageOps;\n";
                }

                //has dimension, expr.coerceTo is color {temporarily changing)
                if (assignmentStatement.getExpr().getCoerceTo() == Type.COLOR || assignmentStatement.getExpr().getCoerceTo() == Type.COLORFLOAT)
                {
                    if (assignmentStatement.getExpr().getType() == Type.INT)
                    {
                        if (!(importStatements.contains("import edu.ufl.cise.plc.runtime.ColorTuple;\n"))) {
                            importStatements = importStatements + "import edu.ufl.cise.plc.runtime.ColorTuple;\n";
                        }
                        int val = assignmentStatement.getExpr().getFirstToken().getIntValue();
                        if(val > 255)
                        {
                            val = 255;
                        }
                        else if(val < 0)
                        {
                            val = 0;
                        }
                        sb.append("ImageOps.setAllPixels(").append(assignmentStatement.getName());
                        sb.append(", ").append(val);
                        sb.rparen();
                    }
                    else
                    {
                        sb.append("for( int ").append(X);
                        //assignmentStatement.getTargetDec().getDim().getWidth().visit(this, sb);
                        sb.append(" = 0; ").append(X);
                        //assignmentStatement.getTargetDec().getDim().getWidth().visit(this, sb);
                        sb.append("<");
                        sb.append(assignmentStatement.getName()).append(".getWidth(); ").append(X);
                        //assignmentStatement.getTargetDec().getDim().getWidth().visit(this, sb);
                        sb.append("++)");

                        sb.newline().tab();
                        sb.append("for (int ").append(Y);
                        //assignmentStatement.getTargetDec().getDim().getHeight().visit(this, sb);
                        sb.append(" = 0; ").append(Y);
                        //assignmentStatement.getTargetDec().getDim().getHeight().visit(this, sb);
                        sb.append(" < ");
                        sb.append(assignmentStatement.getName()).append(".getHeight(); ").append(Y);
                        //assignmentStatement.getTargetDec().getDim().getHeight().visit(this, sb);
                        sb.append("++)");
                        sb.newline().tab();

                        sb.append("ImageOps.setColor(").append(assignmentStatement.getName()).append(", ");
                        //assignmentStatement.getTargetDec().getDim().getWidth().visit(this, sb);
                        sb.append(X).append(", ");
                        //assignmentStatement.getTargetDec().getDim().getHeight().visit(this, sb);
                        sb.append(Y).append(", ");

                        sb.lparen();
                        assignmentStatement.getExpr().visit(this, sb);
                        sb.rparen().rparen();
                    }
                }
                else if (assignmentStatement.getExpr().getType() == Type.COLOR || assignmentStatement.getExpr().getType() == Type.COLORFLOAT)
                {
                    sb.append("for( int ").append(X);
                    //assignmentStatement.getTargetDec().getDim().getWidth().visit(this, sb);
                    sb.append(" = 0; ").append(X);
                    //assignmentStatement.getTargetDec().getDim().getWidth().visit(this, sb);
                    sb.append("<");
                    sb.append(assignmentStatement.getName()).append(".getWidth(); ").append(X);
                    //assignmentStatement.getTargetDec().getDim().getWidth().visit(this, sb);
                    sb.append("++)");

                    sb.newline().tab();
                    sb.append("for (int ").append(Y);
                    //assignmentStatement.getTargetDec().getDim().getHeight().visit(this, sb);
                    sb.append(" = 0; ").append(Y);
                    //assignmentStatement.getTargetDec().getDim().getHeight().visit(this, sb);
                    sb.append(" < ");
                    sb.append(assignmentStatement.getName()).append(".getHeight(); ").append(Y);
                    //assignmentStatement.getTargetDec().getDim().getHeight().visit(this, sb);
                    sb.append("++)");
                    sb.newline().tab();

                    sb.append("ImageOps.setColor(").append(assignmentStatement.getName()).append(", ");
                    //assignmentStatement.getTargetDec().getDim().getWidth().visit(this, sb);
                    sb.append(X).append(", ");
                    //assignmentStatement.getTargetDec().getDim().getHeight().visit(this, sb);
                    sb.append(Y).append(", ");

                    sb.lparen();
                    assignmentStatement.getExpr().visit(this, sb);
                    sb.rparen().rparen();
                }
                //LHS and RHS are images, getDIM is not null (CASE: ImageOps.resize(RHS, x, y);
                else if (assignmentStatement.getExpr().getType() == Type.IMAGE)
                {
                    if (!(importStatements.contains("import edu.ufl.cise.plc.runtime.ImageOps;\n"))) {
                        importStatements = importStatements + "import edu.ufl.cise.plc.runtime.ImageOps;\n";
                    }

                    sb.append(assignmentStatement.getName());
                    sb.append(" = ");
                    sb.append("ImageOps.resize(");
                    assignmentStatement.getExpr().visit(this, sb);
                    sb.append(", ");
                    assignmentStatement.getTargetDec().getDim().getWidth().visit(this, sb);
                    sb.append(", ");
                    assignmentStatement.getTargetDec().getDim().getHeight().visit(this, sb);
                    sb.rparen();
                }
            }
            //MAJOR CASE 2: has no DIM
            //TODO;
            else if (assignmentStatement.getTargetDec().getDim() == null) {
                System.out.println("did not have a dimension");

                if (!(importStatements.contains("import edu.ufl.cise.plc.runtime.ImageOps;\n"))) {
                    importStatements = importStatements + "import edu.ufl.cise.plc.runtime.ImageOps;\n";
                }

                //not sure what to do when the LHS and RHS are colors, only when they're images.
                if (assignmentStatement.getExpr().getType() == Type.IMAGE)
                {
                    if(assignmentStatement.getExpr().getFirstToken().getKind() == IToken.Kind.IDENT)
                    {
                        sb.append(assignmentStatement.getName());
                        sb.append(" =").space();
                        sb.append("ImageOps.clone(");
                        assignmentStatement.getExpr().visit(this, sb);
                        sb.rparen();
                    }
                    else
                    {
                        sb.append(assignmentStatement.getName());
                        sb.append(" =").space();
                        assignmentStatement.getExpr().visit(this, sb);
                    }
                }
                //handles when the RHS is an int coerced to a color
                if (assignmentStatement.getExpr().getCoerceTo() == Type.COLOR) {
                //can probably change this to just if == INT
                    if (assignmentStatement.getExpr().getType() == Type.INT) {
                        if (!(importStatements.contains("import edu.ufl.cise.plc.runtime.ColorTuple;\n"))) {
                            importStatements = importStatements + "import edu.ufl.cise.plc.runtime.ColorTuple;\n";
                        }
                        int val = assignmentStatement.getExpr().getFirstToken().getIntValue();
                        if (val > 255) {
                            val = 255;
                        } else if (val < 0) {
                            val = 0;
                        }
                        sb.append("ImageOps.setAllPixels(").append(assignmentStatement.getName());
                        sb.append(", ").append(val);
                        sb.rparen();
                    }
                }
            }
        }
        //everything else (not an image)
        else {

            sb.append(assignmentStatement.getName()).space().append('=').space();
            if (assignmentStatement.getExpr().getCoerceTo() != null && assignmentStatement.getExpr().getType() != assignmentStatement.getTargetDec().getType()) {
                sb.lparen().append(getTypeName(assignmentStatement.getExpr().getCoerceTo())).rparen().space();
            }
            sb.lparen();
            assignmentStatement.getExpr().visit(this, sb);
            sb.rparen();
        }
        return sb;
    }

    //NOTE: DONE!
    @Override
    public Object visitWriteStatement(WriteStatement writeStatement, Object arg) throws Exception{
        CodeGenStringBuilder sb = (CodeGenStringBuilder) arg;
        if (!(importStatements.contains("import edu.ufl.cise.plc.runtime.ConsoleIO;\n"))) {
            importStatements = importStatements + "import edu.ufl.cise.plc.runtime.ConsoleIO;\n";
        }

        if(writeStatement.getSource().getType() == Type.IMAGE && writeStatement.getDest().getType() == Type.CONSOLE) {
            sb.append("ConsoleIO.displayImageOnScreen(");
            writeStatement.getSource().visit(this, sb);
            sb.rparen();

        }
        else if(writeStatement.getSource().getType() == Type.IMAGE && writeStatement.getDest().getType() == Type.STRING) {

            if (!(importStatements.contains("import edu.ufl.cise.plc.runtime.FileURLIO;\n"))) {
                importStatements = importStatements + "import edu.ufl.cise.plc.runtime.FileURLIO;\n";
            }

            sb.append("FileURLIO.writeImage(");
            writeStatement.getSource().visit(this, sb);
            sb.append(", ");
            writeStatement.getDest().visit(this, sb);
            sb.rparen();
        }
        else if (writeStatement.getDest().getType() == Type.STRING) {

            if (!(importStatements.contains("import edu.ufl.cise.plc.runtime.FileURLIO;\n"))) {
                importStatements = importStatements + "import edu.ufl.cise.plc.runtime.FileURLIO;\n";
            }

            sb.append("FileURLIO.writeValue(");
            writeStatement.getSource().visit(this, sb);
            sb.append(", ");
            writeStatement.getDest().visit(this, sb);
            sb.rparen();
        }
        else {
        sb.append("ConsoleIO.console.println(");
        writeStatement.getSource().visit(this, sb);
        sb.append(")");
        }

        return sb;
        //was return null
    }

    //TODO: POSSIBLY DONE? STILL NEED TO FIGURE OUT WTF HAPPENS WITH PIXELSELECTOR (SAYS ABSOLUTELY NOTHING)
    @Override
    public Object visitReadStatement(ReadStatement readStatement, Object arg) throws Exception{
        CodeGenStringBuilder sb = (CodeGenStringBuilder) arg;
        if(readStatement.getTargetDec().getType() == Type.IMAGE)
        {
            if (!(importStatements.contains("import edu.ufl.cise.plc.runtime.FileURLIO;\n"))) {
                importStatements = importStatements + "import edu.ufl.cise.plc.runtime.FileURLIO;\n";
            }

            if (readStatement.getSelector() == null) {
                if(readStatement.getTargetDec().getDim() != null)
                {

                    sb.append(readStatement.getName()).space();
                    sb.append(" = FileURLIO.readImage(");
                    readStatement.getSource().visit(this, arg);
                    sb.append(", ");
                    readStatement.getTargetDec().getDim().visit(this, sb);
                    sb.rparen().semicolon().newline();
                    sb.append("FileURLIO.closeFiles()");

                }
                else
                {
                    sb.append(readStatement.getName()).space();
                    sb.append("= FileURLIO.readImage(");
                    readStatement.getSource().visit(this, arg);
                    sb.rparen().semicolon().newline();
                    sb.append("FileURLIO.closeFiles()");
                }
            }
            else {
                if(readStatement.getTargetDec().getDim() != null)
                {
                    sb.append(readStatement.getName()).space();
                    sb.append("= FileURLIO.readImage(");
                    readStatement.getSource().visit(this, arg);
                    sb.append(", ");
                    readStatement.getTargetDec().getDim().visit(this, sb);
                    sb.rparen().semicolon().newline();
                    sb.append("FileURLIO.closeFiles()");
                }
                else
                {
                    sb.append(readStatement.getName()).space();
                    sb.append("= FileURLIO.readImage(");
                    readStatement.getSource().visit(this, arg);
                    sb.rparen().semicolon().newline();
                    sb.append("FileURLIO.closeFiles()");
                }
            }
        }
        else
        {
            sb.append(readStatement.getName());
            sb.append(" = ");
            readStatement.getSource().visit(this, sb);
        }
        return sb;
        //was return null
    }
    //NOTE: COMPLETE!
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


    //NOTE: COMPLETE!
    @Override
    public Object visitNameDef(NameDef nameDef, Object arg) throws Exception{
        CodeGenStringBuilder sb  = (CodeGenStringBuilder) arg;
        sb.append(getTypeName(nameDef.getType())).space().append(nameDef.getName());

        if (nameDef.getType() == Type.IMAGE) {
            if (!(importStatements.contains("import java.awt.image.BufferedImage;\n"))) {
                importStatements = importStatements + "import java.awt.image.BufferedImage;\n";
            }
        }

        if (nameDef.getType() == Type.COLOR) {
            if (!(importStatements.contains("import edu.ufl.cise.plc.runtime.ColorTuple;\n"))) {
                importStatements = importStatements + "import edu.ufl.cise.plc.runtime.ColorTuple;\n";
            }
        }

        return sb;
    }
    @Override
    public Object visitNameDefWithDim(NameDefWithDim nameDefWithDim, Object arg) throws Exception{
        if (!(importStatements.contains("import java.awt.image.BufferedImage;\n"))) {
            importStatements = importStatements + "import java.awt.image.BufferedImage;\n";
        }

        CodeGenStringBuilder sb = (CodeGenStringBuilder) arg;

        sb.append("BufferedImage ");
        sb.append(nameDefWithDim.getName());
        return sb;

    }

    @Override
    public Object visitReturnStatement(ReturnStatement returnStatement, Object arg) throws Exception{
        CodeGenStringBuilder sb = (CodeGenStringBuilder) arg;
        Expr expr = returnStatement.getExpr();
        sb.append("return ");
        expr.visit(this, sb);
        return sb;
    }

    //TODO: MASSIVE WORK IN PROGRESS AS WELL :((((((((((((((
    @Override
    public Object visitVarDeclaration(VarDeclaration declaration, Object arg) throws Exception {
        //TODO: AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA

        CodeGenStringBuilder sb = (CodeGenStringBuilder) arg;
        String X = "tempX";
        String Y = "tempY";

        if (declaration.getType() == Type.IMAGE ) {
            if (!(importStatements.contains("import java.awt.image.BufferedImage;\n"))) {
                importStatements = importStatements + "import java.awt.image.BufferedImage;\n";
            }
            if (!(importStatements.contains("import edu.ufl.cise.plc.runtime.FileURLIO;\n"))) {
                importStatements = importStatements + "import edu.ufl.cise.plc.runtime.FileURLIO;\n";
            }
            if (!(importStatements.contains("import edu.ufl.cise.plc.runtime.ImageOps;\n"))) {
                importStatements = importStatements + "import edu.ufl.cise.plc.runtime.ImageOps;\n";
            }


            //case 1: has init
            if (declaration.getExpr() != null)
            {
                //READ OPERATOR (DONE)
                if(declaration.getOp().getKind() == IToken.Kind.LARROW)
                {
                    //has dim
                    if (declaration.getDim() != null) {
                        declaration.getNameDef().visit(this, sb);
                        sb.append("=FileURLIO.readImage(");
                        declaration.getExpr().visit(this, sb);
                        sb.append(",");
                        declaration.getDim().visit(this, sb);
                        sb.append(")").semicolon().newline();
                        sb.append("FileURLIO.closeFiles()");
                    }
                    //else
                    else {
                        declaration.getNameDef().visit(this, sb);
                        sb.append("=FileURLIO.readImage(");
                        declaration.getExpr().visit(this, sb);
                        sb.append(")").semicolon().newline();
                        sb.append("FileURLIO.closeFiles()");
                    }
                }
                //ASSIGNMENT OPERATOR (W.I.P).
                // Image[X,Y] = RED
                else
                {
                    //There is no PixelSelector here. the only way to get X and Y would be through getting the dimensions.
                    //CASE: Has DIM (DONE)
                        //Expr coerce to is color
                            //Expr type = int (DONE)
                            //else (not int) (DONE)
                        //Expr type = color (DONE)
                        //Expr type == IMAGE (LHS AND RHS are images, getDIM is not null ((case: ImageOps.resize(RHS,x,y);
                    //CASE: no DIM
                        //LHS & RHS are both image
                        //EXPR coerce to is color
                            //Expr type = int;


                    //CASE: Has DIM
                    if(declaration.getDim() != null) {
                        System.out.println("had Dim");
                      //Expr coerce to is color
                        if(declaration.getExpr().getCoerceTo() == Type.COLOR || declaration.getExpr().getCoerceTo() == Type.COLORFLOAT) {

                            //Expr type = int
                            if (declaration.getExpr().getType() == Type.INT) {
                                if (!(importStatements.contains("import edu.ufl.cise.plc.runtime.ColorTuple;\n"))) {
                                    importStatements = importStatements + "import edu.ufl.cise.plc.runtime.ColorTuple;\n";
                                }

                                sb.append("BufferedImage ").append(declaration.getName()).append(" = ");
                                int val = declaration.getExpr().getFirstToken().getIntValue();
                                if (val > 255) {
                                    val = 255;
                                }
                                else if (val < 0) {
                                    val = 0;
                                }
                                sb.append("ImageOps.setAllPixels(").append(declaration.getName());
                                sb.append(", ").append(val);
                                sb.rparen();
                            }

                            //else (not int)
                            else {
                                //BufferedImage B = new BufferedImage(x, y, Type.INT.RGB);
                                sb.append("BufferedImage ").append(declaration.getName()).append(" = new BufferedImage(");
                                declaration.getDim().getWidth().visit(this, sb);
                                sb.append(", ");
                                declaration.getDim().getHeight().visit(this, sb);
                                sb.append(", BufferedImage.TYPE_INT_RGB").rparen().semicolon().newline();


                                sb.append("for( int ").append(X);
                                sb.append(" = 0; ").append(X);
                                sb.append("<");
                                sb.append(declaration.getName()).append(".getWidth(); ").append(X);
                                sb.append("++)");

                                sb.newline().tab();
                                sb.append("for (int ").append(Y);
                                sb.append(" = 0; ").append(Y);
                                sb.append(" < ");
                                sb.append(declaration.getName()).append(".getHeight(); ").append(Y);
                                sb.append("++)");
                                sb.newline().tab();

                                sb.append("ImageOps.setColor(").append(declaration.getName()).append(", ");
                                sb.append(X).append(", ");
                                sb.append(Y).append(", ");

                                sb.lparen();
                                declaration.getExpr().visit(this, sb);
                                sb.rparen().rparen();

                            }
                        }

                        //Expr type == Color
                        else if (declaration.getExpr().getType() == Type.COLOR || declaration.getExpr().getType() == Type.COLORFLOAT) {
                            //BufferedImage B = new BufferedImage(x, y, Type.INT.RGB);
                            sb.append("BufferedImage ").append(declaration.getName()).append(" = new BufferedImage(");
                            declaration.getDim().getWidth().visit(this, sb);
                            sb.append(", ");
                            declaration.getDim().getHeight().visit(this, sb);
                            sb.append(", BufferedImage.TYPE_INT_RGB").rparen().semicolon().newline();


                            sb.append("for( int ").append(X);
                            sb.append(" = 0; ").append(X);
                            sb.append("<");
                            sb.append(declaration.getName()).append(".getWidth(); ").append(X);
                            sb.append("++)");

                            sb.newline().tab();
                            sb.append("for (int ").append(Y);
                            sb.append(" = 0; ").append(Y);
                            sb.append(" < ");
                            sb.append(declaration.getName()).append(".getHeight(); ").append(Y);
                            sb.append("++)");
                            sb.newline().tab();

                            sb.append("ImageOps.setColor(").append(declaration.getName()).append(", ");
                            sb.append(X).append(", ");
                            sb.append(Y).append(", ");

                            sb.lparen();
                            declaration.getExpr().visit(this, sb);
                            sb.rparen().rparen();

                        }
                        else if (declaration.getExpr().getType() == Type.IMAGE) {

                            sb.append("BufferedImage ").append(declaration.getName()).append(" = ImageOps.resize(");
                            declaration.getExpr().visit(this, sb);
                            sb.append(",");
                            declaration.getDim().getWidth().visit(this, sb);
                            sb.append(",");
                            declaration.getDim().getHeight().visit(this, sb);
                            sb.rparen();

                        }
                        else {
                            System.out.println("unhandled");
                            System.out.println(declaration.getExpr().getType().toString());
                        }

                    }

                    //CASE: no DIM
                    else if (declaration.getDim() == null)
                    {
                        if (declaration.getExpr().getType() == Type.IMAGE) {
                            if (declaration.getExpr().getFirstToken().getKind() == IToken.Kind.IDENT) {
                                sb.append("BufferedImage ").append(declaration.getName()).append(" = ImageOps.clone(");
                                declaration.getExpr().visit(this, sb);
                                sb.rparen();
                            }
                            else {
                                sb.append("BufferedImage ").append(declaration.getName()).append(" = ");
                                declaration.getExpr().visit(this, sb);
                            }
                        }

                        //handles when the RHS is an int coerced to a color
                        //Expr coerce to is color
                        if(declaration.getExpr().getCoerceTo() == Type.COLOR) {

                            //Expr type = int
                            if (declaration.getExpr().getType() == Type.INT) {
                                if (!(importStatements.contains("import edu.ufl.cise.plc.runtime.ColorTuple;\n"))) {
                                    importStatements = importStatements + "import edu.ufl.cise.plc.runtime.ColorTuple;\n";
                                }

                                sb.append("BufferedImage ").append(declaration.getName()).append(" = ");
                                int val = declaration.getExpr().getFirstToken().getIntValue();
                                if (val > 255) {
                                    val = 255;
                                } else if (val < 0) {
                                    val = 0;
                                }
                                sb.append("ImageOps.setAllPixels(").append(declaration.getName());
                                sb.append(", ").append(val);
                                sb.rparen();
                            }
                        }

                    }
                }
            }
            //case 2: has no init
            if (declaration.getExpr() == null) {
                //has dim
                if (declaration.getDim() != null) {
                    sb.append("BufferedImage ").append(declaration.getName()).append(" = ");
                    sb.append("new BufferedImage(").append(declaration.getDim().getWidth().getText()).append(", ").append(declaration.getDim().getHeight().getText());
                    sb.append(", BufferedImage.TYPE_INT_RGB").rparen();
                } else {
                    throw new Exception("This should not be happening...");
                }
                //else
            }

        }
        else if (declaration.getType() == Type.COLOR) {
            if (!(importStatements.contains("import edu.ufl.cise.plc.runtime.ColorTuple;\n"))) {
                importStatements = importStatements + "import edu.ufl.cise.plc.runtime.ColorTuple;\n";
            }
            if (!(importStatements.contains("import java.awt.Color;\n"))) {
                importStatements = importStatements + "import java.awt.Color;\n";
            }

            sb.append("ColorTuple ").append(declaration.getName());

            if (declaration.getExpr() != null) {
                if (declaration.getOp().getKind() == IToken.Kind.ASSIGN) {
                    sb.append(" = ");
                } else if (declaration.getOp().getKind() == IToken.Kind.LARROW) {
                    sb.append("<-");
                }

                if (declaration.getExpr().getCoerceTo() != null && declaration.getExpr().getType() != declaration.getType()) {
                    sb.lparen().append(getTypeName(declaration.getExpr().getCoerceTo())).rparen().space();
                }

                declaration.getExpr().visit(this, sb);
            }
        }


        //DEFAULT CASE, DO NOT TOUCH
        else {
            declaration.getNameDef().visit(this, sb);
            if (declaration.getExpr() != null) {

                //sb.append(" = ");
                if (declaration.getOp().getKind() == IToken.Kind.ASSIGN) {
                    sb.append(" = ");
                } else if (declaration.getOp().getKind() == IToken.Kind.LARROW) {
                    sb.append("<-");
                }

                if (declaration.getExpr().getCoerceTo() != null && declaration.getExpr().getType() != declaration.getType()) {
                    sb.lparen().append(getTypeName(declaration.getExpr().getCoerceTo())).rparen().space();
                }
                declaration.getExpr().visit(this, sb);
            }
        }
        return sb;
    }
    @Override
    public Object visitUnaryExprPostfix(UnaryExprPostfix unaryExprPostfix, Object arg) throws Exception{
        //TODO: Edit

        CodeGenStringBuilder sb = (CodeGenStringBuilder) arg;

        if (!(importStatements.contains("import java.awt.image.BufferedImage;\n"))) {
            importStatements = importStatements + "import java.awt.image.BufferedImage;\n";
        }
        if (!(importStatements.contains("import edu.ufl.cise.plc.runtime.ColorTuple;\n"))) {
            importStatements = importStatements + "import edu.ufl.cise.plc.runtime.ColorTuple;\n";
        }
        if (!(importStatements.contains("import java.awt.Color;\n"))) {
            importStatements = importStatements + "import java.awt.Color;\n";
        }

        //ColorTuple.unpack(a.getRGB(1,2));
        //unaryExprPostfix.getExpr().toString()
        sb.append("ColorTuple.unpack(");
        unaryExprPostfix.getExpr().visit(this, sb);
        sb.append(".getRGB(");
        sb.setLR(2);
        unaryExprPostfix.getSelector().visit(this, sb);
        sb.rparen().rparen();



        return sb;
    }

}
