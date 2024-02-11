package com.websocket.websocket.services;

import com.websocket.websocket.model.Message;
import com.websocket.websocket.repository.MessageRepository;
import org.springframework.stereotype.Service;

@Service
public class MessageService {

    private final MessageRepository messageRepository;

    public MessageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    public String findMessage(String id) {

        String findId = null;

       if (messageRepository.existsById(id)) {
            findId = id;
        }

       return findId;
    }

    public void add(Message message) {

        messageRepository.save(message);
    }
}
