package engine.expr;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ExtensionExpr implements Expr {
    Expr e;
    int extensionLength;
    boolean isSigned;

    @Override
    public int getLength() {
        return e.getLength() + extensionLength;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof ExtensionExpr other) {
            return (e.equals(other.getE())) &&
                    (extensionLength == other.getExtensionLength()) &&
                    (isSigned == other.isSigned());
        }
        return false;
    }
}
