package expr;

import lombok.Builder;
import lombok.Value;

@Builder
@Value
public class LiteralExpr implements Expr {
    int value;

    @Override
    public boolean equals(Object o) {
        if (o instanceof LiteralExpr other) {
            return value == other.value;
        }
        return false;
    }

    public int getLength() {
        return Expr.LENGTH;
    }
}
