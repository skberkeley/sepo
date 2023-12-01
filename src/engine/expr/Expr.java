package engine.expr;

public interface Expr {
    int LENGTH = 64;
    @Override
    boolean equals(Object o);
    int getLength();
    @Override
    String toString();
}