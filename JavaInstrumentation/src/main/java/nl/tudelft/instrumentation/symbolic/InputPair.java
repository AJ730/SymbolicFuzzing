package nl.tudelft.instrumentation.symbolic;

import java.util.LinkedList;
import java.util.Objects;

public class InputPair implements Comparable<InputPair>{

    Integer size;
    LinkedList<String> inputTrace;

    public InputPair(Integer size, LinkedList<String> inputTrace) {
        this.size = size;
        this.inputTrace = inputTrace;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InputPair inputPair = (InputPair) o;
        return Objects.equals(size, inputPair.size) && Objects.equals(inputTrace, inputPair.inputTrace);
    }

    @Override
    public int hashCode() {
        return Objects.hash(size, inputTrace);
    }

    @Override
    public String toString() {
        return "InputPair{" +
                "lineNmr=" + size +
                ", inputTrace=" + inputTrace +
                '}';
    }

    @Override
    public int compareTo(InputPair o) {
        return o.size - this.size;
    }
}
