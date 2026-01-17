package com.dataplatform.ms.repository;


import com.dataplatform.ms.domain.SubjectArea;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SubjectAreaRepository extends JpaRepository<SubjectArea, UUID> {

    Optional<SubjectArea> findByName(String name);

    boolean existsByName(String name);
}