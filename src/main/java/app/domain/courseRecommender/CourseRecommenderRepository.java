package app.domain.courseRecommender;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CourseRecommenderRepository {

    private final List<CourseRecommender> courseRecommenderList;
    private int lastId;

    public CourseRecommenderRepository() {
        courseRecommenderList = new ArrayList<>();
    }

    public CourseRecommender save(CourseRecommender courseRecommender) {
        if(!courseRecommender.isNew()) {
            return courseRecommender;
        }

        int id = ++lastId;
        courseRecommender.setId(id);
        courseRecommenderList.add(courseRecommender);

        return courseRecommender;
    }

    public List<CourseRecommender> findAll() {
        return courseRecommenderList;
    }

    public boolean deleteById(int id) {
        return courseRecommenderList.removeIf(courseRecommender -> courseRecommender.getId() == id);
    }

    public Optional<CourseRecommender> findById(int id) {
        return courseRecommenderList.stream()
                .filter(courseRecommender -> courseRecommender.getId() == id)
                .findFirst();

    }
}
