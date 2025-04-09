package com.samsungdisplay.student.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.samsungdisplay.student.document.Student;
import com.samsungdisplay.student.dto.StudentSearchRequest;
import com.samsungdisplay.student.repository.StudentRepository;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.DateRangeQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.MatchQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.RangeQuery;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.core.search.SourceConfig;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
@Service
public class StudentService {

    private final StudentRepository studentRepository;
    private final ElasticsearchClient client;

    
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
    


    public List<Student> search(StudentSearchRequest request) {
        // 쿼리 리스트 생성
        List<Query> queries = new ArrayList<>();

        // 이름 검색 (fuzzy 매칭)
        if (StringUtils.hasText(request.getName())) {
            queries.add(Query.of(q -> q
                    .bool(b -> b.should(
                            MatchQuery.of(m -> m.field("name").query(request.getName()).fuzziness("AUTO"))._toQuery()
                    ))
            ));
        }

        // 생년월일 범위 검색
        if (request.getFromDate() != null || request.getToDate() != null) {
            DateRangeQuery.Builder dateRangeQuery = new DateRangeQuery.Builder().field("birthDate");
            if (request.getFromDate() != null) {
                dateRangeQuery.gte(request.getFromDate().toString()); // LocalDate to String
            }
            if (request.getToDate() != null) {
                dateRangeQuery.lte(request.getToDate().toString()); // LocalDate to String
            }
            RangeQuery rangeQuery = RangeQuery.of(r -> r.date(dateRangeQuery.build()));
            queries.add(Query.of(q -> q.range(rangeQuery)));
        }

        // 최종 쿼리 작성
        Query finalQuery = Query.of(q -> q.bool(b -> b.must(queries)));

        try {
            // Elasticsearch 검색 요청
            SearchResponse<Student> response = client.search(s -> {
                s.index("students") // 인덱스 설정
                 .query(finalQuery) // 쿼리 설정
                 .source(new SourceConfig.Builder().fetch(true).build()) // SourceConfig를 사용하여 source 활성화
                 .size(10); // 결과 개수 제한 (필요에 맞게 설정)
                return s;
            }, Student.class);

            // 검색된 결과 처리
            List<Student> students = new ArrayList<>();
            for (Hit<Student> hit : response.hits().hits()) {
                Student student = hit.source();
                student.setId(hit.id()); // id를 hit에서 직접 가져오기
                students.add(student);
            }

            return students;
        } catch (IOException e) {
            throw new RuntimeException("Elasticsearch 검색 실패", e);
        }
    }  
}