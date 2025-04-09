package com.samsungdisplay.student.controller;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.samsungdisplay.student.document.Student;
import com.samsungdisplay.student.dto.StudentSearchRequest;
import com.samsungdisplay.student.service.StudentService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/students")
public class StudentController {

    private final StudentService studentService;

    // 학생 목록 및 검색 기능 처리
    @GetMapping
    public String list(
        @RequestParam(value = "name", required = false) String name,
        @RequestParam(value = "fromDate", required = false) String fromDate,
        @RequestParam(value = "toDate", required = false) String toDate,
        Model model) {

        // 검색 조건을 DTO에 담아서 전달
        StudentSearchRequest request = new StudentSearchRequest();

        // 조건이 없으면 전체 조회
        if (name == null && fromDate == null && toDate == null) {
            List<Student> students = studentService.findAll();
            System.out.println("풀검색" + students.toString());
            model.addAttribute("students", students);
            return "students/list";  // 전체 학생 목록을 보여주는 페이지
        }

        // 검색 조건을 설정
        request.setName(name);

        // 날짜 포맷이 있으면 설정
        try {
            if (fromDate != null && !fromDate.isEmpty()) {
                request.setFromDate(LocalDate.parse(fromDate)); // LocalDate 포맷 변환
            }
            if (toDate != null && !toDate.isEmpty()) {
                request.setToDate(LocalDate.parse(toDate));
            }
        } catch (Exception e) {
            model.addAttribute("error", "날짜 포맷이 잘못되었습니다.");
            return "students/list";  // 잘못된 날짜 입력 처리
        }

        // 검색 요청 처리
        List<Student> students = studentService.search(request);
        System.out.println("인육" + students.toString());

        // 모델에 결과 전달
        model.addAttribute("students", students);
        return "students/list";  // 검색된 목록을 보여주는 페이지
    }
    
    // 생성 폼
    @GetMapping("/new")
    public String newStudentForm(Model model) {
        Student student = new Student();
        student.setSubjectScores(new HashMap<>());
        model.addAttribute("student", student);
        return "students/form";
    }

    // 저장 처리
    @PostMapping
    public String save(
            @RequestParam(value = "id", required = false) String id,
            @RequestParam("name") String name,
            @RequestParam("nationality") String nationality,
            @RequestParam("birthDate") LocalDate birthDate,  // 직접 바인딩
            @RequestParam("korean") Integer korean,
            @RequestParam("english") Integer english,
            @RequestParam("math") Integer math
    ) {
        Map<String, Integer> scores = new HashMap<>();
        scores.put("korean", korean);
        scores.put("english", english);
        scores.put("math", math);

        Student student = new Student();
        student.setId(id);
        student.setName(name);
        student.setNationality(nationality);
        student.setBirthDate(birthDate);  // 그대로 설정
        student.setSubjectScores(scores);

        studentService.save(student);
        return "redirect:/students";
    }

    // 상세 보기
    @GetMapping("/{id}")
    public String detail(@PathVariable("id") String id, Model model) {
        Student student = studentService.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("학생이 존재하지 않습니다: " + id));

        model.addAttribute("student", student);
        return "students/detail";
    }

    // 수정 폼
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable("id") String id, Model model) {
        Student student = studentService.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("학생이 존재하지 않습니다: " + id));
        model.addAttribute("student", student);
        return "students/form";
    }

    // 수정 처리
    @PostMapping("/{id}")
    public String update(@PathVariable("id") String id, @ModelAttribute Student student) {
        student.setId(id);
        studentService.save(student);
        return "redirect:/students";
    }

    // 삭제 처리
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable("id") String id) {
        studentService.deleteById(id);
        return "redirect:/students";
    }
    
}