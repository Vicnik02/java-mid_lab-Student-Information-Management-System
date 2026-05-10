package entity;

import java.util.Objects;

/**
 * 学科类 — 按「专业 + 年级」统一绑定，同专业同年级的学科列表完全相同
 */
public class Subject {
    private String subjectId;   // 学科编号
    private String subjectName; // 学科名称

    public Subject(String subjectId, String subjectName) {
        this.subjectId = subjectId;
        this.subjectName = subjectName;
    }

    public String getSubjectId()   { return subjectId; }
    public String getSubjectName() { return subjectName; }

    public void setSubjectId(String subjectId)     { this.subjectId = subjectId; }
    public void setSubjectName(String subjectName) { this.subjectName = subjectName; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Subject)) return false;
        Subject s = (Subject) o;
        return Objects.equals(subjectId, s.subjectId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(subjectId);
    }

    @Override
    public String toString() {
        return subjectId + " — " + subjectName;
    }
}
