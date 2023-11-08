package expr;

public class ExprUtil {
    private static int symbol_counter = 0;
    public static boolean isConcrete(Expr expr) {
        return switch (expr) {
            case Literal literal -> true;
            case Symbol symbol -> false;
            case SliceExpr sliceExpr -> isConcrete(sliceExpr.getE());
            case BinaryExpr binaryExpr -> isConcrete(binaryExpr.getE1()) && isConcrete(binaryExpr.getE2());
            default -> throw new IllegalStateException("Unexpected value: " + expr);
        };
    }

    public static Expr generateSymbol() {
        return new Symbol("symbol_" + symbol_counter++);
    }
}
