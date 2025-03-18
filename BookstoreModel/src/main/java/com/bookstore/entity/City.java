package com.bookstore.entity;

import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "cities")
public class City extends IdBasedEntity {


    @Column(nullable = false, columnDefinition = "nvarchar(45)")
    private String name;

    @Column(nullable = false, length = 5)
    private String code;

    @OneToMany(mappedBy = "city")
    private Set<District> districts;

    public City() {
        
    }
    
    public City(Integer id) {
        this.id = id;
    }

    public City(String name, String code) {
        this.name = name;
        this.code = code;
    }

    public City(Integer id, String name, String code) {
        this.id = id;
        this.name = name;
        this.code = code;
    }




    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return "City [id=" + id + ", name=" + name + ", code=" + code + "]";
    }

    
}
