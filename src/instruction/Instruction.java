package instruction;

import engine.Opcode;
import engine.expr.Expr;
import lombok.Builder;

@lombok.Value
@Builder
public class Instruction {
    String rs1;
    String rs2;
    String rd;
    Expr imm;
    Opcode opcode;
}
