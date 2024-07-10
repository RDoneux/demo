package com.example.demo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entities.Template;

public interface TemplateRepository extends JpaRepository<Template, String> {

}
