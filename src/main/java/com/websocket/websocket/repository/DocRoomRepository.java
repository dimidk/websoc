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

    public  User findByDocIdAndShareUserContains(String docId, List<User> shareUser);

    public String findByDocIdAndContent(String docId,String content);




    public List<DocRoom> streamDocRoomByDocIdAndShareUser(String docId, List<User> shareUser);
    public void streamDocRoomByDocId(String docId);
    public void streamDocRoomByDocIdAndContent(String docId, String content);

    public boolean existsByDocId(String docId);

    //public List<User> getSharedUsers(String docId);
}
