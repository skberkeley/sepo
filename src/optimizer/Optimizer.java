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
        HashMap<Integer, Integer> hashToFirstIndex = new HashMap<>();
        HashMap<Integer, Integer> hashToLastIndex = new HashMap<>();
        for (int i = 0; i < trace.size(); i++) {
            SimplifiedState state = trace.get(i);
            // hash state
            int hash = state.hashCode();
            // add to hashToFirstIndex if not already there
            if (!hashToFirstIndex.containsKey(hash)) {
                hashToFirstIndex.put(hash, i);
            } else {
                // add to hashToLastIndex if in hashToFirstIndex
                hashToLastIndex.put(hash, i);
            }
        }

        if (hashToLastIndex.isEmpty()) {
            return instructions;
        }

        // if a hash occurs more than once, eliminate dead code in between
        int biggestGapStart = 0;
        int biggestGapEnd = 0;
        for (int hash : hashToLastIndex.keySet()) {
            int firstIndex = hashToFirstIndex.get(hash);
            int lastIndex = hashToLastIndex.get(hash);
            if (lastIndex - firstIndex > biggestGapEnd - biggestGapStart) {
                biggestGapStart = firstIndex;
                biggestGapEnd = lastIndex;
            }
        }

        List<Instruction> newInstructions = removeSublist(instructions, biggestGapStart, biggestGapEnd - 1);

        List<SimplifiedState> newTrace = removeSublist(trace, biggestGapStart, biggestGapEnd);

        return eliminateDeadCode(newInstructions, newTrace);
    }

    private static <T> List<T> removeSublist(List<T> list, int start, int end) {
        // remove sublist from start to end, inclusive of both
        List<T> beforeList = list.subList(0, start);
        List<T> afterList = list.subList(end + 1, list.size());
        return Stream.concat(beforeList.stream(), afterList.stream()).toList();
    }
}
