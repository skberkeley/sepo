import expr.LiteralExpr;
import lombok.Builder;

@lombok.Value
@Builder
public class Instruction {
    Register rs1;
    Register rs2;
    Register rd;
    LiteralExpr imm;
    Opcode opcode;
}
