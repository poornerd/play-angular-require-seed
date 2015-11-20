package models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.hibernate.annotations.GenericGenerator;
import org.joda.time.DateTime;
import play.Logger;
import play.db.jpa.JPA;
import play.libs.F;
import play.mvc.Result;

import javax.persistence.*;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Date;
import java.util.List;

/**
 * Created by bp on 20.11.15.
 */
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(generator="system-uuid")
    @GenericGenerator(name="system-uuid", strategy = "uuid")
    @Column(name = "usr_id", unique = true, nullable = false)
    public String id;

    @Column(name = "usr_email", nullable = false)
    @org.hibernate.annotations.Index(name="idx_email")
    public String email;

    @Column(name = "usr_password")
    @JsonIgnore
    public String password;

    @Column(name = "usr_first_name", nullable = false)
    public String firstName;


    @Column(name = "usr_last_name", nullable = false)
    public String lastName;

    @Column(name = "usr_position")
    public String position;

    @Column(name = "usr_telephone")
    public String telephone;

    @Column(name = "usr_created_by", nullable = true)
    public String createdBy;

    @Column(name = "usr_updated_by", nullable = true)
    public String updatedBy;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "usr_created_at", nullable = false, length = 29)
    public DateTime createdAt;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "usr_updated_at", nullable = false, length = 29)
    public DateTime updatedAt;

    @Column(name = "usr_first_login")
    public DateTime firstLogin;

    @Column(name = "usr_last_login")
    public Date lastLogin;

    @Column(name = "usr_date_of_birth")
    public Date dateOfBirth;

    public static User findById(String id) {
        //return JPA.em().find(User.class, id);
        return findByEmailAndPassword("","");
    }

    public static User findByUsername(String username) {
        try {
            return JPA.em()
                    .createQuery("from User where lower(username) = :n", User.class)
                    .setParameter("n", username.toLowerCase()).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public static User findByEmailAndPassword(String email, String Password) {
        // TODO: find the corresponding user; don't forget to encrypt the password
        User testUser = new User();
        testUser.id = "1111";
        testUser.email = "test@test.com";
        testUser.password = "test";
        testUser.firstName = "John";
        testUser.lastName = "Smith";

        return testUser;
    }

    public static User findByEmailToLower(String email) {
        try {
            return JPA.em()
                    .createQuery("from User where lower(email) = :e", User.class)
                    .setParameter("e", email).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public static List<User> getList(String _search) {
        try {
            String queryString = "select m from User m " +
                    "where (upper(concat(m.lastName, ', ', m.firstName)) like upper(:search)) " +
                    "or (upper(m.firstName) like upper(:search)) " +
                    "order by m.lastName, m.firstName";

            TypedQuery<User> query = JPA.em().createQuery(queryString, User.class)
                    .setParameter("search", _search + "%");

            return query.getResultList();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public static User findUserByName(String _search) {
        try {
            String queryString = "select m from User m " +
                    "where (upper(concat(m.lastName, ', ', m.firstName)) like upper(:search)) " +
                    "order by m.eposId desc";

            Logger.debug(queryString);

            TypedQuery<User> query = JPA.em().createQuery(queryString, User.class)
                    .setParameter("search", _search + "%");

            return query.setMaxResults(1).setFirstResult(0).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }

    }

    public void save() {
        JPA.em().persist(this);
    }

    public void delete() {
        JPA.em().remove(this);
    }

    public String toJson() {
        ObjectMapper mapper = new ObjectMapper();
        final StringWriter w = new StringWriter();
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        try {
            mapper.writeValue(w, this);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return w.toString();
    }

    public String getFullName() {
        return this.getLastName() + ((this.getLastName() != null && this.getLastName().length() > 0) ? ", " : "") + this.getFirstName();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public DateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(DateTime createdAt) {
        this.createdAt = createdAt;
    }

    public DateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(DateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public DateTime getFirstLogin() {
        return firstLogin;
    }

    public void setFirstLogin(DateTime firstLogin) {
        this.firstLogin = firstLogin;
    }

    public Date getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(Date lastLogin) {
        this.lastLogin = lastLogin;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }
}
