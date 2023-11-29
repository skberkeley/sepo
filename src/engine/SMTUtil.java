package engine;

import engine.expr.BinaryExpr;
import engine.expr.ConcatExpr;
import engine.expr.Expr;
import engine.expr.ExtensionExpr;
import engine.expr.LiteralExpr;
import engine.expr.SliceExpr;
import engine.expr.SymbolExpr;
import org.sosy_lab.common.configuration.InvalidConfigurationException;
import org.sosy_lab.java_smt.SolverContextFactory;
import org.sosy_lab.java_smt.api.BitvectorFormula;
import org.sosy_lab.java_smt.api.BitvectorFormulaManager;
import org.sosy_lab.java_smt.api.BooleanFormula;
import org.sosy_lab.java_smt.api.BooleanFormulaManager;
import org.sosy_lab.java_smt.api.Formula;
import org.sosy_lab.java_smt.api.FormulaManager;
import org.sosy_lab.java_smt.api.ProverEnvironment;
import org.sosy_lab.java_smt.api.SolverContext;
import org.sosy_lab.java_smt.api.SolverException;

public class SMTUtil {
    private static final SolverContext solverContext;

    static {
        try {
            solverContext = SolverContextFactory.createSolverContext(
                    SolverContextFactory.Solvers.Z3
            );
        } catch (InvalidConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    private static final FormulaManager formulaManager = solverContext.getFormulaManager();
    private static final BitvectorFormulaManager bvFormulaManager = formulaManager.getBitvectorFormulaManager();
    private static final BooleanFormulaManager boolFormulaManager = formulaManager.getBooleanFormulaManager();

    public static Formula convertExprToJavaSMTFormula(Expr expr) {
        return switch (expr) {
            case LiteralExpr literalExpr -> bvFormulaManager.makeBitvector(32, literalExpr.getValue());
            case SymbolExpr symbolExpr -> bvFormulaManager.makeVariable(32, symbolExpr.getName());
            case SliceExpr sliceExpr -> bvFormulaManager.extract(
                    (BitvectorFormula) convertExprToJavaSMTFormula(sliceExpr.getE()),
                    sliceExpr.getEnd(),
                    sliceExpr.getStart()
            );
            case ConcatExpr concatExpr -> {
                Formula f = null;
                for (SliceExpr sliceExpr : concatExpr.getSlices()) {
                    Formula sliceFormula = convertExprToJavaSMTFormula(sliceExpr);
                    if (f == null) {
                        f = sliceFormula;
                    } else {
                        f = bvFormulaManager.concat((BitvectorFormula) f, (BitvectorFormula) sliceFormula);
                    }
                }
                yield f;
            }
            case BinaryExpr binaryExpr -> {
                BitvectorFormula bve1 = (BitvectorFormula) convertExprToJavaSMTFormula(binaryExpr.getE1());
                BitvectorFormula bve2 = (BitvectorFormula) convertExprToJavaSMTFormula(binaryExpr.getE2());
                yield switch (binaryExpr.getOp()) {
                    case ADD -> bvFormulaManager.add(bve1, bve2);
                    case SUB -> bvFormulaManager.subtract(bve1, bve2);
                    case AND -> bvFormulaManager.and(bve1, bve2);
                    case OR -> bvFormulaManager.or(bve1, bve2);
                    case XOR -> bvFormulaManager.xor(bve1, bve2);
                    case SLL -> bvFormulaManager.shiftLeft(bve1, bve2);
                    case SRL -> bvFormulaManager.shiftRight(bve1, bve2, false);
                    case SRA -> bvFormulaManager.shiftRight(bve1, bve2, true);
                    case SLT -> bvFormulaManager.lessThan(bve1, bve2, true);
                    case SLTU -> bvFormulaManager.lessThan(bve1, bve2, false);
                };
            }
            case ExtensionExpr extensionExpr -> {
                BitvectorFormula bv = (BitvectorFormula) convertExprToJavaSMTFormula(extensionExpr.getE());
                yield bvFormulaManager.extend(bv, extensionExpr.getExtensionLength(), extensionExpr.isSigned());
            }
            default -> throw new IllegalStateException("Unexpected value: " + expr);
        };
    }

    public static Formula simplifyExpr(Expr expr) throws InterruptedException {
        return formulaManager.simplify(convertExprToJavaSMTFormula(expr));
    }

    public static boolean checkFormulasEquiv(Formula f1, Formula f2) throws InterruptedException, SolverException {
        BooleanFormula constraint = boolFormulaManager.not(
                bvFormulaManager.equal((BitvectorFormula) f1, (BitvectorFormula) f2)
        );
        ProverEnvironment prover = solverContext.newProverEnvironment();
        prover.addConstraint(constraint);
        return prover.isUnsat();
    }

    public static boolean checkFormulasCouldBeEqual(Formula f1, Formula f2) throws InterruptedException, SolverException {
        BooleanFormula constraint = bvFormulaManager.equal((BitvectorFormula) f1, (BitvectorFormula) f2);
        ProverEnvironment prover = solverContext.newProverEnvironment();
        prover.addConstraint(constraint);
        return !prover.isUnsat();
    }
}