package ui;

import entity.*;
import service.*;
import java.awt.*;
import java.util.List;
import java.util.Map;
import javax.swing.*;

/**
 * 学科管理面板 — 按专业+年级绑定/解绑学科
 */
public class SubjectPanel extends JPanel {

    private final StudentService svc;
    private DefaultListModel<String> groupListModel;
    private JList<String> groupList;
    private DefaultListModel<String> subjectListModel;
    private JList<String> subjectList;
    private JLabel currentGroupLbl;

    public SubjectPanel(StudentService svc) {
        this.svc = svc;
        setLayout(new BorderLayout(0, 12));
        setBackground(UIUtil.BG);
        add(buildTopBar(), BorderLayout.NORTH);
        add(buildContent(), BorderLayout.CENTER);
    }

    private JPanel buildTopBar() {
        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(UIUtil.BG);

        JLabel title = UIUtil.pageTitle("学科管理");

        JPanel btnBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        btnBar.setBackground(UIUtil.BG);

        JButton addGroupBtn = UIUtil.primaryButton("＋ 新增绑定组");
        JButton addSubBtn = UIUtil.accentButton("＋ 添加学科");
        JButton delSubBtn = UIUtil.dangerButton("删除学科");

        addGroupBtn.addActionListener(e -> showAddGroupDialog());
        addSubBtn.addActionListener(e -> showAddSubjectDialog());
        delSubBtn.addActionListener(e -> deleteSubject());

        btnBar.add(addGroupBtn);
        btnBar.add(addSubBtn);
        btnBar.add(delSubBtn);
        top.add(title, BorderLayout.NORTH);
        top.add(btnBar, BorderLayout.CENTER);
        return top;
    }

    private JPanel buildContent() {
        JPanel content = new JPanel(new GridLayout(1, 2, 12, 0));
        content.setBackground(UIUtil.BG);

        // 左侧：绑定组列表
        JPanel leftCard = UIUtil.cardWithTitle("专业 + 年级 绑定组");
        groupListModel = new DefaultListModel<>();
        groupList = new JList<>(groupListModel);
        groupList.setFont(UIUtil.BODY_FONT);
        groupList.setSelectionBackground(UIUtil.PRIMARY_PALE);
        groupList.setSelectionForeground(UIUtil.TEXT_MAIN);
        groupList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) loadSubjectsForSelected();
        });
        leftCard.add(new JScrollPane(groupList), BorderLayout.CENTER);
        content.add(leftCard);

        // 右侧：学科列表
        JPanel rightCard = UIUtil.cardWithTitle("绑定学科");
        currentGroupLbl = new JLabel("请先在左侧选择绑定组");
        currentGroupLbl.setFont(UIUtil.SMALL_FONT);
        currentGroupLbl.setForeground(UIUtil.TEXT_GRAY);
        rightCard.add(currentGroupLbl, BorderLayout.NORTH);

        subjectListModel = new DefaultListModel<>();
        subjectList = new JList<>(subjectListModel);
        subjectList.setFont(UIUtil.BODY_FONT);
        subjectList.setSelectionBackground(UIUtil.ACCENT_LIGHT);
        rightCard.add(new JScrollPane(subjectList), BorderLayout.CENTER);
        content.add(rightCard);

        return content;
    }

    public void refresh() {
        groupListModel.clear();
        Map<String, List<Subject>> all = svc.getAllSubjectMap();
        for (String key : all.keySet()) {
            String[] parts = key.split(":");
            groupListModel.addElement(parts[0] + " — " + parts[1] + " 级 (" + all.get(key).size() + "科)");
        }
        subjectListModel.clear();
        currentGroupLbl.setText("请先在左侧选择绑定组");
    }

    private void loadSubjectsForSelected() {
        int idx = groupList.getSelectedIndex();
        if (idx < 0) return;

        String raw = groupListModel.get(idx);
        String[] parts = raw.split(" — ");
        String major = parts[0].trim();
        String[] rest = parts[1].split(" ");
        String grade = rest[0].trim();

        currentGroupLbl.setText("当前组：" + major + "  " + grade + " 级");
        subjectListModel.clear();
        List<Subject> subs = svc.getSubjects(major, grade);
        for (Subject s : subs) {
            subjectListModel.addElement(s.getSubjectId() + " — " + s.getSubjectName());
        }
    }

    private String getSelectedGroupMajor() {
        int idx = groupList.getSelectedIndex();
        if (idx < 0) return null;
        return groupListModel.get(idx).split(" — ")[0].trim();
    }

    private String getSelectedGroupGrade() {
        int idx = groupList.getSelectedIndex();
        if (idx < 0) return null;
        String[] parts = groupListModel.get(idx).split(" — ");
        return parts[1].split(" ")[0].trim();
    }

    private void showAddGroupDialog() {
        JTextField majorF = new JTextField(15);
        JTextField gradeF = new JTextField("2024", 15);
        JTextField subIdF = new JTextField(15);
        JTextField subNameF = new JTextField(15);

        majorF.setFont(UIUtil.BODY_FONT);
        gradeF.setFont(UIUtil.BODY_FONT);
        subIdF.setFont(UIUtil.BODY_FONT);
        subNameF.setFont(UIUtil.BODY_FONT);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0; form.add(new JLabel("专业："), gbc);
        gbc.gridx = 1; form.add(majorF, gbc);
        gbc.gridx = 0; gbc.gridy = 1; form.add(new JLabel("年级："), gbc);
        gbc.gridx = 1; form.add(gradeF, gbc);
        gbc.gridx = 0; gbc.gridy = 2; form.add(new JLabel("首个学科编号："), gbc);
        gbc.gridx = 1; form.add(subIdF, gbc);
        gbc.gridx = 0; gbc.gridy = 3; form.add(new JLabel("首个学科名称："), gbc);
        gbc.gridx = 1; form.add(subNameF, gbc);

        int opt = JOptionPane.showConfirmDialog(this, form, "新增绑定组",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (opt == JOptionPane.OK_OPTION) {
            String result = svc.addSubject(majorF.getText().trim(), gradeF.getText().trim(),
                subIdF.getText().trim(), subNameF.getText().trim());
            if (result.startsWith("OK:")) refresh();
            else JOptionPane.showMessageDialog(this, result.substring(6), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showAddSubjectDialog() {
        String major = getSelectedGroupMajor();
        String grade = getSelectedGroupGrade();
        if (major == null) {
            JOptionPane.showMessageDialog(this, "请先在左侧选择绑定组", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        JTextField subIdF = new JTextField(15);
        JTextField subNameF = new JTextField(15);
        subIdF.setFont(UIUtil.BODY_FONT);
        subNameF.setFont(UIUtil.BODY_FONT);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.gridx = 0; gbc.gridy = 0; form.add(new JLabel("学科编号："), gbc);
        gbc.gridx = 1; form.add(subIdF, gbc);
        gbc.gridx = 0; gbc.gridy = 1; form.add(new JLabel("学科名称："), gbc);
        gbc.gridx = 1; form.add(subNameF, gbc);

        int opt = JOptionPane.showConfirmDialog(this, form, "添加学科到 " + major + " " + grade + " 级",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (opt == JOptionPane.OK_OPTION) {
            String result = svc.addSubject(major, grade, subIdF.getText().trim(), subNameF.getText().trim());
            if (result.startsWith("OK:")) { refresh(); loadSubjectsForSelected(); }
            else JOptionPane.showMessageDialog(this, result.substring(6), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteSubject() {
        String major = getSelectedGroupMajor();
        String grade = getSelectedGroupGrade();
        if (major == null || subjectList.getSelectedIndex() < 0) {
            JOptionPane.showMessageDialog(this, "请先选择要删除的学科", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String sel = subjectListModel.get(subjectList.getSelectedIndex());
        String subId = sel.split(" — ")[0];

        int confirm = JOptionPane.showConfirmDialog(this,
            "确认删除学科 " + sel + "？", "确认删除", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            String result = svc.removeSubject(major, grade, subId);
            if (result.startsWith("OK:")) { refresh(); loadSubjectsForSelected(); }
            else JOptionPane.showMessageDialog(this, result.substring(6), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }
}
