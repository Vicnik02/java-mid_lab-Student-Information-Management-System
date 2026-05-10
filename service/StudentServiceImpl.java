package service;

import entity.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 学生信息管理系统 — 服务实现（GUI 版本，纯业务逻辑无控制台依赖）
 */
public class StudentServiceImpl implements StudentService {

    private final Map<String, Student> studentMap = new LinkedHashMap<>();
    private final Map<String, Clazz> classMap = new LinkedHashMap<>();
    private final Map<String, List<Subject>> subjectMap = new LinkedHashMap<>();

    private String makeSubjectKey(String major, String grade) {
        return major + ":" + grade;
    }

    private Clazz getOrCreateClass(String classId, String className, String grade, String major) {
        return classMap.computeIfAbsent(classId,
            k -> new Clazz(classId, className, grade, major));
    }

    private boolean isFull() {
        return studentMap.size() >= Student.MAX_CAPACITY;
    }

    // ==================== 添加学生 ====================

    @Override
    public String addUndergraduate(String studentId, String name, int age,
                                   String grade, String clazz, String major,
                                   String province, String city, String street, String doorNumber) {
        if (isFull()) return "ERROR:系统已满，最多容纳 " + Student.MAX_CAPACITY + " 名学生";
        if (studentMap.containsKey(studentId)) return "ERROR:学号 " + studentId + " 已存在";

        Address addr = new Address(province, city, street, doorNumber);
        Undergraduate u = new Undergraduate(studentId, name, age, grade, clazz, major, addr);
        studentMap.put(studentId, u);
        getOrCreateClass(clazz, clazz, grade, major).addStudent(u);
        return "OK:本科生 " + name + "（" + studentId + "）添加成功";
    }

    @Override
    public String addGraduate(String studentId, String name, int age,
                              String grade, String clazz, String major,
                              String province, String city, String street, String doorNumber,
                              String advisor, String researchDirection) {
        if (isFull()) return "ERROR:系统已满，最多容纳 " + Student.MAX_CAPACITY + " 名学生";
        if (studentMap.containsKey(studentId)) return "ERROR:学号 " + studentId + " 已存在";

        Address addr = new Address(province, city, street, doorNumber);
        Graduate g = new Graduate(studentId, name, age, grade, clazz, major, addr,
                                  advisor, researchDirection);
        studentMap.put(studentId, g);
        getOrCreateClass(clazz, clazz, grade, major).addStudent(g);
        return "OK:研究生 " + name + "（" + studentId + "）添加成功";
    }

    // ==================== 修改学生 ====================

    @Override
    public String updateStudent(String studentId, String name, Integer age,
                                String grade, String clazz, String major,
                                String province, String city, String street, String doorNumber) {
        Student s = studentMap.get(studentId);
        if (s == null) return "ERROR:学号 " + studentId + " 不存在";

        if (clazz != null && !clazz.equals(s.getClazz())) {
            Clazz oldClass = classMap.get(s.getClazz());
            if (oldClass != null) oldClass.removeStudent(studentId);
            Clazz newClass = getOrCreateClass(clazz, clazz,
                grade != null ? grade : s.getGrade(),
                major != null ? major : s.getMajor());
            newClass.addStudent(s);
        }

        if (name != null) s.setName(name);
        if (age != null) s.setAge(age);
        if (grade != null) s.setGrade(grade);
        if (clazz != null) s.setClazz(clazz);
        if (major != null) s.setMajor(major);
        if (s.getAddress() != null) {
            if (province != null) s.getAddress().setProvince(province);
            if (city != null) s.getAddress().setCity(city);
            if (street != null) s.getAddress().setStreet(street);
            if (doorNumber != null) s.getAddress().setDoorNumber(doorNumber);
        }
        return "OK:学号 " + studentId + " 信息修改成功";
    }

    // ==================== 删除学生 ====================

    @Override
    public String deleteStudent(String studentId) {
        if (studentMap.isEmpty()) return "ERROR:系统中没有任何学生，无法删除";
        Student s = studentMap.remove(studentId);
        if (s == null) return "ERROR:学号 " + studentId + " 不存在";

        Clazz c = classMap.get(s.getClazz());
        if (c != null) c.removeStudent(studentId);

        Student.totalCount--;
        if (s instanceof Graduate) Student.gradCount--;
        else Student.undergradCount--;
        return "OK:学生 " + s.getName() + "（" + studentId + "）已删除";
    }

    // ==================== 浏览 ====================

    @Override
    public List<Student> listAll() {
        return new ArrayList<>(studentMap.values());
    }

    @Override
    public List<Student> listUndergraduates() {
        return studentMap.values().stream()
            .filter(s -> s instanceof Undergraduate).collect(Collectors.toList());
    }

    @Override
    public List<Student> listGraduates() {
        return studentMap.values().stream()
            .filter(s -> s instanceof Graduate).collect(Collectors.toList());
    }

    // ==================== 学科管理 ====================

    @Override
    public String addSubject(String major, String grade, String subjectId, String subjectName) {
        String key = makeSubjectKey(major, grade);
        List<Subject> list = subjectMap.computeIfAbsent(key, k -> new ArrayList<>());
        for (Subject sub : list) {
            if (sub.getSubjectId().equals(subjectId))
                return "ERROR:学科编号 " + subjectId + " 已存在";
        }
        list.add(new Subject(subjectId, subjectName));
        return "OK:学科 " + subjectName + " 已绑定到 " + major + " " + grade + " 级";
    }

    @Override
    public String removeSubject(String major, String grade, String subjectId) {
        String key = makeSubjectKey(major, grade);
        List<Subject> list = subjectMap.get(key);
        if (list == null || list.isEmpty()) return "ERROR:该组下没有任何学科";
        if (!list.removeIf(s -> s.getSubjectId().equals(subjectId)))
            return "ERROR:学科编号 " + subjectId + " 不存在";
        return "OK:学科已移除";
    }

    @Override
    public List<Subject> getSubjects(String major, String grade) {
        return subjectMap.getOrDefault(makeSubjectKey(major, grade), Collections.emptyList());
    }

    @Override
    public String listAllSubjects() {
        if (subjectMap.isEmpty()) return null;
        StringBuilder sb = new StringBuilder();
        for (var entry : subjectMap.entrySet()) {
            String[] parts = entry.getKey().split(":");
            sb.append("【").append(parts[0]).append(" ").append(parts[1]).append(" 级】\n");
            for (Subject s : entry.getValue())
                sb.append("  ● ").append(s.getSubjectId()).append(" — ").append(s.getSubjectName()).append("\n");
        }
        return sb.toString();
    }

    @Override
    public Map<String, List<Subject>> getAllSubjectMap() {
        return subjectMap;
    }

    // ==================== 成绩管理 ====================

    @Override
    public String setScore(String studentId, String subjectId, double score) {
        Student student = studentMap.get(studentId);
        if (student == null) return "ERROR:学号 " + studentId + " 不存在";
        if (score < 0 || score > 100) return "ERROR:成绩范围应为 0~100";
        student.setScore(subjectId, score);
        return "OK:成绩已登记";
    }

    // ==================== 查询功能 ====================

    @Override
    public Student searchById(String studentId) {
        return studentMap.get(studentId);
    }

    @Override
    public List<Student> searchByName(String name) {
        return studentMap.values().stream()
            .filter(s -> s.getName().contains(name)).collect(Collectors.toList());
    }

    @Override
    public List<Student> searchByClass(String clazz) {
        return studentMap.values().stream()
            .filter(s -> s.getClazz().equals(clazz)).collect(Collectors.toList());
    }

    @Override
    public List<Student> searchByMajor(String major) {
        return studentMap.values().stream()
            .filter(s -> s.getMajor().equals(major)).collect(Collectors.toList());
    }

    // ==================== 排名 ====================

    @Override
    public List<String> getAllClassIds() {
        return new ArrayList<>(classMap.keySet());
    }

    @Override
    public List<Student> getClassRankingList(String classId) {
        Clazz c = classMap.get(classId);
        if (c == null) return Collections.emptyList();
        return c.rankByTotalScore();
    }

    @Override
    public List<Student> getClassSubjectRankingList(String classId, String subjectId) {
        Clazz c = classMap.get(classId);
        if (c == null) return Collections.emptyList();
        return c.rankBySubject(subjectId);
    }

    // ==================== 统计 ====================

    @Override
    public String getStatistics() {
        StringBuilder sb = new StringBuilder();
        sb.append("══════════════ 系统统计 ══════════════\n\n");
        sb.append(String.format("  学生总人数：   %d\n", Student.totalCount));
        sb.append(String.format("  本科生人数：   %d\n", Student.undergradCount));
        sb.append(String.format("  研究生人数：   %d\n", Student.gradCount));
        sb.append(String.format("  系统容量上限： %d\n", Student.MAX_CAPACITY));
        sb.append(String.format("  当前班级数：   %d\n", classMap.size()));

        if (!classMap.isEmpty()) {
            sb.append("\n  ───────── 各班级人数 ─────────\n");
            for (Clazz c : classMap.values())
                sb.append(String.format("    %s（%s %s级）：%d 人\n",
                    c.getClassName(), c.getMajor(), c.getGrade(), c.studentCount()));
        }

        Map<String, Long> majorCount = studentMap.values().stream()
            .collect(Collectors.groupingBy(Student::getMajor, Collectors.counting()));
        if (!majorCount.isEmpty()) {
            sb.append("\n  ───────── 各专业人数 ─────────\n");
            for (var entry : majorCount.entrySet())
                sb.append(String.format("    %s：%d 人\n", entry.getKey(), entry.getValue()));
        }
        sb.append("\n══════════════════════════════════════");
        return sb.toString();
    }
}
