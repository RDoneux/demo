package com.example.demo.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Entity
public class Attachment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String attachmentId;

    private String fileName;
    private String fileType;
    private String filePath;
    private long fileSize;

    @ManyToOne
    private Chat chat;

}
