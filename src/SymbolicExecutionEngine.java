import expr.BinaryExpr;
import expr.BinaryOp;
import expr.Literal;

import java.util.HashMap;

public class SymbolicExecutionEngine {
    private State state;

    public SymbolicExecutionEngine() {
        this.state = State.builder()
                .registers(new HashMap<>())
                .memory(new HashMap<>())
                .programCounter(0)
                .build();
    }

    public State processInstruction(Instruction instruction) {
        switch (instruction.getOpcode()) {
            case ADDI:
                this.state.getRegisters().put(
                        instruction.getRd(),
                        BinaryExpr.builder()
                                .e1(this.state.getRegisters().get(instruction.getRs1()))
                                .e2(instruction.getImm())
                                .op(BinaryOp.ADD)
                                .build()
                );
            case ADD:
                this.state.getRegisters().put(
                        instruction.getRd(),
                        BinaryExpr.builder()
                                .e1(this.state.getRegisters().get(instruction.getRs1()))
                                .e2(this.state.getRegisters().get(instruction.getRs2()))
                                .op(BinaryOp.ADD)
                                .build()
                );
            case SUB:
                this.state.getRegisters().put(
                        instruction.getRd(),
                        BinaryExpr.builder()
                                .e1(this.state.getRegisters().get(instruction.getRs1()))
                                .e2(this.state.getRegisters().get(instruction.getRs2()))
                                .op(BinaryOp.SUB)
                                .build()
                );
            case AND:
                this.state.getRegisters().put(
                        instruction.getRd(),
                        BinaryExpr.builder()
                                .e1(this.state.getRegisters().get(instruction.getRs1()))
                                .e2(this.state.getRegisters().get(instruction.getRs2()))
                                .op(BinaryOp.AND)
                                .build()
                );
            case ANDI:
                this.state.getRegisters().put(
                        instruction.getRd(),
                        BinaryExpr.builder()
                                .e1(this.state.getRegisters().get(instruction.getRs1()))
                                .e2(instruction.getImm())
                                .op(BinaryOp.AND)
                                .build()
                );
            case OR:
                this.state.getRegisters().put(
                        instruction.getRd(),
                        BinaryExpr.builder()
                                .e1(this.state.getRegisters().get(instruction.getRs1()))
                                .e2(this.state.getRegisters().get(instruction.getRs2()))
                                .op(BinaryOp.OR)
                                .build()
                );
            case ORI:
                this.state.getRegisters().put(
                        instruction.getRd(),
                        BinaryExpr.builder()
                                .e1(this.state.getRegisters().get(instruction.getRs1()))
                                .e2(instruction.getImm())
                                .op(BinaryOp.OR)
                                .build()
                );
            case XOR:
                this.state.getRegisters().put(
                        instruction.getRd(),
                        BinaryExpr.builder()
                                .e1(this.state.getRegisters().get(instruction.getRs1()))
                                .e2(this.state.getRegisters().get(instruction.getRs2()))
                                .op(BinaryOp.XOR)
                                .build()
                );
            case XORI:
                this.state.getRegisters().put(
                        instruction.getRd(),
                        BinaryExpr.builder()
                                .e1(this.state.getRegisters().get(instruction.getRs1()))
                                .e2(instruction.getImm())
                                .op(BinaryOp.XOR)
                                .build()
                );
            case SLL:
                this.state.getRegisters().put(
                        instruction.getRd(),
                        BinaryExpr.builder()
                                .e1(this.state.getRegisters().get(instruction.getRd()))
                                .e2(this.state.getRegisters().get(instruction.getRs2()))
                                .op(BinaryOp.SLL)
                                .build()
                );
            case SLLI:
                this.state.getRegisters().put(
                        instruction.getRd(),
                        BinaryExpr.builder()
                                .e1(this.state.getRegisters().get(instruction.getRd()))
                                .e2(instruction.getImm())
                                .op(BinaryOp.SLL)
                                .build()
                );
            case SRL:
                this.state.getRegisters().put(
                        instruction.getRd(),
                        BinaryExpr.builder()
                                .e1(this.state.getRegisters().get(instruction.getRd()))
                                .e2(this.state.getRegisters().get(instruction.getRs2()))
                                .op(BinaryOp.SRL)
                                .build()
                );
            case SRLI:
                this.state.getRegisters().put(
                        instruction.getRd(),
                        BinaryExpr.builder()
                                .e1(this.state.getRegisters().get(instruction.getRd()))
                                .e2(instruction.getImm())
                                .op(BinaryOp.SRL)
                                .build()
                );
            case SRA:
                this.state.getRegisters().put(
                        instruction.getRd(),
                        BinaryExpr.builder()
                                .e1(this.state.getRegisters().get(instruction.getRd()))
                                .e2(this.state.getRegisters().get(instruction.getRs2()))
                                .op(BinaryOp.SRA)
                                .build()
                );
            case SRAI:
                this.state.getRegisters().put(
                        instruction.getRd(),
                        BinaryExpr.builder()
                                .e1(this.state.getRegisters().get(instruction.getRd()))
                                .e2(instruction.getImm())
                                .op(BinaryOp.SRA)
                                .build()
                );
            case SLT:
                this.state.getRegisters().put(
                        instruction.getRd(),
                        BinaryExpr.builder()
                                .e1(this.state.getRegisters().get(instruction.getRd()))
                                .e2(this.state.getRegisters().get(instruction.getRs2()))
                                .op(BinaryOp.SLT)
                                .build()
                );
            case SLTI:
                this.state.getRegisters().put(
                        instruction.getRd(),
                        BinaryExpr.builder()
                                .e1(this.state.getRegisters().get(instruction.getRd()))
                                .e2(instruction.getImm())
                                .op(BinaryOp.SLT)
                                .build()
                );
            case SLTU:
                this.state.getRegisters().put(
                        instruction.getRd(),
                        BinaryExpr.builder()
                                .e1(this.state.getRegisters().get(instruction.getRd()))
                                .e2(this.state.getRegisters().get(instruction.getRs2()))
                                .op(BinaryOp.SLTU)
                                .build()
                );
            case SLTIU:
                this.state.getRegisters().put(
                        instruction.getRd(),
                        BinaryExpr.builder()
                                .e1(this.state.getRegisters().get(instruction.getRd()))
                                .e2(instruction.getImm())
                                .op(BinaryOp.SLTU)
                                .build()
                );
            case LUI:
                this.state.getRegisters().put(
                        instruction.getRd(),
                        instruction.getImm()
                );
            case AUIPC:
                this.state.getRegisters().put(
                        instruction.getRd(),
                        BinaryExpr.builder()
                                .e1(
                                        Literal.builder().value(this.state.getProgramCounter()).build()
                                )
                                .e2(instruction.getImm())
                                .op(BinaryOp.ADD)
                                .build()
                );
            case NOP:
                break;
            case UNKNOWN:
                throw new RuntimeException("Unknown opcode");
        }
        return this.state;
    }
}
