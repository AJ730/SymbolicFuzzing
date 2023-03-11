import java.io.BufferedReader;
import java.io.InputStreamReader;
import nl.tudelft.instrumentation.symbolic.*;
import nl.tudelft.instrumentation.runner.CallableTraceRunner;

public class ProblemPin implements CallableTraceRunner<Void> {

    static BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));

    private String[] inputs = { "T", "Z", "O", "N", "W", "E", "D", "R", "ENTER", "C" };

    public boolean cf = true;

    public int digit = 0;

    public boolean open = false;

    public void errorCheck() {
        PathTracker.myIf(my_open, open, 14);
        if (open) {
            PathTracker.myAssign(my_cf, PathTracker.tempVar(false), "=");
            cf = false;
            Errors.__VERIFIER_error(0);
        }
    }

    public void calculateOutput(String input, MyVar my_input) {
        PathTracker.myAssign(my_cf, PathTracker.tempVar(true), "=");
        cf = true;
        PathTracker.myIf(PathTracker.binaryExpr(PathTracker.binaryExpr(my_cf, PathTracker.binaryExpr(my_digit, PathTracker.tempVar(0), "=="), "&&"), PathTracker.equals(PathTracker.tempVar("Z"), my_input), "&&"), cf && digit == 0 && input.equals("Z"), 22);
        if (cf && digit == 0 && input.equals("Z")) {
            PathTracker.myAssign(my_digit, PathTracker.tempVar(1), "+=");
            digit += 1;
            PathTracker.myAssign(my_cf, PathTracker.tempVar(false), "=");
            cf = false;
            PathTracker.output("N");
        }
        PathTracker.myIf(PathTracker.binaryExpr(PathTracker.binaryExpr(my_cf, PathTracker.binaryExpr(my_digit, PathTracker.tempVar(1), "=="), "&&"), PathTracker.equals(PathTracker.tempVar("E"), my_input), "&&"), cf && digit == 1 && input.equals("E"), 27);
        if (cf && digit == 1 && input.equals("E")) {
            PathTracker.myAssign(my_digit, PathTracker.tempVar(1), "+=");
            digit += 1;
            PathTracker.myAssign(my_cf, PathTracker.tempVar(false), "=");
            cf = false;
            PathTracker.output("N");
        }
        PathTracker.myIf(PathTracker.binaryExpr(PathTracker.binaryExpr(my_cf, PathTracker.binaryExpr(my_digit, PathTracker.tempVar(2), "=="), "&&"), PathTracker.equals(PathTracker.tempVar("R"), my_input), "&&"), cf && digit == 2 && input.equals("R"), 32);
        if (cf && digit == 2 && input.equals("R")) {
            PathTracker.myAssign(my_digit, PathTracker.tempVar(1), "+=");
            digit += 1;
            PathTracker.myAssign(my_cf, PathTracker.tempVar(false), "=");
            cf = false;
            PathTracker.output("N");
        }
        PathTracker.myIf(PathTracker.binaryExpr(PathTracker.binaryExpr(my_cf, PathTracker.binaryExpr(my_digit, PathTracker.tempVar(3), "=="), "&&"), PathTracker.equals(PathTracker.tempVar("O"), my_input), "&&"), cf && digit == 3 && input.equals("O"), 37);
        if (cf && digit == 3 && input.equals("O")) {
            PathTracker.myAssign(my_digit, PathTracker.tempVar(1), "+=");
            digit += 1;
            PathTracker.myAssign(my_cf, PathTracker.tempVar(false), "=");
            cf = false;
            PathTracker.output("N");
        }
        PathTracker.myIf(PathTracker.binaryExpr(PathTracker.binaryExpr(my_cf, PathTracker.binaryExpr(my_digit, PathTracker.tempVar(4), "=="), "&&"), PathTracker.equals(PathTracker.tempVar("ENTER"), my_input), "&&"), cf && digit == 4 && input.equals("ENTER"), 43);
        if (cf && digit == 4 && input.equals("ENTER")) {
            PathTracker.myAssign(my_cf, PathTracker.tempVar(false), "=");
            cf = false;
            PathTracker.myAssign(my_open, PathTracker.tempVar(true), "=");
            open = true;
            PathTracker.output("Y");
        }
        PathTracker.myIf(PathTracker.binaryExpr(my_cf, PathTracker.equals(PathTracker.tempVar("ENTER"), my_input), "&&"), cf && input.equals("ENTER"), 49);
        if (cf && input.equals("ENTER")) {
            PathTracker.myAssign(my_cf, PathTracker.tempVar(false), "=");
            cf = false;
            PathTracker.myAssign(my_digit, PathTracker.tempVar(0), "=");
            digit = 0;
            PathTracker.output("X");
        }
        PathTracker.myIf(PathTracker.binaryExpr(my_cf, PathTracker.binaryExpr(PathTracker.binaryExpr(PathTracker.binaryExpr(PathTracker.binaryExpr(PathTracker.binaryExpr(PathTracker.binaryExpr(PathTracker.binaryExpr(PathTracker.equals(PathTracker.tempVar("T"), my_input), PathTracker.equals(PathTracker.tempVar("Z"), my_input), "||"), PathTracker.equals(PathTracker.tempVar("O"), my_input), "||"), PathTracker.equals(PathTracker.tempVar("N"), my_input), "||"), PathTracker.equals(PathTracker.tempVar("W"), my_input), "||"), PathTracker.equals(PathTracker.tempVar("E"), my_input), "||"), PathTracker.equals(PathTracker.tempVar("D"), my_input), "||"), PathTracker.equals(PathTracker.tempVar("R"), my_input), "||"), "&&"), cf && (input.equals("T") || input.equals("Z") || input.equals("O") || input.equals("N") || input.equals("W") || input.equals("E") || input.equals("D") || input.equals("R")), 55);
        if (cf && (input.equals("T") || input.equals("Z") || input.equals("O") || input.equals("N") || input.equals("W") || input.equals("E") || input.equals("D") || input.equals("R"))) {
            PathTracker.myAssign(my_cf, PathTracker.tempVar(false), "=");
            cf = false;
            PathTracker.output("N");
        }
        errorCheck();
        PathTracker.myIf(my_cf, cf, 61);
        if (cf) {
            throw new IllegalArgumentException("Current state has no transition for this input!");
        }
    }

    public static void main(String[] args) throws Exception {
        // init system and input reader
        ProblemPin eca = new ProblemPin();
        PathTracker.run(eca.inputs, eca);
    }

    public String[] sequence;

    public Void call() {
        ProblemPin cp = new ProblemPin();
        for (String s : sequence) {
            try {
                MyVar my_s = PathTracker.myInputVar(s, "input");
                cp.calculateOutput(s, my_s);
            } catch (IllegalArgumentException | IllegalStateException e) {
                SymbolicExecutionLab.output("Invalid input: " + e.getMessage());
            }
        }
        return null;
    }

    public void setSequence(String[] trace) {
        sequence = trace;
    }

    static BufferedReader my_stdin = new BufferedReader(new InputStreamReader(System.in));

    private MyVar[] my_inputs = { PathTracker.myVar("T", "my_inputs0"), PathTracker.myVar("Z", "my_inputs1"), PathTracker.myVar("O", "my_inputs2"), PathTracker.myVar("N", "my_inputs3"), PathTracker.myVar("W", "my_inputs4"), PathTracker.myVar("E", "my_inputs5"), PathTracker.myVar("D", "my_inputs6"), PathTracker.myVar("R", "my_inputs7"), PathTracker.myVar("ENTER", "my_inputs8"), PathTracker.myVar("C", "my_inputs9") };

    public MyVar my_cf = PathTracker.myVar(true, "my_cf");

    public MyVar my_digit = PathTracker.myVar(0, "my_digit");

    public MyVar my_open = PathTracker.myVar(false, "my_open");

    public MyVar[] my_sequence;
}

