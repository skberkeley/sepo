package engine;

import engine.expr.BinaryExpr;
import engine.expr.BinaryOp;
import engine.expr.Expr;
import engine.expr.ExprUtil;
import engine.expr.ExtensionExpr;
import engine.expr.LiteralExpr;
import engine.expr.SliceExpr;
import instruction.Instruction;
import lombok.Builder;
import org.sosy_lab.java_smt.api.SolverException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SymbolicExecutionEngine {
    private State state;

    public SymbolicExecutionEngine() {
        this.state = null;
    }

    public List<State> processInstructions(List<Instruction> instructions) {
        // Reset state
        this.state = State.builder()
                .registers(RegisterFile.builder().build())
                .memory(Memory.builder().entries(new HashMap<>()).build())
                .programCounter(0)
                .build();
        // process each instruction
        List<State> states = new ArrayList<>(instructions.size() + 1);
        for (Instruction instruction: instructions) {
            states.add(new State(this.state));
            try {
                this.state = this.processInstruction(instruction);
            } catch (SolverException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        states.add(new State(this.state));
        return states;
    }

    private State processInstruction(Instruction instruction) throws SolverException, InterruptedException {
        switch (instruction.getOpcode()) {
            case ADDI: {
                Expr imm = instruction.getImm();
                if (imm.getLength() < Expr.LENGTH) {
                    imm = ExtensionExpr.builder().e(imm).extensionLength(Expr.LENGTH - imm.getLength()).isSigned(true).build();
                }
                Expr sum = BinaryExpr.builder()
                        .e1(this.state.getRegisters().get(instruction.getRs1()))
                        .e2(imm)
                        .op(BinaryOp.ADD)
                        .build();
                Expr result = ExtensionExpr.builder()
                        .e(SliceExpr.builder().e(sum).start(0).end(31).build())
                        .extensionLength(Expr.LENGTH - 32)
                        .isSigned(true)
                        .build();
                this.state.getRegisters().put(
                        instruction.getRd(),
                        result
                );
                break;
            }
            case ADDIW: {
                Expr val = BinaryExpr.builder()
                        .e1(this.state.getRegisters().get(instruction.getRs1()))
                        .e2(instruction.getImm())
                        .op(BinaryOp.ADD)
                        .build();
                val = SliceExpr.builder().e(val).start(0).end(31).build();
                val = ExtensionExpr.builder().e(val).extensionLength(Expr.LENGTH - 32).isSigned(true).build();
                this.state.getRegisters().put(instruction.getRd(), val);
                break;
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
                break;
            case ADDW: {
                Expr val = BinaryExpr.builder()
                        .e1(this.state.getRegisters().get(instruction.getRs1()))
                        .e2(this.state.getRegisters().get(instruction.getRs2()))
                        .op(BinaryOp.ADD)
                        .build();
                val = SliceExpr.builder().e(val).start(0).end(31).build();
                val = ExtensionExpr.builder().e(val).extensionLength(Expr.LENGTH - 32).isSigned(true).build();
                this.state.getRegisters().put(instruction.getRd(), val);
                break;
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
                break;
            case SUBW: {
                Expr val = BinaryExpr.builder()
                        .e1(this.state.getRegisters().get(instruction.getRs1()))
                        .e2(this.state.getRegisters().get(instruction.getRs2()))
                        .op(BinaryOp.SUB)
                        .build();
                val = SliceExpr.builder().e(val).start(0).end(31).build();
                val = ExtensionExpr.builder().e(val).extensionLength(Expr.LENGTH - 32).isSigned(true).build();
                this.state.getRegisters().put(instruction.getRd(), val);
                break;
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
                break;
            case ANDI:
                this.state.getRegisters().put(
                        instruction.getRd(),
                        BinaryExpr.builder()
                                .e1(this.state.getRegisters().get(instruction.getRs1()))
                                .e2(instruction.getImm())
                                .op(BinaryOp.AND)
                                .build()
                );
                break;
            case OR:
                this.state.getRegisters().put(
                        instruction.getRd(),
                        BinaryExpr.builder()
                                .e1(this.state.getRegisters().get(instruction.getRs1()))
                                .e2(this.state.getRegisters().get(instruction.getRs2()))
                                .op(BinaryOp.OR)
                                .build()
                );
                break;
            case ORI:
                this.state.getRegisters().put(
                        instruction.getRd(),
                        BinaryExpr.builder()
                                .e1(this.state.getRegisters().get(instruction.getRs1()))
                                .e2(instruction.getImm())
                                .op(BinaryOp.OR)
                                .build()
                );
                break;
            case XOR:
                this.state.getRegisters().put(
                        instruction.getRd(),
                        BinaryExpr.builder()
                                .e1(this.state.getRegisters().get(instruction.getRs1()))
                                .e2(this.state.getRegisters().get(instruction.getRs2()))
                                .op(BinaryOp.XOR)
                                .build()
                );
                break;
            case XORI:
                this.state.getRegisters().put(
                        instruction.getRd(),
                        BinaryExpr.builder()
                                .e1(this.state.getRegisters().get(instruction.getRs1()))
                                .e2(instruction.getImm())
                                .op(BinaryOp.XOR)
                                .build()
                );
                break;
            case SLL:
                this.state.getRegisters().put(
                        instruction.getRd(),
                        BinaryExpr.builder()
                                .e1(this.state.getRegisters().get(instruction.getRs1()))
                                .e2(this.state.getRegisters().get(instruction.getRs2()))
                                .op(BinaryOp.SLL)
                                .build()
                );
                break;
            case SLLW: {
                Expr val = BinaryExpr.builder()
                        .e1(this.state.getRegisters().get(instruction.getRs1()))
                        .e2(this.state.getRegisters().get(instruction.getRs2()))
                        .op(BinaryOp.SLL)
                        .build();
                val = SliceExpr.builder().e(val).start(0).end(31).build();
                val = ExtensionExpr.builder().e(val).extensionLength(Expr.LENGTH - 32).isSigned(true).build();
                this.state.getRegisters().put(instruction.getRd(), val);
                break;
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
                break;
            case SLLIW: {
                Expr val = BinaryExpr.builder()
                        .e1(this.state.getRegisters().get(instruction.getRs1()))
                        .e2(instruction.getImm())
                        .op(BinaryOp.SLL)
                        .build();
                val = SliceExpr.builder().e(val).start(0).end(31).build();
                val = ExtensionExpr.builder().e(val).extensionLength(Expr.LENGTH - 32).isSigned(true).build();
                this.state.getRegisters().put(instruction.getRd(), val);
                break;
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
                break;
            case SRLW: {
                Expr val = BinaryExpr.builder()
                        .e1(this.state.getRegisters().get(instruction.getRs1()))
                        .e2(this.state.getRegisters().get(instruction.getRs2()))
                        .op(BinaryOp.SRL)
                        .build();
                val = SliceExpr.builder().e(val).start(0).end(31).build();
                val = ExtensionExpr.builder().e(val).extensionLength(Expr.LENGTH - 32).isSigned(true).build();
                this.state.getRegisters().put(instruction.getRd(), val);
                break;
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
                break;
            case SRLIW: {
                Expr val = BinaryExpr.builder()
                        .e1(this.state.getRegisters().get(instruction.getRs1()))
                        .e2(instruction.getImm())
                        .op(BinaryOp.SRL)
                        .build();
                val = SliceExpr.builder().e(val).start(0).end(31).build();
                val = ExtensionExpr.builder().e(val).extensionLength(Expr.LENGTH - 32).isSigned(true).build();
                this.state.getRegisters().put(instruction.getRd(), val);
                break;
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
                break;
            case SRAW: {
                Expr val = BinaryExpr.builder()
                        .e1(this.state.getRegisters().get(instruction.getRs1()))
                        .e2(this.state.getRegisters().get(instruction.getRs2()))
                        .op(BinaryOp.SRA)
                        .build();
                val = SliceExpr.builder().e(val).start(0).end(31).build();
                val = ExtensionExpr.builder().e(val).extensionLength(Expr.LENGTH - 32).isSigned(true).build();
                this.state.getRegisters().put(instruction.getRd(), val);
                break;
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
                break;
            case SRAIW: {
                Expr val = BinaryExpr.builder()
                        .e1(this.state.getRegisters().get(instruction.getRs1()))
                        .e2(instruction.getImm())
                        .op(BinaryOp.SRA)
                        .build();
                val = SliceExpr.builder().e(val).start(0).end(31).build();
                val = ExtensionExpr.builder().e(val).extensionLength(Expr.LENGTH - 32).isSigned(true).build();
                this.state.getRegisters().put(instruction.getRd(), val);
                break;
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
                break;
            case SLTI:
                this.state.getRegisters().put(
                        instruction.getRd(),
                        BinaryExpr.builder()
                                .e1(this.state.getRegisters().get(instruction.getRs1()))
                                .e2(instruction.getImm())
                                .op(BinaryOp.SLT)
                                .build()
                );
                break;
            case SLTU:
                this.state.getRegisters().put(
                        instruction.getRd(),
                        BinaryExpr.builder()
                                .e1(this.state.getRegisters().get(instruction.getRs1()))
                                .e2(this.state.getRegisters().get(instruction.getRs2()))
                                .op(BinaryOp.SLTU)
                                .build()
                );
                break;
            case SLTIU:
                this.state.getRegisters().put(
                        instruction.getRd(),
                        BinaryExpr.builder()
                                .e1(this.state.getRegisters().get(instruction.getRs1()))
                                .e2(instruction.getImm())
                                .op(BinaryOp.SLTU)
                                .build()
                );
                break;
            case LUI: {
                SliceExpr zeros = SliceExpr.builder().e(LiteralExpr.builder().value(0).build()).start(0).end(11).build();
                Expr lower32 = ExprUtil.concatenateSlices((SliceExpr) instruction.getImm(), zeros);
                this.state.getRegisters().put(
                    instruction.getRd(),
                    ExtensionExpr.builder().e(lower32).extensionLength(Expr.LENGTH - 32).isSigned(true).build()
                );
                break;
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
                break;
            }
            case LD: {
                Expr address = BinaryExpr.builder()
                        .e1(this.state.getRegisters().get(instruction.getRs1()))
                        .e2(instruction.getImm())
                        .op(BinaryOp.ADD)
                        .build();
                Expr value = this.state.getMemory().loadDoubleWord(address);
                this.state.getRegisters().put(instruction.getRd(), value);
                break;
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
                break;
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
                break;
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
                break;
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
                break;
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
                break;
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
                break;
            }
            case SD: {
                Expr address = BinaryExpr.builder()
                        .e1(this.state.getRegisters().get(instruction.getRs1()))
                        .e2(instruction.getImm())
                        .op(BinaryOp.ADD)
                        .build();
                Expr value = this.state.getRegisters().get(instruction.getRs2());
                this.state.getMemory().storeDoubleWord(address, value);
                break;
            }
            case SW: {
                Expr address = BinaryExpr.builder()
                        .e1(this.state.getRegisters().get(instruction.getRs1()))
                        .e2(instruction.getImm())
                        .op(BinaryOp.ADD)
                        .build();
                Expr value = this.state.getRegisters().get(instruction.getRs2());
                this.state.getMemory().storeWord(address, value);
                break;
            }
            case SH: {
                Expr address = BinaryExpr.builder()
                        .e1(this.state.getRegisters().get(instruction.getRs1()))
                        .e2(instruction.getImm())
                        .op(BinaryOp.ADD)
                        .build();
                Expr value = this.state.getRegisters().get(instruction.getRs2());
                this.state.getMemory().storeHalfWord(address, value);
                break;
            }
            case SB: {
                Expr address = BinaryExpr.builder()
                        .e1(this.state.getRegisters().get(instruction.getRs1()))
                        .e2(instruction.getImm())
                        .op(BinaryOp.ADD)
                        .build();
                Expr value = this.state.getRegisters().get(instruction.getRs2());
                this.state.getMemory().storeByte(address, value);
                break;
            }
            case NOP:
                break;
            case UNKNOWN:
                throw new RuntimeException("Unknown opcode");
        }
        return this.state;
    }
}
