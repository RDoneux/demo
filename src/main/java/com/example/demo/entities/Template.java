package com.example.demo.entities;

import java.util.Date;

import com.example.demo.enums.MessageSource;
import com.example.demo.enums.MessageStatus;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder (toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Entity
public class Template {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String templateId;

    String content;
    String title;

    public Message toMessage() {
        return Message.builder().content(this.getContent()).source(MessageSource.sender).status(MessageStatus.sent).timeSent(new Date()).build();
    }

}
