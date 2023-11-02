package expr;

import lombok.Builder;
import lombok.Value;

@Builder
@Value
public class Literal implements Expr {
    int value;
}
