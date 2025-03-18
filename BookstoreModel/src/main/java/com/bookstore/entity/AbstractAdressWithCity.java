package com.bookstore.entity;

import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Transient;

@MappedSuperclass
public class AbstractAdressWithCity extends AbstractAddress{
    @ManyToOne
    @JoinColumn(name = "city_id")
    protected City city;

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }



    @Override
    public String toString() {
        String address = lastName;

        if (lastName != null && !lastName.isEmpty()) address += " " + firstName;
        else address = firstName;

        if (addressLine != null && !addressLine.isEmpty()) address += ", " + addressLine;

        if (!ward.isEmpty()) address += ", " + ward;

        if (district != null && !district.isEmpty()) address += ", " + district + ", ";

        address += city.getName();

        if (!phoneNumber.isEmpty()) address += ". Số điện thoại: " + phoneNumber;

        return address;
    }

}
