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

    Expr load(Expr address) throws InterruptedException, SolverException {
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

    void store(Expr address, Expr value) {
        // TODO
    }
    private void store(Formula address, Expr value) {
        // TODO
    }
}
