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
import entities.Projects;
import entities.Employees;
import entities.Tasks;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author My-Pc
 */
public class TasksJpaController implements Serializable {

    public TasksJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Tasks tasks) {
        if (tasks.getEmployeesList() == null) {
            tasks.setEmployeesList(new ArrayList<Employees>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Projects project = tasks.getProject();
            if (project != null) {
                project = em.getReference(project.getClass(), project.getId());
                tasks.setProject(project);
            }
            List<Employees> attachedEmployeesList = new ArrayList<Employees>();
            for (Employees employeesListEmployeesToAttach : tasks.getEmployeesList()) {
                employeesListEmployeesToAttach = em.getReference(employeesListEmployeesToAttach.getClass(), employeesListEmployeesToAttach.getId());
                attachedEmployeesList.add(employeesListEmployeesToAttach);
            }
            tasks.setEmployeesList(attachedEmployeesList);
            em.persist(tasks);
            if (project != null) {
                project.getTasksList().add(tasks);
                project = em.merge(project);
            }
            for (Employees employeesListEmployees : tasks.getEmployeesList()) {
                employeesListEmployees.getTasksList().add(tasks);
                employeesListEmployees = em.merge(employeesListEmployees);
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Tasks tasks) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Tasks persistentTasks = em.find(Tasks.class, tasks.getId());
            Projects projectOld = persistentTasks.getProject();
            Projects projectNew = tasks.getProject();
            List<Employees> employeesListOld = persistentTasks.getEmployeesList();
            List<Employees> employeesListNew = tasks.getEmployeesList();
            if (projectNew != null) {
                projectNew = em.getReference(projectNew.getClass(), projectNew.getId());
                tasks.setProject(projectNew);
            }
            List<Employees> attachedEmployeesListNew = new ArrayList<Employees>();
            for (Employees employeesListNewEmployeesToAttach : employeesListNew) {
                employeesListNewEmployeesToAttach = em.getReference(employeesListNewEmployeesToAttach.getClass(), employeesListNewEmployeesToAttach.getId());
                attachedEmployeesListNew.add(employeesListNewEmployeesToAttach);
            }
            employeesListNew = attachedEmployeesListNew;
            tasks.setEmployeesList(employeesListNew);
            tasks = em.merge(tasks);
            if (projectOld != null && !projectOld.equals(projectNew)) {
                projectOld.getTasksList().remove(tasks);
                projectOld = em.merge(projectOld);
            }
            if (projectNew != null && !projectNew.equals(projectOld)) {
                projectNew.getTasksList().add(tasks);
                projectNew = em.merge(projectNew);
            }
            for (Employees employeesListOldEmployees : employeesListOld) {
                if (!employeesListNew.contains(employeesListOldEmployees)) {
                    employeesListOldEmployees.getTasksList().remove(tasks);
                    employeesListOldEmployees = em.merge(employeesListOldEmployees);
                }
            }
            for (Employees employeesListNewEmployees : employeesListNew) {
                if (!employeesListOld.contains(employeesListNewEmployees)) {
                    employeesListNewEmployees.getTasksList().add(tasks);
                    employeesListNewEmployees = em.merge(employeesListNewEmployees);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = tasks.getId();
                if (findTasks(id) == null) {
                    throw new NonexistentEntityException("The tasks with id " + id + " no longer exists.");
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
            Tasks tasks;
            try {
                tasks = em.getReference(Tasks.class, id);
                tasks.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The tasks with id " + id + " no longer exists.", enfe);
            }
            Projects project = tasks.getProject();
            if (project != null) {
                project.getTasksList().remove(tasks);
                project = em.merge(project);
            }
            List<Employees> employeesList = tasks.getEmployeesList();
            for (Employees employeesListEmployees : employeesList) {
                employeesListEmployees.getTasksList().remove(tasks);
                employeesListEmployees = em.merge(employeesListEmployees);
            }
            em.remove(tasks);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Tasks> findTasksEntities() {
        return findTasksEntities(true, -1, -1);
    }

    public List<Tasks> findTasksEntities(int maxResults, int firstResult) {
        return findTasksEntities(false, maxResults, firstResult);
    }

    private List<Tasks> findTasksEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Tasks.class));
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

    public Tasks findTasks(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Tasks.class, id);
        } finally {
            em.close();
        }
    }

    public int getTasksCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Tasks> rt = cq.from(Tasks.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
