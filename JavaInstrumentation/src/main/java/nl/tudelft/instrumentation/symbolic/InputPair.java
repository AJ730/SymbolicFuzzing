package nl.tudelft.instrumentation.symbolic;

import java.util.ArrayList;

public class InputPair {

    Integer lineNmr;
    ArrayList<String> inputTrace;

    public InputPair(Integer lineNmr, ArrayList<String> inputTrace) {
        this.lineNmr = lineNmr;
        this.inputTrace = inputTrace;
    }



    @Override
    public String toString() {
        return "InputPair{" +
                "lineNmr=" + lineNmr +
                ", inputTrace=" + inputTrace +
                '}';
    }
}
