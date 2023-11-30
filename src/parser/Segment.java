package parser;

import instruction.Instruction;
import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class Segment {
    List<String> prologue;
    List<Instruction> instructions;
}
