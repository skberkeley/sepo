import expr.BinaryExpr;
import expr.BinaryOp;
import expr.Expr;
import expr.ExtensionExpr;
import expr.LiteralExpr;
import org.sosy_lab.java_smt.api.SolverException;

import java.util.HashMap;

public class SymbolicExecutionEngine {
    private State state;

    public SymbolicExecutionEngine() {
        this.state = State.builder()
                .registers(new HashMap<>())
                .memory(Memory.builder().entries(new HashMap<>()).build())
                .programCounter(0)
                .build();
    }

    public State processInstruction(Instruction instruction) throws SolverException, InterruptedException {
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
                                        LiteralExpr.builder().value(this.state.getProgramCounter()).build()
                                )
                                .e2(instruction.getImm())
                                .op(BinaryOp.ADD)
                                .build()
                );
            case LW: {
                Expr address = BinaryExpr.builder()
                        .e1(this.state.getRegisters().get(instruction.getRs1()))
                        .e2(instruction.getImm())
                        .op(BinaryOp.ADD)
                        .build();
                Expr value = this.state.getMemory().loadWord(address);
                this.state.getRegisters().put(instruction.getRd(), value);
            }
            case LH: {
                Expr address = BinaryExpr.builder()
                        .e1(this.state.getRegisters().get(instruction.getRs1()))
                        .e2(instruction.getImm())
                        .op(BinaryOp.ADD)
                        .build();
                Expr value = this.state.getMemory().loadHalfWord(address);
                Expr signExtended = ExtensionExpr.builder().e(value).extensionLength(16).isSigned(true).build();
                this.state.getRegisters().put(instruction.getRd(), signExtended);
            }
            case LHU: {
                Expr address = BinaryExpr.builder()
                        .e1(this.state.getRegisters().get(instruction.getRs1()))
                        .e2(instruction.getImm())
                        .op(BinaryOp.ADD)
                        .build();
                Expr value = this.state.getMemory().loadHalfWord(address);
                Expr signExtended = ExtensionExpr.builder().e(value).extensionLength(16).isSigned(false).build();
                this.state.getRegisters().put(instruction.getRd(), signExtended);
            }
            case LB: {
                Expr address = BinaryExpr.builder()
                        .e1(this.state.getRegisters().get(instruction.getRs1()))
                        .e2(instruction.getImm())
                        .op(BinaryOp.ADD)
                        .build();
                Expr value = this.state.getMemory().loadByte(address);
                Expr signExtended = ExtensionExpr.builder().e(value).extensionLength(24).isSigned(true).build();
                this.state.getRegisters().put(instruction.getRd(), signExtended);
            }
            case LBU: {
                Expr address = BinaryExpr.builder()
                        .e1(this.state.getRegisters().get(instruction.getRs1()))
                        .e2(instruction.getImm())
                        .op(BinaryOp.ADD)
                        .build();
                Expr value = this.state.getMemory().loadByte(address);
                Expr signExtended = ExtensionExpr.builder().e(value).extensionLength(24).isSigned(false).build();
                this.state.getRegisters().put(instruction.getRd(), signExtended);
            }
            case SW: {
                Expr address = BinaryExpr.builder()
                        .e1(this.state.getRegisters().get(instruction.getRs1()))
                        .e2(instruction.getImm())
                        .op(BinaryOp.ADD)
                        .build();
                Expr value = this.state.getRegisters().get(instruction.getRs2());
                this.state.getMemory().storeWord(address, value);
            }
            case SH: {
                Expr address = BinaryExpr.builder()
                        .e1(this.state.getRegisters().get(instruction.getRs1()))
                        .e2(instruction.getImm())
                        .op(BinaryOp.ADD)
                        .build();
                Expr value = this.state.getRegisters().get(instruction.getRs2());
                this.state.getMemory().storeHalfWord(address, value);
            }
            case SB: {
                Expr address = BinaryExpr.builder()
                        .e1(this.state.getRegisters().get(instruction.getRs1()))
                        .e2(instruction.getImm())
                        .op(BinaryOp.ADD)
                        .build();
                Expr value = this.state.getRegisters().get(instruction.getRs2());
                this.state.getMemory().storeByte(address, value);
            }
            case NOP:
                break;
            case UNKNOWN:
                throw new RuntimeException("Unknown opcode");
        }
        return this.state;
    }
}
