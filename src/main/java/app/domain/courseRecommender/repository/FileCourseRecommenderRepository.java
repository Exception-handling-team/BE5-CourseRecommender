package app.domain.courseRecommender.repository;

import app.domain.courseRecommender.CourseRecommender;
import app.domain.courseRecommender.CourseRecommenderRepository;

import java.io.*;
import java.util.*;

public class FileCourseRecommenderRepository extends CourseRecommenderRepository {
    private final String filePath;
    private final List<CourseRecommender> courseRecommenderList;
    private int lastId;

    public FileCourseRecommenderRepository(String filePath) {
        this.filePath = filePath;
        this.courseRecommenderList = new ArrayList<>();
        loadFromFile();
    }

    @Override
    public CourseRecommender save(CourseRecommender courseRecommender) {
        if (courseRecommender.isNew()) {
            courseRecommender.setId(++lastId);
            courseRecommenderList.add(courseRecommender);
        } else {
            // 기존 과목 수정
            Optional<CourseRecommender> existingCourseOpt = findById(courseRecommender.getId());
            if (existingCourseOpt.isPresent()) {
                CourseRecommender existingCourse = existingCourseOpt.get();
                existingCourse.setCourseName(courseRecommender.getCourseName());
                existingCourse.setCourseCode(courseRecommender.getCourseCode());
                existingCourse.setCourseCredit(courseRecommender.getCourseCredit());
                existingCourse.setCourseGrade(courseRecommender.getCourseGrade());
                existingCourse.setPreCourseName(courseRecommender.getPreCourseName());
                existingCourse.setCourseTime(courseRecommender.getCourseTime());
            } else {
                // ID가 존재하지 않으면 새 과목으로 간주
                courseRecommender.setId(++lastId);
                courseRecommenderList.add(courseRecommender);
            }
        }
        saveToFile();
        return courseRecommender;
    }

    @Override
    public List<CourseRecommender> findAll() {
        return new ArrayList<>(courseRecommenderList);
    }

    @Override
    public boolean deleteById(int id) {
        boolean removed = courseRecommenderList.removeIf(course -> course.getId() == id);
        if (removed) {
            saveToFile();
        }
        return removed;
    }

    @Override
    public Optional<CourseRecommender> findById(int id) {
        return courseRecommenderList.stream()
                .filter(course -> course.getId() == id)
                .findFirst();
    }

    /**
     * 파일에서 데이터를 로드하여 리스트를 초기화합니다.
     */
    private void loadFromFile() {
        File file = new File(filePath);
        if (!file.exists()) {
            // 파일이 없으면 새로 생성
            try {
                file.getParentFile().mkdirs();
                file.createNewFile();
            } catch (IOException e) {
                System.err.println("파일을 생성하는 중 오류가 발생했습니다: " + e.getMessage());
                return;
            }
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            lastId = 0;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue; // 빈 줄 건너뛰기
                String[] parts = line.split("\\|");
                if (parts.length != 7) continue; // 잘못된 형식 건너뛰기

                int id = Integer.parseInt(parts[0]);
                String courseName = parts[1];
                String courseCode = parts[2];
                int courseCredit = Integer.parseInt(parts[3]);
                int courseGrade = Integer.parseInt(parts[4]);
                String preCourseName = parts[5];
                String courseTime = parts[6];

                CourseRecommender course = new CourseRecommender(courseName, courseCode, courseCredit, courseGrade, preCourseName, courseTime);
                course.setId(id);
                courseRecommenderList.add(course);

                if (id > lastId) {
                    lastId = id;
                }
            }
        } catch (IOException e) {
            System.err.println("파일을 읽는 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * 리스트의 모든 데이터를 파일에 저장합니다.
     */
    private void saveToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (CourseRecommender course : courseRecommenderList) {
                writer.write(String.format("%d|%s|%s|%d|%d|%s|%s\n",
                        course.getId(),
                        course.getCourseName(),
                        course.getCourseCode(),
                        course.getCourseCredit(),
                        course.getCourseGrade(),
                        course.getPreCourseName(),
                        course.getCourseTime()));
            }
        } catch (IOException e) {
            System.err.println("파일에 쓰는 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
}