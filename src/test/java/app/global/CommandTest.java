package app.global;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CommandTest {

    @Test
    @DisplayName("command Test 최초 테스트")
    void t1() {
        Command cmd = new Command("");
    }

    @Test
    @DisplayName("action name 얻어오기")
    void t2() {

        Command cmd = new Command("목록?id=1");
        String actionName = cmd.getActionName();


        assertThat(actionName).isEqualTo("목록");
    }
}
