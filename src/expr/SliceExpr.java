package expr;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class SliceExpr implements Expr {
    // start is lsb of slice, end is msb
    Expr e;
    int start;
    int end;
}
