import engine.SMTUtil;
import engine.State;
import engine.SymbolicExecutionEngine;
import engine.expr.BinaryExpr;
import engine.expr.BinaryOp;
import engine.expr.Expr;
import engine.expr.LiteralExpr;
import instruction.Instruction;
import optimizer.Optimizer;
import org.sosy_lab.java_smt.api.Formula;
import parser.Parser;
import parser.Segment;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.IntStream;

public class SEPO {
    public static void main(String[] args) throws IOException {
        Expr two = BinaryExpr.builder().e1(LiteralExpr.builder().value(1).build()).e2(LiteralExpr.builder().value(1).build()).op(BinaryOp.ADD).build();
        Formula formula;
        try {
            formula = SMTUtil.simplifyExpr(two);
        } catch (Exception e) {
            System.out.println("error");
            throw new RuntimeException(e);
        }

        // parse file
        Path file = FileSystems.getDefault().getPath("assembly", "1.s");
        byte[] fileArray = Files.readAllBytes(file);
        String fileString = new String(fileArray);
        // Instantiate parser
        Parser parser = new Parser();
        List<Segment> segments = parser.parse(fileString);
        // get traces
        SymbolicExecutionEngine engine = new SymbolicExecutionEngine();
        List<List<State>> traces = segments.stream().map(s -> engine.processInstructions(s.getInstructions())).toList();
        try {
            System.out.println(SMTUtil.checkFormulasEquiv(
                    SMTUtil.convertExprToJavaSMTFormula(traces.get(0).get(4).getRegisters().get("s0")),
                    SMTUtil.convertExprToJavaSMTFormula(traces.get(0).get(6).getRegisters().get("s0"))
            ));
        } catch (Exception e) {
            System.out.println("error");
            throw new RuntimeException(e);
        }
        // optimize
        List<Segment> newSegments = IntStream
                .range(0, segments.size())
                .mapToObj(i -> {
                    Segment segment = segments.get(i);
                    List<Instruction> instructions = Optimizer.optimize(segment.getInstructions(), traces.get(i));
                    return Segment.builder().instructions(instructions).prologue(segment.getPrologue()).build();
                })
                .toList();
        System.out.println("done");
    }
}
