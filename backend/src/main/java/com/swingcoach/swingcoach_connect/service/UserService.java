package com.swingcoach.swingcoach_connect.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.swingcoach.swingcoach_connect.repository.UserRepository;
import com.swingcoach.swingcoach_connect.model.User;
import com.swingcoach.swingcoach_connect.model.User.AccountStatus;

@Service
public class UserService {

    private UserRepository userRepository;

    UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public Optional<User> findByEmail(String email){
        return this.userRepository.findByEmailAndAccountStatus(email, AccountStatus.ACTIVE);
    }
    
}
