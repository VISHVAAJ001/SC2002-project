package com.ntu.fdae.group1.bto.controllers.project;

import com.ntu.fdae.group1.bto.services.booking.IDataManager;
import com.ntu.fdae.group1.bto.models.project.Project;
import com.ntu.fdae.group1.bto.models.user.HDBManager;
import com.ntu.fdae.group1.bto.models.user.User;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.List;

public class ProjectController {
    private Map<String, Project> projectRepo;
    private IDataManager dataManager;

    public ProjectController(Map<String, Project> projMap, IDataManager dataMgr) {
        this.projectRepo = projMap;
        this.dataManager = dataMgr;
    }

    public Project createProject(String projectId, String projectName, String neighbourhood) {
        // Project project = new Project(projectId, projectName, neighbourhood);
        // projectRepo.put(projectId, project);
        // dataManager.saveProjects(projectRepo);

        // return project;
        return null;
    }

    public boolean editProject(HDBManager manager, String projectId, String projectName, String neighbourhood) {
        // Project project = projectRepo.get(projectId);
        // if (project != null){
        // project.setProjectName(newName);
        // project.setNeighbourhood(newNeighbourhood);
        // dataManager.saveProjects(projectRepo);
        // return true;
        // }
        return false;
    }

    public boolean deleteProject(HDBManager manager, String projectId) {
        // if (projectRepo.containsKey(projectId)){
        // projectRepo.remove(projectId);
        // dataManager.saveProjects(projectRepo);
        // return true;
        // }
        return false;
    }

    public boolean toggleVisibility(HDBManager manager, String projectId) {
        // Project project = projectRepo.get(projectId);
        // if (project != null){
        // project.setVisibility(!project.isVisible());
        // dataManager.saveProjects(projectRepo);
        // return true;
        // }
        return false;
    }

    public List<Project> getVisibleProjectsForUser(User user) {

        return null;
    }

    public List<Project> getAllProjects(HDBManager manager) {

        return null;
    }

    public List<Project> getProjectsManagedBy(String managerNRIC) {

        return null;
    }

    public Project findProjectById(String projectId) {

        return null;
    }

}
