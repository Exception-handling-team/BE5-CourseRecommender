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

    @Test
    @DisplayName("파일 내용 읽기")
    void t3() {

        String testContent = "Hello, Java!";

        String file = "test.txt";
        String content = Util.File.readAsString(file);
        assertThat(content).isEqualTo(testContent);
    }

    @Test
    @DisplayName("파일 내용 수정")
    void t4() {

        String file = "test2.txt";
        String writeContent = "modify";

        Util.File.write(file, writeContent);
        String readContent = Util.File.readAsString(file);
        assertThat(readContent).isEqualTo(writeContent);
    }

    @Test
    @DisplayName("파일 내용 삭제")
    void t5() {

        String file = "test3.txt";

        Util.File.createFile(file);
        assertThat(Files.exists(Paths.get(file)))
                .isTrue();

        Util.File.delete(file);


        assertThat(Files.exists(Paths.get(file)))
                .isFalse();
    }
}
