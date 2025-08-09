package com.example.demo.controller;

import com.example.demo.model.Word;
import com.example.demo.service.WordService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/words") // Base path for all endpoints in this controller
public class WordController {

    private final WordService wordService;

    public WordController(WordService wordService) {
        this.wordService = wordService;
    }

    @GetMapping
    public List<Word> getAllWords() {
        return wordService.findAll();
    }

    @GetMapping("/random")
    public Word getRandomWord() {
        return wordService.getRandomWord();
    }

    @PostMapping
    public Word createWord(@RequestBody Word word) {
        // Ensure ID is null so it's treated as a new entity
        word.setId(null);
        return wordService.saveWord(word);
    }

    @PutMapping("/{id}")
    public Word updateWord(@PathVariable Long id, @RequestBody Word word) {
        word.setId(id); // Ensure the ID from the path is set on the object
        return wordService.saveWord(word);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWord(@PathVariable Long id) {
        // A simple way to delete by ID
        wordService.deleteWord(new Word(id));
        return ResponseEntity.noContent().build();
    }
}