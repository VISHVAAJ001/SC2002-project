package com.ntu.fdae.group1.bto.controllers.project;

import com.ntu.fdae.group1.bto.services.booking.IDataManager;
import com.ntu.fdae.group1.bto.models.*;
import com.ntu.fdae.group1.bto.models.project.*;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.List;


public class ProjectController {
    private Map<String, Project> projectRepo;
    private IDataManager dataManager;

    public ProjectController(Map<String, Project> projMap, IDataManager dataMgr){
        this.projectRepo = projMap;
        this.dataManager = dataMgr;
    }

    public Project createProject(String projectId, String projectName, String neighbourhood){
        Project project = new Project(projectId, projectName, neighbourhood);
        projectRepo.put(projectId, project);
        dataManager.saveProjects(projectRepo);

        return project;

    }

    public boolean editProject(HDBManager manager, String projectId, String projectName, String neighbourhood){
        Project project = projectRepo.get(projectId);
        if (project != null){
            project.setProjectName(newName);
            project.setNeighbourhood(newNeighbourhood);
            dataManager.saveProjects(projectRepo);
            return true;
        }
        return false;
    }

    public boolean deleteProject(HDBManager manager, String projectId){
        if (projectRepo.containsKey(projectId)){
            projectRepo.remove(projectId);
            dataManager.saveProjects(projectRepo);
            return true;
        }
        return false;
    }

    public boolean toggleVisibility(HDBManager manager, String projectId){
        Project project = projectRepo.get(projectId);
        if (project != null){
            project.setVisibility(!project.isVisible());
            dataManager.saveProjects(projectRepo);
            return true;
        }
        return false;
    }

    public List<Project> getVisibleProjectsForUser(User user){

    }

    public List<Project> getAllProjects(HDBManager manager){

    }

    public List<Project> getProjectsManagedBy(String managerNRIC){

    }

    public Project findProjectById(String projectId){

    }
    

}
