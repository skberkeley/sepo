package engine;

import engine.expr.Expr;
import lombok.Builder;
import lombok.Data;

import java.util.HashMap;

@Data
@Builder
public class State {
    private HashMap<Register, Expr> registers;
    private Memory memory;
    private int programCounter;
}