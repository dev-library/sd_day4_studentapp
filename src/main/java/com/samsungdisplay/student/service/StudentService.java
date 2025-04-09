package com.samsungdisplay.student.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.stereotype.Service;

import com.samsungdisplay.student.document.Student;
import com.samsungdisplay.student.repository.StudentRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class StudentService {

    private final StudentRepository studentRepository;
    
    public List<Student> findAll() {
    	return StreamSupport.stream(studentRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());
	}

    public Student save(Student student) {
        return studentRepository.save(student);
    }

    public Optional<Student> findById(String id) {
        return studentRepository.findById(id);
    }

    public void deleteById(String id) {
        studentRepository.deleteById(id);
    }
}