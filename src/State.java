import lombok.Builder;
import lombok.Data;

import java.util.HashMap;

@Data
@Builder
public class State {
    private HashMap<Register, Value> registers;
    private HashMap<Value, Value> memory;
    private int programCounter;
}
