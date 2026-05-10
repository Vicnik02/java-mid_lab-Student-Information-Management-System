package ui;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.HashMap;
import java.util.Map;

/**
 * Swing UI 工具类 — 校园科技风配色、字体、组件工厂
 */
public class UIUtil {

    // ==================== 配色方案 ====================
    // 主色调 — 沉稳校徽蓝
    public static final Color PRIMARY        = new Color(0x1A4B8C);
    public static final Color PRIMARY_LIGHT  = new Color(0x2B6CB0);
    public static final Color PRIMARY_PALE   = new Color(0xE8F0FE);
    public static final Color PRIMARY_DARK   = new Color(0x0F3460);

    // 强调色 — 科技蓝
    public static final Color ACCENT         = new Color(0x4A90E2);
    public static final Color ACCENT_LIGHT   = new Color(0xD6E9FB);
    public static final Color ACCENT_DARK    = new Color(0x3570B0);

    // 状态色 — 柔和
    public static final Color SUCCESS        = new Color(0x4CAF50);
    public static final Color SUCCESS_LIGHT  = new Color(0xE8F5E9);
    public static final Color WARNING        = new Color(0xFF9800);
    public static final Color WARNING_LIGHT  = new Color(0xFFF3E0);
    public static final Color ERROR          = new Color(0xE57373);
    public static final Color ERROR_LIGHT    = new Color(0xFFEBEE);

    // 背景色系
    public static final Color BG             = new Color(0xF0F4F8); // 校园浅灰
    public static final Color CARD_BG        = Color.WHITE;
    public static final Color TEXT_MAIN      = new Color(0x1A1A2E);
    public static final Color TEXT_GRAY      = new Color(0x6B7C93);
    public static final Color TEXT_LIGHT     = new Color(0x95A5B5);

    // 侧边栏
    public static final Color SIDEBAR_BG     = new Color(0x0F3460); // 深校徽蓝
    public static final Color SIDEBAR_HOVER  = new Color(0x1A4B8C);
    public static final Color SIDEBAR_ACTIVE = new Color(0x1A4B8C);
    public static final Color SIDEBAR_HIGHLIGHT_BAR = new Color(0x4A90E2); // 左侧高亮条

    // 表格
    public static final Color TABLE_HEADER_BG  = new Color(0x1A4B8C);
    public static final Color TABLE_ROW_ODD    = Color.WHITE;
    public static final Color TABLE_ROW_EVEN   = new Color(0xF0F6FC);
    public static final Color TABLE_ROW_HOVER  = new Color(0xE3F0FD);
    public static final Color GRID_COLOR       = new Color(0xE5E9F0);

    // 边框/分隔
    public static final Color BORDER_LIGHT     = new Color(0xDDE3EA);
    public static final Color DIVIDER_BLUE     = new Color(0x1A4B8C);

    // 排名前三
    public static final Color TOP1_BG = new Color(0xFFF8E1);
    public static final Color TOP1_FG = new Color(0xE65100);
    public static final Color TOP2_BG = new Color(0xF5F7FA);
    public static final Color TOP2_FG = new Color(0x607D8B);
    public static final Color TOP3_BG = new Color(0xFFF0E8);
    public static final Color TOP3_FG = new Color(0x8D6E63);

    // ==================== 字体 ====================
    public static final Font TITLE_FONT    = new Font("Microsoft YaHei", Font.BOLD, 24);
    public static final Font HEADER_FONT   = new Font("Microsoft YaHei", Font.BOLD, 18);
    public static final Font SUBTITLE_FONT = new Font("Microsoft YaHei", Font.BOLD, 15);
    public static final Font BODY_FONT     = new Font("Microsoft YaHei", Font.PLAIN, 14);
    public static final Font BODY_BOLD     = new Font("Microsoft YaHei", Font.BOLD, 14);
    public static final Font SMALL_FONT    = new Font("Microsoft YaHei", Font.PLAIN, 12);
    public static final Font MONO_FONT     = new Font("Consolas", Font.PLAIN, 13);
    public static final Font SCORE_FONT    = new Font("Microsoft YaHei", Font.BOLD, 15);
    public static final Font STAT_NUM_FONT = new Font("Microsoft YaHei", Font.BOLD, 30);

    // ==================== 阴影边框 ====================
    public static class ShadowBorder extends AbstractBorder {
        private final int radius;
        private final Color shadowColor;
        private final Color lineColor;

        public ShadowBorder(int radius, Color shadowColor, Color lineColor) {
            this.radius = radius;
            this.shadowColor = shadowColor;
            this.lineColor = lineColor;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int arc = radius * 2;
            // 阴影层
            for (int i = 2; i >= 0; i--) {
                g2.setColor(new Color(shadowColor.getRed(), shadowColor.getGreen(),
                    shadowColor.getBlue(), 12 - i * 4));
                g2.drawRoundRect(x + i, y + i + 1, width - 1 - i * 2, height - 1 - i * 2, arc, arc);
            }
            // 边框线
            g2.setColor(lineColor);
            g2.drawRoundRect(x, y, width - 4, height - 4, arc, arc);
            g2.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(4, 4, 7, 7);
        }
    }

    public static final Border CARD_SHADOW = new ShadowBorder(6,
        new Color(0x8A9BB5), BORDER_LIGHT);

    // ==================== 组件工厂 ====================

    /** 圆角渐变按钮 — 基础 */
    private static JButton createGradientButton(String text, Color c1, Color c2,
                                                 Color hoverC1, Color hoverC2,
                                                 Color fg, Font font) {
        JButton btn = new JButton(text) {
            private Color currentC1 = c1;
            private Color currentC2 = c2;

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth();
                int h = getHeight();
                GradientPaint gp = new GradientPaint(0, 0, currentC1, w, h, currentC2);
                g2.setPaint(gp);
                g2.fillRoundRect(1, 1, w - 3, h - 3, 12, 12);
                // 细微高光
                g2.setColor(new Color(255, 255, 255, 30));
                g2.fillRoundRect(1, 1, w - 3, h / 2, 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }

            @Override
            protected void paintBorder(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0, 0, 0, 40));
                g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 12, 12);
                g2.dispose();
            }
        };
        btn.setFont(font);
        btn.setForeground(fg);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(10, 22, 10, 22));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btn.putClientProperty("c1", hoverC1);
                btn.putClientProperty("c2", hoverC2);
                // Dynamic access via overriding isn't easy here; we'll use repaint
                btn.setBackground(hoverC1); // triggers visual change
                btn.repaint();
            }
            public void mouseExited(MouseEvent e) {
                btn.setBackground(c1);
                btn.repaint();
            }
        });

        // Store colors for paintComponent access
        btn.putClientProperty("baseC1", c1);
        btn.putClientProperty("baseC2", c2);
        btn.putClientProperty("hoverC1", hoverC1);
        btn.putClientProperty("hoverC2", hoverC2);

        return btn;
    }

    /** 主色调按钮 — 校徽蓝渐变 */
    public static JButton primaryButton(String text) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                boolean hovered = Boolean.TRUE.equals(getClientProperty("hovered"));
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth(), h = getHeight();
                Color c1 = hovered ? PRIMARY_LIGHT : PRIMARY;
                Color c2 = hovered ? new Color(0x3A7CC8) : new Color(0x2A5A9A);
                GradientPaint gp = new GradientPaint(0, 0, c1, w, h, c2);
                g2.setPaint(gp);
                g2.fillRoundRect(1, 1, w - 3, h - 3, 10, 10);
                g2.setColor(new Color(255, 255, 255, 25));
                g2.fillRoundRect(1, 1, w - 3, h / 2, 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }

            @Override
            protected void paintBorder(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0, 0, 0, 50));
                g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 10, 10);
                g2.dispose();
            }
        };
        btn.setText(text);
        btn.setFont(new Font("Microsoft YaHei", Font.BOLD, 14));
        btn.setForeground(Color.WHITE);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(9, 22, 9, 22));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                ((JButton) e.getSource()).putClientProperty("hovered", true);
                e.getComponent().repaint();
            }
            public void mouseExited(MouseEvent e) {
                ((JButton) e.getSource()).putClientProperty("hovered", false);
                e.getComponent().repaint();
            }
        });
        return btn;
    }

    /** 强调色按钮 — 科技蓝 */
    public static JButton accentButton(String text) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                boolean hovered = Boolean.TRUE.equals(getClientProperty("hovered"));
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth(), h = getHeight();
                Color c1 = hovered ? new Color(0x5BA0F0) : ACCENT;
                Color c2 = hovered ? new Color(0x4A90E2) : ACCENT_DARK;
                GradientPaint gp = new GradientPaint(0, 0, c1, w, h, c2);
                g2.setPaint(gp);
                g2.fillRoundRect(1, 1, w - 3, h - 3, 10, 10);
                g2.setColor(new Color(255, 255, 255, 30));
                g2.fillRoundRect(1, 1, w - 3, h / 2, 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }

            @Override
            protected void paintBorder(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0, 0, 0, 40));
                g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 10, 10);
                g2.dispose();
            }
        };
        btn.setText(text);
        btn.setFont(new Font("Microsoft YaHei", Font.BOLD, 14));
        btn.setForeground(Color.WHITE);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(9, 22, 9, 22));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                ((JButton) e.getSource()).putClientProperty("hovered", true);
                e.getComponent().repaint();
            }
            public void mouseExited(MouseEvent e) {
                ((JButton) e.getSource()).putClientProperty("hovered", false);
                e.getComponent().repaint();
            }
        });
        return btn;
    }

    /** 普通按钮 */
    public static JButton normalButton(String text) {
        JButton btn = new JButton(text) {
            private boolean hovered = false;

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(hovered ? new Color(0xF0F4F8) : Color.WHITE);
                g2.fillRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }

            @Override
            protected void paintBorder(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BORDER_LIGHT);
                g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 8, 8);
                g2.dispose();
            }
        };
        btn.setText(text);
        btn.setFont(new Font("Microsoft YaHei", Font.PLAIN, 14));
        btn.setForeground(TEXT_MAIN);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(8, 20, 8, 20));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                ((JButton) e.getSource()).putClientProperty("hovered", true);
                e.getComponent().repaint();
            }
            public void mouseExited(MouseEvent e) {
                ((JButton) e.getSource()).putClientProperty("hovered", false);
                e.getComponent().repaint();
            }
        });
        return btn;
    }

    /** 危险按钮 — 柔和红色 */
    public static JButton dangerButton(String text) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                boolean hovered = Boolean.TRUE.equals(getClientProperty("hovered"));
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth(), h = getHeight();
                Color c1 = hovered ? new Color(0xEF7A7A) : UIUtil.ERROR;
                Color c2 = hovered ? new Color(0xD55A5A) : new Color(0xC55050);
                GradientPaint gp = new GradientPaint(0, 0, c1, w, h, c2);
                g2.setPaint(gp);
                g2.fillRoundRect(1, 1, w - 3, h - 3, 10, 10);
                g2.setColor(new Color(255, 255, 255, 25));
                g2.fillRoundRect(1, 1, w - 3, h / 2, 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }

            @Override
            protected void paintBorder(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0, 0, 0, 40));
                g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 10, 10);
                g2.dispose();
            }
        };
        btn.setText(text);
        btn.setFont(new Font("Microsoft YaHei", Font.BOLD, 14));
        btn.setForeground(Color.WHITE);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(9, 22, 9, 22));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                ((JButton) e.getSource()).putClientProperty("hovered", true);
                e.getComponent().repaint();
            }
            public void mouseExited(MouseEvent e) {
                ((JButton) e.getSource()).putClientProperty("hovered", false);
                e.getComponent().repaint();
            }
        });
        return btn;
    }

    /** 标签页风格的筛选按钮 */
    public static JButton tabButton(String text) {
        JButton btn = new JButton(text) {
            private boolean hovered = false;
            private boolean selected = false;

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth(), h = getHeight();
                if (selected) {
                    // 选中态：主色填充
                    GradientPaint gp = new GradientPaint(0, 0, PRIMARY, w, h, PRIMARY_LIGHT);
                    g2.setPaint(gp);
                    g2.fillRoundRect(1, 1, w - 2, h - 2, 6, 6);
                    setForeground(Color.WHITE);
                } else if (hovered) {
                    // 悬停：淡蓝填充
                    g2.setColor(ACCENT_LIGHT);
                    g2.fillRoundRect(1, 1, w - 2, h - 2, 6, 6);
                    setForeground(PRIMARY);
                } else {
                    // 默认：白底灰边
                    g2.setColor(Color.WHITE);
                    g2.fillRoundRect(1, 1, w - 2, h - 2, 6, 6);
                    setForeground(TEXT_GRAY);
                }
                g2.dispose();
                super.paintComponent(g);
            }

            @Override
            protected void paintBorder(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (selected) {
                    g2.setColor(new Color(0, 0, 0, 40));
                } else {
                    g2.setColor(BORDER_LIGHT);
                }
                g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 6, 6);
                g2.dispose();
            }
        };
        btn.setFont(new Font("Microsoft YaHei", Font.BOLD, 13));
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(7, 18, 7, 18));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                JButton b = (JButton) e.getSource();
                b.putClientProperty("hovered", true);
                b.repaint();
            }
            public void mouseExited(MouseEvent e) {
                JButton b = (JButton) e.getSource();
                b.putClientProperty("hovered", false);
                b.repaint();
            }
        });
        return btn;
    }

    /** 设置标签按钮选中状态 */
    public static void setTabSelected(JButton btn, boolean selected) {
        btn.putClientProperty("selected", selected);
        btn.repaint();
    }

    /** 页面标题 — 加粗无衬线 */
    public static JLabel pageTitle(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(HEADER_FONT);
        lbl.setForeground(PRIMARY);
        lbl.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 2, 0, DIVIDER_BLUE),
            BorderFactory.createEmptyBorder(0, 0, 8, 0)));
        return lbl;
    }

    /** 带阴影的白色卡片面板 */
    public static JPanel card() {
        JPanel panel = new JPanel();
        panel.setBackground(CARD_BG);
        panel.setBorder(BorderFactory.createCompoundBorder(
            CARD_SHADOW,
            new EmptyBorder(20, 24, 20, 24)));
        return panel;
    }

    /** 带标题的阴影卡片 */
    public static JPanel cardWithTitle(String title) {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setBackground(CARD_BG);
        panel.setBorder(BorderFactory.createCompoundBorder(
            CARD_SHADOW,
            new EmptyBorder(20, 24, 20, 24)));
        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(SUBTITLE_FONT);
        titleLbl.setForeground(PRIMARY);
        panel.add(titleLbl, BorderLayout.NORTH);
        return panel;
    }

    /** 美化 JTable — 表头半透明主色调、隔行浅蓝 */
    public static JTable styledTable(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setFont(BODY_FONT);
        table.setRowHeight(36);
        table.setShowVerticalLines(false);
        table.setShowHorizontalLines(false);
        table.setGridColor(GRID_COLOR);
        table.setSelectionBackground(ACCENT_LIGHT);
        table.setSelectionForeground(TEXT_MAIN);
        table.setIntercellSpacing(new Dimension(0, 0));

        // 表头样式
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Microsoft YaHei", Font.BOLD, 13));
        header.setForeground(Color.WHITE);
        header.setBackground(TABLE_HEADER_BG);
        header.setBorder(BorderFactory.createEmptyBorder());
        header.setPreferredSize(new Dimension(0, 38));
        header.setReorderingAllowed(false);

        // 表头渲染器 — 圆角顶部
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value,
                    boolean isSelected, boolean hasFocus, int row, int col) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(
                    t, value, isSelected, hasFocus, row, col);
                lbl.setBackground(TABLE_HEADER_BG);
                lbl.setForeground(Color.WHITE);
                lbl.setFont(new Font("Microsoft YaHei", Font.BOLD, 13));
                lbl.setHorizontalAlignment(SwingConstants.CENTER);
                lbl.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
                return lbl;
            }
        });

        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        return table;
    }

    /** 表格交替行着色 + 悬停高亮 */
    public static void alternateRowColors(JTable table) {
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value,
                    boolean isSelected, boolean hasFocus, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, col);
                if (!isSelected) {
                    Integer hoverRow = (Integer) t.getClientProperty("hoverRow");
                    if (hoverRow != null && hoverRow == row) {
                        c.setBackground(TABLE_ROW_HOVER);
                    } else {
                        c.setBackground(row % 2 == 0 ? TABLE_ROW_ODD : TABLE_ROW_EVEN);
                    }
                }
                setFont(BODY_FONT);
                setBorder(new EmptyBorder(2, 10, 2, 10));
                return c;
            }
        });
    }

    /** 表格悬停追踪 */
    public static void addTableHoverEffect(JTable table) {
        table.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                Integer prev = (Integer) table.getClientProperty("hoverRow");
                if (prev == null || prev != row) {
                    table.putClientProperty("hoverRow", row);
                    table.repaint();
                }
            }
        });
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                table.putClientProperty("hoverRow", -1);
                table.repaint();
            }
        });
    }

    /** 统计卡片 — 左侧色条 + 数字，简洁风格 */
    public static JPanel statCard(String title, String value, Color accentColor, Color textColor) {
        JPanel card = new JPanel(new BorderLayout(12, 0));
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
            new ShadowBorder(8, new Color(0x8A9BB5), BORDER_LIGHT),
            new EmptyBorder(18, 20, 18, 20)));

        // 左侧色条
        JPanel bar = new JPanel();
        bar.setBackground(accentColor);
        bar.setPreferredSize(new Dimension(4, 40));

        // 文字区域
        JPanel textPanel = new JPanel(new GridLayout(2, 1, 0, 2));
        textPanel.setBackground(CARD_BG);
        JLabel valLbl = new JLabel(value);
        valLbl.setFont(STAT_NUM_FONT);
        valLbl.setForeground(textColor);
        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(SMALL_FONT);
        titleLbl.setForeground(TEXT_GRAY);
        textPanel.add(valLbl);
        textPanel.add(titleLbl);

        card.add(bar, BorderLayout.WEST);
        card.add(textPanel, BorderLayout.CENTER);
        return card;
    }

    /** 信息标签（键值对） */
    public static JPanel infoRow(String label, String value) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        row.setBackground(CARD_BG);
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Microsoft YaHei", Font.BOLD, 14));
        lbl.setForeground(TEXT_GRAY);
        JLabel val = new JLabel(value);
        val.setFont(BODY_FONT);
        val.setForeground(TEXT_MAIN);
        row.add(lbl);
        row.add(val);
        return row;
    }
}
