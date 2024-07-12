package com.example.demo.entities;

import com.example.demo.enums.MessageSource;
import com.example.demo.enums.MessageStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Entity
public class Attachment {

    @Id
    private String attachmentId;

    private String fileName;
    private String fileType;
    private String filePath;
    private long fileSize;
    private String downloadLink;
    private MessageSource source;
    private MessageStatus status;

    @Lob
    @Column(columnDefinition = "LONGBLOB")
    @JsonIgnore
    private byte[] data;

    @ManyToOne
    @JsonIgnore
    private Chat chat;

}
