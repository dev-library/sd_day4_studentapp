package com.samsungdisplay.student.controller;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
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
import com.samsungdisplay.student.service.StudentService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/students")
public class StudentController {

    private final StudentService studentService;

    // 전체 목록 보기
    @GetMapping
    public String list(Model model) {
        model.addAttribute("students", studentService.findAll());
        return "students/list";
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