/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import controllers.exceptions.NonexistentEntityException;
import entities.Projects;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import entities.Technologies;
import java.util.ArrayList;
import java.util.List;
import entities.Tasks;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author My-Pc
 */
public class ProjectsJpaController implements Serializable {

    public ProjectsJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Projects projects) {
        if (projects.getTechnologiesList() == null) {
            projects.setTechnologiesList(new ArrayList<Technologies>());
        }
        if (projects.getTasksList() == null) {
            projects.setTasksList(new ArrayList<Tasks>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<Technologies> attachedTechnologiesList = new ArrayList<Technologies>();
            for (Technologies technologiesListTechnologiesToAttach : projects.getTechnologiesList()) {
                technologiesListTechnologiesToAttach = em.getReference(technologiesListTechnologiesToAttach.getClass(), technologiesListTechnologiesToAttach.getId());
                attachedTechnologiesList.add(technologiesListTechnologiesToAttach);
            }
            projects.setTechnologiesList(attachedTechnologiesList);
            List<Tasks> attachedTasksList = new ArrayList<Tasks>();
            for (Tasks tasksListTasksToAttach : projects.getTasksList()) {
                tasksListTasksToAttach = em.getReference(tasksListTasksToAttach.getClass(), tasksListTasksToAttach.getId());
                attachedTasksList.add(tasksListTasksToAttach);
            }
            projects.setTasksList(attachedTasksList);
            em.persist(projects);
            for (Technologies technologiesListTechnologies : projects.getTechnologiesList()) {
                technologiesListTechnologies.getProjectsList().add(projects);
                technologiesListTechnologies = em.merge(technologiesListTechnologies);
            }
            for (Tasks tasksListTasks : projects.getTasksList()) {
                Projects oldProjectOfTasksListTasks = tasksListTasks.getProject();
                tasksListTasks.setProject(projects);
                tasksListTasks = em.merge(tasksListTasks);
                if (oldProjectOfTasksListTasks != null) {
                    oldProjectOfTasksListTasks.getTasksList().remove(tasksListTasks);
                    oldProjectOfTasksListTasks = em.merge(oldProjectOfTasksListTasks);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Projects projects) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Projects persistentProjects = em.find(Projects.class, projects.getId());
            List<Technologies> technologiesListOld = persistentProjects.getTechnologiesList();
            List<Technologies> technologiesListNew = projects.getTechnologiesList();
            List<Tasks> tasksListOld = persistentProjects.getTasksList();
            List<Tasks> tasksListNew = projects.getTasksList();
            List<Technologies> attachedTechnologiesListNew = new ArrayList<Technologies>();
            for (Technologies technologiesListNewTechnologiesToAttach : technologiesListNew) {
                technologiesListNewTechnologiesToAttach = em.getReference(technologiesListNewTechnologiesToAttach.getClass(), technologiesListNewTechnologiesToAttach.getId());
                attachedTechnologiesListNew.add(technologiesListNewTechnologiesToAttach);
            }
            technologiesListNew = attachedTechnologiesListNew;
            projects.setTechnologiesList(technologiesListNew);
            List<Tasks> attachedTasksListNew = new ArrayList<Tasks>();
            for (Tasks tasksListNewTasksToAttach : tasksListNew) {
                tasksListNewTasksToAttach = em.getReference(tasksListNewTasksToAttach.getClass(), tasksListNewTasksToAttach.getId());
                attachedTasksListNew.add(tasksListNewTasksToAttach);
            }
            tasksListNew = attachedTasksListNew;
            projects.setTasksList(tasksListNew);
            projects = em.merge(projects);
            for (Technologies technologiesListOldTechnologies : technologiesListOld) {
                if (!technologiesListNew.contains(technologiesListOldTechnologies)) {
                    technologiesListOldTechnologies.getProjectsList().remove(projects);
                    technologiesListOldTechnologies = em.merge(technologiesListOldTechnologies);
                }
            }
            for (Technologies technologiesListNewTechnologies : technologiesListNew) {
                if (!technologiesListOld.contains(technologiesListNewTechnologies)) {
                    technologiesListNewTechnologies.getProjectsList().add(projects);
                    technologiesListNewTechnologies = em.merge(technologiesListNewTechnologies);
                }
            }
            for (Tasks tasksListOldTasks : tasksListOld) {
                if (!tasksListNew.contains(tasksListOldTasks)) {
                    tasksListOldTasks.setProject(null);
                    tasksListOldTasks = em.merge(tasksListOldTasks);
                }
            }
            for (Tasks tasksListNewTasks : tasksListNew) {
                if (!tasksListOld.contains(tasksListNewTasks)) {
                    Projects oldProjectOfTasksListNewTasks = tasksListNewTasks.getProject();
                    tasksListNewTasks.setProject(projects);
                    tasksListNewTasks = em.merge(tasksListNewTasks);
                    if (oldProjectOfTasksListNewTasks != null && !oldProjectOfTasksListNewTasks.equals(projects)) {
                        oldProjectOfTasksListNewTasks.getTasksList().remove(tasksListNewTasks);
                        oldProjectOfTasksListNewTasks = em.merge(oldProjectOfTasksListNewTasks);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = projects.getId();
                if (findProjects(id) == null) {
                    throw new NonexistentEntityException("The projects with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Projects projects;
            try {
                projects = em.getReference(Projects.class, id);
                projects.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The projects with id " + id + " no longer exists.", enfe);
            }
            List<Technologies> technologiesList = projects.getTechnologiesList();
            for (Technologies technologiesListTechnologies : technologiesList) {
                technologiesListTechnologies.getProjectsList().remove(projects);
                technologiesListTechnologies = em.merge(technologiesListTechnologies);
            }
            List<Tasks> tasksList = projects.getTasksList();
            for (Tasks tasksListTasks : tasksList) {
                tasksListTasks.setProject(null);
                tasksListTasks = em.merge(tasksListTasks);
            }
            em.remove(projects);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Projects> findProjectsEntities() {
        return findProjectsEntities(true, -1, -1);
    }

    public List<Projects> findProjectsEntities(int maxResults, int firstResult) {
        return findProjectsEntities(false, maxResults, firstResult);
    }

    private List<Projects> findProjectsEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Projects.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public Projects findProjects(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Projects.class, id);
        } finally {
            em.close();
        }
    }

    public int getProjectsCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Projects> rt = cq.from(Projects.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
