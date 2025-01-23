package com.jmy.courseRecommender.domain.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class CourseRecommender {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column
    private String courseName;

    @Column
    private String courseCode;

    @Column
    private int courseCredit;

    @Column
    private int courseGrade;

    @Column
    private String preCourseName;

    @Column
    private String courseTime;
}
