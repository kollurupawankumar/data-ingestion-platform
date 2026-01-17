package com.dataplatform.ms.service;

import com.dataplatform.ms.domain.Dataset;
import com.dataplatform.ms.domain.SubjectArea;
import com.dataplatform.ms.repository.DatasetRepository;
import com.dataplatform.ms.repository.SubjectAreaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class DatasetService {

    private final DatasetRepository datasetRepository;
    private final SubjectAreaRepository subjectAreaRepository;

    public DatasetService(DatasetRepository datasetRepository,
                          SubjectAreaRepository subjectAreaRepository) {
        this.datasetRepository = datasetRepository;
        this.subjectAreaRepository = subjectAreaRepository;
    }

    public Dataset create(UUID subjectAreaId, String name, String description) {

        SubjectArea subjectArea = subjectAreaRepository.findById(subjectAreaId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Subject area not found: " + subjectAreaId
                ));

        if (datasetRepository.existsByNameAndSubjectArea(name, subjectArea)) {
            throw new IllegalArgumentException(
                    "Dataset already exists: " + name +
                            " under subject area " + subjectArea.getName()
            );
        }

        Dataset dataset = new Dataset();
        dataset.setName(name);
        dataset.setDescription(description);
        dataset.setSubjectArea(subjectArea);
        dataset.setEnabled(true);

        return datasetRepository.save(dataset);
    }

    @Transactional(readOnly = true)
    public Dataset getById(Long id) {
        return datasetRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Dataset not found: " + id
                ));
    }

    @Transactional(readOnly = true)
    public List<Dataset> getBySubjectArea(UUID subjectAreaId) {
        SubjectArea subjectArea = subjectAreaRepository.findById(subjectAreaId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Subject area not found: " + subjectAreaId
                ));

        return datasetRepository.findAllBySubjectArea(subjectArea);
    }

    @Transactional(readOnly = true)
    public SubjectArea getBySubjectAreaName(String name) {
        return subjectAreaRepository.findByName(name)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Subject area not found: " + name
                ));


    }
}
