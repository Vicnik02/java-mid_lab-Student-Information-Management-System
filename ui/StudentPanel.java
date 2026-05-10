package ui;

import entity.*;
import service.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import javax.swing.*;
import javax.swing.table.*;

/**
 * 学生管理面板 — 添加、修改、删除、浏览学生
 */
public class StudentPanel extends JPanel {

    private final StudentService svc;
    private final MainFrame mainFrame;
    private JTable table;
    private DefaultTableModel tableModel;
    private String filter = "all";
    private JPanel statCardsPanel;
    private JLabel countLbl;

    // 筛选按钮
    private JButton allBtn, ugBtn, gradBtn;

    public StudentPanel(StudentService svc, MainFrame mainFrame) {
        this.svc = svc;
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout(0, 12));
        setBackground(UIUtil.BG);

        add(buildTopBar(), BorderLayout.NORTH);
        add(buildTableArea(), BorderLayout.CENTER);
        add(buildBottomBar(), BorderLayout.SOUTH);
    }

    private JPanel buildTopBar() {
        JPanel top = new JPanel(new BorderLayout(0, 8));
        top.setBackground(UIUtil.BG);

        // 标题 — 放在 NORTH 使其撑满宽度
        top.add(UIUtil.pageTitle("学生管理"), BorderLayout.NORTH);

        // 操作按钮 + 筛选栏
        JPanel actionsRow = new JPanel(new BorderLayout());
        actionsRow.setBackground(UIUtil.BG);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        btnRow.setBackground(UIUtil.BG);

        JButton addUgBtn = UIUtil.primaryButton("＋ 添加本科生");
        JButton addGradBtn = UIUtil.accentButton("＋ 添加研究生");
        JButton editBtn = UIUtil.primaryButton("修改");
        JButton delBtn = UIUtil.dangerButton("删除");

        addUgBtn.addActionListener(e -> showAddDialog(false));
        addGradBtn.addActionListener(e -> showAddDialog(true));
        editBtn.addActionListener(e -> editSelected());
        delBtn.addActionListener(e -> deleteSelected());

        btnRow.add(addUgBtn);
        btnRow.add(addGradBtn);
        btnRow.add(Box.createHorizontalStrut(8));
        btnRow.add(editBtn);
        btnRow.add(delBtn);

        // 筛选标签栏
        JPanel filterBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        filterBar.setBackground(UIUtil.BG);

        allBtn = UIUtil.tabButton("全部");
        ugBtn = UIUtil.tabButton("本科生");
        gradBtn = UIUtil.tabButton("研究生");

        allBtn.addActionListener(e -> { filter = "all"; updateFilterUI(); refresh(); });
        ugBtn.addActionListener(e -> { filter = "undergrad"; updateFilterUI(); refresh(); });
        gradBtn.addActionListener(e -> { filter = "grad"; updateFilterUI(); refresh(); });

        UIUtil.setTabSelected(allBtn, true);

        filterBar.add(new JLabel("筛选："));
        filterBar.add(allBtn);
        filterBar.add(ugBtn);
        filterBar.add(gradBtn);

        actionsRow.add(btnRow, BorderLayout.WEST);
        actionsRow.add(filterBar, BorderLayout.EAST);
        top.add(actionsRow, BorderLayout.CENTER);
        return top;
    }

    private void updateFilterUI() {
        UIUtil.setTabSelected(allBtn, filter.equals("all"));
        UIUtil.setTabSelected(ugBtn, filter.equals("undergrad"));
        UIUtil.setTabSelected(gradBtn, filter.equals("grad"));
    }

    private JScrollPane buildTableArea() {
        String[] cols = {"学号", "姓名", "年龄", "年级", "班级", "专业", "类型", "总分", "平均分"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = UIUtil.styledTable(tableModel);
        UIUtil.alternateRowColors(table);
        UIUtil.addTableHoverEffect(table);

        // 列居中对齐：学号(0)、年龄(2)、年级(3)、类型(6)、总分(7)、平均分(8)
        int[] centerCols = {0, 2, 3, 6, 7, 8};
        for (int col : centerCols) {
            table.getColumnModel().getColumn(col).setCellRenderer(new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable t, Object value,
                        boolean isSelected, boolean hasFocus, int row, int col) {
                    Component c = super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, col);
                    setHorizontalAlignment(SwingConstants.CENTER);
                    if (!isSelected) {
                        Integer hoverRow = (Integer) t.getClientProperty("hoverRow");
                        if (hoverRow != null && hoverRow == row) {
                            c.setBackground(UIUtil.TABLE_ROW_HOVER);
                        } else {
                            c.setBackground(row % 2 == 0 ? UIUtil.TABLE_ROW_ODD : UIUtil.TABLE_ROW_EVEN);
                        }
                    }
                    // 成绩列加粗
                    if (col == 7 || col == 8) {
                        setFont(UIUtil.SCORE_FONT);
                    } else {
                        setFont(UIUtil.BODY_FONT);
                    }
                    setBorder(BorderFactory.createEmptyBorder(2, 10, 2, 10));
                    return c;
                }
            });
        }

        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createCompoundBorder(
            UIUtil.CARD_SHADOW,
            BorderFactory.createEmptyBorder(0, 0, 0, 0)));
        sp.setBackground(Color.WHITE);
        sp.getViewport().setBackground(Color.WHITE);
        return sp;
    }

    private JPanel buildBottomBar() {
        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setBackground(UIUtil.BG);

        // 学生数量提示
        countLbl = new JLabel();
        countLbl.setFont(UIUtil.SMALL_FONT);
        countLbl.setForeground(UIUtil.TEXT_GRAY);
        countLbl.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

        // 三张统计卡片
        statCardsPanel = new JPanel(new GridLayout(1, 3, 12, 0));
        statCardsPanel.setBackground(UIUtil.BG);

        bottom.add(countLbl, BorderLayout.NORTH);
        bottom.add(statCardsPanel, BorderLayout.CENTER);
        return bottom;
    }

    // ==================== 数据刷新 ====================

    public void refresh() {
        List<Student> list = switch (filter) {
            case "undergrad" -> svc.listUndergraduates();
            case "grad"      -> svc.listGraduates();
            default          -> svc.listAll();
        };

        tableModel.setRowCount(0);
        for (Student s : list) {
            tableModel.addRow(new Object[]{
                s.getStudentId(), s.getName(), s.getAge(), s.getGrade(),
                s.getClazz(), s.getMajor(), s.getTypeLabel(),
                String.format("%.1f", s.calculateTotal()),
                String.format("%.1f", s.calculateAverage())
            });
        }

        // 更新底部统计卡片
        statCardsPanel.removeAll();
        statCardsPanel.add(UIUtil.statCard("学生总人数",
            String.valueOf(Student.totalCount), UIUtil.PRIMARY, UIUtil.PRIMARY_DARK));
        statCardsPanel.add(UIUtil.statCard("本科生",
            String.valueOf(Student.undergradCount), UIUtil.PRIMARY_LIGHT, UIUtil.PRIMARY_LIGHT));
        statCardsPanel.add(UIUtil.statCard("研究生",
            String.valueOf(Student.gradCount), UIUtil.ACCENT, UIUtil.ACCENT_DARK));
        statCardsPanel.revalidate();
        statCardsPanel.repaint();

        countLbl.setText("当前显示 " + list.size() + " 名学生  |  系统容量 " + Student.MAX_CAPACITY);
    }

    // ==================== 添加对话框 ====================

    private void showAddDialog(boolean isGraduate) {
        String type = isGraduate ? "添加研究生" : "添加本科生";
        JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), type, true);
        dialog.setSize(480, isGraduate ? 580 : 500);
        dialog.setLocationRelativeTo(this);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));
        form.setBackground(Color.WHITE);

        JTextField idF = new JTextField(15);
        JTextField nameF = new JTextField(15);
        JTextField ageF = new JTextField(15);
        JTextField gradeF = new JTextField("2024", 15);
        JTextField clazzF = new JTextField("1班", 15);
        JTextField majorF = new JTextField("软件工程", 15);
        JTextField provF = new JTextField(15);
        JTextField cityF = new JTextField(15);
        JTextField streetF = new JTextField(15);
        JTextField doorF = new JTextField(15);
        JTextField advF = new JTextField(15);
        JTextField dirF = new JTextField(15);

        String[] labels = {"学号", "姓名", "年龄", "年级", "班级", "专业",
                           "省份", "城市", "街道", "门牌号"};
        JTextField[] fields = {idF, nameF, ageF, gradeF, clazzF, majorF,
                               provF, cityF, streetF, doorF};

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.WEST;

        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = 0; gbc.gridy = i;
            JLabel lbl = new JLabel(labels[i] + "：");
            lbl.setFont(UIUtil.BODY_FONT);
            form.add(lbl, gbc);
            gbc.gridx = 1;
            fields[i].setFont(UIUtil.BODY_FONT);
            form.add(fields[i], gbc);
        }

        if (isGraduate) {
            int row = labels.length;
            gbc.gridx = 0; gbc.gridy = row;
            form.add(new JLabel("导师："), gbc);
            gbc.gridx = 1;
            advF.setFont(UIUtil.BODY_FONT);
            form.add(advF, gbc);
            gbc.gridx = 0; gbc.gridy = row + 1;
            form.add(new JLabel("研究方向："), gbc);
            gbc.gridx = 1;
            dirF.setFont(UIUtil.BODY_FONT);
            form.add(dirF, gbc);
        }

        JButton submitBtn = UIUtil.primaryButton("确认添加");
        gbc.gridx = 0; gbc.gridy = labels.length + (isGraduate ? 2 : 0) + 1;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        form.add(submitBtn, gbc);

        submitBtn.addActionListener(e -> {
            try {
                int age = Integer.parseInt(ageF.getText().trim());
                String result;
                if (isGraduate) {
                    result = svc.addGraduate(idF.getText().trim(), nameF.getText().trim(), age,
                        gradeF.getText().trim(), clazzF.getText().trim(), majorF.getText().trim(),
                        provF.getText().trim(), cityF.getText().trim(),
                        streetF.getText().trim(), doorF.getText().trim(),
                        advF.getText().trim(), dirF.getText().trim());
                } else {
                    result = svc.addUndergraduate(idF.getText().trim(), nameF.getText().trim(), age,
                        gradeF.getText().trim(), clazzF.getText().trim(), majorF.getText().trim(),
                        provF.getText().trim(), cityF.getText().trim(),
                        streetF.getText().trim(), doorF.getText().trim());
                }
                if (result.startsWith("OK:")) {
                    dialog.dispose();
                    refresh();
                    JOptionPane.showMessageDialog(this, result.substring(3), "成功", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(dialog, result.substring(6), "错误", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "年龄请输入数字", "错误", JOptionPane.ERROR_MESSAGE);
            }
        });

        dialog.add(form);
        dialog.setVisible(true);
    }

    // ==================== 修改 ====================

    private void editSelected() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "请先在表格中选择一名学生", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String id = (String) tableModel.getValueAt(row, 0);
        Student s = svc.searchById(id);
        if (s == null) return;

        JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "修改学生 — " + id, true);
        dialog.setSize(480, 520);
        dialog.setLocationRelativeTo(this);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));
        form.setBackground(Color.WHITE);

        JTextField nameF = new JTextField(s.getName(), 15);
        JTextField ageF = new JTextField(String.valueOf(s.getAge()), 15);
        JTextField gradeF = new JTextField(s.getGrade(), 15);
        JTextField clazzF = new JTextField(s.getClazz(), 15);
        JTextField majorF = new JTextField(s.getMajor(), 15);
        JTextField provF = new JTextField(s.getAddress() != null ? s.getAddress().getProvince() : "", 15);
        JTextField cityF = new JTextField(s.getAddress() != null ? s.getAddress().getCity() : "", 15);
        JTextField streetF = new JTextField(s.getAddress() != null ? s.getAddress().getStreet() : "", 15);
        JTextField doorF = new JTextField(s.getAddress() != null ? s.getAddress().getDoorNumber() : "", 15);

        String[] labels = {"姓名", "年龄", "年级", "班级", "专业", "省份", "城市", "街道", "门牌号"};
        JTextField[] fields = {nameF, ageF, gradeF, clazzF, majorF, provF, cityF, streetF, doorF};

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.WEST;

        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = 0; gbc.gridy = i;
            form.add(new JLabel(labels[i] + "："), gbc);
            gbc.gridx = 1;
            fields[i].setFont(UIUtil.BODY_FONT);
            form.add(fields[i], gbc);
        }

        JButton submitBtn = UIUtil.primaryButton("保存修改");
        gbc.gridx = 0; gbc.gridy = labels.length + 1;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        form.add(submitBtn, gbc);

        submitBtn.addActionListener(e -> {
            try {
                int age = Integer.parseInt(ageF.getText().trim());
                String result = svc.updateStudent(id, nameF.getText().trim(), age,
                    gradeF.getText().trim(), clazzF.getText().trim(), majorF.getText().trim(),
                    provF.getText().trim(), cityF.getText().trim(),
                    streetF.getText().trim(), doorF.getText().trim());
                if (result.startsWith("OK:")) {
                    dialog.dispose();
                    refresh();
                    JOptionPane.showMessageDialog(this, result.substring(3), "成功", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(dialog, result.substring(6), "错误", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "年龄请输入数字", "错误", JOptionPane.ERROR_MESSAGE);
            }
        });

        dialog.add(form);
        dialog.setVisible(true);
    }

    // ==================== 删除 ====================

    private void deleteSelected() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "请先在表格中选择一名学生", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String id = (String) tableModel.getValueAt(row, 0);
        String name = (String) tableModel.getValueAt(row, 1);
        int confirm = JOptionPane.showConfirmDialog(this,
            "确认删除学生 " + name + "（" + id + "）？\n此操作不可恢复！",
            "确认删除", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            String result = svc.deleteStudent(id);
            if (result.startsWith("OK:")) {
                refresh();
                JOptionPane.showMessageDialog(this, result.substring(3), "成功", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, result.substring(6), "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
