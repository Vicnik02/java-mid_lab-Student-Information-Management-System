package entity;

/**
 * 本科生 — 继承 Student
 * 新增属性：无（专业已在父类中，此处体现继承层次）
 */
public class Undergraduate extends Student {

    public Undergraduate(String studentId, String name, int age,
                         String grade, String clazz, String major, Address address) {
        super(studentId, name, age, grade, clazz, major, address);
        undergradCount++;
    }

    @Override
    public String getTypeLabel() {
        return "本科生";
    }

    @Override
    public String getInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append("════════════ 本科生信息 ════════════\n");
        sb.append(String.format("  学号：%s\n", studentId));
        sb.append(String.format("  姓名：%s\n", name));
        sb.append(String.format("  年龄：%d\n", age));
        sb.append(String.format("  年级：%s\n", grade));
        sb.append(String.format("  班级：%s\n", clazz));
        sb.append(String.format("  专业：%s\n", major));
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
}
