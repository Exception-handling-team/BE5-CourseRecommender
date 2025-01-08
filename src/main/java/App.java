import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class App {
    private final Scanner sc;
    private final CourseRecommenderController courseRecommenderController;
    private final SystemController systemController;

    public App(Scanner sc) {
        this.sc = sc;
        systemController = new SystemController();
        courseRecommenderController = new CourseRecommenderController(sc);
    }
    public void run() {
        System.out.println("--- 수강신청 추천 시스템 ---");

        while (true) {
            System.out.println("명령 ) ");
            String cmd = sc.nextLine();


            switch(cmd) {
                case "종료" -> systemController.exit();
                case "등록" -> courseRecommenderController.register();
                case "목록" -> courseRecommenderController.list();
                case "수강신청" -> courseRecommenderController.recommend();
                case "삭제?id=1" -> courseRecommenderController.delete();
                default -> courseRecommenderController.wrongCmd();
            }

            if(cmd.equals("종료")) break;
        }
    }
}
