import expr.Expr;
import lombok.Builder;
import lombok.Data;

import java.util.HashMap;

@Data
@Builder
public class State {
    private HashMap<Register, Expr> registers;
    private HashMap<Expr, Expr> memory;
    private int programCounter;
}
