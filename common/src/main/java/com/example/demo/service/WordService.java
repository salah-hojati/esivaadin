package com.example.demo.service;

import com.example.demo.model.Word;
import com.example.demo.repository.WordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Service
public class WordService {

    private final WordRepository wordRepository;
    private final Random random = new Random();

    @Autowired
    public WordService(WordRepository wordRepository) {
        this.wordRepository = wordRepository;
    }

    public Word getRandomWord() {
        List<Word> words = wordRepository.findAll();
        if (words.isEmpty()) {
            return null;
        }
        return words.get(random.nextInt(words.size()));
    }

    // --- New methods for CRUD operations ---

    public List<Word> findAll() {
        return wordRepository.findAll();
    }

    public void deleteWord(Word word) {
        wordRepository.delete(word);
    }

    public Word saveWord(Word word) {
        if (word == null) {
            System.err.println("Word is null. Are you sure you have configured your form correctly?");
            return null;
        }
        return wordRepository.save(word);
    }
}