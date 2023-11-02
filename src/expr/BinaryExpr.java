package expr;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class BinaryExpr implements Expr {
    Expr e1;
    Expr e2;
    BinaryOp op;
}
