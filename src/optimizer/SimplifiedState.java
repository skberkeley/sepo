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
import org.sosy_lab.java_smt.api.SolverException;

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

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof SimplifiedState other)) {
            return false;
        }

        if (registers.size() != other.registers.size()) {
            return false;
        }

        // check each formula in registers is equivalent
        for (String register : registers.keySet()) {
            if (register.equals("x0")) {
                continue;
            }
            try {
                Formula thisValue = registers.get(register);
                Formula otherValue = other.registers.get(register);
                if (thisValue.equals(otherValue)) {
                    continue;
                }
                if (otherValue == null || !SMTUtil.checkFormulasEquiv(registers.get(register), otherValue)) {
                    return false;
                }
            } catch (InterruptedException | SolverException e) {
                throw new RuntimeException(e);
            }
        }

        // check each formula in memory is equivalent
        if (memory.size() != other.memory.size()) {
            return false;
        }

        for (Formula address : memory.keySet()) {
            try {
                for (Formula otherAddress : other.memory.keySet()) {
                    if (SMTUtil.checkFormulasEquiv(address, otherAddress)) {
                        Formula otherValue = other.memory.get(otherAddress);
                        if (!SMTUtil.checkFormulasEquiv(memory.get(address), otherValue)) {
                            return false;
                        }
                    }
                }
            } catch (InterruptedException | SolverException e) {
                throw new RuntimeException(e);
            }
        }

        return true;
    }
}
