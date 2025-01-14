package app.standard;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;

public class UtilTest {

    @Test
    @DisplayName("최초 파일 테스트")
    void t1() {
        Util.File.test();
    }

    @Test
    @DisplayName("파일 생성 - 빈 파일")
    void t2() {

        String file = "test.txt";

        Util.File.createFile(file);

        assertThat(Files.exists(Paths.get(file)))
                .isTrue();
    }
}
