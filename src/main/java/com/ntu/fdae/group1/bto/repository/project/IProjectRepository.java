package com.ntu.fdae.group1.bto.repository.project;

import com.ntu.fdae.group1.bto.models.project.Project;

public interface IProjectRepository {
    void save(Project project);
    Project findById(String projectId);
}
