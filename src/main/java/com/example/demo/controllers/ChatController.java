package com.example.demo.controllers;

import java.util.Date;
import java.util.Optional;
import java.util.Set;
import java.util.List;
import java.util.UUID;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.entities.Chat;
import com.example.demo.entities.Message;
import com.example.demo.entities.Recipient;
import com.example.demo.entities.Template;
import com.example.demo.enums.ChatState;
import com.example.demo.repositories.ChatRepository;
import com.example.demo.repositories.MessageRepository;
import com.example.demo.repositories.RecipientRepository;
import com.example.demo.repositories.TemplateRepository;

@RestController()
public class ChatController {

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private RecipientRepository recipientRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private TemplateRepository templateRepository;

    @GetMapping("/create-chat")
    ResponseEntity<String> createChat() {
        Optional<Recipient> targetRecipient = recipientRepository.findById("3245033b-b6a7-4b82-9011-50baf9340fd4");

        Chat chat = Chat.builder().chatId(UUID.randomUUID().toString()).recipient(targetRecipient.get())
                .messageHistory(Collections.emptySet())
                .chatState(ChatState.active).chatExpiry(new Date()).build();

        Chat savedChat = chatRepository.save(chat);

        return new ResponseEntity<String>(savedChat.getChatId(), HttpStatus.OK);
    }

    @GetMapping("/message-history")
    ResponseEntity<Chat> getMessages(@RequestParam("chatId") String chatId) {

        Optional<Chat> mightBeChat = chatRepository.findById(chatId);

        if (mightBeChat.isPresent())
            return new ResponseEntity<Chat>(mightBeChat.get(), HttpStatus.OK);

        return new ResponseEntity<Chat>(HttpStatus.NOT_FOUND);

    }

    @PutMapping("/send-message")
    ResponseEntity<Message> sendMessage(@RequestParam("chatId") String chatId, @RequestBody() Message message) {

        Optional<Chat> mightBeChat = chatRepository.findById(chatId);

        if (mightBeChat.isPresent()) {

            Message savedMessage = messageRepository.save(message.toBuilder().chat(mightBeChat.get()).build());

            Set<Message> newMessageHistory = mightBeChat.get().getMessageHistory();
            newMessageHistory.add(savedMessage);

            Chat newChat = mightBeChat.get().toBuilder().messageHistory(newMessageHistory).build();

            chatRepository.save(newChat);

            return new ResponseEntity<Message>(savedMessage, HttpStatus.OK);

        }

        return new ResponseEntity<Message>(HttpStatus.NOT_FOUND);

    }

    @GetMapping("/template")
    ResponseEntity<List<Template>> getTemplates() {
        return new ResponseEntity<List<Template>>(templateRepository.findAll(), HttpStatus.OK);
    }

    @PutMapping("/template")
    ResponseEntity<Message> sendTemplate(@RequestParam("chatId") String chatId,
            @RequestParam("templateId") String templateId) {
        Optional<Chat> maybeChat = chatRepository.findById(chatId);
        Optional<Template> maybeTemplate = templateRepository.findById(templateId);

        if (maybeChat.isEmpty() || maybeTemplate.isEmpty())
            return new ResponseEntity<Message>(HttpStatus.NOT_FOUND);

        Message templateConvertedToMessage = maybeTemplate.get().toMessage();

        return this.sendMessage(maybeChat.get().getChatId(), templateConvertedToMessage);
    }

}
