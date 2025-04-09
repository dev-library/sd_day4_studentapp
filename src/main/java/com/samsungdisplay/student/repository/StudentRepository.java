package com.samsungdisplay.student.repository;

import java.util.List;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.samsungdisplay.student.document.Student;

public interface StudentRepository extends ElasticsearchRepository<Student, String> {

}