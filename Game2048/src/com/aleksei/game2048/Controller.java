package com.aleksei.game2048;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import static java.awt.event.KeyEvent.*;

public class Controller extends KeyAdapter {
    private static final int WINNING_TILE = 2048;
    private Model model;
    private View view;

    public Tile[][] getGameTiles() {
        return model.getGameTiles();
    }

    public int getScore() {
        return model.score;
    }

    public Controller(Model model) {
        this.model = model;
        view = new View(this);
    }

    public void resetGame() {
        model.resetGameTiles();
        model.score = 0;
        view.isGameLost = false;
        view.isGameWon = false;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) resetGame();
        if (!model.canMove()) view.isGameLost = true;
        if (!view.isGameLost && !view.isGameWon) {
            switch (e.getKeyCode()) {
                case VK_LEFT:
                    model.left();
                    break;
                case VK_RIGHT:
                    model.right();
                    break;
                case VK_UP:
                    model.up();
                    break;
                case VK_DOWN:
                    model.down();
                    break;
                case VK_Z:
                    model.rollback();
                    break;
                case VK_R:
                    model.randomMove();
                    break;
                case VK_A:
                    model.autoMove();
                    break;
            }
            if (model.maxTile == WINNING_TILE) view.isGameWon = true;
        }
        view.repaint();
    }

    public View getView() {
        return view;
    }
}
