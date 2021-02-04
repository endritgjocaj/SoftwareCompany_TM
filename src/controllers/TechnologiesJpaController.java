/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import controllers.exceptions.NonexistentEntityException;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import entities.Employees;
import java.util.ArrayList;
import java.util.List;
import entities.Projects;
import entities.Technologies;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author My-Pc
 */
public class TechnologiesJpaController implements Serializable {

    public TechnologiesJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Technologies technologies) {
        if (technologies.getEmployeesList() == null) {
            technologies.setEmployeesList(new ArrayList<Employees>());
        }
        if (technologies.getProjectsList() == null) {
            technologies.setProjectsList(new ArrayList<Projects>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<Employees> attachedEmployeesList = new ArrayList<Employees>();
            for (Employees employeesListEmployeesToAttach : technologies.getEmployeesList()) {
                employeesListEmployeesToAttach = em.getReference(employeesListEmployeesToAttach.getClass(), employeesListEmployeesToAttach.getId());
                attachedEmployeesList.add(employeesListEmployeesToAttach);
            }
            technologies.setEmployeesList(attachedEmployeesList);
            List<Projects> attachedProjectsList = new ArrayList<Projects>();
            for (Projects projectsListProjectsToAttach : technologies.getProjectsList()) {
                projectsListProjectsToAttach = em.getReference(projectsListProjectsToAttach.getClass(), projectsListProjectsToAttach.getId());
                attachedProjectsList.add(projectsListProjectsToAttach);
            }
            technologies.setProjectsList(attachedProjectsList);
            em.persist(technologies);
            for (Employees employeesListEmployees : technologies.getEmployeesList()) {
                employeesListEmployees.getTechnologiesList().add(technologies);
                employeesListEmployees = em.merge(employeesListEmployees);
            }
            for (Projects projectsListProjects : technologies.getProjectsList()) {
                projectsListProjects.getTechnologiesList().add(technologies);
                projectsListProjects = em.merge(projectsListProjects);
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Technologies technologies) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Technologies persistentTechnologies = em.find(Technologies.class, technologies.getId());
            List<Employees> employeesListOld = persistentTechnologies.getEmployeesList();
            List<Employees> employeesListNew = technologies.getEmployeesList();
            List<Projects> projectsListOld = persistentTechnologies.getProjectsList();
            List<Projects> projectsListNew = technologies.getProjectsList();
            List<Employees> attachedEmployeesListNew = new ArrayList<Employees>();
            for (Employees employeesListNewEmployeesToAttach : employeesListNew) {
                employeesListNewEmployeesToAttach = em.getReference(employeesListNewEmployeesToAttach.getClass(), employeesListNewEmployeesToAttach.getId());
                attachedEmployeesListNew.add(employeesListNewEmployeesToAttach);
            }
            employeesListNew = attachedEmployeesListNew;
            technologies.setEmployeesList(employeesListNew);
            List<Projects> attachedProjectsListNew = new ArrayList<Projects>();
            for (Projects projectsListNewProjectsToAttach : projectsListNew) {
                projectsListNewProjectsToAttach = em.getReference(projectsListNewProjectsToAttach.getClass(), projectsListNewProjectsToAttach.getId());
                attachedProjectsListNew.add(projectsListNewProjectsToAttach);
            }
            projectsListNew = attachedProjectsListNew;
            technologies.setProjectsList(projectsListNew);
            technologies = em.merge(technologies);
            for (Employees employeesListOldEmployees : employeesListOld) {
                if (!employeesListNew.contains(employeesListOldEmployees)) {
                    employeesListOldEmployees.getTechnologiesList().remove(technologies);
                    employeesListOldEmployees = em.merge(employeesListOldEmployees);
                }
            }
            for (Employees employeesListNewEmployees : employeesListNew) {
                if (!employeesListOld.contains(employeesListNewEmployees)) {
                    employeesListNewEmployees.getTechnologiesList().add(technologies);
                    employeesListNewEmployees = em.merge(employeesListNewEmployees);
                }
            }
            for (Projects projectsListOldProjects : projectsListOld) {
                if (!projectsListNew.contains(projectsListOldProjects)) {
                    projectsListOldProjects.getTechnologiesList().remove(technologies);
                    projectsListOldProjects = em.merge(projectsListOldProjects);
                }
            }
            for (Projects projectsListNewProjects : projectsListNew) {
                if (!projectsListOld.contains(projectsListNewProjects)) {
                    projectsListNewProjects.getTechnologiesList().add(technologies);
                    projectsListNewProjects = em.merge(projectsListNewProjects);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = technologies.getId();
                if (findTechnologies(id) == null) {
                    throw new NonexistentEntityException("The technologies with id " + id + " no longer exists.");
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
            Technologies technologies;
            try {
                technologies = em.getReference(Technologies.class, id);
                technologies.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The technologies with id " + id + " no longer exists.", enfe);
            }
            List<Employees> employeesList = technologies.getEmployeesList();
            for (Employees employeesListEmployees : employeesList) {
                employeesListEmployees.getTechnologiesList().remove(technologies);
                employeesListEmployees = em.merge(employeesListEmployees);
            }
            List<Projects> projectsList = technologies.getProjectsList();
            for (Projects projectsListProjects : projectsList) {
                projectsListProjects.getTechnologiesList().remove(technologies);
                projectsListProjects = em.merge(projectsListProjects);
            }
            em.remove(technologies);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Technologies> findTechnologiesEntities() {
        return findTechnologiesEntities(true, -1, -1);
    }

    public List<Technologies> findTechnologiesEntities(int maxResults, int firstResult) {
        return findTechnologiesEntities(false, maxResults, firstResult);
    }

    private List<Technologies> findTechnologiesEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Technologies.class));
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

    public Technologies findTechnologies(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Technologies.class, id);
        } finally {
            em.close();
        }
    }

    public int getTechnologiesCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Technologies> rt = cq.from(Technologies.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
