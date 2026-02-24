package rps.bll.player;

import rps.bll.game.IGameState;
import rps.bll.game.Move;
import rps.bll.game.Result;
import static rps.bll.player.PlayerType.AI;

import java.util.ArrayList;


public class BotHuset implements IPlayer
{
    private String name = "BotHuset";
    private PlayerType type = AI;

    @Override
    public String getPlayerName() {
        return "";
    }

    @Override
    public PlayerType getPlayerType() {
        return null;
    }

    @Override
    public Move doMove(IGameState state)
    {
        ArrayList<Result> results = (ArrayList<Result>) state.getHistoricResults();
        return Move.Rock;
    }
}
