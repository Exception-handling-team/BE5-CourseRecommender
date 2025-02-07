package com.jmy.courseRecommender.domain.service;


import com.jmy.courseRecommender.domain.entity.User;
import com.jmy.courseRecommender.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User registerUser(User user) {
        return userRepository.save(user);
    }

    public User loginUser(String username, String password) {
        User user = userRepository.findByUsername(username);
        if (user != null && user.getPassword().equals(password)) {
            return user;
        } else {
            throw new RuntimeException("잘못된 비밀번호 입니다.");
        }
    }
}
