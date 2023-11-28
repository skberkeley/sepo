package expr;

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
}
