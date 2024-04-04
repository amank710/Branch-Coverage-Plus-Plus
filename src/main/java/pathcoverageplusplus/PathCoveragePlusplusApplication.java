package pathcoverageplusplus;

import common.Constants;

import java.util.Optional;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class PathCoveragePlusplusApplication {

    public static void main(String[] args) {
        Optional.ofNullable(System.getProperty(Constants.SANDBOX_HOME_PROP)).orElseThrow(() -> new RuntimeException("SANDBOX_HOME not set"));
        SpringApplication.run(PathCoveragePlusplusApplication.class, args);
    }

}
