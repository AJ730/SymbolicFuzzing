import java.io.BufferedReader;
import java.io.InputStreamReader;
import nl.tudelft.instrumentation.runner.CallableTraceRunner;
import nl.tudelft.instrumentation.symbolic.*;

public class Problem2 implements CallableTraceRunner<Void> {

    static BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));

    private String[] inputs = { "iA" };

    public String a1855872761 = "g";

    public boolean cf = true;

    public String a547336540 = "g";

    public void calculateOutput(String input, MyVar my_input) {
        PathTracker.myAssign(my_cf, PathTracker.tempVar(false), "=");
        cf = false;
        PathTracker.myIf(PathTracker.binaryExpr(my_input, PathTracker.tempVar("iB"), "=="), input == "iB", 21);
        if (input == "iB") {
            PathTracker.output("yes");
        }
        PathTracker.output("hi");
    }

    public static void main(String[] args) throws Exception {
        // init system and input reader
        Problem2 eca = new Problem2();
        PathTracker.run(eca.inputs, eca);
    }

    public String[] sequence;

    public Void call() {
        Problem2 cp = new Problem2();
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

    private MyVar[] my_inputs = { PathTracker.myVar("iA", "my_inputs0") };

    public MyVar my_a1855872761 = PathTracker.myVar("g", "my_a1855872761");

    public MyVar my_cf = PathTracker.myVar(true, "my_cf");

    public MyVar my_a547336540 = PathTracker.myVar("g", "my_a547336540");

    public MyVar[] my_sequence;
}

