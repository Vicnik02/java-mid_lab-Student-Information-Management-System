package ui;

import entity.*;
import service.*;
import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

/**
 * 排名面板 — 班级总分排名 / 单科排名
 */
public class RankPanel extends JPanel {

    private final StudentService svc;
    private JComboBox<String> classCombo;
    private JComboBox<String> subjectCombo;
    private JTable rankTable;
    private DefaultTableModel tableModel;
    private JLabel infoLbl;
    private boolean isSubjectRank = false;

    public RankPanel(StudentService svc) {
        this.svc = svc;
        setLayout(new BorderLayout(0, 12));
        setBackground(UIUtil.BG);
        add(buildTopBar(), BorderLayout.NORTH);
        add(buildTableArea(), BorderLayout.CENTER);
    }

    private JPanel buildTopBar() {
        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(UIUtil.BG);
        JLabel title = UIUtil.pageTitle("排名功能");

        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        bar.setBackground(Color.WHITE);
        bar.setBorder(BorderFactory.createCompoundBorder(
            UIUtil.CARD_SHADOW,
            new EmptyBorder(8, 12, 8, 12)));

        bar.add(new JLabel("班级："));
        classCombo = new JComboBox<>();
        classCombo.setFont(UIUtil.BODY_FONT);
        classCombo.setPreferredSize(new Dimension(150, 32));
        bar.add(classCombo);

        bar.add(new JLabel("单科（可选）："));
        subjectCombo = new JComboBox<>();
        subjectCombo.setFont(UIUtil.BODY_FONT);
        subjectCombo.setPreferredSize(new Dimension(160, 32));
        subjectCombo.addItem("— 总分排名 —");
        bar.add(subjectCombo);

        JButton rankBtn = UIUtil.primaryButton("查看排名");
        rankBtn.addActionListener(e -> doRank());
        bar.add(rankBtn);

        infoLbl = new JLabel("  请选择班级后查看排名");
        infoLbl.setFont(UIUtil.SMALL_FONT);
        infoLbl.setForeground(UIUtil.TEXT_GRAY);

        top.add(title, BorderLayout.NORTH);
        top.add(bar, BorderLayout.CENTER);
        top.add(infoLbl, BorderLayout.SOUTH);
        return top;
    }

    private JScrollPane buildTableArea() {
        String[] cols = {"名次", "学号", "姓名", "类型", "分数"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        rankTable = UIUtil.styledTable(tableModel);

        JScrollPane sp = new JScrollPane(rankTable);
        sp.setBorder(BorderFactory.createEmptyBorder());
        sp.getViewport().setBackground(Color.WHITE);
        return sp;
    }

    public void refresh() {
        classCombo.removeAllItems();
        for (String cid : svc.getAllClassIds()) {
            classCombo.addItem(cid);
        }
        subjectCombo.removeAllItems();
        subjectCombo.addItem("— 总分排名 —");
    }

    private void doRank() {
        String classId = (String) classCombo.getSelectedItem();
        if (classId == null) {
            JOptionPane.showMessageDialog(this, "请先选择班级", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String subSel = (String) subjectCombo.getSelectedItem();
        isSubjectRank = subSel != null && !subSel.startsWith("—");

        List<Student> ranked;
        if (isSubjectRank) {
            String subId = subSel.split(" — ")[0];
            ranked = svc.getClassSubjectRankingList(classId, subId);
        } else {
            ranked = svc.getClassRankingList(classId);
        }

        if (ranked.isEmpty()) {
            infoLbl.setText("  该班级暂无学生数据");
            tableModel.setRowCount(0);
            return;
        }

        // 根据班级加载学科列表
        if (!isSubjectRank && !ranked.isEmpty()) {
            Student first = ranked.get(0);
            List<Subject> subs = svc.getSubjects(first.getMajor(), first.getGrade());
            subjectCombo.removeAllItems();
            subjectCombo.addItem("— 总分排名 —");
            for (Subject sub : subs) {
                subjectCombo.addItem(sub.getSubjectId() + " — " + sub.getSubjectName());
            }
        }

        infoLbl.setText("  班级：" + classId + "  |  人数：" + ranked.size()
            + "  |  " + (isSubjectRank ? "单科排名" : "总分排名"));

        tableModel.setRowCount(0);
        for (int i = 0; i < ranked.size(); i++) {
            Student s = ranked.get(i);
            String score;
            if (isSubjectRank) {
                double sc = s.getScore(subSel.split(" — ")[0]);
                score = sc >= 0 ? String.format("%.1f", sc) : "未录入";
            } else {
                score = String.format("%.1f", s.calculateTotal());
            }
            tableModel.addRow(new Object[]{i + 1, s.getStudentId(), s.getName(), s.getTypeLabel(), score});
        }

        highlightTop3();
    }

    private void highlightTop3() {
        rankTable.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value,
                    boolean isSelected, boolean hasFocus, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, col);
                if (!isSelected) {
                    if (row == 0) c.setBackground(UIUtil.TOP1_BG);
                    else if (row == 1) c.setBackground(UIUtil.TOP2_BG);
                    else if (row == 2) c.setBackground(UIUtil.TOP3_BG);
                    else c.setBackground(row % 2 == 0 ? UIUtil.TABLE_ROW_ODD : UIUtil.TABLE_ROW_EVEN);
                }
                // 第一名粗体
                setFont(row == 0 ? UIUtil.BODY_BOLD : UIUtil.BODY_FONT);
                if (col == 0 && row == 0) setForeground(UIUtil.TOP1_FG);
                else if (col == 0 && row == 1) setForeground(UIUtil.TOP2_FG);
                else if (col == 0 && row == 2) setForeground(UIUtil.TOP3_FG);
                else setForeground(UIUtil.TEXT_MAIN);
                setHorizontalAlignment(col == 0 || col == 4 ? SwingConstants.CENTER : SwingConstants.LEFT);
                setBorder(BorderFactory.createEmptyBorder(2, 10, 2, 10));
                return c;
            }
        });
    }
}
