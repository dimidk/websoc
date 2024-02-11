package com.websocket.websocket.services;

import com.websocket.websocket.model.TypeMessage;
import com.websocket.websocket.model.User;
import com.websocket.websocket.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;


import java.util.List;

@Service
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void addNew(User user) {

        if (user == null) {
            throw new IllegalArgumentException("no user");
        }
        userRepository.insert(user);
        //userRepository.save(user);
    }

    public boolean exists(User user) {

        return userRepository.existsById(user.getName());

    }

    public boolean existsByName(String name) {

        if (name == null) {
            throw new IllegalArgumentException("no name");
        }

        return userRepository.existsByName(name);
    }
    public User findUser(String name) {

        if (name == null) {
            throw new IllegalArgumentException("no name");
        }

        return userRepository.findUserByName(name);
        //return userRepository.findById(id).get();
    }

    public List<User> findAllConnectedUsers() {

        return userRepository.findAllByStatus(TypeMessage.JOIN);
    }

    public void updateUser(String name,TypeMessage status) {

        if (name == null) {
            throw new IllegalArgumentException("no name");
        }
        if (status == null) {
            throw new IllegalArgumentException("no given status");
        }

        User user = findUser(name);
        //log.info("user info");
        //log.info("user with {} {}",user.getName(),user.getStatus());
        //log.info("update user");

        user.setStatus(status);

        userRepository.save(user);
    }
}
