package com.openclassrooms.starterjwt.models;


import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "SESSIONS")
@EntityListeners(AuditingEntityListener.class)
@Data
@Accessors(chain = true)
@EqualsAndHashCode(of = {"id"})
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Session {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
 
    public Session(Long id, @NotBlank @Size(max = 50) String name, @NotNull Date date,
			@NotNull @Size(max = 2500) String description, Teacher teacher, List<User> users, LocalDateTime createdAt,
			LocalDateTime updatedAt) {
		super();
		this.id = id;
		this.name = name;
		this.date = date;
		this.description = description;
		this.teacher = teacher;
		this.users = users;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}

	@NotBlank
    @Size(max = 50)
    private String name;

    @NotNull
    private Date date;

    @NotNull
    @Size(max = 2500)
    private String description;

    @OneToOne
    @JoinColumn(name = "teacher_id", referencedColumnName = "id")
    private Teacher teacher;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "PARTICIPATE",
            joinColumns = @JoinColumn( name = "session_id" ),
            inverseJoinColumns = @JoinColumn( name = "user_id" ) )
    private List<User> users;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public List<User> getUsers() {
		return users;
	}

	public void setUsers(List<User> users) {
		this.users = users;
	}

	

	
}
