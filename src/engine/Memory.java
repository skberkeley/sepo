package engine;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import engine.expr.BinaryExpr;
import engine.expr.BinaryOp;
import engine.expr.Expr;
import engine.expr.ExprUtil;
import engine.expr.LiteralExpr;
import engine.expr.SliceExpr;
import engine.expr.SymbolExpr;
import lombok.Builder;
import lombok.Value;
import org.sosy_lab.java_smt.api.Formula;
import org.sosy_lab.java_smt.api.SolverException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Value
public class Memory {
    /**
     * Each entry in memory is a slice of length 8 bits. Words are stored across four entries and so on.
     */
    HashMap<Formula, SliceExpr> entries;

    @Builder
    public Memory(HashMap<Formula, SliceExpr> entries) {
        this.entries = entries;
    }

    public Memory(Memory memory) {
        entries = new HashMap<>();
        for (Formula address : memory.entries.keySet()) {
            entries.put(address, memory.entries.get(address));
        }
    }

    private SliceExpr load(Expr address) throws InterruptedException, SolverException {
        if (ExprUtil.isConcrete(address)) {
            Formula simplified = SMTUtil.simplifyExpr(address);
            if (!entries.containsKey(simplified)) {
                SymbolExpr symbol = ExprUtil.generateSymbol();
                SliceExpr slice = SliceExpr.builder().e(symbol).start(0).end(7).build();
                this.store(simplified, slice);
            }
            return entries.get(simplified);
        }

        Formula addressFormula = SMTUtil.simplifyExpr(address);
        for (Formula key : entries.keySet()) {
            if (SMTUtil.checkFormulasEquiv(addressFormula, key)) {
                return entries.get(key);
            }
        }
        SymbolExpr symbol = ExprUtil.generateSymbol();
        SliceExpr slice = SliceExpr.builder().e(symbol).start(0).end(7).build();
        this.store(addressFormula, slice);
        return entries.get(addressFormula);
    }

    private void store(Expr address, SliceExpr value) throws InterruptedException, SolverException {
        store(SMTUtil.simplifyExpr(address), value);
    }

    private void store(Formula address, SliceExpr value) throws SolverException, InterruptedException {
        List<Formula> keysToRemove = new ArrayList<>();
        for (Formula key : entries.keySet()) {
            if (SMTUtil.checkFormulasCouldBeEqual(key, address)) {
                keysToRemove.add(key);
            }
        }
        for (Formula key : keysToRemove) {
             entries.remove(key);
        }
        entries.put(address, value);
    }

    public void storeDoubleWord(Expr address, Expr value) throws SolverException, InterruptedException {
        if (value.getLength() < 64) {
            throw new IllegalArgumentException("Value to store word in memory must be at least 32 bits long");
        }
        for (int i = 0; i < 8; i++) {
            SliceExpr slice = SliceExpr.builder().e(value).start(i * 8).end(i * 8 + 7).build();
            Expr sliceAddress = BinaryExpr.builder()
                    .e1(address)
                    .e2(LiteralExpr.builder().value(i).build())
                    .op(BinaryOp.ADD)
                    .build();
            store(sliceAddress, slice);
        }
    }
    public void storeWord(Expr address, Expr value) throws SolverException, InterruptedException {
        if (value.getLength() < 32) {
            throw new IllegalArgumentException("Value to store word in memory must be at least 32 bits long");
        }
        for (int i = 0; i < 4; i++) {
            SliceExpr slice = SliceExpr.builder().e(value).start(i * 8).end(i * 8 + 7).build();
            Expr sliceAddress = BinaryExpr.builder()
                    .e1(address)
                    .e2(LiteralExpr.builder().value(i).build())
                    .op(BinaryOp.ADD)
                    .build();
            store(sliceAddress, slice);
        }
    }
    public void storeHalfWord(Expr address, Expr value) throws SolverException, InterruptedException {
        if (value.getLength() < 16) {
            throw new IllegalArgumentException("Value to store half word in memory must be at least 16 bits long");
        }
        for (int i = 0; i < 2; i++) {
            SliceExpr slice = SliceExpr.builder().e(value).start(i * 8).end(i * 8 + 7).build();
            Expr sliceAddress = BinaryExpr.builder()
                    .e1(address)
                    .e2(LiteralExpr.builder().value(i).build())
                    .op(BinaryOp.ADD)
                    .build();
            store(sliceAddress, slice);
        }
    }
    public void storeByte(Expr address, Expr value) throws SolverException, InterruptedException {
        if (value.getLength() < 8) {
            throw new IllegalArgumentException("Value to store byte in memory must be at least 8 bits long");
        }
        SliceExpr slice = SliceExpr.builder().e(value).start(0).end(7).build();
        store(address, slice);
    }
    public Expr loadDoubleWord(Expr address) throws InterruptedException, SolverException {
        SliceExpr[] slices = new SliceExpr[8];
        for (int i = 0; i < 8; i++) {
            slices[i] = load(address);
            address = BinaryExpr.builder()
                    .e1(address)
                    .e2(LiteralExpr.builder().value(1).build())
                    .op(BinaryOp.ADD)
                    .build();
        }
        return ExprUtil.concatenateSlices(slices);
    }
    public Expr loadWord(Expr address) throws InterruptedException, SolverException {
        SliceExpr[] slices = new SliceExpr[4];
        for (int i = 0; i < 4; i++) {
            slices[i] = load(address);
            address = BinaryExpr.builder()
                    .e1(address)
                    .e2(LiteralExpr.builder().value(1).build())
                    .op(BinaryOp.ADD)
                    .build();
        }
        return ExprUtil.concatenateSlices(slices);
    }
    public Expr loadHalfWord(Expr address) throws InterruptedException, SolverException {
        SliceExpr slice1 = load(address);
        address = BinaryExpr.builder()
                .e1(address)
                .e2(LiteralExpr.builder().value(1).build())
                .op(BinaryOp.ADD)
                .build();
        SliceExpr slice2 = load(address);
        return ExprUtil.concatenateSlices(slice1, slice2);
    }
    public Expr loadByte(Expr address) throws InterruptedException, SolverException {
        return load(address);
    }

    public JsonElement toJson() {
        JsonObject obj = new JsonObject();
        for (Formula address : entries.keySet()) {
            obj.addProperty(address.toString(), entries.get(address).toString());
        }
        return obj;
    }

    public String toString() {
        return toJson().toString();
    }

    public int hashCode() {
        return entries.hashCode();
    }
}
