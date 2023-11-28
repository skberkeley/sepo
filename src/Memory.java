import expr.Expr;
import expr.ExprUtil;
import lombok.Builder;
import lombok.Value;
import org.sosy_lab.java_smt.api.Formula;
import org.sosy_lab.java_smt.api.SolverException;

import java.util.HashMap;

@Builder
@Value
public class Memory {
    HashMap<Formula, Expr> entries;

    public Expr load(Expr address) throws InterruptedException, SolverException {
        if (ExprUtil.isConcrete(address)) {
            Formula simplified = SMTUtil.simplifyExpr(address);
            if (!entries.containsKey(simplified)) {
                this.store(simplified, ExprUtil.generateSymbol());
            }
            return entries.get(simplified);
        }

        Formula addressFormula = SMTUtil.simplifyExpr(address);
        for (Formula key : entries.keySet()) {
            if (SMTUtil.checkFormulasEquiv(addressFormula, key)) {
                return entries.get(key);
            }
        }
        this.store(addressFormula, ExprUtil.generateSymbol());
        return entries.get(addressFormula);
    }

    public void store(Expr address, Expr value) throws InterruptedException, SolverException {
        store(SMTUtil.simplifyExpr(address), value);
    }
    private void store(Formula address, Expr value) throws SolverException, InterruptedException {
        for (Formula key : entries.keySet()) {
            if (SMTUtil.checkFormulasCouldBeEqual(key, address)) {
                entries.remove(key);
            }
        }
        entries.put(address, value);
    }
}
