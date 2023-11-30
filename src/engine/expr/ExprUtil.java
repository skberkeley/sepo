package engine.expr;

import java.util.ArrayList;
import java.util.List;

public class ExprUtil {
    private static int symbol_counter = 0;
    public static boolean isConcrete(Expr expr) {
        if (expr instanceof LiteralExpr literalExpr) {
            return true;
        } else if (expr instanceof SymbolExpr symbolExpr) {
            return false;
        } else if (expr instanceof SliceExpr sliceExpr) {
            return isConcrete(sliceExpr.getE());
        } else if (expr instanceof BinaryExpr binaryExpr) {
            return isConcrete(binaryExpr.getE1()) && isConcrete(binaryExpr.getE2());
        } else if (expr instanceof ConcatExpr concatExpr) {
            for (SliceExpr sliceExpr : concatExpr.getSlices()) {
                if (!isConcrete(sliceExpr)) {
                    return false;
                }
            }
            return true;
        } else if (expr instanceof ExtensionExpr extensionExpr) {
            return isConcrete(extensionExpr.getE());
        } else {
            throw new IllegalStateException("Unexpected value: " + expr);
        }
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
