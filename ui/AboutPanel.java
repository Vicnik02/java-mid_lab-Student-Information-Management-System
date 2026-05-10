package ui;

import java.awt.*;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

/**
 * 关于面板 — 显示程序信息，校园科技风卡片设计
 */
public class AboutPanel extends JPanel {

    public AboutPanel() {
        setLayout(new BorderLayout(0, 12));
        setBackground(UIUtil.BG);

        JLabel title = UIUtil.pageTitle("关于系统");
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 16, 0));

        JPanel card = UIUtil.card();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        // 南昌大学校徽（等比缩放）
        JPanel iconRow = new JPanel(new FlowLayout(FlowLayout.CENTER));
        iconRow.setBackground(Color.WHITE);
        try {
            File f = new File("9a3721239fe84a5fb9f79411a4b2d6b9.jpg");
            if (f.exists()) {
                BufferedImage img = ImageIO.read(f);
                int w = img.getWidth();
                int h = img.getHeight();
                double ratio = Math.min(100.0 / w, 100.0 / h);
                Image scaled = img.getScaledInstance((int)(w * ratio), (int)(h * ratio), Image.SCALE_SMOOTH);
                JLabel logoLbl = new JLabel(new ImageIcon(scaled));
                iconRow.add(logoLbl);
            }
        } catch (Exception ignored) {}
        card.add(iconRow);
        card.add(Box.createVerticalStrut(8));

        String[][] info = {
            {"程序名称", "学生信息管理系统"},
            {"版本号",   "v1.0.0"},
            {"构建时间", "2026-05"},
            {"作者",     "邱世豪"},
            {"学号",     "8002125052"},
            {"班级",     "软件工程2502班"},
            {"开发语言", "Java (JDK 21)"},
            {"运行方式", "Swing 图形界面"},
        };

        for (String[] row : info) {
            JPanel line = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 4));
            line.setBackground(Color.WHITE);
            JLabel lbl = new JLabel(row[0] + "：");
            lbl.setFont(new Font("Microsoft YaHei", Font.BOLD, 14));
            lbl.setForeground(UIUtil.TEXT_GRAY);
            lbl.setPreferredSize(new Dimension(80, 24));
            line.add(lbl);
            JLabel val = new JLabel(row[1]);
            val.setFont(UIUtil.BODY_FONT);
            val.setForeground(UIUtil.TEXT_MAIN);
            line.add(val);
            card.add(line);
        }

        card.add(Box.createVerticalStrut(12));
        JPanel sep = new JPanel();
        sep.setBackground(new Color(0xE5E9F0));
        sep.setMaximumSize(new Dimension(400, 1));
        card.add(sep);
        card.add(Box.createVerticalStrut(8));

        JLabel featuresTitle = new JLabel("技术亮点");
        featuresTitle.setFont(new Font("Microsoft YaHei", Font.BOLD, 15));
        featuresTitle.setForeground(UIUtil.PRIMARY);
        featuresTitle.setAlignmentX(CENTER_ALIGNMENT);
        card.add(featuresTitle);
        card.add(Box.createVerticalStrut(4));

        String[] features = {
            "面向对象：继承（Student → Undergrad / Graduate）+ 组合（Address）",
            "Swing GUI：侧边栏导航 + 卡片布局 + 表格展示",
            "校园科技风配色：校徽蓝主题 + 前三名高亮 + 交替行着色",
            "学科动态绑定：按专业+年级统一管理",
            "班级排名：总分排名 + 单科排名",
            "全边界处理：判满 / 判空 / 输入校验",
        };

        for (String feat : features) {
            JPanel line = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 3));
            line.setBackground(Color.WHITE);
            JLabel dot = new JLabel("◆");
            dot.setFont(UIUtil.SMALL_FONT);
            dot.setForeground(UIUtil.ACCENT);
            JLabel fl = new JLabel(feat);
            fl.setFont(UIUtil.SMALL_FONT);
            fl.setForeground(UIUtil.TEXT_GRAY);
            line.add(dot);
            line.add(fl);
            card.add(line);
        }

        add(title, BorderLayout.NORTH);
        add(card, BorderLayout.CENTER);
    }
}
