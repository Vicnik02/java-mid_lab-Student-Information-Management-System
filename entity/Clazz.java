package entity;

import java.util.*;

/**
 * 班级类 — 维护本班学生列表，提供班级排名功能
 */
public class Clazz {
    private String classId;     // 班级编号
    private String className;   // 班级名称
    private String grade;       // 年级
    private String major;       // 专业
    private List<Student> students;

    public Clazz(String classId, String className, String grade, String major) {
        this.classId = classId;
        this.className = className;
        this.grade = grade;
        this.major = major;
        this.students = new ArrayList<>();
    }

    // ==================== 学生维护 ====================

    public void addStudent(Student s) {
        if (!students.contains(s)) {
            students.add(s);
        }
    }

    public boolean removeStudent(String studentId) {
        return students.removeIf(s -> s.getStudentId().equals(studentId));
    }

    public int studentCount() {
        return students.size();
    }

    // ==================== 排名 ====================

    /** 按总分排名（降序） */
    public List<Student> rankByTotalScore() {
        List<Student> sorted = new ArrayList<>(students);
        sorted.sort((a, b) -> Double.compare(b.calculateTotal(), a.calculateTotal()));
        return sorted;
    }

    /** 按单科成绩排名（降序），未录入成绩的排最后 */
    public List<Student> rankBySubject(String subjectId) {
        List<Student> sorted = new ArrayList<>(students);
        sorted.sort((a, b) -> {
            double sa = a.getScore(subjectId);
            double sb = b.getScore(subjectId);
            if (sa < 0 && sb < 0) return 0;
            if (sa < 0) return 1;
            if (sb < 0) return -1;
            return Double.compare(sb, sa);
        });
        return sorted;
    }

    // ==================== Getters ====================

    public String getClassId()   { return classId; }
    public String getClassName() { return className; }
    public String getGrade()     { return grade; }
    public String getMajor()     { return major; }
    public List<Student> getStudents() { return students; }
}
