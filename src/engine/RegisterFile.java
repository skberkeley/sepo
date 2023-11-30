package engine;

import engine.expr.Expr;
import engine.expr.ExprUtil;
import engine.expr.LiteralExpr;
import lombok.Builder;
import java.util.HashMap;

public class RegisterFile {
    HashMap<String, Expr> registers;

    @Builder
    public RegisterFile() {
        registers = new HashMap<>();
        registers.put("x0", LiteralExpr.builder().value(0).build());
    }

    public Expr get(String register) {
        if (!registers.containsKey(register)) {
            registers.put(register, ExprUtil.generateSymbol());
        }
        return registers.get(register);
    }

    public void put(String register, Expr value) {
        if (register.equals("x0")) {
            return;
        }
        registers.put(register, value);
    }
}
