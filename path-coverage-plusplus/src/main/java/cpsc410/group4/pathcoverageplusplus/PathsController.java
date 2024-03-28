package cpsc410.group4.pathcoverageplusplus;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/paths")
public class PathsController {

    private final List<Integer> sample_input = Arrays.asList(1, 4, 5, 10);

    @GetMapping
    public ResponseEntity<List<Integer>> getPathValues() {
        return new ResponseEntity<List<Integer>>(sample_input, HttpStatus.OK);
    }
}
