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
        return null;
    }
}
