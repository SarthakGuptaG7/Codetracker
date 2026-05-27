package com.example.demo.controller;

import com.example.demo.model.Room;
import com.example.demo.model.User;
import com.example.demo.repository.RoomRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/rooms")
@CrossOrigin(origins = "*")
public class RoomController {

    private final RoomRepository roomRepository;
    private final UserRepository userRepository;

    public RoomController(RoomRepository roomRepository, UserRepository userRepository) {
        this.roomRepository = roomRepository;
        this.userRepository = userRepository;
    }

    @PostMapping
    public ResponseEntity<Room> createRoom(@RequestBody Room roomRequest) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User faculty = userRepository.findByUsername(username).orElseThrow();
        
        Room room = new Room();
        room.setName(roomRequest.getName());
        room.setFaculty(faculty);
        room.setCode(UUID.randomUUID().toString().substring(0, 6).toUpperCase());
        
        return ResponseEntity.ok(roomRepository.save(room));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Room> getRoom(@PathVariable Long id) {
        return ResponseEntity.ok(roomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Room not found")));
    }

    @PostMapping("/join/{code}")
    public ResponseEntity<String> joinRoom(@PathVariable String code) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User student = userRepository.findByUsername(username).orElseThrow();
        
        Room room = roomRepository.findByCode(code.trim().toUpperCase())
                .orElseThrow(() -> new RuntimeException("Room not found"));
                
        student.setRoom(room);
        userRepository.save(student);
        
        return ResponseEntity.ok("Joined room successfully");
    }

    @GetMapping("/{id}/students")
    public ResponseEntity<List<User>> getStudents(@PathVariable Long id) {
        return ResponseEntity.ok(userRepository.findByRoomId(id));
    }

    @GetMapping("/my-rooms")
    public ResponseEntity<List<Room>> getMyRooms() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User faculty = userRepository.findByUsername(username).orElseThrow();
        return ResponseEntity.ok(roomRepository.findByFacultyId(faculty.getId()));
    }
}
