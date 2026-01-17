package com.dataplatform.ms.service;

import com.dataplatform.ms.domain.SubjectArea;
import com.dataplatform.ms.repository.SubjectAreaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class SubjectAreaService {

    private final SubjectAreaRepository subjectAreaRepository;

    public SubjectAreaService(SubjectAreaRepository subjectAreaRepository) {
        this.subjectAreaRepository = subjectAreaRepository;
    }

    public SubjectArea create(String name, String description) {
        if (subjectAreaRepository.existsByName(name)) {
            throw new IllegalArgumentException(
                    "Subject area already exists: " + name
            );
        }

        SubjectArea subjectArea = new SubjectArea();
        subjectArea.setName(name);
        subjectArea.setDescription(description);
        subjectArea.setActive(true);

        return subjectAreaRepository.save(subjectArea);
    }

    @Transactional(readOnly = true)
    public SubjectArea getById(UUID id) {
        return subjectAreaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Subject area not found: " + id
                ));
    }

    @Transactional(readOnly = true)
    public SubjectArea getByName(String name) {
        return subjectAreaRepository.findByName(name)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Subject area not found: " + name
                ));
    }

    @Transactional(readOnly = true)
    public List<SubjectArea> getAll() {
        return subjectAreaRepository.findAll();
    }
}
