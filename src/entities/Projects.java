/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author My-Pc
 */
@Entity
@Table(name = "projects")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Projects.findAll", query = "SELECT p FROM Projects p")
    , @NamedQuery(name = "Projects.findById", query = "SELECT p FROM Projects p WHERE p.id = :id")
    , @NamedQuery(name = "Projects.findByProjectName", query = "SELECT p FROM Projects p WHERE p.projectName = :projectName")
    , @NamedQuery(name = "Projects.findBySector", query = "SELECT p FROM Projects p WHERE p.sector = :sector")
    , @NamedQuery(name = "Projects.findByClient", query = "SELECT p FROM Projects p WHERE p.client = :client")
    , @NamedQuery(name = "Projects.findByStartDate", query = "SELECT p FROM Projects p WHERE p.startDate = :startDate")
    , @NamedQuery(name = "Projects.findByDueDate", query = "SELECT p FROM Projects p WHERE p.dueDate = :dueDate")})
public class Projects implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Column(name = "projectName")
    private String projectName;
    @Basic(optional = false)
    @Column(name = "sector")
    private String sector;
    @Basic(optional = false)
    @Column(name = "client")
    private String client;
    @Basic(optional = false)
    @Column(name = "startDate")
    private String startDate;
    @Basic(optional = false)
    @Column(name = "dueDate")
    private String dueDate;
    
    @JoinTable(name = "projects_technologies", joinColumns = {
        @JoinColumn(name = "projectID", referencedColumnName = "id")}, inverseJoinColumns = {
        @JoinColumn(name = "technologyID", referencedColumnName = "id")})
    @ManyToMany(fetch = FetchType.EAGER)
    private List<Technologies> technologiesList;
    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "project")
    private List<Tasks> tasksList;

    public Projects() {
    }

    public Projects(Integer id) {
        this.id = id;
    }

    public Projects(Integer id, String sector, String client, String startDate, String dueDate) {
        this.id = id;
        this.sector = sector;
        this.client = client;
        this.startDate = startDate;
        this.dueDate = dueDate;
    }
    
    public Projects(String projectName, String sector, List<Technologies> technologiesList, String client, String startDate, String dueDate) {
        this.projectName = projectName;
        this.sector = sector;
        this.technologiesList = technologiesList;
        this.client = client;
        this.startDate = startDate;
        this.dueDate = dueDate;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getSector() {
        return sector;
    }

    public void setSector(String sector) {
        this.sector = sector;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    @XmlTransient
    public List<Technologies> getTechnologiesList() {
        return technologiesList;
    }

    public void setTechnologiesList(List<Technologies> technologiesList) {
        this.technologiesList = technologiesList;
    }

    @XmlTransient
    public List<Tasks> getTasksList() {
        return tasksList;
    }

    public void setTasksList(List<Tasks> tasksList) {
        this.tasksList = tasksList;
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
        if (!(object instanceof Projects)) {
            return false;
        }
        Projects other = (Projects) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return id + " " + projectName;
    }
    
}
