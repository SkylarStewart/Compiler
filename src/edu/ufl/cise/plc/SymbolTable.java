package edu.ufl.cise.plc;

import edu.ufl.cise.plc.ast.Declaration;
import java.util.HashMap;


//the SymbolTable is added to by the visit functions of VarDeclaration, NameDef, and NameDefWithDim

public class SymbolTable {

    HashMap<String,Declaration> table;

    SymbolTable() {
        this.table = new HashMap<>();
    }

    public boolean insert(String name, Declaration declaration) {
        System.out.println("added " + name + " to the symbol table");
        return (table.putIfAbsent(name,declaration) == null);
    }

    public Declaration search(String name) {
        return (table.get(name));
    }

    public void delete(String name) {
        System.out.println("deleted " + name + " from the symbol table");
        table.remove(name);
    }

}
