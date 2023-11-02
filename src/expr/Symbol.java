package expr;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class Symbol implements Expr {
    String name;
}
