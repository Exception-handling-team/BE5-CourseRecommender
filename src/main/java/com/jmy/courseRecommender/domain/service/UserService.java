package com.jmy.courseRecommender.domain.service;

import com.jmy.courseRecommender.domain.entity.User;
import com.jmy.courseRecommender.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    // 회원가입: 평문 비밀번호 그대로 저장 (운영 환경에서는 암호화 권장)
    public User registerUser(User user) {
        return userRepository.save(user);
    }

    // 로그인: username과 비밀번호를 비교
    public User loginUser(String username, String password) {
        User user = userRepository.findByUsername(username);
        if (user != null && user.getPassword().equals(password)) {
            return user;
        } else {
            throw new RuntimeException("Invalid username or password");
        }
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    // 사용자 정보 변경(학년, 목표 학점, 이수과목)
    public User updateUser(Long id, User updatedUser) {
        return userRepository.findById(id).map(user -> {
            user.setGrade(updatedUser.getGrade());
            user.setCredit(updatedUser.getCredit());
            user.setPreCourses(updatedUser.getPreCourses());
            return userRepository.save(user);
        }).orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
    }
}
