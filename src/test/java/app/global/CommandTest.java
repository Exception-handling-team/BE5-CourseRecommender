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
        String strId = cmd.getParam("id");
        int id = Integer.parseInt(strId);
        assertThat(id).isEqualTo(2);
    }

    @Test
    @DisplayName("파라미터가 불완전할 때")
    void t6() {
        Command cmd1 = new Command("목록?expr=1=1");
        String param1 = cmd1.getParam("expr");
        Command cmd2 = new Command("목록?page");
        String param2 = cmd2.getParam("page");
        Command cmd3 = new Command("삭제?id=aa");
        String param3 = cmd3.getParam("id");

        assertThat(param1).isEqualTo("1=1");
        assertThat(param2).isNull();
        assertThat(param3).isEqualTo("aa");

    }

    @Test
    @DisplayName("파라미터가 여러 개 있을 때, 파라미터 가져오기")
    void t7() {
        Command cmd = new Command("목록?key1=val1&key2=val2");
        String param1 = cmd.getParam("key1");
        String param2 = cmd.getParam("key2");

        assertThat(param1).isEqualTo("val1");
        assertThat(param2).isEqualTo("val2");
    }
}
