package expr;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SliceExpr implements Expr {
    // start is lsb of slice, end is msb
    Expr e;
    int start;
    int end;

    @Override
    public boolean equals(Object o) {
        if (o instanceof SliceExpr other) {
            return (e.equals(other.getE())) &&
                    (start == other.getStart()) &&
                    (end == other.getEnd());
        }
        return false;
    }

    public int getLength() {
        return end - start + 1;
    }
}
