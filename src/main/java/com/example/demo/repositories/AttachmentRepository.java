package com.example.demo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entities.Attachment;

public interface AttachmentRepository extends JpaRepository<Attachment, String> {

}
