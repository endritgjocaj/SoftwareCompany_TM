/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import controllers.exceptions.NonexistentEntityException;
import controllers.exceptions.PreexistingEntityException;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import entities.Employees;
import entities.User;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;

/**
 *
 * @author My-Pc
 */
public class UserJpaController implements Serializable {

    public UserJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(User user) throws PreexistingEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Employees employeesId = user.getEmployeesId();
            if (employeesId != null) {
                employeesId = em.getReference(employeesId.getClass(), employeesId.getId());
                user.setEmployeesId(employeesId);
            }
            em.persist(user);
            if (employeesId != null) {
                User oldUserOfEmployeesId = employeesId.getUser();
                if (oldUserOfEmployeesId != null) {
                    oldUserOfEmployeesId.setEmployeesId(null);
                    oldUserOfEmployeesId = em.merge(oldUserOfEmployeesId);
                }
                employeesId.setUser(user);
                employeesId = em.merge(employeesId);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findUser(user.getId()) != null) {
                throw new PreexistingEntityException("User " + user + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(User user) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            User persistentUser = em.find(User.class, user.getId());
            Employees employeesIdOld = persistentUser.getEmployeesId();
            Employees employeesIdNew = user.getEmployeesId();
            if (employeesIdNew != null) {
                employeesIdNew = em.getReference(employeesIdNew.getClass(), employeesIdNew.getId());
                user.setEmployeesId(employeesIdNew);
            }
            user = em.merge(user);
            if (employeesIdOld != null && !employeesIdOld.equals(employeesIdNew)) {
                employeesIdOld.setUser(null);
                employeesIdOld = em.merge(employeesIdOld);
            }
            if (employeesIdNew != null && !employeesIdNew.equals(employeesIdOld)) {
                User oldUserOfEmployeesId = employeesIdNew.getUser();
                if (oldUserOfEmployeesId != null) {
                    oldUserOfEmployeesId.setEmployeesId(null);
                    oldUserOfEmployeesId = em.merge(oldUserOfEmployeesId);
                }
                employeesIdNew.setUser(user);
                employeesIdNew = em.merge(employeesIdNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = user.getId();
                if (findUser(id) == null) {
                    throw new NonexistentEntityException("The user with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }
    
    public User findByUsernameAndPassword(String username, String password) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes("UTF-8"));
            String encoded = Base64.getEncoder().encodeToString(hash);
            TypedQuery<User> query = em.createNamedQuery("User.loginUser", User.class).setParameter("username", username).setParameter("password", encoded);
            return query.getSingleResult();
        } catch (Exception ex) {
            
        } finally {
            if (em != null) {
                em.close();
            }
        }
        return null;
    }
    
    public void destroy(Integer id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            User user;
            try {
                user = em.getReference(User.class, id);
                user.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The user with id " + id + " no longer exists.", enfe);
            }
            Employees employeesId = user.getEmployeesId();
            if (employeesId != null) {
                employeesId.setUser(null);
                employeesId = em.merge(employeesId);
            }
            em.remove(user);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<User> findUserEntities() {
        return findUserEntities(true, -1, -1);
    }

    public List<User> findUserEntities(int maxResults, int firstResult) {
        return findUserEntities(false, maxResults, firstResult);
    }

    private List<User> findUserEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(User.class));
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

    public User findUser(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(User.class, id);
        } finally {
            em.close();
        }
    }

    public int getUserCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<User> rt = cq.from(User.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
}
