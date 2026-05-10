package ui;

import entity.*;
import service.*;
import java.awt.*;
import java.util.Map;
import java.util.stream.Collectors;
import javax.swing.*;

/**
 * 统计面板 — 总人数 / 本研分类 / 班级 / 专业统计
 */
public class StatPanel extends JPanel {

    private final StudentService svc;
    private JPanel cardsPanel;
    private JPanel detailPanel;

    public StatPanel(StudentService svc) {
        this.svc = svc;
        setLayout(new BorderLayout(0, 12));
        setBackground(UIUtil.BG);
        add(buildTopBar(), BorderLayout.NORTH);
        add(buildContent(), BorderLayout.CENTER);
    }

    private JPanel buildTopBar() {
        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(UIUtil.BG);
        top.add(UIUtil.pageTitle("统计信息"), BorderLayout.NORTH);

        JButton refreshBtn = UIUtil.accentButton("刷新统计");
        refreshBtn.addActionListener(e -> refresh());
        JPanel btnBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnBar.setBackground(UIUtil.BG);
        btnBar.add(refreshBtn);
        top.add(btnBar, BorderLayout.CENTER);
        return top;
    }

    private JPanel buildContent() {
        JPanel content = new JPanel(new BorderLayout(0, 16));
        content.setBackground(UIUtil.BG);

        // 顶部统计卡片 — 4张
        cardsPanel = new JPanel(new GridLayout(1, 4, 16, 0));
        cardsPanel.setBackground(UIUtil.BG);
        content.add(cardsPanel, BorderLayout.NORTH);

        // 下方详情面板
        detailPanel = new JPanel(new GridLayout(1, 2, 16, 0));
        detailPanel.setBackground(UIUtil.BG);
        content.add(detailPanel, BorderLayout.CENTER);

        return content;
    }

    public void refresh() {
        cardsPanel.removeAll();
        detailPanel.removeAll();

        // 统计卡片 — 带图标
        cardsPanel.add(UIUtil.statCard("学生总人数",
            String.valueOf(Student.totalCount),
            UIUtil.PRIMARY, UIUtil.PRIMARY_DARK));
        cardsPanel.add(UIUtil.statCard("本科生人数",
            String.valueOf(Student.undergradCount),
            UIUtil.PRIMARY_LIGHT, UIUtil.ACCENT));
        cardsPanel.add(UIUtil.statCard("研究生人数",
            String.valueOf(Student.gradCount),
            UIUtil.ACCENT, UIUtil.ACCENT_DARK));
        cardsPanel.add(UIUtil.statCard("系统容量",
            String.valueOf(Student.MAX_CAPACITY),
            new Color(0x546E7A), new Color(0x37474F)));

        // 班级人数统计
        JPanel classCard = UIUtil.cardWithTitle("各班级人数");
        JPanel classContent = new JPanel();
        classContent.setLayout(new BoxLayout(classContent, BoxLayout.Y_AXIS));
        classContent.setBackground(Color.WHITE);

        java.util.Set<String> seen = new java.util.HashSet<>();
        for (Student s : svc.listAll()) {
            String key = s.getClazz() + " (" + s.getMajor() + " " + s.getGrade() + "级)";
            if (seen.add(key)) {
                long count = svc.searchByClass(s.getClazz()).size();
                JPanel row = new JPanel(new BorderLayout());
                row.setBackground(Color.WHITE);
                row.setBorder(BorderFactory.createEmptyBorder(5, 8, 5, 8));
                JLabel lbl = new JLabel(key);
                lbl.setFont(UIUtil.BODY_FONT);
                row.add(lbl, BorderLayout.WEST);
                JLabel cnt = new JLabel(count + " 人");
                cnt.setFont(UIUtil.BODY_BOLD);
                cnt.setForeground(UIUtil.PRIMARY);
                row.add(cnt, BorderLayout.EAST);
                classContent.add(row);
            }
        }
        if (seen.isEmpty()) {
            JLabel emptyLbl = new JLabel("  暂无数据");
            emptyLbl.setFont(UIUtil.SMALL_FONT);
            emptyLbl.setForeground(UIUtil.TEXT_GRAY);
            classContent.add(emptyLbl);
        }
        classCard.add(new JScrollPane(classContent), BorderLayout.CENTER);
        detailPanel.add(classCard);

        // 各专业人数
        JPanel majorCard = UIUtil.cardWithTitle("各专业人数");
        JPanel majorContent = new JPanel();
        majorContent.setLayout(new BoxLayout(majorContent, BoxLayout.Y_AXIS));
        majorContent.setBackground(Color.WHITE);

        Map<String, Long> majorCount = svc.listAll().stream()
            .collect(Collectors.groupingBy(Student::getMajor, Collectors.counting()));
        if (majorCount.isEmpty()) {
            JLabel emptyLbl = new JLabel("  暂无数据");
            emptyLbl.setFont(UIUtil.SMALL_FONT);
            emptyLbl.setForeground(UIUtil.TEXT_GRAY);
            majorContent.add(emptyLbl);
        } else {
            for (var entry : majorCount.entrySet()) {
                JPanel row = new JPanel(new BorderLayout());
                row.setBackground(Color.WHITE);
                row.setBorder(BorderFactory.createEmptyBorder(5, 8, 5, 8));
                JLabel lbl = new JLabel(entry.getKey());
                lbl.setFont(UIUtil.BODY_FONT);
                row.add(lbl, BorderLayout.WEST);
                JLabel cnt = new JLabel(entry.getValue() + " 人");
                cnt.setFont(UIUtil.BODY_BOLD);
                cnt.setForeground(UIUtil.ACCENT);
                row.add(cnt, BorderLayout.EAST);
                majorContent.add(row);
            }
        }
        majorCard.add(new JScrollPane(majorContent), BorderLayout.CENTER);
        detailPanel.add(majorCard);

        cardsPanel.revalidate();
        cardsPanel.repaint();
        detailPanel.revalidate();
        detailPanel.repaint();
    }
}
