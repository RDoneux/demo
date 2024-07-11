package com.example.demo.controllers;

import java.util.Date;
import java.util.HashMap;
import java.util.Optional;
import java.util.Set;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.io.IOException;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.entities.Attachment;
import com.example.demo.entities.Chat;
import com.example.demo.entities.Message;
import com.example.demo.entities.Recipient;
import com.example.demo.entities.Template;
import com.example.demo.enums.ChatState;
import com.example.demo.repositories.AttachmentRepository;
import com.example.demo.repositories.ChatRepository;
import com.example.demo.repositories.MessageRepository;
import com.example.demo.repositories.RecipientRepository;
import com.example.demo.repositories.TemplateRepository;

@RestController()
@CrossOrigin(origins = "http://localhost:8080")
public class ChatController {

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private RecipientRepository recipientRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private TemplateRepository templateRepository;

    @Autowired
    private AttachmentRepository attachmentRepository;

    @GetMapping("/create-chat")
    ResponseEntity<Map<String, String>> createChat() {
        Optional<Recipient> targetRecipient = recipientRepository.findById("3245033b-b6a7-4b82-9011-50baf9340fd4");

        Chat chat = Chat.builder().chatId(UUID.randomUUID().toString()).recipient(targetRecipient.get())
                .messageHistory(Collections.emptySet())
                .chatState(ChatState.active).chatExpiry(new Date()).build();

        Chat savedChat = chatRepository.save(chat);
        Map<String, String> responseJSON = new HashMap<>();
        responseJSON.put("chatId", savedChat.getChatId());

        return new ResponseEntity<Map<String, String>>(responseJSON, HttpStatus.OK);
    }

    @GetMapping("/message-history")
    ResponseEntity<Chat> getMessages(@RequestParam("id") String chatId) {

        Optional<Chat> mightBeChat = chatRepository.findById(chatId);

        if (mightBeChat.isPresent())
            return new ResponseEntity<Chat>(mightBeChat.get(), HttpStatus.OK);

        return new ResponseEntity<Chat>(HttpStatus.NOT_FOUND);

    }

    @GetMapping("/message-attachments")
    ResponseEntity<Chat> getAttachments(@RequestParam("id") String chatId) {
        Optional<Chat> mightBeChat = chatRepository.findById(chatId);

        if (mightBeChat.isPresent())
            return new ResponseEntity<Chat>(mightBeChat.get(), HttpStatus.OK);

        return new ResponseEntity<Chat>(HttpStatus.NOT_FOUND);
    }

    @PutMapping("/send-message")
    ResponseEntity<Message> sendMessage(@RequestParam("id") String chatId, @RequestBody() Message message) {

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

    @PutMapping("/send-attachment")
    ResponseEntity<Attachment> sendAttachment(@RequestParam("id") String chatId, @RequestBody() MultipartFile file) {

        try {
            Optional<Chat> mightBeChat = chatRepository.findById(chatId);

            if (mightBeChat.isPresent()) {

                String attachmentId = UUID.randomUUID().toString();
                Attachment attachment = Attachment.builder().attachmentId(attachmentId).chat(mightBeChat.get())
                        .downloadLink("https://demo-production-e943.up.railway.app/download/" + attachmentId)
                        .fileName(file.getOriginalFilename()).data(file.getBytes()).fileSize(file.getSize())
                        .fileType(file.getContentType()).build();
                Attachment savedAttachment = attachmentRepository.save(attachment);

                Set<Attachment> updatedAttachments = mightBeChat.get().getAttachments();
                updatedAttachments.add(savedAttachment);

                Chat updatedChat = mightBeChat.get().toBuilder().attachments(updatedAttachments).build();

                chatRepository.save(updatedChat);

                return new ResponseEntity<Attachment>(savedAttachment, HttpStatus.OK);

            }
            return new ResponseEntity<Attachment>(HttpStatus.NOT_FOUND);

        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<Attachment>(HttpStatus.INTERNAL_SERVER_ERROR);

        }

    }

    @GetMapping("/download/{id}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable String id) {
        Optional<Attachment> file = attachmentRepository.findById(id);

        if (file.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", file.get().getFileName());

        return new ResponseEntity<>(file.get().getData(), headers, HttpStatus.OK);
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
