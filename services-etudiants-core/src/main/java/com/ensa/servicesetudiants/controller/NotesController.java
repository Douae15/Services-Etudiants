package com.ensa.servicesetudiants.controller;

import com.ensa.servicesetudiants.entity.Notes;
import com.ensa.servicesetudiants.service.NotesService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notes")
public class NotesController {

    private final NotesService notesService;

    public NotesController(NotesService notesService) {
        this.notesService = notesService;
    }

    @GetMapping
    public List<Notes> getAllNotes() {
        return notesService.getAllNotes();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Notes> getNoteById(@PathVariable Integer id) {
        return notesService.getNoteById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Notes createNote(@RequestBody Notes note) {
        return notesService.saveNote(note);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Notes> updateNote(@PathVariable Integer id, @RequestBody Notes note) {
        return notesService.getNoteById(id)
                .map(existing -> {
                    note.setId(id);
                    return ResponseEntity.ok(notesService.saveNote(note));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNote(@PathVariable Integer id) {
        if (notesService.getNoteById(id).isPresent()) {
            notesService.deleteNote(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}
