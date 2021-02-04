/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author My-Pc
 */
@Entity
@Table(name = "tasks")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Tasks.findAll", query = "SELECT t FROM Tasks t")
    , @NamedQuery(name = "Tasks.findById", query = "SELECT t FROM Tasks t WHERE t.id = :id")
    , @NamedQuery(name = "Tasks.findByDescription", query = "SELECT t FROM Tasks t WHERE t.description = :description")
    , @NamedQuery(name = "Tasks.findByStartDate", query = "SELECT t FROM Tasks t WHERE t.startDate = :startDate")
    , @NamedQuery(name = "Tasks.findByEndDate", query = "SELECT t FROM Tasks t WHERE t.endDate = :endDate")
    , @NamedQuery(name = "Tasks.findByStatus", query = "SELECT t FROM Tasks t WHERE t.status = :status")})
public class Tasks implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @Column(name = "description")
    private String description;
    @Column(name = "startDate")
    private String startDate;
    @Column(name = "endDate")
    private String endDate;
    @Basic(optional = false)
    @Column(name = "status")
    private String status;
    
    @JoinTable(name = "employees_tasks", joinColumns = {
        @JoinColumn(name = "taskID", referencedColumnName = "id")}, inverseJoinColumns = {
        @JoinColumn(name = "employeeID", referencedColumnName = "id")})
    @ManyToMany(fetch = FetchType.EAGER)
    private List<Employees> employeesList;
    
    @JoinColumn(name = "project", referencedColumnName = "id")
    @ManyToOne
    private Projects project;

    public Tasks() {
    }

    public Tasks(Integer id) {
        this.id = id;
    }

    public Tasks(Integer id, String description, String status) {
        this.id = id;
        this.description = description;
        this.status = status;
    }
    
    public Tasks(String description, Projects project, List<Employees> employeesList, String startDate, String endDate, String status) {
        this.description = description;
        this.project = project;
        this.employeesList = employeesList;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
    }
    
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @XmlTransient
    public List<Employees> getEmployeesList() {
        return employeesList;
    }

    public void setEmployeesList(List<Employees> employeesList) {
        this.employeesList = employeesList;
    }

    public Projects getProject() {
        return project;
    }

    public void setProject(Projects project) {
        this.project = project;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Tasks)) {
            return false;
        }
        Tasks other = (Tasks) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return id + " " + description;
    }    
}
