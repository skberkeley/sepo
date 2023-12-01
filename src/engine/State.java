package engine;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.Builder;
import lombok.Data;

@Data
public class State {
    private RegisterFile registers;
    private Memory memory;
    private int programCounter;

    @Builder
    public State(RegisterFile registers, Memory memory, int programCounter) {
        this.registers = registers;
        this.memory = memory;
        this.programCounter = programCounter;
    }

    public State(State state) {
        this.registers = new RegisterFile(state.registers);
        this.memory = new Memory(state.memory);
        this.programCounter = state.programCounter;
    }

    public JsonElement toJson() {
        JsonObject obj = new JsonObject();
        obj.add("registers", registers.toJson());
        obj.add("memory", memory.toJson());
        obj.addProperty("programCounter", programCounter);
        return obj;
    }

    public String toString() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(toJson());
    }
}
