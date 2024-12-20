package com.taltech.alpopo.securepasswordmanager.repository;

import com.taltech.alpopo.securepasswordmanager.entity.Credential;
import com.taltech.alpopo.securepasswordmanager.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CredentialRepository extends JpaRepository<Credential, UUID> {
    List<Credential> findByUser(User user);
}
