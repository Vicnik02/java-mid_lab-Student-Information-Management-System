package ui;

import entity.*;
import service.*;
import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/**
 * 查询面板 — 按学号 / 姓名 / 班级 / 专业检索
 */
public class QueryPanel extends JPanel {

    private final StudentService svc;
    private JComboBox<String> typeCombo;
    private JTextField keywordField;
    private JTable resultTable;
    private DefaultTableModel tableModel;
    private JLabel resultCountLbl;

    public QueryPanel(StudentService svc) {
        this.svc = svc;
        setLayout(new BorderLayout(0, 12));
        setBackground(UIUtil.BG);
        add(buildTopBar(), BorderLayout.NORTH);
        add(buildTableArea(), BorderLayout.CENTER);
    }

    private JPanel buildTopBar() {
        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(UIUtil.BG);
        JLabel title = UIUtil.pageTitle("查询功能");

        JPanel searchBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        searchBar.setBackground(Color.WHITE);
        searchBar.setBorder(BorderFactory.createCompoundBorder(
            UIUtil.CARD_SHADOW,
            BorderFactory.createEmptyBorder(8, 12, 8, 12)));

        typeCombo = new JComboBox<>(new String[]{"按学号", "按姓名", "按班级", "按专业"});
        typeCombo.setFont(UIUtil.BODY_FONT);
        keywordField = new JTextField(18);
        keywordField.setFont(UIUtil.BODY_FONT);
        JButton searchBtn = UIUtil.primaryButton("搜索");
        JButton viewBtn = UIUtil.accentButton("查看详情");

        searchBtn.addActionListener(e -> doSearch());
        viewBtn.addActionListener(e -> viewDetail());

        // 回车触发搜索
        keywordField.addActionListener(e -> doSearch());

        searchBar.add(new JLabel("查询方式："));
        searchBar.add(typeCombo);
        searchBar.add(new JLabel("关键字："));
        searchBar.add(keywordField);
        searchBar.add(searchBtn);
        searchBar.add(viewBtn);

        resultCountLbl = new JLabel("  请输入关键字后点击搜索");
        resultCountLbl.setFont(UIUtil.SMALL_FONT);
        resultCountLbl.setForeground(UIUtil.TEXT_GRAY);

        top.add(title, BorderLayout.NORTH);
        top.add(searchBar, BorderLayout.CENTER);
        top.add(resultCountLbl, BorderLayout.SOUTH);
        return top;
    }

    private JScrollPane buildTableArea() {
        String[] cols = {"学号", "姓名", "年龄", "年级", "班级", "专业", "类型", "总分", "平均分"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        resultTable = UIUtil.styledTable(tableModel);
        UIUtil.alternateRowColors(resultTable);
        UIUtil.addTableHoverEffect(resultTable);

        JScrollPane sp = new JScrollPane(resultTable);
        sp.setBorder(BorderFactory.createEmptyBorder());
        sp.getViewport().setBackground(Color.WHITE);
        return sp;
    }

    public void refresh() {
        // 保留搜索结果，不清空
    }

    private void doSearch() {
        String keyword = keywordField.getText().trim();
        if (keyword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请输入查询关键字", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int type = typeCombo.getSelectedIndex();
        List<Student> results = switch (type) {
            case 0 -> { Student s = svc.searchById(keyword); yield s != null ? List.of(s) : List.of(); }
            case 1 -> svc.searchByName(keyword);
            case 2 -> svc.searchByClass(keyword);
            case 3 -> svc.searchByMajor(keyword);
            default -> List.of();
        };

        tableModel.setRowCount(0);
        for (Student s : results) {
            tableModel.addRow(new Object[]{
                s.getStudentId(), s.getName(), s.getAge(), s.getGrade(),
                s.getClazz(), s.getMajor(), s.getTypeLabel(),
                String.format("%.1f", s.calculateTotal()),
                String.format("%.1f", s.calculateAverage())
            });
        }
        resultCountLbl.setText("  找到 " + results.size() + " 条结果");
    }

    private void viewDetail() {
        int row = resultTable.getSelectedRow();
        if (row < 0) {
            if (tableModel.getRowCount() == 1) {
                String id = (String) tableModel.getValueAt(0, 0);
                Student s = svc.searchById(id);
                if (s != null) {
                    JTextArea area = new JTextArea(s.getInfo());
                    area.setEditable(false);
                    area.setFont(UIUtil.MONO_FONT);
                    JOptionPane.showMessageDialog(this, new JScrollPane(area),
                        "学生详情", JOptionPane.INFORMATION_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "请先在结果中选择一名学生", "提示", JOptionPane.WARNING_MESSAGE);
            }
            return;
        }
        String id = (String) tableModel.getValueAt(row, 0);
        Student s = svc.searchById(id);
        if (s != null) {
            JTextArea area = new JTextArea(s.getInfo());
            area.setEditable(false);
            area.setFont(UIUtil.MONO_FONT);
            JOptionPane.showMessageDialog(this, new JScrollPane(area),
                "学生详情", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}
