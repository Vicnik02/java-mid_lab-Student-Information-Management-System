import service.*;
import ui.MainFrame;
import javax.swing.*;

/**
 * 学生信息管理系统 — Swing GUI 入口
 * 版本 1.0 | 2026-05
 */
public class Main {

    public static void main(String[] args) {
        // 设置系统外观
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        // 启用抗锯齿
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");

        // 初始化服务
        StudentService svc = new StudentServiceImpl();
        initDemoData(svc);

        // 启动 GUI
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame(svc);
            frame.setVisible(true);
        });
    }

    private static void initDemoData(StudentService svc) {
        // 绑定学科
        svc.addSubject("软件工程", "2024", "MATH101", "高等数学");
        svc.addSubject("软件工程", "2024", "ENG101",  "大学英语");
        svc.addSubject("软件工程", "2024", "CS101",   "程序设计基础");
        svc.addSubject("软件工程", "2024", "CS102",   "数据结构");

        svc.addSubject("计算机科学", "2024", "MATH201", "高级算法");
        svc.addSubject("计算机科学", "2024", "CS201",   "人工智能");
        svc.addSubject("计算机科学", "2024", "CS202",   "机器学习");

        // 添加本科生
        svc.addUndergraduate("2024001", "张三", 19, "2024", "1班", "软件工程",
            "广东", "广州", "大学城", "100");
        svc.addUndergraduate("2024002", "李四", 20, "2024", "1班", "软件工程",
            "浙江", "杭州", "西湖区", "200");
        svc.addUndergraduate("2024003", "王五", 19, "2024", "1班", "软件工程",
            "江苏", "南京", "鼓楼区", "300");
        svc.addUndergraduate("2024004", "赵六", 21, "2024", "2班", "软件工程",
            "湖北", "武汉", "洪山区", "400");

        // 添加研究生
        svc.addGraduate("2024G01", "孙七", 24, "2024", "研1班", "计算机科学",
            "北京", "北京", "海淀区", "500", "陈教授", "自然语言处理");
        svc.addGraduate("2024G02", "周八", 23, "2024", "研1班", "计算机科学",
            "上海", "上海", "浦东新区", "600", "刘教授", "计算机视觉");

        // 预设成绩
        svc.setScore("2024001", "MATH101", 92);
        svc.setScore("2024001", "ENG101",  85);
        svc.setScore("2024001", "CS101",   88);
        svc.setScore("2024001", "CS102",   90);

        svc.setScore("2024002", "MATH101", 78);
        svc.setScore("2024002", "ENG101",  82);
        svc.setScore("2024002", "CS101",   95);
        svc.setScore("2024002", "CS102",   80);

        svc.setScore("2024003", "MATH101", 65);
        svc.setScore("2024003", "ENG101",  70);
        svc.setScore("2024003", "CS101",   72);
        svc.setScore("2024003", "CS102",   68);

        svc.setScore("2024004", "MATH101", 88);
        svc.setScore("2024004", "ENG101",  91);
        svc.setScore("2024004", "CS101",   76);
        svc.setScore("2024004", "CS102",   85);

        svc.setScore("2024G01", "MATH201", 90);
        svc.setScore("2024G01", "CS201",   93);
        svc.setScore("2024G01", "CS202",   87);

        svc.setScore("2024G02", "MATH201", 85);
        svc.setScore("2024G02", "CS201",   79);
        svc.setScore("2024G02", "CS202",   92);
    }
}
