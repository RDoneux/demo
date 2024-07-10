package com.example.demo.entities;

import java.util.Date;
import java.util.Set;

import com.example.demo.enums.ChatState;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder (toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Entity
public class Chat {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String chatId;

    @Column
    private ChatState chatState;

    @Column
    private Date chatExpiry;

    @ManyToOne
    private Recipient recipient;

    @OneToMany(mappedBy="chat", cascade=CascadeType.ALL, orphanRemoval = true)
    private Set<Message> messageHistory;

    @OneToMany(mappedBy="chat", cascade=CascadeType.ALL, orphanRemoval = true)
    private Set<Attachment> attachments;


}
