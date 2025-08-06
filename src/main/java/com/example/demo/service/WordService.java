package com.example.demo.service;

import com.example.demo.model.Word;
import com.example.demo.repository.WordRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Service
public class WordService {

    private final WordRepository repo;
    private final Random random = new Random();

    public WordService(WordRepository repo) {
        this.repo = repo;
    }

    public Word getRandomWord() {
        List<Word> all = repo.findAll();
        return all.isEmpty() ? null : all.get(random.nextInt(all.size()));
    }
}
