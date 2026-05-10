package entity;

import java.util.HashMap;
import java.util.Map;

/**
 * 学生父类（抽象） — 包含所有公共属性，使用组合包含 Address，使用静态变量统计人数
 */
public abstract class Student {
    protected String studentId;   // 学号（唯一）
    protected String name;        // 姓名
    protected int    age;         // 年龄
    protected String grade;       // 年级，如 "2024"
    protected String clazz;       // 班级，如 "1班"
    protected String major;       // 专业
    protected Address address;    // 地址（组合）
    protected Map<String, Double> scores; // 学科编号 → 成绩

    // ========== 静态变量：人数统计 ==========
    public static int totalCount    = 0;
    public static int undergradCount = 0;
    public static int gradCount     = 0;
    public static final int MAX_CAPACITY = 200;

    public Student(String studentId, String name, int age,
                   String grade, String clazz, String major, Address address) {
        this.studentId = studentId;
        this.name = name;
        this.age = age;
        this.grade = grade;
        this.clazz = clazz;
        this.major = major;
        this.address = address;
        this.scores = new HashMap<>();
        totalCount++;
    }

    // ==================== 成绩相关 ====================

    /** 设置某学科成绩 */
    public void setScore(String subjectId, double score) {
        scores.put(subjectId, score);
    }

    /** 获取某学科成绩，未录入返回 -1 */
    public double getScore(String subjectId) {
        return scores.getOrDefault(subjectId, -1.0);
    }

    /** 计算总分 */
    public double calculateTotal() {
        double total = 0;
        for (double s : scores.values()) {
            total += s;
        }
        return total;
    }

    /** 计算平均分，无成绩返回 0 */
    public double calculateAverage() {
        if (scores.isEmpty()) return 0;
        return calculateTotal() / scores.size();
    }

    /** 成绩数量 */
    public int scoreCount() {
        return scores.size();
    }

    // ==================== 抽象方法 ====================

    /** 获取学生类型标签 */
    public abstract String getTypeLabel();

    /** 获取详细信息字符串 */
    public abstract String getInfo();

    // ==================== 工具方法 ====================

    /** 打印学生信息到控制台 */
    public void printInfo() {
        System.out.println(getInfo());
    }

    // ==================== Getters / Setters ====================

    public String getStudentId()  { return studentId; }
    public void setStudentId(String id) { this.studentId = id; }

    public String getName()       { return name; }
    public void setName(String n) { this.name = n; }

    public int    getAge()        { return age; }
    public void setAge(int a)     { this.age = a; }

    public String getGrade()      { return grade; }
    public void setGrade(String g) { this.grade = g; }

    public String getClazz()      { return clazz; }
    public void setClazz(String c) { this.clazz = c; }

    public String getMajor()      { return major; }
    public void setMajor(String m) { this.major = m; }

    public Address getAddress()       { return address; }
    public void setAddress(Address a) { this.address = a; }

    public Map<String, Double> getScores() { return scores; }
}
