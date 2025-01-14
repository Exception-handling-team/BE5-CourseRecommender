package app.domain.courseRecommender;

import java.util.*;
import java.util.stream.Collectors;

public class CourseRecommenderController {
    private final Scanner sc;
    private CourseRecommenderService courseRecommenderService;
    private static final int PAGE_SIZE = 5;

    public CourseRecommenderController(Scanner sc) {
        this.sc = sc;
        courseRecommenderService = new CourseRecommenderService();
    }

    public void register() {
        try {
            System.out.print("과목 명: ");
            String courseName = sc.nextLine().trim();
            if (courseName.isEmpty()) {
                System.out.println("과목 명은 비어 있을 수 없습니다.");
                return;
            }

            System.out.print("과목 코드: ");
            String courseCode = sc.nextLine().trim();
            if (courseCode.isEmpty()) {
                System.out.println("과목 코드는 비어 있을 수 없습니다.");
                return;
            }

            System.out.print("학점: ");
            int courseCredit = Integer.parseInt(sc.nextLine().trim());
            if (courseCredit <= 0) {
                System.out.println("학점은 양수여야 합니다.");
                return;
            }

            System.out.print("학년: ");
            int courseGrade = Integer.parseInt(sc.nextLine().trim());
            if (courseGrade <= 0) {
                System.out.println("학년은 양수여야 합니다.");
                return;
            }

            System.out.print("선수 과목 (없으면 '없음' 입력): ");
            String preCourseName = sc.nextLine().trim();
            if (preCourseName.isEmpty()) {
                preCourseName = "없음";
            }

            System.out.print("수업 교시 (예: 화 5 6 목 7): ");
            String courseTime = sc.nextLine().trim();
            if (courseTime.isEmpty()) {
                System.out.println("수업 교시는 비어 있을 수 없습니다.");
                return;
            }

            CourseRecommender courseRecommender = courseRecommenderService.write(
                    courseName, courseCode, courseCredit, courseGrade, preCourseName, courseTime);
            System.out.println(courseRecommender.getId() + "번 과목이 등록되었습니다.");
        } catch (NumberFormatException e) {
            System.out.println("학점과 학년은 숫자로 입력해야 합니다.");
        } catch (Exception e) {
            System.out.println("과목 등록 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    public void list() {
        List<CourseRecommender> courseRecommenderList = courseRecommenderService.getAllItems();

        if (courseRecommenderList.isEmpty()) {
            System.out.println("등록된 과목이 없습니다.");
            return;
        }

        // 내림차순 정렬
        courseRecommenderList = courseRecommenderList.stream()
                .sorted(Comparator.comparingInt(CourseRecommender::getId).reversed())
                .collect(Collectors.toList());

        int totalCourses = courseRecommenderList.size();
        int totalPages = (int) Math.ceil((double) totalCourses / PAGE_SIZE);
        int currentPage = 1;

        while (true) {
            int startIndex = (currentPage - 1) * PAGE_SIZE;
            int endIndex = Math.min(startIndex + PAGE_SIZE, totalCourses);
            List<CourseRecommender> pageList = courseRecommenderList.subList(startIndex, endIndex);

            System.out.printf("\n--- 과목 목록 (페이지 %d/%d) ---\n", currentPage, totalPages);
            System.out.println("번호 / 과목 / 과목코드 / 학점 / 학년 / 선수과목 / 수업 교시");
            System.out.println("-----------------------------------------------------");

            for (CourseRecommender c : pageList) {
                System.out.printf("%d / %s / %s / %d / %d / %s / %s\n",
                        c.getId(),
                        c.getCourseName(),
                        c.getCourseCode(),
                        c.getCourseCredit(),
                        c.getCourseGrade(),
                        c.getPreCourseName(),
                        c.getCourseTime());
            }

            System.out.println("\n명령어: [n] 다음 페이지 | [p] 이전 페이지 | [q] 종료");
            System.out.print("명령을 입력하세요: ");
            String command = sc.nextLine().trim().toLowerCase();

            if (command.equals("n")) {
                if (currentPage < totalPages) {
                    currentPage++;
                } else {
                    System.out.println("더 이상 다음 페이지가 없습니다.");
                }
            } else if (command.equals("p")) {
                if (currentPage > 1) {
                    currentPage--;
                } else {
                    System.out.println("더 이상 이전 페이지가 없습니다.");
                }
            } else if (command.equals("q")) {
                break;
            } else {
                System.out.println("유효하지 않은 명령어입니다. 다시 시도해주세요.");
            }
        }
    }

    public void recommend() {
        try {
            System.out.print("현재 학년을 입력하세요: ");
            int currentGrade = Integer.parseInt(sc.nextLine().trim());
            if (currentGrade <= 0) {
                System.out.println("학년은 양수여야 합니다.");
                return;
            }

            System.out.print("이수한 과목명을 ','로 구분하여 입력하세요 (없으면 엔터): ");
            String completedInput = sc.nextLine().trim();
            List<String> completedCourses = completedInput.isEmpty()
                    ? new ArrayList<>()
                    : Arrays.stream(completedInput.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toList());

            System.out.print("수강하고 싶은 총 학점을 입력하세요: ");
            int targetCredits = Integer.parseInt(sc.nextLine().trim());
            if (targetCredits <= 0) {
                System.out.println("총 학점은 양수여야 합니다.");
                return;
            }

            List<CourseRecommender> recommendations = courseRecommenderService.recommendCourses(currentGrade, completedCourses, targetCredits);

            if (recommendations.isEmpty()) {
                System.out.println("추천할 과목이 없습니다.");
            } else {
                System.out.println("\n--- 수강신청 추천 목록 ---");
                System.out.println("번호 / 과목 / 과목코드 / 학점 / 학년 / 선수과목 / 수업 교시");
                System.out.println("-----------------------------------------------------");
                for (CourseRecommender course : recommendations) {
                    System.out.printf("%d / %s / %s / %d / %d / %s / %s\n",
                            course.getId(),
                            course.getCourseName(),
                            course.getCourseCode(),
                            course.getCourseCredit(),
                            course.getCourseGrade(),
                            course.getPreCourseName(),
                            course.getCourseTime());
                }

                // 추천 결과 파일에 저장
                courseRecommenderService.saveRecommendations(recommendations, "data/recommended.txt");
                System.out.println("\n추천 결과가 data/recommended.txt에 저장되었습니다.");
            }
        } catch (NumberFormatException e) {
            System.out.println("학년과 총 학점은 숫자로 입력해야 합니다.");
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
        } catch (Exception e) {
            System.out.println("과목 추천 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    public void wrongCmd() {
        System.out.println("잘못된 명령입니다.");
    }


    public void delete(String cmd) {
        try {
            String param = cmd.split("\\?")[1];
            String[] paramBits = param.split("=");
            if (paramBits.length != 2 || !paramBits[0].equals("id")) {
                throw new IllegalArgumentException("삭제 명령어 형식이 잘못되었습니다.");
            }
            int id = Integer.parseInt(paramBits[1].trim());

            boolean result = courseRecommenderService.delete(id);

            if (!result) {
                System.out.println(id + "번 과목은 존재하지 않습니다.");
            } else {
                System.out.println(id + "번 과목이 삭제되었습니다.");
            }
        } catch (ArrayIndexOutOfBoundsException | IllegalArgumentException e) {
            wrongCmd();
        }
    }

    public void modify(String cmd) {
        try {
            String param = cmd.split("\\?")[1];
            String[] paramBits = param.split("=");
            if (paramBits.length != 2 || !paramBits[0].equals("id")) {
                throw new IllegalArgumentException("수정 명령어 형식이 잘못되었습니다.");
            }
            int id = Integer.parseInt(paramBits[1].trim());

            Optional<CourseRecommender> opCourseRecommender = courseRecommenderService.getItems(id);
            CourseRecommender c = opCourseRecommender.orElse(null);

            if (c == null) {
                System.out.println(id + "번 과목은 존재하지 않습니다.");
                return;
            }

            System.out.printf("(기존) %s / %s / %d / %d / %s / %s\n",
                    c.getCourseName(),
                    c.getCourseCode(),
                    c.getCourseCredit(),
                    c.getCourseGrade(),
                    c.getPreCourseName(),
                    c.getCourseTime());

            System.out.print("과목 명: ");
            String newCourseName = sc.nextLine().trim();
            if (newCourseName.isEmpty()) {
                newCourseName = c.getCourseName();
            }

            System.out.print("과목 코드: ");
            String newCourseCode = sc.nextLine().trim();
            if (newCourseCode.isEmpty()) {
                newCourseCode = c.getCourseCode();
            }

            System.out.print("학점: ");
            String courseCreditInput = sc.nextLine().trim();
            int newCourseCredit = courseCreditInput.isEmpty() ? c.getCourseCredit() : Integer.parseInt(courseCreditInput);

            System.out.print("학년: ");
            String courseGradeInput = sc.nextLine().trim();
            int newCourseGrade = courseGradeInput.isEmpty() ? c.getCourseGrade() : Integer.parseInt(courseGradeInput);

            System.out.print("선수 과목: ");
            String newPreCourseName = sc.nextLine().trim();
            if (newPreCourseName.isEmpty()) {
                newPreCourseName = c.getPreCourseName();
            }

            System.out.print("수업 교시: ");
            String newCourseTime = sc.nextLine().trim();
            if (newCourseTime.isEmpty()) {
                newCourseTime = c.getCourseTime();
            }

            courseRecommenderService.modify(c, newCourseName, newCourseCode, newCourseCredit, newCourseGrade, newPreCourseName, newCourseTime);
            System.out.println(id + "번 과목이 수정되었습니다.");
        } catch (ArrayIndexOutOfBoundsException | IllegalArgumentException e) {
            wrongCmd();
        }
    }
}