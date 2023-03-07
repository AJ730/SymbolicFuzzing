package nl.tudelft.instrumentation.symbolic;

import java.util.*;

import com.microsoft.z3.*;
import org.apache.commons.lang3.StringUtils;

import java.util.Random;

/**
 * You should write your solution using this class.
 */
public class SymbolicExecutionLab {

    static Random r = new Random();
    static Boolean isFinished = false;
    static List<String> currentTrace;
    static int traceLength = 10;
    private static PriorityQueue<BoolExpr> visitedBranches;
    private static boolean satisfied = true;

    private static BoolExpr previousNegtatedBranch;

    private static PriorityQueue<BoolExpr> unsatisfiedBranches;

    private static HashSet<Pair> branches;

    static Set<Integer> errors;

    static Context c;

    static void initialize(String[] inputSymbols) {
        // Initialise a random trace from the input symbols of the problem.
        currentTrace = generateRandomTrace(inputSymbols);
        c = PathTracker.ctx;
        visitedBranches = new PriorityQueue<>();
        branches = new HashSet<>();
        errors = new TreeSet<>();
        previousNegtatedBranch = null;
    }

    static MyVar createVar(String name, Expr value, Sort s) {
        Context c = PathTracker.ctx;
        /**
         * Create var, assign value and add to path constraint.
         * We show how to do it for creating new symbols, please
         * add similar steps to the functions below in order to
         * obtain a path constraint.
         */
        Expr z3var = c.mkConst(c.mkSymbol(name + "_" + PathTracker.z3counter++), s);
        PathTracker.addToModel(c.mkEq(z3var, value));
        return new MyVar(z3var, name);
    }

    static MyVar createInput(String name, Expr value, Sort s) {
        // Create an input var, these should be free variables!
        Expr z3var = c.mkConst(c.mkSymbol(name + "_" + PathTracker.z3counter++), s); //create a new symbolic variable

        BoolExpr constraint = c.mkFalse();//make the constraint false
        for (String input : PathTracker.inputSymbols) { //loop through all input symbols
            constraint = c.mkOr(c.mkEq(z3var, c.mkString(input)), constraint); //or the constraints (a.k.a constrain the problem to input symbols)
        }
        PathTracker.addToModel(c.mkAnd(constraint)); // add the constraints to the model

        MyVar returnVar = new MyVar(z3var, name); //create new return var
        PathTracker.inputs.add(returnVar); // add them to inputs

        return returnVar;
    }

    static MyVar createBoolExpr(BoolExpr var, String operator) {
        // Any unary expression (!)
        if (operator.equals("!")) return new MyVar(PathTracker.ctx.mkNot(var));
        else throw new UnsupportedOperationException("Unsupported Unary operator" + operator);
    }

    static MyVar createBoolExpr(BoolExpr left_var, BoolExpr right_var, String operator) {
        // Any binary expression (&, &&, |, ||)
        switch (operator) {
            case "&":
            case "&&":
                return new MyVar(c.mkAnd(left_var, right_var));
            case "|":
            case "||":
                return new MyVar(c.mkOr(left_var, right_var));
            case "==":
                return new MyVar(c.mkEq(left_var, right_var));
            case "!=":
                return new MyVar(c.mkNot(c.mkEq(left_var, right_var)));
            default:
                throw new UnsupportedOperationException("Unsupported Binary Operator" + operator);
        }
    }

    static MyVar createIntExpr(IntExpr var, String operator) {
        // Any unary expression (+, -)

        switch (operator) {
            case "+":
                return new MyVar(var);
            case "-":
                return new MyVar(c.mkUnaryMinus(var));//ask this

            default:
                throw new UnsupportedOperationException("Unsupported Unary Operator" + operator);
        }
    }

    static MyVar createIntExpr(IntExpr left_var, IntExpr right_var, String operator) {
        // Any binary expression (+, -, /, etc)

        switch (operator) {
            case "+":
                return new MyVar(c.mkAdd(left_var, right_var));
            case "-":
                return new MyVar(c.mkSub(left_var, right_var));
            case "/":
                return new MyVar(c.mkDiv(left_var, right_var));
            case "*":
                return new MyVar(c.mkMul(left_var, right_var));
            case "%":
                return new MyVar(c.mkMod(left_var, right_var));
            case "==":
                return new MyVar(c.mkEq(left_var, right_var));
            case "!=":
                return new MyVar(c.mkNot(c.mkEq(left_var, right_var)));
            case "<=":
                return new MyVar(c.mkLe(left_var, right_var));
            case ">=":
                return new MyVar(c.mkGe(left_var, right_var));
            case ">":
                return new MyVar(c.mkGt(left_var, right_var));
            case "<":
                return new MyVar(c.mkLt(left_var, right_var));
            case "^": return new MyVar(c.mkPower(left_var, right_var));

            default:
                throw new UnsupportedOperationException("Unsupported Binary Operator" + operator);
        }
    }

    static MyVar createStringExpr(SeqExpr left_var, SeqExpr right_var, String operator) {
        // We only support String.equals
        switch (operator){
            case "==":
                return new MyVar(c.mkEq(left_var, right_var));
            case "!=":
                return new MyVar(c.mkNot(c.mkEq(left_var, right_var)));
            default:
                throw new UnsupportedOperationException("Unsupported Binary Operator" + operator);
        }
    }

    static void assign(MyVar var, String name, Expr value, Sort s) {
        // All variable assignments, use single static assignment
        Expr z3var = c.mkConst(c.mkSymbol(name + "_" + PathTracker.z3counter++), s); // make a new static assignment
        var.z3var = z3var; // make use the pointer is updated
        PathTracker.addToModel(c.mkEq(z3var, value)); //add to the model
    }

    static void encounteredNewBranch(MyVar condition, boolean value, int line_nr) {

        //add branches
        branches.add(new Pair(line_nr, value));

        // Call the solver
        if(condition.z3var instanceof  BoolExpr){

            if(!satisfied) unsatisfiedBranches.add(previousNegtatedBranch);

            BoolExpr expr = (BoolExpr) condition.z3var; // make sure branch is a boolean expr
            visitedBranches.add(expr);//add current branch to expr

            BoolExpr negExpr = c.mkNot(expr); // negate the boolean expr
            if(unsatisfiedBranches.contains(negExpr)) return;

            if(visitedBranches.contains(negExpr)){
                return; // we have already seen the negative branch
            }

            previousNegtatedBranch = negExpr;
            satisfied = false;
            PathTracker.solve(negExpr, true);
        }

        throw new UnsupportedOperationException("New Branch does not have a boolean condition");
    }

    static void newSatisfiableInput(LinkedList<String> new_inputs) {
        // Hurray! found a new branch using these new inputs!
        currentTrace = new_inputs;
        satisfied = true;
    }

    /**
     * Method for fuzzing new inputs for a program.
     *
     * @param inputSymbols the inputSymbols to fuzz from.
     * @return a fuzzed sequence
     */
    static List<String> fuzz(String[] inputSymbols) {
        /*
         * Add here your code for fuzzing a new sequence for the RERS problem.
         * You can guide your fuzzer to fuzz "smart" input sequences to cover
         * more branches using symbolic execution. Right now we just generate
         * a complete random sequence using the given input symbols. Please
         * change it to your own code.
         */
        return generateRandomTrace(inputSymbols);
    }

    /**
     * Generate a random trace from an array of symbols.
     *
     * @param symbols the symbols from which a trace should be generated from.
     * @return a random trace that is generated from the given symbols.
     */
    static List<String> generateRandomTrace(String[] symbols) {
        ArrayList<String> trace = new ArrayList<>();
        for (int i = 0; i < traceLength; i++) {
            trace.add(symbols[r.nextInt(symbols.length)]);
        }
        return trace;
    }

    static void run() {
        initialize(PathTracker.inputSymbols);


        // Place here your code to guide your fuzzer with its search using Symbolic Execution.
        while (!isFinished) {
            // Do things!
            visitedBranches.forEach(PathTracker::addToBranches); // add all visited branches

            if(!satisfied){
                unsatisfiedBranches.add(previousNegtatedBranch);
            }

            System.out.println("Branch size " +branches);
            System.out.println("Errors: " + errors);
            System.out.println("currentTrace:" + currentTrace);

            PathTracker.runNextFuzzedSequence(currentTrace.toArray(new String[0]));
//            PathTracker.reset();//reset the branches;
            satisfied = true;
        }
    }

    public static void output(String out) {
//        System.out.println(out);
        if (StringUtils.contains(out, "error")) {
            errors.add(Integer.parseInt(out.replaceAll("Invalid input: error_", "")));
        }
    }


    ////////////////////////custom methods////////////////////////////////



}