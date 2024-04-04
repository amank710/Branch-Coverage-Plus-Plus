package pathcoverageplusplus;

import common.PathCoverage;
import common.functions.Path;
import common.util.Tuple;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
@RequestMapping("/api/paths")
public class PathsController {
    // MOCK INPUT BLOCK START
    Map<String, Map<Integer, Integer>> mockHits = new HashMap<>();
    Map<Integer, Integer> checkA = new HashMap<>();
    Map<Integer, Integer> checkB = new HashMap<>();
    Map<String, ArrayList<ArrayList<Integer>>> mockUncovered = new HashMap<>();
    ArrayList<ArrayList<Integer>> uncoveredPathsA;
    ArrayList<ArrayList<Integer>> uncoveredPathsB;
    ArrayList<Integer> checkAUncovered;
    ArrayList<Integer> checkBUncovered;
    Map<String, Tuple<Integer, Integer>> mockPathCoverageMetadata = new HashMap<>();

    PathCoverage mockInput;

    // MOCK INPUT BLOCK END

    @GetMapping
    public ResponseEntity<PathCoverage> getPathValues() {
        // Setting mock input block START

        // Mapping line numbers to number of hits per line
        checkA.put(11, 2);
        checkA.put(12, 2);
        checkA.put(14, 3);

        checkB.put(19, 4);
        checkB.put(20, 2);
        checkB.put(21, 2);

        // Mapping Method names to line hits
        mockHits.put("checkA", checkA);
        mockHits.put("checkB", checkB);

        // Mapping uncovered line numbers to the array lists
        checkAUncovered = new ArrayList<>();
        checkAUncovered.add(9);
        checkAUncovered.add(10);

        checkBUncovered = new ArrayList<>();
        checkBUncovered.add(22);
        checkBUncovered.add(23);
        checkBUncovered.add(24);

        uncoveredPathsA = new ArrayList<>();
        uncoveredPathsA.add(checkAUncovered);
        uncoveredPathsB = new ArrayList<>();
        uncoveredPathsB.add(checkBUncovered);

        mockUncovered.put("checkA", uncoveredPathsA);
        mockUncovered.put("checkB", uncoveredPathsB);

        // Mapping method names to path coverage metadata
        mockPathCoverageMetadata.put("checkA", new Tuple<>(5, 3));
        mockPathCoverageMetadata.put("checkB", new Tuple<>(5, 2));

        // setting mock input
        mockInput = new PathCoverage(
                mockPathCoverageMetadata,
                mockHits,
                mockUncovered
        );

        // Setting mock input block END
        return new ResponseEntity<PathCoverage>(mockInput, HttpStatus.OK);
    }
}
