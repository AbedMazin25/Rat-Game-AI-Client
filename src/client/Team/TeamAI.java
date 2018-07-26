package client.Team;

import Game.*;

public class TeamAI extends client.AI {

    @Override
    public String getTeamName() {
        return "AuraM";
    }

    @Override
    public void think(Game game) {
        if (game.getTurnNumber() == 0) {
            PathFinder.getPathFinder().setGame(game);
        }
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