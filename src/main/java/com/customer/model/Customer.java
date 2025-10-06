package com.customer.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "customer", indexes = {
    @Index(name = "idx_company_name", columnList = "companyName"),
    @Index(name = "idx_contact_name", columnList = "contactName"),
    @Index(name = "idx_contact_email", columnList = "contactEmail"),
    @Index(name = "idx_phone", columnList = "phone")
})
public class Customer {

    @Id
    @Column(name = "customer_id", length = 5, nullable = false)
    @NotBlank(message = "Customer ID is required")
    @Size(min = 5, max = 5, message = "Customer ID must be exactly 5 characters")
    private String customerId;

    @Column(name = "company_name", nullable = false, length = 40)
    @NotBlank(message = "Company name is required")
    @Size(max = 40, message = "Company name must not exceed 40 characters")
    private String companyName;

    @Column(name = "contact_name", length = 30)
    @Size(max = 30, message = "Contact name must not exceed 30 characters")
    private String contactName;

    @Column(name = "contact_title", length = 30)
    @Size(max = 30, message = "Contact title must not exceed 30 characters")
    private String contactTitle;

    @Column(name = "address", length = 60)
    @Size(max = 60, message = "Address must not exceed 60 characters")
    private String address;

    @Column(name = "city", length = 15)
    @Size(max = 15, message = "City must not exceed 15 characters")
    private String city;

    @Column(name = "region", length = 15)
    @Size(max = 15, message = "Region must not exceed 15 characters")
    private String region;

    @Column(name = "postal_code", length = 10)
    @Size(max = 10, message = "Postal code must not exceed 10 characters")
    private String postalCode;

    @Column(name = "country", length = 15)
    @Size(max = 15, message = "Country must not exceed 15 characters")
    private String country;

    @Column(name = "phone", length = 24)
    @Size(max = 24, message = "Phone must not exceed 24 characters")
    private String phone;

    @Column(name = "fax", length = 24)
    @Size(max = 24, message = "Fax must not exceed 24 characters")
    private String fax;

    @Column(name = "contact_email", length = 255)
    @Email(message = "Contact email must be valid")
    @Size(max = 255, message = "Contact email must not exceed 255 characters")
    private String contactEmail;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Constructors
    public Customer() {
    }

    public Customer(String customerId, String companyName) {
        this.customerId = customerId;
        this.companyName = companyName;
    }

    // Getters and Setters
    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactTitle() {
        return contactTitle;
    }

    public void setContactTitle(String contactTitle) {
        this.contactTitle = contactTitle;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Customer customer = (Customer) o;
        return Objects.equals(customerId, customer.customerId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(customerId);
    }

    @Override
    public String toString() {
        return "Customer{" +
                "customerId='" + customerId + '\'' +
                ", companyName='" + companyName + '\'' +
                ", contactName='" + contactName + '\'' +
                ", contactEmail='" + contactEmail + '\'' +
                '}';
    }
}
