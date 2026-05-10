package ui;

import entity.*;
import service.*;
import java.awt.*;
import java.util.List;
import javax.swing.*;

/**
 * 成绩管理面板 — 选择学生、自动加载对应学科、录入成绩
 */
public class ScorePanel extends JPanel {

    private final StudentService svc;
    private JComboBox<String> studentCombo;
    private JTextArea infoArea;
    private JPanel scoreCard;
    private java.util.List<JTextField> scoreFields;
    private java.util.List<Subject> currentSubjects;
    private Student currentStudent;

    public ScorePanel(StudentService svc) {
        this.svc = svc;
        setLayout(new BorderLayout(0, 12));
        setBackground(UIUtil.BG);
        add(buildTopBar(), BorderLayout.NORTH);
        add(buildContent(), BorderLayout.CENTER);
    }

    private JPanel buildTopBar() {
        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(UIUtil.BG);
        JLabel title = UIUtil.pageTitle("成绩管理");

        JPanel selBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 4));
        selBar.setBackground(UIUtil.BG);
        selBar.add(new JLabel("选择学生："));
        studentCombo = new JComboBox<>();
        studentCombo.setFont(UIUtil.BODY_FONT);
        studentCombo.setPreferredSize(new Dimension(220, 32));
        selBar.add(studentCombo);

        JButton loadBtn = UIUtil.primaryButton("加载学科");
        loadBtn.addActionListener(e -> loadStudentSubjects());
        selBar.add(loadBtn);

        top.add(title, BorderLayout.NORTH);
        top.add(selBar, BorderLayout.CENTER);
        return top;
    }

    private JPanel buildContent() {
        JPanel content = new JPanel(new GridLayout(1, 2, 12, 0));
        content.setBackground(UIUtil.BG);

        // 左侧：学生信息卡片
        JPanel leftCard = UIUtil.cardWithTitle("学生信息");
        infoArea = new JTextArea();
        infoArea.setEditable(false);
        infoArea.setFont(UIUtil.BODY_FONT);
        infoArea.setBackground(UIUtil.BG);
        JScrollPane infoSP = new JScrollPane(infoArea);
        infoSP.setBorder(BorderFactory.createEmptyBorder());
        leftCard.add(infoSP, BorderLayout.CENTER);
        content.add(leftCard);

        // 右侧：成绩录入卡片 — 保留标题，内容区动态替换
        scoreCard = UIUtil.cardWithTitle("录入成绩");
        // 初始占位
        JLabel placeholder = new JLabel("请选择学生并点击「加载学科」", SwingConstants.CENTER);
        placeholder.setFont(UIUtil.SMALL_FONT);
        placeholder.setForeground(UIUtil.TEXT_GRAY);
        scoreCard.add(placeholder, BorderLayout.CENTER);
        content.add(scoreCard);

        return content;
    }

    public void refresh() {
        studentCombo.removeAllItems();
        List<Student> list = svc.listAll();
        for (Student s : list) {
            studentCombo.addItem(s.getStudentId() + " — " + s.getName() + " (" + s.getTypeLabel() + ")");
        }
    }

    private void loadStudentSubjects() {
        int idx = studentCombo.getSelectedIndex();
        if (idx < 0) {
            JOptionPane.showMessageDialog(this, "请先选择学生", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String sel = (String) studentCombo.getSelectedItem();
        String studentId = sel.split(" — ")[0];
        currentStudent = svc.searchById(studentId);
        if (currentStudent == null) return;

        // 显示学生信息
        infoArea.setText(currentStudent.getInfo());

        // 加载对应学科
        currentSubjects = svc.getSubjects(currentStudent.getMajor(), currentStudent.getGrade());
        if (currentSubjects.isEmpty()) {
            replaceCenter(scoreCard, buildHintPanel("该专业+年级尚未绑定学科，请先在学科管理中配置"));
            scoreCard.revalidate();
            scoreCard.repaint();
            JOptionPane.showMessageDialog(this, "该专业+年级尚未绑定学科", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 重建成绩录入表单
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        scoreFields = new java.util.ArrayList<>();

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.anchor = GridBagConstraints.WEST;

        for (int i = 0; i < currentSubjects.size(); i++) {
            Subject sub = currentSubjects.get(i);
            gbc.gridx = 0; gbc.gridy = i;
            JLabel lbl = new JLabel(sub.getSubjectId() + " — " + sub.getSubjectName() + "：");
            lbl.setFont(UIUtil.BODY_FONT);
            form.add(lbl, gbc);

            gbc.gridx = 1;
            JTextField f = new JTextField(8);
            f.setFont(UIUtil.BODY_FONT);
            double existing = currentStudent.getScore(sub.getSubjectId());
            if (existing >= 0) f.setText(String.valueOf(existing));
            scoreFields.add(f);
            form.add(f, gbc);

            gbc.gridx = 2;
            JLabel hint = new JLabel("0~100");
            hint.setFont(UIUtil.SMALL_FONT);
            hint.setForeground(UIUtil.TEXT_GRAY);
            form.add(hint, gbc);
        }

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        btnRow.setBackground(Color.WHITE);
        JButton saveBtn = UIUtil.accentButton("保存全部成绩");
        saveBtn.addActionListener(e -> saveScores());
        btnRow.add(saveBtn);

        gbc.gridx = 0; gbc.gridy = currentSubjects.size();
        gbc.gridwidth = 3;
        gbc.anchor = GridBagConstraints.CENTER;
        form.add(btnRow, gbc);

        JScrollPane sp = new JScrollPane(form);
        sp.setBorder(BorderFactory.createEmptyBorder());
        replaceCenter(scoreCard, sp);
        scoreCard.revalidate();
        scoreCard.repaint();
    }

    /** 替换卡片 CENTER 区域内容，保留 NORTH 标题 */
    private void replaceCenter(JPanel card, JComponent comp) {
        BorderLayout layout = (BorderLayout) card.getLayout();
        Component old = layout.getLayoutComponent(BorderLayout.CENTER);
        if (old != null) card.remove(old);
        card.add(comp, BorderLayout.CENTER);
    }

    private JPanel buildHintPanel(String text) {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(Color.WHITE);
        JLabel hint = new JLabel(text);
        hint.setFont(UIUtil.SMALL_FONT);
        hint.setForeground(UIUtil.TEXT_GRAY);
        p.add(hint);
        return p;
    }

    private void saveScores() {
        if (currentStudent == null || currentSubjects == null) return;

        StringBuilder msgs = new StringBuilder();
        for (int i = 0; i < currentSubjects.size(); i++) {
            String text = scoreFields.get(i).getText().trim();
            if (text.isEmpty()) continue;
            try {
                double score = Double.parseDouble(text);
                String result = svc.setScore(currentStudent.getStudentId(),
                    currentSubjects.get(i).getSubjectId(), score);
                if (result.startsWith("OK:")) {
                    msgs.append(currentSubjects.get(i).getSubjectName()).append(" → ").append(score).append(" 分\n");
                } else {
                    msgs.append(currentSubjects.get(i).getSubjectName()).append("：").append(result.substring(6)).append("\n");
                }
            } catch (NumberFormatException e) {
                msgs.append(currentSubjects.get(i).getSubjectName()).append("：格式错误\n");
            }
        }
        JOptionPane.showMessageDialog(this, msgs.toString(), "保存结果", JOptionPane.INFORMATION_MESSAGE);
        loadStudentSubjects(); // 刷新显示
    }
}
