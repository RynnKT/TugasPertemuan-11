/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pertemuan_11;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 *
 * @author Ridho
 */
@Entity
@Table(name = "ligasawah")
@NamedQueries({
    @NamedQuery(name = "Ligasawah.findAll", query = "SELECT l FROM Ligasawah l"),
    @NamedQuery(name = "Ligasawah.findByNomor", query = "SELECT l FROM Ligasawah l WHERE l.nomor = :nomor"),
    @NamedQuery(name = "Ligasawah.findByKlub", query = "SELECT l FROM Ligasawah l WHERE l.klub = :klub"),
    @NamedQuery(name = "Ligasawah.findByKotaasal", query = "SELECT l FROM Ligasawah l WHERE l.kotaasal = :kotaasal"),
    @NamedQuery(name = "Ligasawah.findByPointkemenangan", query = "SELECT l FROM Ligasawah l WHERE l.pointkemenangan = :pointkemenangan")})
public class Ligasawah implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "nomor")
    private Integer nomor;
    @Column(name = "klub")
    private String klub;
    @Column(name = "kotaasal")
    private String kotaasal;
    @Column(name = "pointkemenangan")
    private Integer pointkemenangan;

    public Ligasawah() {
    }

    public Ligasawah(Integer nomor) {
        this.nomor = nomor;
    }

    public Integer getNomor() {
        return nomor;
    }

    public void setNomor(Integer nomor) {
        this.nomor = nomor;
    }

    public String getKlub() {
        return klub;
    }

    public void setKlub(String klub) {
        this.klub = klub;
    }

    public String getKotaasal() {
        return kotaasal;
    }

    public void setKotaasal(String kotaasal) {
        this.kotaasal = kotaasal;
    }

    public Integer getPointkemenangan() {
        return pointkemenangan;
    }

    public void setPointkemenangan(Integer pointkemenangan) {
        this.pointkemenangan = pointkemenangan;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (nomor != null ? nomor.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Ligasawah)) {
            return false;
        }
        Ligasawah other = (Ligasawah) object;
        if ((this.nomor == null && other.nomor != null) || (this.nomor != null && !this.nomor.equals(other.nomor))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "pertemuan_11.Ligasawah[ nomor=" + nomor + " ]";
    }
    
}
