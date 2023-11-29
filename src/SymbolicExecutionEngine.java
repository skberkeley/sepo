import expr.BinaryExpr;
import expr.BinaryOp;
import expr.ConcatExpr;
import expr.Expr;
import expr.ExprUtil;
import expr.ExtensionExpr;
import expr.LiteralExpr;
import expr.SliceExpr;
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
            case ADDIW: {
                Expr val = BinaryExpr.builder()
                        .e1(this.state.getRegisters().get(instruction.getRs1()))
                        .e2(instruction.getImm())
                        .op(BinaryOp.ADD)
                        .build();
                val = SliceExpr.builder().e(val).start(0).end(31).build();
                val = ExtensionExpr.builder().e(val).extensionLength(Expr.LENGTH - 32).isSigned(true).build();
                this.state.getRegisters().put(instruction.getRd(), val);
            }
            case ADD:
                this.state.getRegisters().put(
                        instruction.getRd(),
                        BinaryExpr.builder()
                                .e1(this.state.getRegisters().get(instruction.getRs1()))
                                .e2(this.state.getRegisters().get(instruction.getRs2()))
                                .op(BinaryOp.ADD)
                                .build()
                );
            case ADDW: {
                Expr val = BinaryExpr.builder()
                        .e1(this.state.getRegisters().get(instruction.getRs1()))
                        .e2(this.state.getRegisters().get(instruction.getRs2()))
                        .op(BinaryOp.ADD)
                        .build();
                val = SliceExpr.builder().e(val).start(0).end(31).build();
                val = ExtensionExpr.builder().e(val).extensionLength(Expr.LENGTH - 32).isSigned(true).build();
                this.state.getRegisters().put(instruction.getRd(), val);
            }
            case SUB:
                this.state.getRegisters().put(
                        instruction.getRd(),
                        BinaryExpr.builder()
                                .e1(this.state.getRegisters().get(instruction.getRs1()))
                                .e2(this.state.getRegisters().get(instruction.getRs2()))
                                .op(BinaryOp.SUB)
                                .build()
                );
            case SUBW: {
                Expr val = BinaryExpr.builder()
                        .e1(this.state.getRegisters().get(instruction.getRs1()))
                        .e2(this.state.getRegisters().get(instruction.getRs2()))
                        .op(BinaryOp.SUB)
                        .build();
                val = SliceExpr.builder().e(val).start(0).end(31).build();
                val = ExtensionExpr.builder().e(val).extensionLength(Expr.LENGTH - 32).isSigned(true).build();
                this.state.getRegisters().put(instruction.getRd(), val);
            }
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
                                .e1(this.state.getRegisters().get(instruction.getRs1()))
                                .e2(this.state.getRegisters().get(instruction.getRs2()))
                                .op(BinaryOp.SLL)
                                .build()
                );
            case SLLW: {
                Expr val = BinaryExpr.builder()
                        .e1(this.state.getRegisters().get(instruction.getRs1()))
                        .e2(this.state.getRegisters().get(instruction.getRs2()))
                        .op(BinaryOp.SLL)
                        .build();
                val = SliceExpr.builder().e(val).start(0).end(31).build();
                val = ExtensionExpr.builder().e(val).extensionLength(Expr.LENGTH - 32).isSigned(true).build();
                this.state.getRegisters().put(instruction.getRd(), val);
            }
            case SLLI:
                this.state.getRegisters().put(
                        instruction.getRd(),
                        BinaryExpr.builder()
                                .e1(this.state.getRegisters().get(instruction.getRs1()))
                                .e2(instruction.getImm())
                                .op(BinaryOp.SLL)
                                .build()
                );
            case SLLIW: {
                Expr val = BinaryExpr.builder()
                        .e1(this.state.getRegisters().get(instruction.getRs1()))
                        .e2(instruction.getImm())
                        .op(BinaryOp.SLL)
                        .build();
                val = SliceExpr.builder().e(val).start(0).end(31).build();
                val = ExtensionExpr.builder().e(val).extensionLength(Expr.LENGTH - 32).isSigned(true).build();
                this.state.getRegisters().put(instruction.getRd(), val);
            }
            case SRL:
                this.state.getRegisters().put(
                        instruction.getRd(),
                        BinaryExpr.builder()
                                .e1(this.state.getRegisters().get(instruction.getRs1()))
                                .e2(this.state.getRegisters().get(instruction.getRs2()))
                                .op(BinaryOp.SRL)
                                .build()
                );
            case SRLW: {
                Expr val = BinaryExpr.builder()
                        .e1(this.state.getRegisters().get(instruction.getRs1()))
                        .e2(this.state.getRegisters().get(instruction.getRs2()))
                        .op(BinaryOp.SRL)
                        .build();
                val = SliceExpr.builder().e(val).start(0).end(31).build();
                val = ExtensionExpr.builder().e(val).extensionLength(Expr.LENGTH - 32).isSigned(true).build();
                this.state.getRegisters().put(instruction.getRd(), val);
            }
            case SRLI:
                this.state.getRegisters().put(
                        instruction.getRd(),
                        BinaryExpr.builder()
                                .e1(this.state.getRegisters().get(instruction.getRs1()))
                                .e2(instruction.getImm())
                                .op(BinaryOp.SRL)
                                .build()
                );
            case SRLIW: {
                Expr val = BinaryExpr.builder()
                        .e1(this.state.getRegisters().get(instruction.getRs1()))
                        .e2(instruction.getImm())
                        .op(BinaryOp.SRL)
                        .build();
                val = SliceExpr.builder().e(val).start(0).end(31).build();
                val = ExtensionExpr.builder().e(val).extensionLength(Expr.LENGTH - 32).isSigned(true).build();
                this.state.getRegisters().put(instruction.getRd(), val);
            }
            case SRA:
                this.state.getRegisters().put(
                        instruction.getRd(),
                        BinaryExpr.builder()
                                .e1(this.state.getRegisters().get(instruction.getRs1()))
                                .e2(this.state.getRegisters().get(instruction.getRs2()))
                                .op(BinaryOp.SRA)
                                .build()
                );
            case SRAW: {
                Expr val = BinaryExpr.builder()
                        .e1(this.state.getRegisters().get(instruction.getRs1()))
                        .e2(this.state.getRegisters().get(instruction.getRs2()))
                        .op(BinaryOp.SRA)
                        .build();
                val = SliceExpr.builder().e(val).start(0).end(31).build();
                val = ExtensionExpr.builder().e(val).extensionLength(Expr.LENGTH - 32).isSigned(true).build();
                this.state.getRegisters().put(instruction.getRd(), val);
            }
            case SRAI:
                this.state.getRegisters().put(
                        instruction.getRd(),
                        BinaryExpr.builder()
                                .e1(this.state.getRegisters().get(instruction.getRs1()))
                                .e2(instruction.getImm())
                                .op(BinaryOp.SRA)
                                .build()
                );
            case SRAIW: {
                Expr val = BinaryExpr.builder()
                        .e1(this.state.getRegisters().get(instruction.getRs1()))
                        .e2(instruction.getImm())
                        .op(BinaryOp.SRA)
                        .build();
                val = SliceExpr.builder().e(val).start(0).end(31).build();
                val = ExtensionExpr.builder().e(val).extensionLength(Expr.LENGTH - 32).isSigned(true).build();
                this.state.getRegisters().put(instruction.getRd(), val);
            }
            case SLT:
                this.state.getRegisters().put(
                        instruction.getRd(),
                        BinaryExpr.builder()
                                .e1(this.state.getRegisters().get(instruction.getRs1()))
                                .e2(this.state.getRegisters().get(instruction.getRs2()))
                                .op(BinaryOp.SLT)
                                .build()
                );
            case SLTI:
                this.state.getRegisters().put(
                        instruction.getRd(),
                        BinaryExpr.builder()
                                .e1(this.state.getRegisters().get(instruction.getRs1()))
                                .e2(instruction.getImm())
                                .op(BinaryOp.SLT)
                                .build()
                );
            case SLTU:
                this.state.getRegisters().put(
                        instruction.getRd(),
                        BinaryExpr.builder()
                                .e1(this.state.getRegisters().get(instruction.getRs1()))
                                .e2(this.state.getRegisters().get(instruction.getRs2()))
                                .op(BinaryOp.SLTU)
                                .build()
                );
            case SLTIU:
                this.state.getRegisters().put(
                        instruction.getRd(),
                        BinaryExpr.builder()
                                .e1(this.state.getRegisters().get(instruction.getRs1()))
                                .e2(instruction.getImm())
                                .op(BinaryOp.SLTU)
                                .build()
                );
            case LUI: {
                SliceExpr imm = SliceExpr.builder().e(instruction.getImm()).start(0).end(19).build();
                SliceExpr zeros = SliceExpr.builder().e(LiteralExpr.builder().value(0).build()).start(0).end(11).build();
                Expr lower32 = ExprUtil.concatenateSlices(imm, zeros);
                this.state.getRegisters().put(
                    instruction.getRd(),
                    ExtensionExpr.builder().e(lower32).extensionLength(Expr.LENGTH - 32).isSigned(true).build()
                );
            }
            case AUIPC: {
                SliceExpr imm = SliceExpr.builder().e(instruction.getImm()).start(0).end(19).build();
                SliceExpr zeros = SliceExpr.builder().e(LiteralExpr.builder().value(0).build()).start(0).end(11).build();
                Expr lower32 = ExprUtil.concatenateSlices(imm, zeros);
                this.state.getRegisters().put(
                        instruction.getRd(),
                        BinaryExpr.builder()
                            .e1(LiteralExpr.builder().value(this.state.getProgramCounter()).build())
                            .e2(
                                ExtensionExpr.builder()
                                    .e(lower32)
                                    .extensionLength(Expr.LENGTH - 32)
                                    .isSigned(true)
                                    .build()
                            )
                            .op(BinaryOp.ADD)
                            .build()
                );
            }
            case LD: {
                Expr address = BinaryExpr.builder()
                        .e1(this.state.getRegisters().get(instruction.getRs1()))
                        .e2(instruction.getImm())
                        .op(BinaryOp.ADD)
                        .build();
                Expr value = this.state.getMemory().loadDoubleWord(address);
                this.state.getRegisters().put(instruction.getRd(), value);
            }
            case LW: {
                Expr address = BinaryExpr.builder()
                        .e1(this.state.getRegisters().get(instruction.getRs1()))
                        .e2(instruction.getImm())
                        .op(BinaryOp.ADD)
                        .build();
                Expr value = this.state.getMemory().loadWord(address);
                Expr signExtended = ExtensionExpr.builder().e(value).extensionLength(Expr.LENGTH - 32).isSigned(true).build();
                this.state.getRegisters().put(instruction.getRd(), signExtended);
            }
            case LWU: {
                Expr address = BinaryExpr.builder()
                        .e1(this.state.getRegisters().get(instruction.getRs1()))
                        .e2(instruction.getImm())
                        .op(BinaryOp.ADD)
                        .build();
                Expr value = this.state.getMemory().loadHalfWord(address);
                Expr signExtended = ExtensionExpr.builder().e(value).extensionLength(Expr.LENGTH - 32).isSigned(false).build();
                this.state.getRegisters().put(instruction.getRd(), signExtended);
            }
            case LH: {
                Expr address = BinaryExpr.builder()
                        .e1(this.state.getRegisters().get(instruction.getRs1()))
                        .e2(instruction.getImm())
                        .op(BinaryOp.ADD)
                        .build();
                Expr value = this.state.getMemory().loadHalfWord(address);
                Expr signExtended = ExtensionExpr.builder().e(value).extensionLength(Expr.LENGTH - 16).isSigned(true).build();
                this.state.getRegisters().put(instruction.getRd(), signExtended);
            }
            case LHU: {
                Expr address = BinaryExpr.builder()
                        .e1(this.state.getRegisters().get(instruction.getRs1()))
                        .e2(instruction.getImm())
                        .op(BinaryOp.ADD)
                        .build();
                Expr value = this.state.getMemory().loadHalfWord(address);
                Expr signExtended = ExtensionExpr.builder().e(value).extensionLength(Expr.LENGTH - 16).isSigned(false).build();
                this.state.getRegisters().put(instruction.getRd(), signExtended);
            }
            case LB: {
                Expr address = BinaryExpr.builder()
                        .e1(this.state.getRegisters().get(instruction.getRs1()))
                        .e2(instruction.getImm())
                        .op(BinaryOp.ADD)
                        .build();
                Expr value = this.state.getMemory().loadByte(address);
                Expr signExtended = ExtensionExpr.builder().e(value).extensionLength(Expr.LENGTH - 8).isSigned(true).build();
                this.state.getRegisters().put(instruction.getRd(), signExtended);
            }
            case LBU: {
                Expr address = BinaryExpr.builder()
                        .e1(this.state.getRegisters().get(instruction.getRs1()))
                        .e2(instruction.getImm())
                        .op(BinaryOp.ADD)
                        .build();
                Expr value = this.state.getMemory().loadByte(address);
                Expr signExtended = ExtensionExpr.builder().e(value).extensionLength(Expr.LENGTH - 8).isSigned(false).build();
                this.state.getRegisters().put(instruction.getRd(), signExtended);
            }
            case SD: {
                Expr address = BinaryExpr.builder()
                        .e1(this.state.getRegisters().get(instruction.getRs1()))
                        .e2(instruction.getImm())
                        .op(BinaryOp.ADD)
                        .build();
                Expr value = this.state.getRegisters().get(instruction.getRs2());
                this.state.getMemory().storeDoubleWord(address, value);
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
