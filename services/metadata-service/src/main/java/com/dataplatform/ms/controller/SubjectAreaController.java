package com.dataplatform.ms.controller;


import com.dataplatform.ms.domain.SubjectArea;
import com.dataplatform.ms.dto.SubjectAreaRequest;
import com.dataplatform.ms.dto.SubjectAreaResponse;
import com.dataplatform.ms.service.SubjectAreaService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/subject-areas")
public class SubjectAreaController {

    private final SubjectAreaService subjectAreaService;

    public SubjectAreaController(SubjectAreaService subjectAreaService) {
        this.subjectAreaService = subjectAreaService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SubjectAreaResponse create(@RequestBody SubjectAreaRequest request) {

        SubjectArea sa = subjectAreaService.create(
                request.getName(),
                request.getDescription()
        );

        return toResponse(sa);
    }

    @GetMapping
    public List<SubjectAreaResponse> getAll() {
        return subjectAreaService.getAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public SubjectAreaResponse getById(@PathVariable String id) {
        return toResponse(subjectAreaService.getById(
                java.util.UUID.fromString(id)
        ));
    }

    private SubjectAreaResponse toResponse(SubjectArea sa) {
        SubjectAreaResponse res = new SubjectAreaResponse();
        res.setId(sa.getId());
        res.setName(sa.getName());
        res.setDescription(sa.getDescription());
        res.setActive(sa.isActive());
        return res;
    }
}

