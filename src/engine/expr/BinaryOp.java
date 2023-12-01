package engine.expr;

public enum BinaryOp {
    ADD,
    SUB,
    AND,
    OR,
    XOR,
    SLL,
    SRL,
    SRA,
    SLT,
    SLTU;

    public boolean isCommutative() {
        return switch (this) {
            case ADD, AND, OR, XOR -> true;
            default -> false;
        };
    }

    @Override
    public String toString() {
        return switch (this) {
            case ADD -> "+";
            case SUB -> "-";
            case AND -> "&";
            case OR -> "|";
            case XOR -> "^";
            case SLL -> "<<";
            case SRL -> ">>";
            case SRA -> ">>>";
            case SLT -> "<";
            case SLTU -> "<u";
        };
    }
}
