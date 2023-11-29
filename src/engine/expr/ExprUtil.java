package engine.expr;

import java.util.ArrayList;
import java.util.List;

public class ExprUtil {
    private static int symbol_counter = 0;
    public static boolean isConcrete(Expr expr) {
        return switch (expr) {
            case LiteralExpr literalExpr -> true;
            case SymbolExpr symbolExpr -> false;
            case SliceExpr sliceExpr -> isConcrete(sliceExpr.getE());
            case BinaryExpr binaryExpr -> isConcrete(binaryExpr.getE1()) && isConcrete(binaryExpr.getE2());
            default -> throw new IllegalStateException("Unexpected value: " + expr);
        };
    }

    public static SymbolExpr generateSymbol() {
        return new SymbolExpr("symbol_" + symbol_counter++);
    }

    public static Expr concatenateSlices(SliceExpr ... sliceExprs) {
        List<SliceExpr> allSlices = new ArrayList<>();
        SliceExpr currentSlice = sliceExprs[0];
        for (int i = 1; i < sliceExprs.length; i++) {
            if (sliceExprs[i].getE().equals(currentSlice.getE()) && currentSlice.getEnd() + 1 == sliceExprs[i].getStart()) {
                currentSlice.setEnd(sliceExprs[i].getEnd());
            } else {
                allSlices.add(currentSlice);
                currentSlice = sliceExprs[i];
            }
        }
        allSlices.add(currentSlice);
        if (allSlices.size() == 1) {
            return allSlices.get(0);
        } else {
            return new ConcatExpr(allSlices);
        }
    }
}
