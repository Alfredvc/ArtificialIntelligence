package com.alfredvc.module4;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.*;

public class Game2048 extends JPanel {
    private static final Color BG_COLOR = new Color(0xbbada0);
    private static final String FONT_NAME = "Arial";
    private static final int TILE_SIZE = 64;
    private static final int TILES_MARGIN = 16;


    private final Logic2048 logic2048;

    private long board;
    private boolean lost = false;
    private int score = 0;

    public Game2048() {
        this.logic2048 = new Logic2048(null);
        setFocusable(true);
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    resetGame();
                }

                if (!lost) {
                    switch (e.getKeyCode()) {
                        case KeyEvent.VK_LEFT:
                            left();
                            break;
                        case KeyEvent.VK_RIGHT:
                            right();
                            break;
                        case KeyEvent.VK_DOWN:
                            down();
                            break;
                        case KeyEvent.VK_UP:
                            up();
                            break;
                    }
                }
                repaint();
            }
        });
        resetGame();
    }

    private long getNextBoard(long nextBoard) {
        if (nextBoard == board) {
            if (Logic2048.getEmptyCountInBoard(board) == 0) lost = true;
            return board;
        } else {
            return logic2048.generateRandomTwoOrFour(nextBoard);
        }
    }

    private void up() {
        board = getNextBoard(logic2048.moveUp(board));
    }


    private void down() {
        board = getNextBoard(logic2048.moveDown(board));
    }

    private void right() {
        board = getNextBoard(logic2048.moveRight(board));
    }

    private void left() {
        board = getNextBoard(logic2048.moveLeft(board));
    }

    public void resetGame() {
        score = 0;
        lost = false;
        board = logic2048.generateRandomTwoOrFour(0L);
    }
    @Override
    public void paint(Graphics g) {
        super.paint(g);
        g.setColor(BG_COLOR);
        g.fillRect(0, 0, this.getSize().width, this.getSize().height);
        for (int y = 0; y < 4; y++) {
            for (int x = 0; x < 4; x++) {
                drawTile(g, x, y, getValueForIndex(board, x + y * 4));
            }
        }
    }

    private void drawTile(Graphics g2, int x, int y, int value) {
        Graphics2D g = ((Graphics2D) g2);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);
        int xOffset = offsetCoors(x);
        int yOffset = offsetCoors(y);
        g.setColor(getColorForBackground(value));
        g.fillRoundRect(xOffset, yOffset, TILE_SIZE, TILE_SIZE, 14, 14);
        g.setColor(getColorForForeground(value));
        final int size = value < 100 ? 36 : value < 1000 ? 32 : 24;
        final Font font = new Font(FONT_NAME, Font.BOLD, size);
        g.setFont(font);

        String s = String.valueOf(value);
        final FontMetrics fm = getFontMetrics(font);

        final int w = fm.stringWidth(s);
        final int h = -(int) fm.getLineMetrics(s, g).getBaselineOffsets()[2];

        if (value != 0)
            g.drawString(s, xOffset + (TILE_SIZE - w) / 2, yOffset + TILE_SIZE - (TILE_SIZE - h) / 2 - 2);


        if (lost) {
            g.setColor(new Color(255, 255, 255, 30));
            g.fillRect(0, 0, getWidth(), getHeight());
            g.setColor(new Color(78, 139, 202));
            g.setFont(new Font(FONT_NAME, Font.BOLD, 48));
            if (lost) {
                g.drawString("Game over!", 50, 130);
                g.drawString("You lose!", 64, 200);
            }
            if (lost) {
                g.setFont(new Font(FONT_NAME, Font.PLAIN, 16));
                g.setColor(new Color(128, 128, 128, 128));
                g.drawString("Press ESC to play again", 80, getHeight() - 40);
            }
        }
        g.setFont(new Font(FONT_NAME, Font.PLAIN, 18));
        g.drawString("Score: " + score, 200, 365);

    }

    private int getValueForIndex(long board, int index) {
        char c = (char) (((board & (0xfl << 60 - index*4)) >> 60 - index*4) & 0xf);
        return c == 0 ? 0 : 1 << c;
    }

    private static int offsetCoors(int arg) {
        return arg * (TILES_MARGIN + TILE_SIZE) + TILES_MARGIN;
    }

    public Color getColorForForeground(int value) {
        return value < 16 ? new Color(0x776e65) :  new Color(0xf9f6f2);
    }

    private static Color getColorForBackground(int value) {

        switch (value) {
            case 0:    return new Color(0x776e65);
            case 2:    return new Color(0xeee4da);
            case 4:    return new Color(0xede0c8);
            case 8:    return new Color(0xf2b179);
            case 16:   return new Color(0xf59563);
            case 32:   return new Color(0xf67c5f);
            case 64:   return new Color(0xf65e3b);
            case 128:  return new Color(0xedcf72);
            case 256:  return new Color(0xedcc61);
            case 512:  return new Color(0xedc850);
            case 1024: return new Color(0xedc53f);
            case 2048: return new Color(0xedc22e);
            case 4096: return new Color(0xedc22e);
            case 8192: return new Color(0xedc22e);
            case 16384: return new Color(0xedc22e);
            case 32768: return new Color(0xedc22e);
        }
        return null;
    }


    public static void main(String[] args) {
        JFrame game = new JFrame();
        game.setTitle("2048 Game");
        game.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        game.setSize(340, 400);
        game.setResizable(false);

        game.add(new Game2048());

        game.setLocationRelativeTo(null);
        game.setVisible(true);
    }
}