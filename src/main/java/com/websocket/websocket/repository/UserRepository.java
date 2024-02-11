package com.websocket.websocket.repository;

import com.websocket.websocket.model.TypeMessage;
import com.websocket.websocket.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface UserRepository extends MongoRepository<User,String> {

    public User findUserByName(String name);

    public boolean existsByName(String name);

    @Query(value = "{'status': 'JOIN'}")
    public List<User> findAllByStatus(TypeMessage status);



}
