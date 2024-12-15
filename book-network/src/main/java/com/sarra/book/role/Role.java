package com.sarra.book.role;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sarra.book.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
//@Table(name = "role")
@EntityListeners(AuditingEntityListener.class)
public class Role {

    @Id
    @GeneratedValue
    private Integer id;

    @Column(unique = true)

    private String name;

    @ManyToMany(mappedBy = "roles")
    @JsonIgnore // we added this here so that the roles won't fetch the user only the user will need to load the role
    private List<User> user;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    // updatable = false means that when we update a user this fieald can't be updated
    private LocalDateTime createdDate;

    @LastModifiedDate
    @Column(insertable = false) //when we add a new user this fied can't be inserted it statys vide
    private LocalDateTime lastModifiedDate;
}
