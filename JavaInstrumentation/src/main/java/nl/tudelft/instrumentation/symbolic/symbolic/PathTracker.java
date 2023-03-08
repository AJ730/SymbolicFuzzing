package nl.tudelft.instrumentation.symbolic.symbolic;

import com.microsoft.z3.*;
import nl.tudelft.instrumentation.runner.CallableTraceRunner;
import nl.tudelft.instrumentation.symbolic.MyVar;
import nl.tudelft.instrumentation.symbolic.SymbolicExecutionLab;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.*;

/**
 * This class is used for the symbolic execution lab.
 * @author Clinton Cao, Sicco Verwer
 */
public class PathTracker {
    public static HashMap<String, String> cfg = new HashMap<String, String>() {{ put("model","true"); }};
    public static Context ctx = new Context(cfg);

    public static int z3counter = 1; // used to give an ID to each variable.
    private static BoolExpr z3model= ctx.mkTrue();
    private static BoolExpr z3branches = ctx.mkTrue();

    public static Solver solver = ctx.mkSolver();

    public static LinkedList<nl.tudelft.instrumentation.symbolic.MyVar> inputs = new LinkedList<nl.tudelft.instrumentation.symbolic.MyVar>();
    static ScheduledExecutorService executor = Executors.newScheduledThreadPool(2);
    static CallableTraceRunner<Void> problem;
    static String[] inputSymbols;
    // Longest a single testcase is allowed to run
    static final int timeoutMS = 10000;

    /**
     * Used to reset the constraints and everything else of z3 before running the next sequence.
     */
    public static void reset(){
        z3counter  = 1;
        z3model    = ctx.mkTrue();
        z3branches = ctx.mkTrue();
        inputs.clear();
        solver = ctx.mkSolver();
    }


    public static void addToModel(BoolExpr expr) {
        z3model = ctx.mkAnd(expr, z3model);
        solver.add(expr);
    }

    public static void addToBranches(BoolExpr expr) {
        z3branches = ctx.mkAnd(expr, z3branches);
        solver.add(expr);
    }

    /**
     * This method contains code that calls the Z3 solver and check whether it solve the path
     * constraint that is constructed for the new branch. The solver will try to find inputs
     * that can satisfy the path constraint. We can use these inputs to reach this newly
     * discovered branch.
     * @param new_branch the branch that we have discovered and want to visit.
     * @param printModel boolean value that specifies whether the path constraint should
     *                   be printed in the terminal or not.
     */
    public static void solve(BoolExpr new_branch, boolean printModel){
        // Save the state of the solver before adding the branch constraint
        solver.push();
        solver.add(new_branch);

        if(printModel){
            System.out.print("Model: ");
            System.out.println(PathTracker.z3model);
            System.out.print("Branches: ");
            System.out.println(PathTracker.z3branches);
            System.out.print("New branch: ");
            System.out.println(new_branch);
        }

        if(solver.check() == Status.SATISFIABLE){
            //System.out.println("satisfiable");
            Model m = solver.getModel();
            LinkedList<String> new_inputs = new LinkedList<String>();
            for(nl.tudelft.instrumentation.symbolic.MyVar v : PathTracker.inputs){
                new_inputs.add(m.evaluate(v.z3var, true).toString());
            }
            nl.tudelft.instrumentation.symbolic.SymbolicExecutionLab.newSatisfiableInput(new_inputs);
        } else {
            //System.out.println("unsatisfiable");
        }
        // Restore the state of the solver to remove the branch constraint
        solver.pop();
    }

    // Making temporary variables, i.e., within if-conditions
    public static nl.tudelft.instrumentation.symbolic.MyVar tempVar(boolean value){
        return new nl.tudelft.instrumentation.symbolic.MyVar(ctx.mkBool(value));
    }
    public static nl.tudelft.instrumentation.symbolic.MyVar tempVar(int value){
        return new nl.tudelft.instrumentation.symbolic.MyVar(ctx.mkInt(value));
    }
    public static nl.tudelft.instrumentation.symbolic.MyVar tempVar(String value){
        return new nl.tudelft.instrumentation.symbolic.MyVar(ctx.mkString(value));
    }

    // Making new stored variables
    public static nl.tudelft.instrumentation.symbolic.MyVar myVar(boolean value, String name){
        return nl.tudelft.instrumentation.symbolic.SymbolicExecutionLab.createVar(name, ctx.mkBool(value), ctx.getBoolSort());
    }
    public static nl.tudelft.instrumentation.symbolic.MyVar myVar(int value, String name){
        return nl.tudelft.instrumentation.symbolic.SymbolicExecutionLab.createVar(name, ctx.mkInt(value), ctx.getIntSort());
    }
    public static nl.tudelft.instrumentation.symbolic.MyVar myVar(String value, String name){
        return nl.tudelft.instrumentation.symbolic.SymbolicExecutionLab.createVar(name, ctx.mkString(value), ctx.getStringSort());
    }
    public static nl.tudelft.instrumentation.symbolic.MyVar myVar(nl.tudelft.instrumentation.symbolic.MyVar value, String name){
        return nl.tudelft.instrumentation.symbolic.SymbolicExecutionLab.createVar(name, value.z3var, value.z3var.getSort());
    }

    // Making a new input variable
    public static nl.tudelft.instrumentation.symbolic.MyVar myInputVar(String value, String name){
        return nl.tudelft.instrumentation.symbolic.SymbolicExecutionLab.createInput(name, ctx.mkString(value), ctx.getStringSort());
    }

    // for assigning an array to a variable.
    public static nl.tudelft.instrumentation.symbolic.MyVar[] myVar(nl.tudelft.instrumentation.symbolic.MyVar[] value, String name){
        nl.tudelft.instrumentation.symbolic.MyVar[] vars = new nl.tudelft.instrumentation.symbolic.MyVar[value.length];
        for(int i = 0; i < value.length; i++){
            vars[i] = value[i];
        }
        return vars;
    }

    /**
     * Arrays are tricky, this is how we deal with them.
     * this assignment creates a reference and does not need new variables
     */
    public static nl.tudelft.instrumentation.symbolic.MyVar[] myVar(nl.tudelft.instrumentation.symbolic.MyVar[] value){
        nl.tudelft.instrumentation.symbolic.MyVar[] vars = new nl.tudelft.instrumentation.symbolic.MyVar[value.length];
        for(int i = 0; i < value.length; i++){
            vars[i] = value[i];
        }
        return vars;
    }

    /**
     * Main construction for creating the path constraint.
     * This part is for handling arithmetic and boolean logic.
     */
    public static nl.tudelft.instrumentation.symbolic.MyVar unaryExpr(nl.tudelft.instrumentation.symbolic.MyVar i, String operator){
        if(i.z3var instanceof BoolExpr){
            return nl.tudelft.instrumentation.symbolic.SymbolicExecutionLab.createBoolExpr((BoolExpr)i.z3var, operator);
        }
        if(i.z3var instanceof IntExpr || i.z3var instanceof ArithExpr){
            return nl.tudelft.instrumentation.symbolic.SymbolicExecutionLab.createIntExpr((IntExpr)i.z3var, operator);
        }
        return new nl.tudelft.instrumentation.symbolic.MyVar(ctx.mkFalse());
    }
    public static nl.tudelft.instrumentation.symbolic.MyVar binaryExpr(nl.tudelft.instrumentation.symbolic.MyVar i, nl.tudelft.instrumentation.symbolic.MyVar j, String operator ){
        if(i.z3var instanceof BoolExpr){
            return nl.tudelft.instrumentation.symbolic.SymbolicExecutionLab.createBoolExpr((BoolExpr)i.z3var, (BoolExpr)j.z3var, operator);
        }
        if(i.z3var instanceof IntExpr){
            return nl.tudelft.instrumentation.symbolic.SymbolicExecutionLab.createIntExpr((IntExpr)i.z3var, (IntExpr)j.z3var, operator);
        }
        return new nl.tudelft.instrumentation.symbolic.MyVar(ctx.mkFalse());
    }
    public static nl.tudelft.instrumentation.symbolic.MyVar equals(nl.tudelft.instrumentation.symbolic.MyVar i, nl.tudelft.instrumentation.symbolic.MyVar j){
        return nl.tudelft.instrumentation.symbolic.SymbolicExecutionLab.createStringExpr((SeqExpr)i.z3var, (SeqExpr)j.z3var, "==");
    }

    // We handle arrays, which needs an iterated if-then-else.
    public static nl.tudelft.instrumentation.symbolic.MyVar arrayInd(nl.tudelft.instrumentation.symbolic.MyVar[] name, nl.tudelft.instrumentation.symbolic.MyVar index){
        Expr ite_expr = name[0].z3var;
        for(int i = 1; i < name.length; i++){
            ite_expr = ctx.mkITE(ctx.mkEq(ctx.mkInt(i),(IntExpr)index.z3var), name[i].z3var, ite_expr);
        }
        return new nl.tudelft.instrumentation.symbolic.MyVar(ite_expr);
    }

    // We handle increments, forwarded to assignments.
    public static nl.tudelft.instrumentation.symbolic.MyVar increment(nl.tudelft.instrumentation.symbolic.MyVar i, String operator, boolean prefix){
        if(prefix){
            if(operator.equals("++")) myAssign(i, new nl.tudelft.instrumentation.symbolic.MyVar(ctx.mkAdd((IntExpr)i.z3var,ctx.mkInt(1))), "=");
            if(operator.equals("--")) myAssign(i, new nl.tudelft.instrumentation.symbolic.MyVar(ctx.mkAdd((IntExpr)i.z3var,ctx.mkInt(-1))), "=");
            return i;
        } else {
            nl.tudelft.instrumentation.symbolic.MyVar old_var = new nl.tudelft.instrumentation.symbolic.MyVar(i.z3var);
            if(operator.equals("++")) myAssign(i, new nl.tudelft.instrumentation.symbolic.MyVar(ctx.mkAdd((IntExpr)i.z3var,ctx.mkInt(1))), "=");
            if(operator.equals("--")) myAssign(i, new nl.tudelft.instrumentation.symbolic.MyVar(ctx.mkAdd((IntExpr)i.z3var,ctx.mkInt(-1))), "=");
            return old_var;
        }
    }

    // We handle conditionals, which is an if-then-else.
    public static nl.tudelft.instrumentation.symbolic.MyVar conditional(nl.tudelft.instrumentation.symbolic.MyVar b, nl.tudelft.instrumentation.symbolic.MyVar t, nl.tudelft.instrumentation.symbolic.MyVar e){
        return new nl.tudelft.instrumentation.symbolic.MyVar(ctx.mkITE((BoolExpr)b.z3var, t.z3var, e.z3var));
    }

    // Assignment changes the z3var in a MyVar variable.
    public static void myAssign(nl.tudelft.instrumentation.symbolic.MyVar target, nl.tudelft.instrumentation.symbolic.MyVar value, String operator){
        // first add or subtract if necessary
        Expr new_value = value.z3var;
        if(operator.equals("-=")) new_value = ctx.mkSub((IntExpr)target.z3var,(IntExpr)value.z3var);
        if(operator.equals("+=")) new_value = ctx.mkAdd((IntExpr)target.z3var,(IntExpr)value.z3var);

        nl.tudelft.instrumentation.symbolic.SymbolicExecutionLab.assign(target, target.name, new_value, target.z3var.getSort());
    }

    // We handle arrays, again using if-then-else and call standard variable assignment for all indices.
    public static void myAssign(nl.tudelft.instrumentation.symbolic.MyVar[] name, nl.tudelft.instrumentation.symbolic.MyVar index, nl.tudelft.instrumentation.symbolic.MyVar value, String operator){
        for(int i = 0; i < name.length; i++){
            Expr old_expr = name[i].z3var;
            Expr new_value = value.z3var;
            if(operator.equals("-=")) new_value = ctx.mkSub((IntExpr)old_expr,(IntExpr)value.z3var);
            if(operator.equals("+=")) new_value = ctx.mkAdd((IntExpr)old_expr,(IntExpr)value.z3var);

            nl.tudelft.instrumentation.symbolic.SymbolicExecutionLab.assign(name[i], name[i].name, ctx.mkITE(ctx.mkEq(ctx.mkInt(i),index.z3var), new_value, old_expr), name[i].z3var.getSort());
        }
    }

    // Direct assign for array references
    public static void myAssign(nl.tudelft.instrumentation.symbolic.MyVar[] name1, nl.tudelft.instrumentation.symbolic.MyVar[] name2, String operator){
        for(int i = 0; i < name1.length; i++){
            name1[i] = name2[i];
        }
    }

    /**
     * Converts an if-statement into a custom myIf-statement. This method is used to
     * call the encounteredNewBranch method which contains the logic for computing
     * the branch distance when a new branch has been found.
     * @param condition the condition of the if-statement.
     * @param value the value of the condition.
     * @param line_nr the line number of the if-statement.
     */
    public static void myIf(MyVar condition, boolean value, int line_nr){
        nl.tudelft.instrumentation.symbolic.SymbolicExecutionLab.encounteredNewBranch(condition, value, line_nr);
    }

    /**
     * Used to catch output from the standard out.
     * @param out the string that has been outputted in the standard out.
     */
    public static void output(String out){
        nl.tudelft.instrumentation.symbolic.SymbolicExecutionLab.output(out);
    }

    /**
     * Initialize and hand over control to FuzzingLab
     * @param eca The current problem instance
     * @param s the input symbols of the problem
     */
    public static void run(String[] s, CallableTraceRunner<Void> eca) {
        problem = eca;
        inputSymbols = s;
        SymbolicExecutionLab.run();
    }

    /**
     * This method is used for running the fuzzed input. It first assigns the
     * fuzzed sequence that needs to be run and then user a handler to
     * start running the sequence through the problem.
     * @param sequence the fuzzed sequence that needs top be run.
     */
    public static void runNextFuzzedSequence(String[] sequence) {
        problem.setSequence(sequence);
        final Future handler = executor.submit(problem);
        executor.schedule(() -> {
            handler.cancel(true);
        }, timeoutMS, TimeUnit.MILLISECONDS);

        // Wait for it to be completed
        try {
            handler.get();
        } catch (CancellationException e) {
            System.out.println("TIMEOUT!");
            System.exit(-1);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            System.exit(-1);
        }

    }

}