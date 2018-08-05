package client.Team;

import Game.*;

import java.util.*;

class CheesePicker {
    private static final CheesePicker cheesePicker = new CheesePicker();
    private Game game;
    private ArrayList<Cell> cheese;
    private int[][] cgraph;
    private int INF = Integer.MAX_VALUE;
    private Map<Cell, Map<Cell, Integer>> graph;
    private CheesePicker() {
        this.cheese = new ArrayList<>();
    }
    void setGraph(Map<Cell, Map<Cell, Integer>> graph) {
        this.cgraph = new int[cheese.size()][cheese.size()];
        this.graph = graph;
        for(int i=0; i<cheese.size(); i++) {
            for(int j=0; j<cheese.size(); j++) {
                cgraph[i][j] = INF;
            }
        }
        for(int i=0; i<cheese.size(); i++) {
            pathWeight(cheese.get(i), i);
        }
    }
    static CheesePicker getCheesePicker(Game game) {
        return cheesePicker.init(game);
    }
    private CheesePicker init(Game game) {
        this.game = game;
        for(int i=0; i<game.getNumberOfRows(); i++) {
            for(int j=0; j<game.getNumberOfColumns(); j++) {
                if(game.getMap()[i][j].hasCheese()) {
                    cheese.add(game.getMap()[i][j]);
                }
            }
        }
        return this;
    }
//    public List<Cell> findBestPath() {
//
//
//    }
//    private int dfs(Cell cell, int ind)
    private void pathWeight(Cell cell, int index) {
        Cell[][] anc = new Cell[game.getNumberOfRows()][game.getNumberOfColumns()];
        int[][] weight = new int[game.getNumberOfRows()][game.getNumberOfColumns()];
        for(int i=0; i<game.getNumberOfRows(); i++) {
            for(int j=0; j<game.getNumberOfColumns(); j++) {
                weight[i][j] = INF;
            }
        }
        boolean[][] visit = new boolean[game.getNumberOfRows()][game.getNumberOfColumns()];
        Queue<Cell> q = new LinkedList<Cell>();
        visit[cell.getRow()][cell.getCol()] = true;
        weight[cell.getRow()][cell.getCol()] = 0;
        q.add(cell);
        Cell node;
        while(!q.isEmpty()) {
            node = q.poll();
            assert node != null;
            if(node.hasCheese()) {
                int nodeIndex = this.cheese.indexOf(node);
                this.cgraph[index][nodeIndex] = weight[node.getRow()][node.getCol()];
            }
            for(Cell tmp : graph.get(node).keySet()) {
                if(!visit[tmp.getRow()][tmp.getCol()] && tmp.getRatInside() == null) {
                    visit[tmp.getRow()][tmp.getCol()] = true;
                    q.add(tmp);
                    anc[tmp.getRow()][tmp.getCol()] = node;
                    weight[tmp.getRow()][tmp.getCol()] = weight[node.getRow()][node.getCol()] + graph.get(node).get(tmp);
                }
            }
        }
    }
}
