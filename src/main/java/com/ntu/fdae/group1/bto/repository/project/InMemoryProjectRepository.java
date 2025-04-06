package com.ntu.fdae.group1.bto.repository.project;

import com.ntu.fdae.group1.bto.models.project.Project;
import java.util.HashMap;
import java.util.Map;

public class InMemoryProjectRepository implements IProjectRepository {
    private Map<String, Project> projects = new HashMap<>();

    @Override
    public void save(Project project) {
        projects.put(project.getProjectId(), project);
    }

    @Override
    public Project findById(String projectId) {
        return projects.get(projectId);
    }
}
