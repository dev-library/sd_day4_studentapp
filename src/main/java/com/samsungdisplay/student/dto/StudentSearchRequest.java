package com.samsungdisplay.student.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class StudentSearchRequest {
	// 이번 교안에서 사용하는 필드들
    private String name; // 이름 조회
    private LocalDate fromDate; // 생년월일 시작 날짜
    private LocalDate toDate; // 생년월일 종료 날짜
    
    // 다음 교안에서 사용할 필드들
    // 점수 검색 범위 (점수 필터링을 위한 범위 추가)
    private Integer minScore; // 최소 점수
    private Integer maxScore; // 최대 점수
    
    // 특정 과목 점수 범위 추가 (필요 시)
    private String subject; // 과목 (예: "korean", "english", "math")
    
    // 평균 점수 집계를 위한 추가 필드 (추후 사용)
    private boolean calculateAverage; // 평균 계산 여부
}
