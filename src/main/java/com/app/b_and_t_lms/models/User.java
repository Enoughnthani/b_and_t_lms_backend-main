package com.app.b_and_t_lms.models;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.app.b_and_t_lms.models.Role.RoleName;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "user", uniqueConstraints = {
        @UniqueConstraint(name = "uk_user_email", columnNames = "email"),
        @UniqueConstraint(name = "uk_user_id_number", columnNames = "id_number")
})
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstname;
    private String lastname;
    @Column(unique = true)
    private String email;
    private String password;
    private Timestamp createdAt;
    @Column(unique = true)
    private String idNumber;
    private LocalDate dob;
    private String gender;
    private String contactNumber;

    @Enumerated(EnumType.STRING)
    private Status status;
    private LocalDateTime lastLogin;
    private LocalDateTime prevLogin;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private List<Role> roles;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "user")
    private Enrollment enrollment;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user", orphanRemoval = true)
    private List<ProgramStaff> programStaffs;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "submittedBy", orphanRemoval = true)
    private List<Report> reports;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "createdBy", orphanRemoval = true)
    private List<Discussion> discussions;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private UserOtp userOtp;

    @Column(nullable = false)
    private boolean superUser = false;

    public User() {
    }

    public User(Long id, String firstname, String lastname, String email, String password, Timestamp createdAt,
            String idNumber, LocalDate dob, String gender, String contactNumber, Status status, LocalDateTime lastLogin,
            LocalDateTime prevLogin, List<Role> roles, Enrollment enrollment, List<ProgramStaff> programStaffs,
            List<Report> reports, List<Discussion> discussions, UserOtp userOtp, boolean superUser) {
        this.id = id;
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.password = password;
        this.createdAt = createdAt;
        this.idNumber = idNumber;
        this.dob = dob;
        this.gender = gender;
        this.contactNumber = contactNumber;
        this.status = status;
        this.lastLogin = lastLogin;
        this.prevLogin = prevLogin;
        this.roles = roles;
        this.enrollment = enrollment;
        this.programStaffs = programStaffs;
        this.reports = reports;
        this.discussions = discussions;
        this.userOtp = userOtp;
        this.superUser = superUser;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
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

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public LocalDate getDob() {
        return dob;
    }

    public void setDob(LocalDate dob) {
        this.dob = dob;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (roles == null || roles.isEmpty()) {
            return Collections.emptyList();
        }

        return roles.stream()
                .map(r -> new SimpleGrantedAuthority("ROLE_" + r.getName().name()))
                .collect(Collectors.toList());
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return status.equals(Status.ACTIVE);
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return status.equals(Status.ACTIVE);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Enrollment getEnrollment() {
        return enrollment;
    }

    public void setEnrollment(Enrollment enrollment) {
        this.enrollment = enrollment;
    }

    public List<ProgramStaff> getProgramStaffs() {
        return programStaffs;
    }

    public void setProgramStaffs(List<ProgramStaff> programStaffs) {
        this.programStaffs = programStaffs;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    public UserOtp getUserOtp() {
        return userOtp;
    }

    public void setUserOtp(UserOtp userOtp) {
        this.userOtp = userOtp;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public LocalDateTime getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(LocalDateTime lastLogin) {
        this.lastLogin = lastLogin;
    }

    public LocalDateTime getPrevLogin() {
        return prevLogin;
    }

    public void setPrevLogin(LocalDateTime prevLogin) {
        this.prevLogin = prevLogin;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    private static final Set<RoleName> STAFF_ROLES = Set.of(
            RoleName.FACILITATOR,
            RoleName.ASSESSOR,
            RoleName.MODERATOR,
            RoleName.ADMIN,
            RoleName.PROGRAM_MANAGER);

    public boolean isStaff() {
        return roles != null && roles.stream()
                .anyMatch(r -> STAFF_ROLES.contains(r.getName()));
    }

    public boolean isSuperUser() {
        return superUser;
    }

    public void setSuperUser(boolean superUser) {
        this.superUser = superUser;
    }

    public static Set<RoleName> getStaffRoles() {
        return STAFF_ROLES;
    }

    public List<Report> getReports() {
        return reports;
    }

    public void setReports(List<Report> reports) {
        this.reports = reports;
    }

    public List<Discussion> getDiscussions() {
        return discussions;
    }

    public void setDiscussions(List<Discussion> discussions) {
        this.discussions = discussions;
    }

}
