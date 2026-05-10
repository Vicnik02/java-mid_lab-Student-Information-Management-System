package ui;

import entity.*;
import service.*;
import java.awt.*;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

/**
 * 主窗口 — 校园风侧边栏导航 + 卡片式内容区
 */
public class MainFrame extends JFrame {

    private final StudentService svc;
    private final CardLayout cardLayout;
    private final JPanel contentPanel;
    private final JPanel sidebarPanel;
    private JButton activeNavBtn;

    // 功能面板
    private StudentPanel studentPanel;
    private SubjectPanel subjectPanel;
    private ScorePanel   scorePanel;
    private QueryPanel   queryPanel;
    private RankPanel    rankPanel;
    private StatPanel    statPanel;
    private AboutPanel   aboutPanel;

    public MainFrame(StudentService svc) {
        this.svc = svc;
        setTitle("学生信息管理系统 v1.0");
        setSize(1120, 740);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // === 整体布局 ===
        setLayout(new BorderLayout());

        // === 顶部标题栏 ===
        add(createTitleBar(), BorderLayout.NORTH);

        // === 左侧导航栏 ===
        sidebarPanel = createSidebar();
        add(sidebarPanel, BorderLayout.WEST);

        // === 中央内容区 ===
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(UIUtil.BG);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        // 初始化所有面板
        studentPanel = new StudentPanel(svc, this);
        subjectPanel = new SubjectPanel(svc);
        scorePanel   = new ScorePanel(svc);
        queryPanel   = new QueryPanel(svc);
        rankPanel    = new RankPanel(svc);
        statPanel    = new StatPanel(svc);
        aboutPanel   = new AboutPanel();

        contentPanel.add(studentPanel, "student");
        contentPanel.add(subjectPanel, "subject");
        contentPanel.add(scorePanel,   "score");
        contentPanel.add(queryPanel,   "query");
        contentPanel.add(rankPanel,    "rank");
        contentPanel.add(statPanel,    "stat");
        contentPanel.add(aboutPanel,   "about");

        add(contentPanel, BorderLayout.CENTER);

        // 默认显示学生管理
        switchTo("student");
    }

    /** 按比例缩放校徽图片 */
    private static ImageIcon loadLogo(int maxWidth, int maxHeight) {
        try {
            File f = new File("9a3721239fe84a5fb9f79411a4b2d6b9.jpg");
            if (!f.exists()) return null;
            BufferedImage img = ImageIO.read(f);
            int w = img.getWidth();
            int h = img.getHeight();
            double ratio = Math.min((double) maxWidth / w, (double) maxHeight / h);
            Image scaled = img.getScaledInstance((int)(w * ratio), (int)(h * ratio), Image.SCALE_SMOOTH);
            return new ImageIcon(scaled);
        } catch (Exception e) {
            return null;
        }
    }

    // ==================== 顶部标题栏 ====================

    private JPanel createTitleBar() {
        JPanel outer = new JPanel(new BorderLayout());
        outer.setBackground(UIUtil.BG);

        JPanel bar = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, new Color(0xF5F8FC),
                    getWidth(), 0, new Color(0xE8EDF5));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        bar.setPreferredSize(new Dimension(0, 64));

        // 左侧：校徽 + 标题
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 0));
        leftPanel.setOpaque(false);

        ImageIcon logo = loadLogo(52, 52);
        if (logo != null) {
            JLabel logoLbl = new JLabel(logo);
            leftPanel.add(logoLbl);
        }

        JLabel titleLbl = new JLabel("学生信息管理系统");
        titleLbl.setFont(new Font("Microsoft YaHei", Font.BOLD, 22));
        titleLbl.setForeground(UIUtil.PRIMARY);
        leftPanel.add(titleLbl);
        bar.add(leftPanel, BorderLayout.WEST);

        // 右侧：版本信息
        JLabel verLbl = new JLabel("v1.0  |  Java Swing  ");
        verLbl.setFont(UIUtil.SMALL_FONT);
        verLbl.setForeground(UIUtil.TEXT_LIGHT);
        bar.add(verLbl, BorderLayout.EAST);

        outer.add(bar, BorderLayout.CENTER);
        JPanel divider = new JPanel();
        divider.setBackground(UIUtil.DIVIDER_BLUE);
        divider.setPreferredSize(new Dimension(0, 2));
        outer.add(divider, BorderLayout.SOUTH);

        return outer;
    }

    // ==================== 侧边栏 ====================

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(UIUtil.SIDEBAR_BG);
        sidebar.setPreferredSize(new Dimension(170, 0));
        sidebar.setBorder(BorderFactory.createEmptyBorder(20, 0, 12, 0));

        // 侧边栏标题
        JPanel logoArea = new JPanel(new FlowLayout(FlowLayout.CENTER));
        logoArea.setBackground(UIUtil.SIDEBAR_BG);
        logoArea.setMaximumSize(new Dimension(170, 36));
        JLabel logoText = new JLabel("导航菜单");
        logoText.setFont(new Font("Microsoft YaHei", Font.BOLD, 13));
        logoText.setForeground(new Color(0xB0C4DE));
        logoArea.add(logoText);
        sidebar.add(logoArea);
        sidebar.add(Box.createVerticalStrut(4));

        // 分隔线
        JPanel sep = new JPanel();
        sep.setBackground(new Color(0x2A5080));
        sep.setMaximumSize(new Dimension(150, 1));
        sidebar.add(sep);
        sidebar.add(Box.createVerticalStrut(8));

        String[] navItems = {
            "学生管理",
            "学科管理",
            "成绩管理",
            "查询功能",
            "排名功能",
            "统计信息",
            "关于系统",
        };

        for (String label : navItems) {
            JButton btn = createNavButton(label);
            sidebar.add(btn);
            sidebar.add(Box.createVerticalStrut(1));
        }

        return sidebar;
    }

    private JButton createNavButton(String label) {
        JButton btn = new JButton("   " + label) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth(), h = getHeight();

                Boolean isActive = (Boolean) getClientProperty("navActive");
                Boolean isHover = (Boolean) getClientProperty("navHover");

                if (Boolean.TRUE.equals(isActive)) {
                    g2.setColor(UIUtil.SIDEBAR_ACTIVE);
                    g2.fillRect(0, 0, w, h);
                    g2.setColor(UIUtil.SIDEBAR_HIGHLIGHT_BAR);
                    g2.fillRect(0, 0, 4, h);
                } else if (Boolean.TRUE.equals(isHover)) {
                    g2.setColor(new Color(0x4A90E2, true));
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.15f));
                    g2.fillRect(0, 0, w, h);
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };

        btn.setFont(new Font("Microsoft YaHei", Font.PLAIN, 15));
        btn.setForeground(new Color(0xB0C4DE));
        btn.setBackground(UIUtil.SIDEBAR_BG);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setBorder(BorderFactory.createEmptyBorder(12, 18, 12, 16));
        btn.setMaximumSize(new Dimension(170, 46));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                if (!Boolean.TRUE.equals(btn.getClientProperty("navActive"))) {
                    btn.putClientProperty("navHover", true);
                    btn.setForeground(Color.WHITE);
                    btn.repaint();
                }
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.putClientProperty("navHover", false);
                if (!Boolean.TRUE.equals(btn.getClientProperty("navActive"))) {
                    btn.setForeground(new Color(0xB0C4DE));
                }
                btn.repaint();
            }
        });

        btn.addActionListener(e -> switchTo(getNavKey(label)));
        return btn;
    }

    private String getNavKey(String label) {
        return switch (label) {
            case "学生管理" -> "student";
            case "学科管理" -> "subject";
            case "成绩管理" -> "score";
            case "查询功能" -> "query";
            case "排名功能" -> "rank";
            case "统计信息" -> "stat";
            default -> "about";
        };
    }

    /** 切换内容面板 */
    public void switchTo(String key) {
        cardLayout.show(contentPanel, key);
        for (java.awt.Component comp : sidebarPanel.getComponents()) {
            if (comp instanceof JButton btn) {
                String btnText = btn.getText().trim();
                boolean active = getNavKey(btnText).equals(key);
                btn.putClientProperty("navActive", active);
                btn.putClientProperty("navHover", false);
                if (active) {
                    btn.setForeground(Color.WHITE);
                    btn.setFont(new Font("Microsoft YaHei", Font.BOLD, 15));
                    activeNavBtn = btn;
                } else {
                    btn.setForeground(new Color(0xB0C4DE));
                    btn.setFont(new Font("Microsoft YaHei", Font.PLAIN, 15));
                }
                btn.repaint();
            }
        }
        switch (key) {
            case "student" -> studentPanel.refresh();
            case "subject" -> subjectPanel.refresh();
            case "score"   -> scorePanel.refresh();
            case "query"   -> queryPanel.refresh();
            case "rank"    -> rankPanel.refresh();
            case "stat"    -> statPanel.refresh();
        }
    }
}
