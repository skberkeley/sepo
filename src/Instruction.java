import expr.Literal;
import lombok.Builder;

@lombok.Value
@Builder
public class Instruction {
    Register rs1;
    Register rs2;
    Register rd;
    Literal imm;
    Opcode opcode;
}
