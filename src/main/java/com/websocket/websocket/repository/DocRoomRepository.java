package com.websocket.websocket.repository;

import com.websocket.websocket.model.DocRoom;
import com.websocket.websocket.model.User;
import org.springframework.data.domain.Example;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.FluentQuery;
import org.springframework.stereotype.Repository;

import javax.print.Doc;
import java.util.List;
import java.util.function.Function;

@Repository
public interface DocRoomRepository extends MongoRepository<DocRoom,String> {

    public DocRoom findByDocId(String docId);

    public User findByDocIdAndOwnerUser(String docId, User ownerUser);

    public boolean existsByDocId(String docId);
    public boolean existsByDocIdAndOwnerUser(String docId, User ownerUser);
}
