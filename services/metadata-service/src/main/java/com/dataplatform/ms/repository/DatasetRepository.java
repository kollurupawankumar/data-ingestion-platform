package com.dataplatform.ms.repository;

import com.dataplatform.ms.domain.Dataset;
import com.dataplatform.ms.domain.SubjectArea;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface DatasetRepository extends JpaRepository<Dataset, Long> {

    Optional<Dataset> findByName(String name);

    List<Dataset> findAllBySubjectArea(SubjectArea subjectArea);

    boolean existsByNameAndSubjectArea(String name, SubjectArea subjectArea);
}