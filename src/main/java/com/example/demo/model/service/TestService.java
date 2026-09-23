package com.example.demo.model.service;

import java.util.List;

import com.example.demo.model.domain.TestDB;
import com.example.demo.model.repository.TestRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TestService {

    private final TestRepository testRepository;

    public TestService(TestRepository testRepository) {
        this.testRepository = testRepository;
    }

    public TestDB findByName(String name) {
        return testRepository.findByName(name);
    }

    public List<TestDB> findAll() {
        return testRepository.findAll();
    }

    @Transactional
    public void saveSampleDataIfEmpty() {
        if (testRepository.count() > 0) {
            return;
        }

        testRepository.save(new TestDB("홍길동", "hong@example.com", "서울시"));
        testRepository.save(new TestDB("기경민", "km@example.com", "성결대학교"));
        testRepository.save(new TestDB("김자바", "java@example.com", "Spring Boot"));
    }
}
