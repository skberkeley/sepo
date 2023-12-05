package optimizer;

import instruction.Instruction;
import engine.State;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;

public class Optimizer {
    public static List<Instruction> optimize(List<Instruction> instructions, List<State> trace) {
        List<SimplifiedState> simplifiedTrace = trace.stream()
                .map(s -> {
                    try {
                        return SimplifiedState.builder().state(s).build();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                })
                .toList();
        return eliminateDeadCode(instructions, simplifiedTrace);
    }

    private static List<Instruction> eliminateDeadCode(List<Instruction> instructions, List<SimplifiedState> trace) {
        int biggestGapStart = -1;
        int biggestGapEnd = 0;

        for (int i = 0; i < trace.size(); i++) {
            SimplifiedState state = trace.get(i);
            for (int j = trace.size() - 1; j > i; j--) {
                if (state.equals(trace.get(j))) {
                    biggestGapStart = i;
                    biggestGapEnd = j;
                    break;
                }
            }
            if (biggestGapStart != -1) {
                break;
            }
        }

        // eliminate dead code in between biggestGapStart and biggestGapEnd
        if (biggestGapStart == -1) {
            return instructions;
        }
        return removeSublist(instructions, biggestGapStart, biggestGapEnd - 1);
    }

    private static <T> List<T> removeSublist(List<T> list, int start, int end) {
        // remove sublist from start to end, inclusive of both
        List<T> beforeList = list.subList(0, start);
        List<T> afterList = list.subList(end + 1, list.size());
        return Stream.concat(beforeList.stream(), afterList.stream()).toList();
    }
}
