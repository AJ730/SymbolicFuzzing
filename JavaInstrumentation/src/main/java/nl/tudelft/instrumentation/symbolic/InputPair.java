package nl.tudelft.instrumentation.symbolic;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Objects;

public class InputPair implements Comparable<InputPair>{

    Integer lineNmr;
    LinkedList<String> inputTrace;

    public InputPair(Integer lineNmr, LinkedList<String> inputTrace) {
        this.lineNmr = lineNmr;
        this.inputTrace = inputTrace;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InputPair inputPair = (InputPair) o;
        return Objects.equals(lineNmr, inputPair.lineNmr) && Objects.equals(inputTrace, inputPair.inputTrace);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lineNmr, inputTrace);
    }

    @Override
    public String toString() {
        return "InputPair{" +
                "lineNmr=" + lineNmr +
                ", inputTrace=" + inputTrace +
                '}';
    }

    @Override
    public int compareTo(InputPair o) {
        return this.lineNmr - o.lineNmr;
    }
}
