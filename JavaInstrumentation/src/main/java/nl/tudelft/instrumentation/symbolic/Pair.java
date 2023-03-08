package nl.tudelft.instrumentation.symbolic;

import java.util.Objects;

public class Pair  {
    int lineNmr;
    boolean triggered;


    @Override
    public String toString() {
        return "{lineNmr:" + lineNmr +
                ", branch triggered:" + triggered;
    }

    Pair(int lineNmr, boolean triggered) {
        this.triggered = triggered;
        this.lineNmr = lineNmr;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pair pair = (Pair) o;
        return Double.compare(pair.lineNmr, this.lineNmr) == 0 && this.triggered == pair.triggered;
    }

    @Override
    public int hashCode() {
        return Objects.hash(lineNmr, triggered);
    }


}