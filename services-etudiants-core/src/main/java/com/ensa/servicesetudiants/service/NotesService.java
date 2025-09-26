package com.ensa.servicesetudiants.service;

import com.ensa.servicesetudiants.entity.Notes;
import com.ensa.servicesetudiants.repository.NotesRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class NotesService {

    private final NotesRepository notesRepository;

    public NotesService(NotesRepository notesRepository) {
        this.notesRepository = notesRepository;
    }

    public List<Notes> getAllNotes() {
        return notesRepository.findAll();
    }

    public Optional<Notes> getNoteById(Integer id) {
        return notesRepository.findById(id);
    }

    public Notes saveNote(Notes note) {
        return notesRepository.save(note);
    }

    public void deleteNote(Integer id) {
        notesRepository.deleteById(id);
    }
}
