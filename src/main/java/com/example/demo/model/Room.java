package com.example.demo.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "rooms")
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true, length = 6)
    private String code;

    @ManyToOne
    @JoinColumn(name = "faculty_id", nullable = false)
    @JsonIgnoreProperties({"room", "password", "authorities"})
    private User faculty;

    @OneToMany(mappedBy = "room")
    @JsonIgnoreProperties({"room", "password", "authorities"})
    private List<User> students = new ArrayList<>();

    public Room() {}

    public Room(String name, String code, User faculty) {
        this.name = name;
        this.code = code;
        this.faculty = faculty;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public User getFaculty() { return faculty; }
    public void setFaculty(User faculty) { this.faculty = faculty; }

    public List<User> getStudents() { return students; }
    public void setStudents(List<User> students) { this.students = students; }
}
