package expr;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class SymbolExpr implements Expr {
    String name;

    @Override
    public boolean equals(Object o) {
        if (o instanceof SymbolExpr other) {
            return name.equals(other.name);
        }
        return false;
    }

    public int getLength() {
        return Expr.LENGTH;
    }
}
