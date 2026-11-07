package matching;

import java.util.ArrayList;
import java.util.List;

public class MatchResult {
    public final String algorithm;
    public final List<Integer> positions = new ArrayList<>();
    public long charComparisons;
    public long hashComparisons;
    public long spuriousHits;
    public long preprocessingOps;

    public MatchResult(String algorithm) {
        this.algorithm = algorithm;
    }

    @Override
    public String toString() {
        return algorithm + " " + positions;
    }
}
