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
