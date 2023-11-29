import expr.BinaryExpr;
import expr.BinaryOp;
import expr.Expr;
import expr.ExprUtil;
import expr.LiteralExpr;
import expr.SliceExpr;
import expr.SymbolExpr;
import lombok.Builder;
import lombok.Value;
import org.sosy_lab.java_smt.api.Formula;
import org.sosy_lab.java_smt.api.SolverException;

import java.util.HashMap;

@Builder
@Value
public class Memory {
    /**
     * Each entry in memory is a slice of length 8 bits. Words are stored across four entries and so on.
     */
    HashMap<Formula, SliceExpr> entries;

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
        for (Formula key : entries.keySet()) {
            if (SMTUtil.checkFormulasCouldBeEqual(key, address)) {
                entries.remove(key);
            }
        }
        entries.put(address, value);
    }

    public void storeDoubleWord(Expr address, Expr value) throws SolverException, InterruptedException {
        if (value.getLength() < 64) {
            throw new IllegalArgumentException("Value to store word in memory must be at least 32 bits long");
        }
        storeWord(address, value);
        SliceExpr slice = SliceExpr.builder().e(value).start(32).end(63).build();
        address = BinaryExpr.builder()
                .e1(address)
                .e2(LiteralExpr.builder().value(4).build())
                .op(BinaryOp.ADD)
                .build();
        storeWord(address, slice);
    }
    public void storeWord(Expr address, Expr value) throws SolverException, InterruptedException {
        if (value.getLength() < 32) {
            throw new IllegalArgumentException("Value to store word in memory must be at least 32 bits long");
        }
        storeHalfWord(address, value);
        SliceExpr slice = SliceExpr.builder().e(value).start(16).end(31).build();
        address = BinaryExpr.builder()
                .e1(address)
                .e2(LiteralExpr.builder().value(2).build())
                .op(BinaryOp.ADD)
                .build();
        storeHalfWord(address, slice);
    }
    public void storeHalfWord(Expr address, Expr value) throws SolverException, InterruptedException {
        if (value.getLength() < 16) {
            throw new IllegalArgumentException("Value to store half word in memory must be at least 16 bits long");
        }
        storeByte(address, value);
        SliceExpr slice = SliceExpr.builder().e(value).start(8).end(15).build();
        address = BinaryExpr.builder()
                .e1(address)
                .e2(LiteralExpr.builder().value(1).build())
                .op(BinaryOp.ADD)
                .build();
        storeByte(address, slice);
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
}
