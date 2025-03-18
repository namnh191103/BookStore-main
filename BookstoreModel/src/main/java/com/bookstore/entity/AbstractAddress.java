package com.bookstore.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class AbstractAddress extends IdBasedEntity {

    @Column(name = "first_name", nullable = false, columnDefinition = "nvarchar(45)")
    protected String firstName;

    @Column(name = "last_name", nullable = false, columnDefinition = "nvarchar(45)")
    protected String lastName;

    @Column(name = "phone_number", nullable = false, length = 15)
    protected String phoneNumber;

    @Column(name = "address_line", nullable = false, columnDefinition = "nvarchar(100)")
    protected String addressLine;

    @Column(nullable = false, columnDefinition = "nvarchar(45)")
    protected String ward;

    @Column(nullable = false, columnDefinition = "nvarchar(45)")
    protected String district;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddressLine() {
        return addressLine;
    }

    public void setAddressLine(String addressLine) {
        this.addressLine = addressLine;
    }

    public String getWard() {
        return ward;
    }

    public void setWard(String ward) {
        this.ward = ward;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }
}
