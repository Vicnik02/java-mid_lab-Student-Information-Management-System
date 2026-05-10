package service;

import entity.*;
import java.util.List;

/**
 * 学生信息管理系统 — 服务接口（GUI 版本）
 */
public interface StudentService {

    // ==================== 学生管理 ====================

    String addUndergraduate(String studentId, String name, int age,
                            String grade, String clazz, String major,
                            String province, String city, String street, String doorNumber);

    String addGraduate(String studentId, String name, int age,
                       String grade, String clazz, String major,
                       String province, String city, String street, String doorNumber,
                       String advisor, String researchDirection);

    String updateStudent(String studentId, String name, Integer age,
                         String grade, String clazz, String major,
                         String province, String city, String street, String doorNumber);

    String deleteStudent(String studentId);

    List<Student> listAll();
    List<Student> listUndergraduates();
    List<Student> listGraduates();

    // ==================== 学科管理 ====================

    String addSubject(String major, String grade, String subjectId, String subjectName);
    String removeSubject(String major, String grade, String subjectId);
    List<Subject> getSubjects(String major, String grade);
    String listAllSubjects();
    java.util.Map<String, java.util.List<Subject>> getAllSubjectMap();

    // ==================== 成绩管理 ====================

    String setScore(String studentId, String subjectId, double score);

    // ==================== 查询功能 ====================

    Student       searchById(String studentId);
    List<Student> searchByName(String name);
    List<Student> searchByClass(String clazz);
    List<Student> searchByMajor(String major);

    // ==================== 排名功能 ====================

    /** 获取所有班级 ID 列表 */
    List<String> getAllClassIds();

    /** 班级总分排名数据 */
    List<Student> getClassRankingList(String classId);

    /** 班级单科排名数据 */
    List<Student> getClassSubjectRankingList(String classId, String subjectId);

    // ==================== 统计功能 ====================

    String getStatistics();
}
