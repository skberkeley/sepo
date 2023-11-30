package parser;

import engine.Opcode;
import engine.expr.Expr;
import engine.expr.ExprUtil;
import engine.expr.LiteralExpr;
import engine.expr.SliceExpr;
import engine.expr.SymbolExpr;
import instruction.Instruction;
import instruction.InstructionType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {
    private List<String> lines;
    private HashMap<String, SymbolExpr> seenConstants;
    private List<Instruction> currInstructions;

    public Parser() {
        seenConstants = new HashMap<>();
        currInstructions = new ArrayList<>();
    }

    public List<Segment> parse(String input) {
        seenConstants = new HashMap<>();
        currInstructions = new ArrayList<>();
        // Split on newline
        lines = new ArrayList<>(Arrays.asList(input.split(System.lineSeparator())));
        // While there's lines remaining, parse a segment out of input
        List<Segment> segments = new ArrayList<>();
        while (lines.size() > 0) {
            segments.add(parseSegment());
        }
        return segments;
    }

    private Segment parseSegment() {
        currInstructions = new ArrayList<>();
        // here, lines is still not empty
        List<String> prologue = parsePrologue();
        // here, lines could be empty
        List<Instruction> instructions;
        if (lines.size() == 0) {
            instructions = List.of();
        } else {
            instructions = parseInstructions();
        }
        return Segment.builder().prologue(prologue).instructions(instructions).build();
    }

    private List<String> parsePrologue() {
        List<String> prologue = new ArrayList<>();
        while (lines.size() > 0 && parseInstruction(lines.get(0)) == null) {
            prologue.add(lines.remove(0));
        }
        currInstructions = new ArrayList<>();
        return prologue;
    }

    private List<Instruction> parseInstructions() {
        while (lines.size() > 0) {
            Instruction instruction = parseInstruction(lines.get(0));
            if (instruction == null) {
                break;
            }
            lines.remove(0);
        }
        return currInstructions;
    }

    /** returns first instruction added to currInstructions */
    private Instruction parseInstruction(String line) {
        Pattern opcodePattern = Pattern.compile("^\\s([a-z]+(?:.w)?)");
        // if line doesn't match with opcode pattern, return null
        Matcher opcodeMatcher = opcodePattern.matcher(line);
        if (!opcodeMatcher.find()) {
            return null;
        }
        String opcode = opcodeMatcher.group(1);
        // deduce instruction type from opcode
        InstructionType instructionType = InstructionType.getInstructionType(opcode);
        // corresponds to non-straight line instructions
        if (instructionType == null) {
            return null;
        }
        // parse instruction based on instruction type
        // each method adds to currInstructions and returns the first instruction added to currInstructions
        return switch (instructionType) {
            case R -> parseRInstruction(line);
            case I -> parseIInstruction(line);
            case S -> parseSInstruction(line);
            case U -> parseUInstruction(line);
            case LOAD -> parseLoadInstruction(line);
            case PSEUDO -> parsePseudoInstruction(line, opcode);
        };
    }

    private Instruction parseLoadInstruction(String line) {
        Pattern loadPattern = Pattern.compile("^\\s([a-z]+(?:.w)?)\\s([a-z0-9]+),(\\S+)\\(([a-z0-9]+)\\)$");
        Matcher loadMatcher = loadPattern.matcher(line);
        if (!loadMatcher.find()) {
            throw new RuntimeException("Load instruction does not match regex");
        }
        Opcode opcode = Opcode.fromString(loadMatcher.group(1));
        String rd = loadMatcher.group(2);
        Expr imm = parseImmediate(loadMatcher.group(3));
        String rs1 = loadMatcher.group(4);
        Instruction instr = Instruction.builder().opcode(opcode).rs1(rs1).rd(rd).imm(imm).build();
        currInstructions.add(instr);
        return instr;
    }

    private Instruction parsePseudoInstruction(String line, String opcode) {
        switch (opcode) {
            case "nop" -> {
                Instruction instr = Instruction.builder().opcode(Opcode.ADDI).rd("x0").rs1("x0").imm(LiteralExpr.builder().value(0).build()).build();
                currInstructions.add(instr);
                return instr;
            }
            case "li" -> {
                Pattern liPattern = Pattern.compile("^\\sli\\s([a-z0-9]+),(\\S+)$");
                Matcher liMatcher = liPattern.matcher(line);
                if (!liMatcher.find()) {
                    throw new RuntimeException("li instruction does not match regex");
                }
                String rd = liMatcher.group(1);
                Expr imm = parseImmediate(liMatcher.group(2));
                Instruction instr1 = Instruction.builder()
                        .opcode(Opcode.LUI)
                        .rd(rd)
                        .imm(SliceExpr.builder().e(imm).start(12).end(31).build())
                        .build();
                Instruction instr2 = Instruction.builder()
                        .opcode(Opcode.ADDI)
                        .rd(rd)
                        .rs1(rd)
                        .imm(SliceExpr.builder().e(imm).start(0).end(11).build())
                        .build();
                currInstructions.add(instr1);
                currInstructions.add(instr2);
                return instr2;
            }
            default -> {
                Pattern pseudoPattern = Pattern.compile("^\\s[a-z\\.]+\\s([a-z0-9]+),([a-z0-9]+)$");
                Matcher pseudoMatcher = pseudoPattern.matcher(line);
                if (!pseudoMatcher.find()) {
                    throw new RuntimeException("pseudo instruction does not match regex");
                }
                String rd = pseudoMatcher.group(1);
                String rs = pseudoMatcher.group(2);
                Instruction instr = switch (opcode) {
                    case "mv" ->
                        Instruction.builder()
                                .opcode(Opcode.ADDI)
                                .rd(rd)
                                .rs1(rs)
                                .imm(LiteralExpr.builder().value(0).build())
                                .build();
                    case "not" -> Instruction.builder().opcode(Opcode.XORI).rd(rd).rs1(rs).imm(LiteralExpr.builder().value(-1).build()).build();
                    case "neg" ->
                        // Assuming compiler outputs x0 instead of zero
                        Instruction.builder().opcode(Opcode.SUB).rd(rd).rs1("x0").rs2(rs).build();
                    case "negw" ->
                        // Assuming compiler outputs x0 instead of zero
                        Instruction.builder().opcode(Opcode.SUBW).rd(rd).rs1("x0").rs2(rs).build();
                    case "sext.w" -> Instruction.builder().opcode(Opcode.ADDIW).rd(rd).rs1(rs).imm(LiteralExpr.builder().value(0).build()).build();
                    case "seqz" -> Instruction.builder().opcode(Opcode.SLTIU).rd(rd).rs1(rs).imm(LiteralExpr.builder().value(1).build()).build();
                    case "snez" -> Instruction.builder().opcode(Opcode.SLTU).rd(rd).rs1("x0").rs2(rs).build();
                    case "sltz" -> Instruction.builder().opcode(Opcode.SLT).rd(rd).rs1(rs).rs2("x0").build();
                    case "sgtz" -> Instruction.builder().opcode(Opcode.SLT).rd(rd).rs1("x0").rs2(rs).build();
                    default -> throw new RuntimeException("Unknown pseudo instruction");
                };
                currInstructions.add(instr);
                return instr;
            }
        }
    }

    private Instruction parseUInstruction(String line) {
        Pattern uPattern = Pattern.compile("^\\s([a-z]+(?:.w)?)\\s([a-z0-9]+),(\\S+)$");
        Matcher uMatcher = uPattern.matcher(line);
        if (!uMatcher.find()) {
            throw new RuntimeException("U instruction does not match regex");
        }
        Opcode opcode = Opcode.fromString(uMatcher.group(1));
        String rd = uMatcher.group(2);
        Expr imm = parseImmediate(uMatcher.group(3));
        Instruction instr = Instruction.builder().opcode(opcode).rd(rd).imm(imm).build();
        currInstructions.add(instr);
        return instr;
    }

    private Instruction parseSInstruction(String line) {
        Pattern sPattern = Pattern.compile("^\\s([a-z]+(?:.w)?)\\s([a-z0-9]+),(\\S+)\\(([a-z0-9]+)\\)$");
        Matcher sMatcher = sPattern.matcher(line);
        if (!sMatcher.find()) {
            throw new RuntimeException("S instruction does not match regex");
        }
        Opcode opcode = Opcode.fromString(sMatcher.group(1));
        String rs1 = sMatcher.group(2);
        Expr imm = parseImmediate(sMatcher.group(3));
        String rs2 = sMatcher.group(4);
        Instruction instr = Instruction.builder().opcode(opcode).rs1(rs1).rs2(rs2).imm(imm).build();
        currInstructions.add(instr);
        return instr;
    }

    private Instruction parseIInstruction(String line) {
        Pattern iPattern = Pattern.compile("^\\s([a-z]+(?:.w)?)\\s([a-z0-9]+),([a-z0-9]+),(\\S+)$");
        Matcher iMatcher = iPattern.matcher(line);
        if (!iMatcher.find()) {
            throw new RuntimeException("I instruction does not match regex");
        }
        Opcode opcode = Opcode.fromString(iMatcher.group(1));
        String rd = iMatcher.group(2);
        String rs1 = iMatcher.group(3);
        Expr imm = parseImmediate(iMatcher.group(4));
        Instruction instr = Instruction.builder().opcode(opcode).rd(rd).rs1(rs1).imm(imm).build();
        currInstructions.add(instr);
        return instr;
    }

    private Instruction parseRInstruction(String line) {
        Pattern rPattern = Pattern.compile("^\\s([a-z]+(?:.w)?)\\s([a-z0-9]+),([a-z0-9]+),([a-z0-9]+)$");
        Matcher rMatcher = rPattern.matcher(line);
        if (!rMatcher.find()) {
            throw new RuntimeException("R instruction does not match regex");
        }
        Opcode opcode = Opcode.fromString(rMatcher.group(1));
        String rd = rMatcher.group(2);
        String rs1 = rMatcher.group(3);
        String rs2 = rMatcher.group(4);
        Instruction instr = Instruction.builder().opcode(opcode).rd(rd).rs1(rs1).rs2(rs2).build();
        currInstructions.add(instr);
        return instr;
    }

    private Expr parseImmediate(String imm) {
        Pattern assemblerDirPattern = Pattern.compile("(%hi|%lo)\\((\\S+)\\)");
        Matcher assemblerDirMatcher = assemblerDirPattern.matcher(imm);
        if (assemblerDirMatcher.find()) {
            String constantName = assemblerDirMatcher.group(2);
            SymbolExpr symbolExpr;
            if (seenConstants.containsKey(constantName)) {
                symbolExpr = seenConstants.get(constantName);
            } else {
                symbolExpr = ExprUtil.generateSymbol();
                seenConstants.put(constantName, symbolExpr);
            }
            if (assemblerDirMatcher.group(1).equals("%hi")) {
                return SliceExpr.builder().e(symbolExpr).start(12).end(31).build();
            } else {
                return SliceExpr.builder().e(symbolExpr).start(0).end(11).build();
            }
        } else {
            return LiteralExpr.builder().value(Integer.parseInt(imm)).build();
        }
    }
}
