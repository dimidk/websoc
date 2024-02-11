package com.websocket.websocket.services;

import com.mongodb.MongoChangeStreamException;
import com.mongodb.client.internal.MongoChangeStreamCursorImpl;
import com.websocket.websocket.actions.EditActions;
import com.websocket.websocket.model.DocRoom;
import com.websocket.websocket.model.TypeMessage;
import com.websocket.websocket.model.User;
import com.websocket.websocket.repository.DocRoomRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.print.Doc;
import java.util.Iterator;
import java.util.Optional;
import java.util.List;
import java.util.stream.Stream;

@Service
@Slf4j
public class DocRoomService {

    private final DocRoomRepository docRoomRepository;

    public DocRoomService(DocRoomRepository docRoomRepository) {

        this.docRoomRepository = docRoomRepository;
    }

    public DocRoom getDocRoom(DocRoom docRoom) {

        String id = null;

        if (docRoomRepository.existsById(docRoom.getDocId())) {
            return docRoom;
        }
        else {
            return null;
        }

    }

    public DocRoom getDocRoom(String docId) {

        if (docId == null) {
            throw new IllegalArgumentException("no document id");

        }

        return docRoomRepository.findByDocId(docId);
    }

    public boolean exist(String docId) {

        if (docId == null) {
            throw new IllegalArgumentException("no document id");

        }
        return docRoomRepository.existsByDocId(docId);
    }


    public void addDoc(String docId) {

        if (docId == null) {
            throw new IllegalArgumentException("no document id");

        }

        DocRoom docRoom = DocRoom.builder()
                        .docId(docId)
                                .content("new doc")
                                        .build();
        docRoomRepository.save(docRoom);
    }

    public void addDoc(DocRoom docRoom) {

        if (docRoom == null) {
            throw new IllegalArgumentException("no document id");

        }

        docRoomRepository.save(docRoom);
    }

    public String getContentDocId(String docId) {

        if (docId == null) {
            throw new IllegalArgumentException("no document id");

        }

        DocRoom docRoom = docRoomRepository.findByDocId(docId);
        return docRoom.getContent();
    }

    public List<DocRoom> findAllDocs() {

        return docRoomRepository.findAll();
    }


    public List<User> getSharedUsers(String docId) {

        //List<User> shared = null;

        DocRoom docRoom = docRoomRepository.findByDocId(docId);


        return docRoom.getShareUser();

    }

    public Optional<User> findShareUser(String docId,String name) {

        User temp = null;
        boolean a = false;

        if (docId == null) {
            throw new IllegalArgumentException("no document id");

        }
        if (name == null) {
            throw new IllegalArgumentException("no document id");

        }


        log.info("roomService: find if user exists in share list");
        List<User> share = docRoomRepository.findByDocId(docId).getShareUser();
        log.info("share list is empty: {}",share.isEmpty());
        if (!share.isEmpty()) {

            Iterator<User> it = share.iterator();
            while (it.hasNext()) {
                temp = it.next();
                //log.info("roomService: if user in share list {}",user.getName());
                if (temp.getName().compareTo(name) == 0) {
                    log.info("roomService: user exists in share list");
                    a = true;
                    //temp = it.next();
                    break;
                }

            }
        }
        if (a)
            return Optional.of(temp);

        else return Optional.empty();
    }

    public void updateDocument(String docId, String msg) {

        if (docId == null) {
            throw new IllegalArgumentException("no document id");

        }
        if (msg == null) {
            throw new IllegalArgumentException("no document id");

        }

        DocRoom docRoom = docRoomRepository.findByDocId(docId);
        docRoom.setContent(msg);

        docRoomRepository.save(docRoom);

    }
}
