package expr;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class SliceExpr implements Expr {
    Expr e;
    int start;
    int end;
}
