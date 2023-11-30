package instruction;

public enum InstructionType {
    R,
    I,
    S,
    U,
    LOAD,
    PSEUDO;
    public static InstructionType getInstructionType(String opcode) {
        return switch (opcode) {
            case "add", "sub", "sll", "slt", "sltu", "xor", "srl", "sra", "or", "and", "addw", "subw", "sllw", "srlw", "sraw" -> InstructionType.R;
            case "addi", "slti", "sltiu", "xori", "ori", "andi", "slli", "srli", "srai",  "addiw", "slliw", "srliw", "sraiw" -> InstructionType.I;
            case "sb", "sh", "sw", "sd" -> InstructionType.S;
            case "lui", "auipc" -> InstructionType.U;
            case "lb", "lh", "lw", "lbu", "lhu", "lwu", "ld" -> InstructionType.LOAD;
            case "nop", "li", "mv", "not", "neg", "negw", "sext.w", "seqz", "snez", "sltz", "sgtz" -> InstructionType.PSEUDO;
            default -> null;
        };
    }
}
