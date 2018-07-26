package client.Team;

import Game.*;

import java.util.*;

class PathFinder {
    private static final PathFinder pathfinder= new PathFinder();
    private int[][] delta = {{0, 1}, {-1, 0}, {0, -1}, {1, 0}};
    private Game game;
    private Map<Cell, Set<Cell>> graph;
    private int oppscore;
    private int myscore;
    private boolean[][] poison = new boolean[50][50];
    private PathFinder() {
        this.graph = new HashMap<Cell, Set<Cell>>();
    }

    void setGame(Game game) {
        this.game = game;
        init(game);
    }

    static PathFinder getPathFinder() {
        return pathfinder;
    }
    private void init(Game game) {
        Cell[][] map = game.getMap();

        for(int i=0; i<game.getNumberOfRows(); i++) {
            for(int j=0; j<game.getNumberOfColumns(); j++) {
                Cell tmp = map[i][j];
                this.graph.put(tmp, new HashSet<>());
                for(int k=0; k<4; k++) {
                    int x = i + delta[k][0];
                    int y = j + delta[k][1];
                    if(x >= 0
                            && y >= 0
                            && x < game.getNumberOfRows()
                            && y < game.getNumberOfColumns()
                            && ((tmp.hasLadder() || !map[x][y].hasWall()) || (tmp.hasWall()))
                            ) {
                        this.graph.get(tmp).add(map[x][y]);
                    }
                }
            }
        }
    }

    boolean[][] getPoison() {
        return poison;
    }

    void poisonWatcher() {

        if(game.getOppScore() < this.oppscore) {
            poison[game.getOppRat().getCell().getRow()][game.getOppRat().getCell().getCol()] = true;
        }
        if(game.getMyScore() < this.myscore) {
            poison[game.getMyRat().getCell().getRow()][game.getMyRat().getCell().getCol()] = true;
        }
        this.oppscore = game.getOppScore();
        this.myscore = game.getMyScore();
    }
    private Cell bfsCheese() {
        Cell[][] anc = new Cell[game.getNumberOfRows()][game.getNumberOfColumns()];
        boolean[][] visit = new boolean[game.getNumberOfRows()][game.getNumberOfColumns()];
        Queue<Cell> q = new LinkedList<Cell>();
        visit[game.getMyRat().getCell().getRow()][game.getMyRat().getCell().getCol()] = true;
        q.add(game.getMyRat().getCell());
        Cell node = null;
        while(!q.isEmpty()) {
            node = q.poll();
            assert node != null;
            if(node.hasCheese() && !poison[node.getRow()][node.getCol()]) {
                break;
            }
            for(Cell tmp : graph.get(node)) {
                if(!visit[tmp.getRow()][tmp.getCol()] && tmp.getRatInside() == null) {
                    visit[tmp.getRow()][tmp.getCol()] = true;
                    q.add(tmp);
                    anc[tmp.getRow()][tmp.getCol()] = node;
                }
            }
        }
        if(node != null) {
            Cell ja = node;
            Cell pre = node;
            while (ja != game.getMyRat().getCell()) {
                pre = anc[ja.getRow()][ja.getCol()];
                if (pre == game.getMyRat().getCell()) {
                    pre = ja;
                    ja = game.getMyRat().getCell();
                } else {
                    ja = pre;
                }
            }
            return pre;
        }
        return null;
    }
    void nextMove() {
        Cell next = bfsCheese();
        if(next != null) {
            int di = next.getRow() - game.getMyRat().getCell().getRow();
            int dj = next.getCol() - game.getMyRat().getCell().getCol();
            if (di < 0 && dj == 0) {
                game.getMyRat().moveUp();
                return;
            }
            if (di > 0 && dj == 0) {
                game.getMyRat().moveDown();
                return;
            }
            if (di == 0 && dj < 0) {
                game.getMyRat().moveLeft();
                return;
            }
            if (di == 0 && dj > 0) {
                game.getMyRat().moveRight();
            }
        }
    }
}
