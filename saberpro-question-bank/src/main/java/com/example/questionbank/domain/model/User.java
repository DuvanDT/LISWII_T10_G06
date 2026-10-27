package com.example.questionbank.domain.model;

/**
 * Pure domain representation of a system user. Free of persistence annotations.
 */
public class User {
    private Long id;
    private String username;
    private String email;
    private String fullName;
    private UserRole role;

    /** Default constructor for frameworks or serialization. */
    public User() {}

    /**
     * Creates a new user entity.
     *
     * @param id database identifier
     * @param username unique login username
     * @param email unique email address
     * @param fullName person's full name
     * @param role user's authorization role
     */
    public User(Long id, String username, String email, String fullName, UserRole role) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.fullName = fullName;
        this.role = role;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public UserRole getRole() { return role; }
    public void setRole(UserRole role) { this.role = role; }
}
