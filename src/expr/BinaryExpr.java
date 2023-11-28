package expr;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class BinaryExpr implements Expr {
    Expr e1;
    Expr e2;
    BinaryOp op;

    @Override
    public boolean equals(Object o) {
        if (o instanceof BinaryExpr other) {
            if (!other.getOp().equals(op)) {
                return false;
            } else if (op.isCommutative()) {
                return (e1.equals(other.e1) && e2.equals(other.e2)) ||
                        (e1.equals(other.e2) && e2.equals(other.e1));
            } else {
                return e1.equals(other.e1) && e2.equals(other.e2);
            }
        }
        return false;
    }
}
