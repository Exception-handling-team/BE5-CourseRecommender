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

    @Test
    @DisplayName("action name 얻어오기 - 입력값 삭제, ?가 없으면 잘 나오나 확인")
    void t3() {
        Command cmd = new Command("삭제");
        String actionName = cmd.getActionName();
        assertThat(actionName).isEqualTo("삭제");
    }

    @Test
    @DisplayName("불완전한 입력이 들어왔을 때, 삭제?1, 삭제?id?1")
    void t4() {
        Command cmd = new Command("삭제");
        String actionName = cmd.getActionName();
        assertThat(actionName).isEqualTo("삭제");
    }

    @Test
    @DisplayName("입력값 - 삭제?id=1 일 때, 파라미터를 달라고 하면 1 출력")
    void t5() {
        Command cmd = new Command("삭제?id=2");
        int id = cmd.getParam();
        assertThat(id).isEqualTo(2);
    }
}
