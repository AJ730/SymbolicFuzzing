package nl.tudelft.instrumentation.symbolic;

import java.util.Objects;

public class BranchPair implements Comparable<BranchPair> {

    int lineNmr;
    boolean value;


    public BranchPair(int lineNmr, boolean value) {
        this.lineNmr = lineNmr;
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BranchPair branchPair = (BranchPair) o;
        return lineNmr == branchPair.lineNmr && value == branchPair.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(lineNmr, value);
    }

    @Override
    public int compareTo(BranchPair o) {
        return this.lineNmr - o.lineNmr;
    }
}
