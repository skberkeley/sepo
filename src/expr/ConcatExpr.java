package expr;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
public class ConcatExpr implements Expr {
    /** Expression representing the concatenation of two slice expressions. e1 is the expression for the lower bits of
     * the concatenation, and e2 is the expression for the higher bits of the concatenation.
    */
    List<SliceExpr> slices;

    @Builder
    public ConcatExpr(List<SliceExpr> slices) {
        int totalLength = 0;
        for (SliceExpr slice : slices) {
            totalLength += slice.getEnd() - slice.getStart() + 1;
        }
        if (totalLength > 32) {
            throw new IllegalArgumentException("Concatenation of expressions would create a value with width" +
                    " greater than  32 bits");
        }
        this.slices = slices;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof ConcatExpr other) {
            return slices.equals(other.slices);
        }
        return false;
    }
}
