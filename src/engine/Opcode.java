package engine;

public enum Opcode {
    ADDI,
    ADDIW,
    ADD,
    ADDW,
    SUB,
    SUBW,
    AND,
    ANDI,
    OR,
    ORI,
    XOR,
    XORI,
    SLL,
    SLLW,
    SLLI,
    SLLIW,
    SRL,
    SRLW,
    SRLI,
    SRLIW,
    SRA,
    SRAW,
    SRAI,
    SRAIW,
    SLT,
    SLTI,
    SLTU,
    SLTIU,
    LD,
    LW,
    LWU,
    LB,
    LBU,
    LH,
    LHU,
    SD,
    SW,
    SB,
    SH,
    LUI,
    AUIPC,
    NOP,
    UNKNOWN;

    public static Opcode fromString(String s) {
        return switch (s) {
            case "addi" -> ADDI;
            case "addiw" -> ADDIW;
            case "add" -> ADD;
            case "addw" -> ADDW;
            case "sub" -> SUB;
            case "subw" -> SUBW;
            case "and" -> AND;
            case "andi" -> ANDI;
            case "or" -> OR;
            case "ori" -> ORI;
            case "xor" -> XOR;
            case "xori" -> XORI;
            case "sll" -> SLL;
            case "sllw" -> SLLW;
            case "slli" -> SLLI;
            case "slliw" -> SLLIW;
            case "srl" -> SRL;
            case "srlw" -> SRLW;
            case "srli" -> SRLI;
            case "srliw" -> SRLIW;
            case "sra" -> SRA;
            case "sraw" -> SRAW;
            case "srai" -> SRAI;
            case "sraiw" -> SRAIW;
            case "slt" -> SLT;
            case "slti" -> SLTI;
            case "sltu" -> SLTU;
            case "sltiu" -> SLTIU;
            case "ld" -> LD;
            case "lw" -> LW;
            case "lwu" -> LWU;
            case "lb" -> LB;
            case "lbu" -> LBU;
            case "lh" -> LH;
            case "lhu" -> LHU;
            case "sd" -> SD;
            case "sw" -> SW;
            case "sb" -> SB;
            case "sh" -> SH;
            case "lui" -> LUI;
            case "auipc" -> AUIPC;
            case "nop" -> NOP;
            default -> UNKNOWN;
        };
    }
}
