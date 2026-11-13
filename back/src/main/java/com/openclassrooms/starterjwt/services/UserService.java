package com.openclassrooms.starterjwt.services;

import com.openclassrooms.starterjwt.exception.NotFoundException;
import com.openclassrooms.starterjwt.exception.UnauthorizedException;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public void delete(Long id, String requestingUsername) {
        User user = this.getById(id);
        if (!Objects.equals(requestingUsername, user.getEmail())) {
            throw new UnauthorizedException();
        }

        this.userRepository.delete(user);
    }

    public User findById(Long id) {
        return this.userRepository.findById(id).orElse(null);
    }

    public User getById(Long id) {
        return this.userRepository.findById(id)
                .orElseThrow(NotFoundException::new);
    }
}
