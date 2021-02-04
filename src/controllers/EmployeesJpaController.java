/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import controllers.exceptions.NonexistentEntityException;
import entities.Employees;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import entities.User;
import entities.Tasks;
import java.util.ArrayList;
import java.util.List;
import entities.Technologies;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author My-Pc
 */
public class EmployeesJpaController implements Serializable {

    public EmployeesJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Employees employees) {
        if (employees.getTasksList() == null) {
            employees.setTasksList(new ArrayList<Tasks>());
        }
        if (employees.getTechnologiesList() == null) {
            employees.setTechnologiesList(new ArrayList<Technologies>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            User user = employees.getUser();
            if (user != null) {
                user = em.getReference(user.getClass(), user.getId());
                employees.setUser(user);
            }
            List<Tasks> attachedTasksList = new ArrayList<Tasks>();
            for (Tasks tasksListTasksToAttach : employees.getTasksList()) {
                tasksListTasksToAttach = em.getReference(tasksListTasksToAttach.getClass(), tasksListTasksToAttach.getId());
                attachedTasksList.add(tasksListTasksToAttach);
            }
            employees.setTasksList(attachedTasksList);
            List<Technologies> attachedTechnologiesList = new ArrayList<Technologies>();
            for (Technologies technologiesListTechnologiesToAttach : employees.getTechnologiesList()) {
                technologiesListTechnologiesToAttach = em.getReference(technologiesListTechnologiesToAttach.getClass(), technologiesListTechnologiesToAttach.getId());
                attachedTechnologiesList.add(technologiesListTechnologiesToAttach);
            }
            employees.setTechnologiesList(attachedTechnologiesList);
            em.persist(employees);
            if (user != null) {
                Employees oldEmployeesIdOfUser = user.getEmployeesId();
                if (oldEmployeesIdOfUser != null) {
                    oldEmployeesIdOfUser.setUser(null);
                    oldEmployeesIdOfUser = em.merge(oldEmployeesIdOfUser);
                }
                user.setEmployeesId(employees);
                user = em.merge(user);
            }
            for (Tasks tasksListTasks : employees.getTasksList()) {
                tasksListTasks.getEmployeesList().add(employees);
                tasksListTasks = em.merge(tasksListTasks);
            }
            for (Technologies technologiesListTechnologies : employees.getTechnologiesList()) {
                technologiesListTechnologies.getEmployeesList().add(employees);
                technologiesListTechnologies = em.merge(technologiesListTechnologies);
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Employees employees) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Employees persistentEmployees = em.find(Employees.class, employees.getId());
            User userOld = persistentEmployees.getUser();
            User userNew = employees.getUser();
            List<Tasks> tasksListOld = persistentEmployees.getTasksList();
            List<Tasks> tasksListNew = employees.getTasksList();
            List<Technologies> technologiesListOld = persistentEmployees.getTechnologiesList();
            List<Technologies> technologiesListNew = employees.getTechnologiesList();
            if (userNew != null) {
                userNew = em.getReference(userNew.getClass(), userNew.getId());
                employees.setUser(userNew);
            }
            List<Tasks> attachedTasksListNew = new ArrayList<Tasks>();
            for (Tasks tasksListNewTasksToAttach : tasksListNew) {
                tasksListNewTasksToAttach = em.getReference(tasksListNewTasksToAttach.getClass(), tasksListNewTasksToAttach.getId());
                attachedTasksListNew.add(tasksListNewTasksToAttach);
            }
            tasksListNew = attachedTasksListNew;
            employees.setTasksList(tasksListNew);
            List<Technologies> attachedTechnologiesListNew = new ArrayList<Technologies>();
            for (Technologies technologiesListNewTechnologiesToAttach : technologiesListNew) {
                technologiesListNewTechnologiesToAttach = em.getReference(technologiesListNewTechnologiesToAttach.getClass(), technologiesListNewTechnologiesToAttach.getId());
                attachedTechnologiesListNew.add(technologiesListNewTechnologiesToAttach);
            }
            technologiesListNew = attachedTechnologiesListNew;
            employees.setTechnologiesList(technologiesListNew);
            employees = em.merge(employees);
            if (userOld != null && !userOld.equals(userNew)) {
                userOld.setEmployeesId(null);
                userOld = em.merge(userOld);
            }
            if (userNew != null && !userNew.equals(userOld)) {
                Employees oldEmployeesIdOfUser = userNew.getEmployeesId();
                if (oldEmployeesIdOfUser != null) {
                    oldEmployeesIdOfUser.setUser(null);
                    oldEmployeesIdOfUser = em.merge(oldEmployeesIdOfUser);
                }
                userNew.setEmployeesId(employees);
                userNew = em.merge(userNew);
            }
            for (Tasks tasksListOldTasks : tasksListOld) {
                if (!tasksListNew.contains(tasksListOldTasks)) {
                    tasksListOldTasks.getEmployeesList().remove(employees);
                    tasksListOldTasks = em.merge(tasksListOldTasks);
                }
            }
            for (Tasks tasksListNewTasks : tasksListNew) {
                if (!tasksListOld.contains(tasksListNewTasks)) {
                    tasksListNewTasks.getEmployeesList().add(employees);
                    tasksListNewTasks = em.merge(tasksListNewTasks);
                }
            }
            for (Technologies technologiesListOldTechnologies : technologiesListOld) {
                if (!technologiesListNew.contains(technologiesListOldTechnologies)) {
                    technologiesListOldTechnologies.getEmployeesList().remove(employees);
                    technologiesListOldTechnologies = em.merge(technologiesListOldTechnologies);
                }
            }
            for (Technologies technologiesListNewTechnologies : technologiesListNew) {
                if (!technologiesListOld.contains(technologiesListNewTechnologies)) {
                    technologiesListNewTechnologies.getEmployeesList().add(employees);
                    technologiesListNewTechnologies = em.merge(technologiesListNewTechnologies);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = employees.getId();
                if (findEmployees(id) == null) {
                    throw new NonexistentEntityException("The employees with id " + id + " no longer exists.");
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
            Employees employees;
            try {
                employees = em.getReference(Employees.class, id);
                employees.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The employees with id " + id + " no longer exists.", enfe);
            }
            User user = employees.getUser();
            if (user != null) {
                user.setEmployeesId(null);
                user = em.merge(user);
            }
            List<Tasks> tasksList = employees.getTasksList();
            for (Tasks tasksListTasks : tasksList) {
                tasksListTasks.getEmployeesList().remove(employees);
                tasksListTasks = em.merge(tasksListTasks);
            }
            List<Technologies> technologiesList = employees.getTechnologiesList();
            for (Technologies technologiesListTechnologies : technologiesList) {
                technologiesListTechnologies.getEmployeesList().remove(employees);
                technologiesListTechnologies = em.merge(technologiesListTechnologies);
            }
            em.remove(employees);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Employees> findEmployeesEntities() {
        return findEmployeesEntities(true, -1, -1);
    }

    public List<Employees> findEmployeesEntities(int maxResults, int firstResult) {
        return findEmployeesEntities(false, maxResults, firstResult);
    }

    private List<Employees> findEmployeesEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Employees.class));
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

    public Employees findEmployees(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Employees.class, id);
        } finally {
            em.close();
        }
    }

    public int getEmployeesCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Employees> rt = cq.from(Employees.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
