package engine;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import engine.expr.Expr;
import engine.expr.ExprUtil;
import engine.expr.LiteralExpr;
import lombok.Builder;

import java.util.HashMap;

public class RegisterFile {
    HashMap<String, Expr> registers;

    @Builder
    public RegisterFile() {
        registers = new HashMap<>();
        registers.put("x0", LiteralExpr.builder().value(0).build());
    }

    public RegisterFile(RegisterFile registerFile) {
        registers = new HashMap<>();
        for (String register : registerFile.registers.keySet()) {
            registers.put(register, registerFile.registers.get(register));
        }
    }

    public Expr get(String register) {
        if (!registers.containsKey(register)) {
            registers.put(register, ExprUtil.generateSymbol());
        }
        return registers.get(register);
    }

    public void put(String register, Expr value) {
        if (register.equals("x0")) {
            return;
        }
        registers.put(register, value);
    }

    public JsonElement toJson() {
        JsonObject obj = new JsonObject();
        for (String register : registers.keySet()) {
            obj.addProperty(register, registers.get(register).toString());
        }
        return obj;
    }

    public String toString() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(toJson());
    }
}
