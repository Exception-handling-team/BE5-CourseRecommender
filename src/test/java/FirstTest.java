import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class FirstTest {

    @Test
    @DisplayName("앱 시작 시 '--- 수강신청 추천 시스템 --- 출력")
    void t1() {

        String out = TestBot.run("");
        assertThat(out)
                .containsSubsequence("--- 수강신청 추천 시스템 ---", "프로그램을 종료합니다.");

    }

    @Test
    @DisplayName("create - DB에 과목 정보 1개 입력")
    void t2() {

        String out = TestBot.run("""
                등록
                객체지향프로그래밍
                T043385
                3
                1
                없음
                수 1 2 금 3
                """);
        assertThat(out)
                .containsSubsequence("과목 명: ", "과목 코드: ","학점: ","학년: ","선수 과목: ", "수업 교시: ");
    }

//    @Test
//    @DisplayName("명령을 여러번 입력할 수 있다.")
//    void t3() {
//
//        String out = TestBot.run("""
//                등록
//                등록
//                등록
//                등록
//                종료
//                """);
//        long count = out.split("명령 \\)").length - 1;
//
//        assertThat(count).isEqualTo(5);
//    }

    @Test
    @DisplayName("과목 정보를 입력하면 과목 번호 출력")
    void t4() {

        String out = TestBot.run("""
                등록
                객체지향프로그래밍
                T043385
                3
                1
                없음
                수 1 2 금 3
                """);

        assertThat(out).contains("1번 과목이 등록되었습니다.");
    }

    @Test
    @DisplayName("과목 2개 입력, 과목 번호 증가")
    void t5() {

        String out = TestBot.run("""
                등록
                객체지향프로그래밍
                T043385
                3
                1
                없음
                수 1 2 금 3
                등록
                선형대수
                T031086
                3
                1
                없음
                화 5 6 목 7
                """);

        assertThat(out).containsSubsequence("1번 과목이 등록되었습니다.", "2번 과목이 등록되었습니다.");
    }

    @Test
    @DisplayName("목록 입력 시 저장된 목록 출력")
    void t6() {

        String out = TestBot.run("""
                등록
                객체지향프로그래밍
                T043585
                3
                2
                없음
                수 1 2 금 3
                등록
                선형대수
                T031086
                3
                1
                없음
                화 5 6 목 7
                목록
                """);

        assertThat(out)
                .contains("번호 / 과목 / 과목코드 / 학점 / 학년 / 선수과목 / 수업 교시")
                .contains("----------------------------------------------")
                .containsSubsequence("2 / 선형대수 / T031086 / 3 / 1 / 없음 / 화 5 6 목 7", "1 / 객체지향프로그래밍 / T043585 / 3 / 2 / 없음 / 수 1 2 금 3");
    }

    @Test
    @DisplayName("수강신청 입력 시 수강신청 추천 목록을 출력")
    void t7() {
        String out = TestBot.run("""
                등록
                객체지향프로그래밍
                T043585
                3
                2
                없음
                수 1 2 금 3
                등록
                선형대수
                T031086
                3
                1
                없음
                화 5 6 목 7
                수강신청
                """);
        assertThat(out)
                .contains("--- 수강신청 추천 목록 ---")
                .contains("번호 / 과목 / 과목코드 / 학점 / 학년 / 선수과목 / 수업 교시")
                .contains("----------------------------------------------")
                .containsSubsequence("2 / 선형대수 / T031086 / 3 / 1 / 없음 / 화 5 6 목 7", "1 / 객체지향프로그래밍 / T043585 / 3 / 2 / 없음 / 수 1 2 금 3");
    }

    @Test
    @DisplayName("잘못된 명령 입력 시 예외처리")
    void t8() {
        String out = TestBot.run("""
               audfud
                """);

        assertThat(out)
                .containsSubsequence("잘못된 명령입니다.");
    }

    @Test
    @DisplayName("과목 삭제 ex)삭제?id=1 이면 1번 과목 삭제")
    void t9() {
        String out = TestBot.run("""
                등록
                객체지향프로그래밍
                T043585
                3
                2
                없음
                수 1 2 금 3
                등록
                선형대수
                T031086
                3
                1
                없음
                화 5 6 목 7
                삭제?id=1
                목록
                """);

        assertThat(out)
                .contains("2 / 선형대수 / T031086 / 3 / 1 / 없음 / 화 5 6 목 7")
                .doesNotContain("1 / 객체지향프로그래밍 / T043585 / 3 / 2 / 없음 / 수 1 2 금 3");
    }

    @Test
    @DisplayName("과목 수정 ex 수정?id=1")
    void t10() {
        String out = TestBot.run("""
                등록
                객체지향프로그래밍
                T043585
                3
                2
                없음
                수 1 2 금 3
                등록
                선형대수
                T031086
                3
                1
                없음
                화 5 6 목 7
                수정?id=1
                새 과목
                새 코드
                3
                3
                없음
                화 5 6 목 8
                목록
                """);

        assertThat(out)
                .contains("1 / 새 과목 / 새 코드 / 3 / 3 / 없음 / 화 5 6 목 8")
                .doesNotContain("1 / 객체지향프로그래밍 / T043585 / 3 / 2 / 없음 / 수 1 2 금 3");
    }

    @Test
    @DisplayName("삭제 예외 처리 - 없는 id로 삭제를 시도하면 예외처리 메세지 출력")
    void t11() {
        String out = TestBot.run("""
                등록
                객체지향프로그래밍
                T043585
                3
                2
                없음
                수 1 2 금 3
                등록
                선형대수
                T031086
                3
                1
                없음
                화 5 6 목 7
                삭제?id=1
                삭제?id=1
                목록
                """);

        assertThat(out)
                .contains("1번 명언은 존재하지 않습니다.");
    }


}

