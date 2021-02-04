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
@Table(name = "technologies")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Technologies.findAll", query = "SELECT t FROM Technologies t")
    , @NamedQuery(name = "Technologies.findById", query = "SELECT t FROM Technologies t WHERE t.id = :id")
    , @NamedQuery(name = "Technologies.findByTechnology", query = "SELECT t FROM Technologies t WHERE t.technology = :technology")})
public class Technologies implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Column(name = "technology")
    private String technology;
    
    @JoinTable(name = "employees_technologies", joinColumns = {
        @JoinColumn(name = "technologyID", referencedColumnName = "id")}, inverseJoinColumns = {
        @JoinColumn(name = "employeeID", referencedColumnName = "id")})
    @ManyToMany(fetch = FetchType.EAGER)
    private List<Employees> employeesList;
    
    @ManyToMany(fetch = FetchType.EAGER, mappedBy = "technologiesList")
    private List<Projects> projectsList;

    public Technologies() {
    }

    public Technologies(Integer id) {
        this.id = id;
    }
    
    public Technologies(String technology) {
        this.technology = technology;
    }

    public Technologies(Integer id, String technology) {
        this.id = id;
        this.technology = technology;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTechnology() {
        return technology;
    }

    public void setTechnology(String technology) {
        this.technology = technology;
    }

    @XmlTransient
    public List<Employees> getEmployeesList() {
        return employeesList;
    }

    public void setEmployeesList(List<Employees> employeesList) {
        this.employeesList = employeesList;
    }

    @XmlTransient
    public List<Projects> getProjectsList() {
        return projectsList;
    }

    public void setProjectsList(List<Projects> projectsList) {
        this.projectsList = projectsList;
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
        if (!(object instanceof Technologies)) {
            return false;
        }
        Technologies other = (Technologies) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return technology;
    }
    
}
