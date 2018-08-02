package client.Team;

import Game.*;

//import java.awt.*;
import java.util.*;
//import java.util.List;

class PathFinder {
    private static final PathFinder pathfinder = new PathFinder();
    private int[][] delta = {{0, 1}, {-1, 0}, {0, -1}, {1, 0}};
    private Game game;
    private Map<Cell, Map<Cell, Integer>> graph;
    private int oppscore;
    private int myscore;
    private boolean[][] poison = new boolean[50][50];
    private ArrayList<Set<Cell>> sclusters;
//    private Map<Cell, Integer> clusters;
    private int[][] clusterIndeces;

    private PathFinder() {
        this.graph = new HashMap<Cell, Map<Cell, Integer>>();
        this.sclusters = new ArrayList<>();
//        this.clusters = new HashMap<Cell, Integer>();
    }

    void setGame(Game game) {
        this.game = game;
        this.clusterIndeces = new int[game.getNumberOfRows()][game.getNumberOfColumns()];
        init(game);
    }

    static PathFinder getPathFinder() {
        return pathfinder;
    }

//    private void drawCluster(){
//        System.out.println("the size is : " + sclusters.size() + "___________");
//        Random rand = new Random();
//        for(Set<Cell> sCell: sclusters){
//
//
//
//            int r = rand.nextInt(256);
//            int g = rand.nextInt(256);
//            int b = rand.nextInt(256);
//
//            TeamAI.debuger.addColor(sCell, new Color(r, g, b));
//        }
//    }
//
//
//    private Color pathColor;
//    private void drawPath(List<Cell> list){
//        TeamAI.debuger.clear();
//        TeamAI.debuger.addColor(list, pathColor);
//    }


    private void init(Game game) {
        Cell[][] map = game.getMap();
        this.clustering();
//        this.drawCluster();

        for(int i=0; i<game.getNumberOfRows(); i++) {
            for(int j=0; j<game.getNumberOfColumns(); j++) {
                Cell tmp = map[i][j];
                this.graph.put(tmp, new HashMap<>());
                for(int k=0; k<4; k++) {
                    int x = i + delta[k][0];
                    int y = j + delta[k][1];
                    if(x >= 0
                            && y >= 0
                            && x < game.getNumberOfRows()
                            && y < game.getNumberOfColumns()
                            && ((tmp.hasLadder() || !map[x][y].hasWall()) || (tmp.hasWall()))
                            ) {
                        int w = 1;
                        if(clusterIndeces[tmp.getRow()][tmp.getCol()] != clusterIndeces[x][y]) {
                            w *= 200;
                        }
                        if(map[x][y].hasWall()) {
                            w *= 200;
                        }
                        this.graph.get(tmp).put(map[x][y], w);
                    }
                }
            }
        }

//
//        Random rand = new Random();
//        int r = rand.nextInt(256);
//        int g = rand.nextInt(256);
//        int b = rand.nextInt(256);
//
//        this.pathColor = new Color(r, g, b);
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
    private Cell bfsCheese(Cell dist) {
        Cell[][] anc = new Cell[game.getNumberOfRows()][game.getNumberOfColumns()];
        boolean[][] visit = new boolean[game.getNumberOfRows()][game.getNumberOfColumns()];
        Queue<Cell> q = new LinkedList<Cell>();
        visit[game.getMyRat().getCell().getRow()][game.getMyRat().getCell().getCol()] = true;
        q.add(game.getMyRat().getCell());
        Cell node = null;
        while(!q.isEmpty()) {
            node = q.poll();
            assert node != null;
            if(node.equals(dist)) {
                break;
            }
            for(Cell tmp : graph.get(node).keySet()) {
                if(!visit[tmp.getRow()][tmp.getCol()] && tmp.getRatInside() == null) {
                    visit[tmp.getRow()][tmp.getCol()] = true;
                    q.add(tmp);
                    anc[tmp.getRow()][tmp.getCol()] = node;
                }
            }
        }
//        List<Cell> list = new LinkedList<Cell>();

        if(node != null) {
            Cell ja = node;
            Cell pre = node;
            while (ja != game.getMyRat().getCell()) {
//                list.add(pre);
                pre = anc[ja.getRow()][ja.getCol()];
                if (pre == game.getMyRat().getCell()) {
                    pre = ja;
                    ja = game.getMyRat().getCell();
                } else {
                    ja = pre;
                }
            }

//            this.drawPath(list);
            return pre;
        }
        return null;
    }
    private LinkedList<Cell> fill() {
        LinkedList<Cell> tmp = new LinkedList<Cell>();
        for(int i=0; i<game.getNumberOfRows(); i++) {
            for(int j=0; j<game.getNumberOfColumns(); j++) {
                tmp.add(game.getMap()[i][j]);
            }
        }
        return tmp;
    }
    private void clustering() {
        Queue<Cell> que;
        que = new LinkedList();
        Queue<Cell> cells = fill();
        int clusterIndex = 0;
        boolean[][] visit = new boolean[game.getNumberOfRows()][game.getNumberOfColumns()];

        while(!cells.isEmpty()) {

            this.sclusters.add(new HashSet<>());
            Cell first = cells.peek();
            que.add(first);
            assert first != null;
            visit[first.getRow()][first.getCol()] = true;
            cells.remove(game.getMap()[first.getRow()][first.getCol()]);
            this.clusterIndeces[first.getRow()][first.getCol()] = clusterIndex;
//            this.clusters.put(game.getMap()[first.getRow()][first.getCol()], clusterIndex);
            this.sclusters.get(clusterIndex).add(game.getMap()[first.getRow()][first.getCol()]);

            while (!que.isEmpty()) {
                Cell tmp = que.poll();

                for (int d = 0; d < 4; d++) {
                    assert tmp != null;
                    int i = tmp.getRow() + delta[d][0];
                    int j = tmp.getCol() + delta[d][1];
                    if (    i < game.getNumberOfRows() &&
                            j < game.getNumberOfColumns() &&
                            j >= 0 &&
                            i >= 0 &&
                            !visit[i][j]
                            &&  (
                                 (!tmp.hasWall() && !game.getMap()[i][j].hasWall()) || tmp.hasLadder() || (tmp.hasWall() && game.getMap()[i][j].hasWall()) || game.getMap()[i][j].hasLadder()
                                )
                            ) {


                        que.add(game.getMap()[i][j]);
                        visit[i][j] = true;
                        this.clusterIndeces[i][j] = clusterIndex;
//                        this.clusters.put(game.getMap()[i][j], clusterIndex);
                        this.sclusters.get(clusterIndex).add(game.getMap()[i][j]);
                        cells.remove(game.getMap()[i][j]);

                    }
                }

            }
            clusterIndex++;
        }
    }

    private Cell dijkstra(Cell str) {
        Map<Cell, Integer> dis = new HashMap<Cell, Integer>();
        Map<Cell, Boolean> mark = new HashMap<Cell, Boolean>();
        dis.put(str, 0);
        mark.put(str, false);
        int INF = Integer.MAX_VALUE;
        for(Cell Tmp : graph.keySet()) {
            if(Tmp != str) {
                dis.put(Tmp, INF);
                mark.put(Tmp, false);
            }
        }
        for(int rep = 0; rep<graph.size(); rep++) {
            Cell u = null;
            int du = INF;
            for(Cell Tmp : graph.keySet()) {
                if(!mark.get(Tmp) && dis.get(Tmp) <= du) {
                    u = Tmp;
                    du = dis.get(Tmp);
                }
            }
            mark.put(u, true);
            for(Cell tmp : graph.keySet()) {
                if(graph.get(u).keySet().contains(tmp)) {
                    dis.put(tmp, Math.min(dis.get(tmp), dis.get(u) + graph.get(u).get(tmp)));
                }
            }
        }
        Cell cheese = null;
        int dc = INF;
        for(Cell tmp : dis.keySet()) {
            if(tmp.hasCheese() && dis.get(tmp) < dc && !poison[tmp.getRow()][tmp.getCol()]) {
                cheese = tmp;
                dc = dis.get(tmp);
            }
        }
        return cheese;
    }

    void nextMove() {
        Cell cheese = dijkstra(game.getMyRat().getCell());
        Cell next = bfsCheese(cheese);
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
