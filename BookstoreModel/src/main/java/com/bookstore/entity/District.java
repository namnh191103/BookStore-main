package com.bookstore.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "districts")
public class District extends IdBasedEntity {

    @Column(nullable = false, columnDefinition = "nvarchar(45)")
    private String name;

    @ManyToOne
    @JoinColumn(name = "city_id")
    private City city;

    public District() {

    }

    public District(String name, City city) {
        this.name = name;
        this.city = city;
    }
    

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    @Override
    public String toString() {
        return "District [id=" + id + ", name=" + name + "]";
    }

    
}
