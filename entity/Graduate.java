package entity;

/**
 * 研究生 — 继承 Student
 * 新增属性：导师、研究方向
 */
public class Graduate extends Student {
    private String advisor;           // 导师
    private String researchDirection; // 研究方向

    public Graduate(String studentId, String name, int age,
                    String grade, String clazz, String major, Address address,
                    String advisor, String researchDirection) {
        super(studentId, name, age, grade, clazz, major, address);
        this.advisor = advisor;
        this.researchDirection = researchDirection;
        gradCount++;
    }

    @Override
    public String getTypeLabel() {
        return "研究生";
    }

    @Override
    public String getInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append("════════════ 研究生信息 ════════════\n");
        sb.append(String.format("  学号：%s\n", studentId));
        sb.append(String.format("  姓名：%s\n", name));
        sb.append(String.format("  年龄：%d\n", age));
        sb.append(String.format("  年级：%s\n", grade));
        sb.append(String.format("  班级：%s\n", clazz));
        sb.append(String.format("  专业：%s\n", major));
        sb.append(String.format("  导师：%s\n", advisor));
        sb.append(String.format("  研究方向：%s\n", researchDirection));
        sb.append(String.format("  地址：%s\n", address.format()));
        if (scores.isEmpty()) {
            sb.append("  成绩：暂未录入\n");
        } else {
            sb.append("  成绩：\n");
            for (var entry : scores.entrySet()) {
                sb.append(String.format("     %s → %.1f 分\n", entry.getKey(), entry.getValue()));
            }
            sb.append(String.format("  总分：%.1f | 平均：%.1f\n", calculateTotal(), calculateAverage()));
        }
        sb.append("════════════════════════════════");
        return sb.toString();
    }

    // ==================== Getters / Setters ====================

    public String getAdvisor()           { return advisor; }
    public void setAdvisor(String a)      { this.advisor = a; }

    public String getResearchDirection()  { return researchDirection; }
    public void setResearchDirection(String d) { this.researchDirection = d; }
}
