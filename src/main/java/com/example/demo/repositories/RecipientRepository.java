package com.example.demo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entities.Recipient;

public interface RecipientRepository extends JpaRepository<Recipient, String> {}
