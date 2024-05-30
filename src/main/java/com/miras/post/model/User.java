package com.miras.post.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.io.Serializable;
import java.util.UUID;

@Entity
@Table(name = "users")
@JsonIgnoreProperties({"createdDate", "modifiedDate", "password"})
public class User extends AuditModel implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotNull(message = "First name is required")
    @Size(max = 30, message = "Firstname should not be greater than 30 characters")
    @Column(length = 30, nullable = false)
    private String firstName;

    @NotNull(message = "Last name is required")
    @Size(max = 30, message = "Last name should not be greater than 30 characters")
    @Column(length = 30, nullable = false)
    private String lastName;

    @NotNull(message = "Email is required")
    @Size(max = 255)
    @Column(unique=true, nullable = false)
    private String email;

    @JsonIgnore
    @NotNull(message = "Password is required")
    @Size(max = 255)
    @Column(nullable = false)
    private String password;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
