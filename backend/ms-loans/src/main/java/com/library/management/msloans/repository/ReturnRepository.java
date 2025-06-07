package com.library.management.msloans.repository;

import com.library.management.msloans.model.Return;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ReturnRepository extends JpaRepository<Return, UUID> {
}