package com.example.demo.repository;

import com.example.demo.model.Secret;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SecretRepository extends JpaRepository<Secret, Long> {

    Optional<Secret> getBySecretText(String secretText);

}
