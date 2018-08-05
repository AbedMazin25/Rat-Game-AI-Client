package client.Team;

//import Debuger.Debuger;
import Game.*;

//import java.awt.Color;
//import java.util.LinkedList;
//import java.util.List;


public class TeamAI extends client.AI {


//    public static Debuger debuger;



    @Override
    public String getTeamName() {
        return "AuraM";
    }


//    private void init(Game game){
//        this.debuger = new Debuger(game);
//    }

    @Override
    public void think(Game game) {
        if(game.getTurnNumber() == 0){
//            init(game);
            PathFinder.getPathFinder().setGame(game);

            CheesePicker.getCheesePicker(game).setGraph(PathFinder.getPathFinder().getGraph());
        }

//        this.debuger.update();


//        List<Cell> list = new LinkedList<Cell>();
//        for(int i=0; i<10 ; i++){
//            for(int j=0; j<10; j++){
//                if(i != j)
//                  list.add(game.getMap()[i][j]);
//            }
//        }
//        TeamAI.debuger.addColor(list, Color.red);

        PathFinder.getPathFinder().poisonWatcher();
        boolean[][] poison = PathFinder.getPathFinder().getPoison();
        if(game.getMyRat().getCell().hasCheese()
                && !poison[game.getMyRat().getCell().getRow()][game.getMyRat().getCell().getCol()]) {
            game.getMyRat().eat();
            return;
        }
        PathFinder.getPathFinder().nextMove();
    }
}