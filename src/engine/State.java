package engine;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class State {
    private RegisterFile registers;
    private Memory memory;
    private int programCounter;
}
