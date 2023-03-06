package nl.tudelft.instrumentation.symbolic;

import java.util.*;

import com.microsoft.z3.*;
import nl.tudelft.instrumentation.fuzzing.DistanceTracker;

import java.util.Random;
import java.io.FileWriter;
import java.io.IOException;

/**
 * You should write your solution using this class.
 */
public class SymbolicExecutionLab {

    static Random r = new Random();
    static Boolean isFinished = false;
    static List<String> currentTrace;
    static int traceLength = 10;

    static Context c;

    static void initialize(String[] inputSymbols) {
        // Initialise a random trace from the input symbols of the problem.
        currentTrace = generateRandomTrace(inputSymbols);
        c = PathTracker.ctx;
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
//            case "^": return new MyVar(c.mkPower()); (I dont know about this)

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
        Expr z3var = c.mkConst(c.mkSymbol(name + "_" + PathTracker.z3counter++), s);
        var.z3var = z3var;
        PathTracker.addToModel(c.mkEq(z3var, value));
    }

    static void encounteredNewBranch(MyVar condition, boolean value, int line_nr) {
        // Call the solver
        System.out.println(PathTracker.z3model);

    }

    static void newSatisfiableInput(LinkedList<String> new_inputs) {
        // Hurray! found a new branch using these new inputs!
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
        PathTracker.runNextFuzzedSequence(currentTrace.toArray(new String[0]));
        // Place here your code to guide your fuzzer with its search using Symbolic Execution.
        while (!isFinished) {
            // Do things!
            try {
                System.out.println("Woohoo, looping!");
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void output(String out) {
//        System.out.println(out);
    }


}