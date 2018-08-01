package Debuger;

import Game.*;

import java.awt.Color;
import java.util.List;
import java.util.Set;

/**
 * Created by armanaxh on 8/1/18.
 */
public class Debuger {

    private View view;
    private Game game;

    public Debuger(Game game) {
        this.game = game;
        view = new View(game.getNumberOfRows(), game.getNumberOfColumns());
        view.setItems(game);
        view.setTurnNum("0 / " + game.getTurnNumber());
        view.setScores(0, 0);
        view.setScores(1, 0);
        view.setVisible(true);
    }


    public void update(){
        view.setItems(this.game);
    }

    public void addColor(Cell cell,Color color){
        view.setItem(cell, color);
    }

    public void addColor(List<Cell> cells, Color color){
        view.setItem(cells, color);
    }

    public void addColor(Set<Cell> cells, Color color){
        view.setItem(cells, color);
    }

    public void clear(){
        view.clear(game);
    }

}
