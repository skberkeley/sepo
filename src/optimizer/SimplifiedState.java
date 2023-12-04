package optimizer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import engine.SMTUtil;
import engine.State;
import engine.expr.Expr;
import lombok.Builder;
import lombok.Data;
import org.sosy_lab.java_smt.api.Formula;

import java.util.HashMap;

@Data
public class SimplifiedState {
    HashMap<String, Formula> registers;
    HashMap<Formula, Formula> memory;

    @Builder
    public SimplifiedState(State state) throws InterruptedException {
        this.registers = new HashMap<>();
        for (String register : state.getRegisters().getRegisters().keySet()) {
            Expr exprValue = state.getRegisters().getRegisters().get(register);
            Formula simplified;
            try {
                simplified = SMTUtil.simplifyExpr(exprValue);
            } catch (Exception e) {
                System.out.println("error");
                throw e;
            }
            this.registers.put(register, simplified);
        }
        this.memory = new HashMap<>();
        for (Formula address : state.getMemory().getEntries().keySet()) {
            Expr value = state.getMemory().getEntries().get(address);
            this.memory.put(address, SMTUtil.simplifyExpr(value));
        }
    }

    @Override
    public int hashCode() {
        return registers.hashCode() ^ memory.hashCode();
    }

    public JsonElement toJson() {
        JsonObject obj = new JsonObject();
        JsonObject registersObj = new JsonObject();
        for (String register : registers.keySet()) {
            registersObj.addProperty(register, registers.get(register).toString());
        }
        obj.add("registers", registersObj);
        JsonObject memoryObj = new JsonObject();
        for (Formula address : memory.keySet()) {
            memoryObj.addProperty(address.toString(), memory.get(address).toString());
        }
        obj.add("memory", memoryObj);
        return obj;
    }

    public String toString() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(toJson());
    }
}
