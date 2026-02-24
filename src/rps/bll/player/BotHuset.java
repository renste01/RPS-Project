package rps.bll.player;

import rps.bll.game.IGameState;
import rps.bll.game.Move;
import rps.bll.game.Result;

import static rps.bll.player.Player.getMove;
import static rps.bll.player.PlayerType.AI;

import java.util.ArrayList;
import java.util.Random;


public class BotHuset implements IPlayer
{
    private String name;
    private PlayerType type;

    @Override
    public String getPlayerName() {
        return "BotHuset";
    }

    @Override
    public PlayerType getPlayerType() {
        return AI;
    }

    @Override
    public Move doMove(IGameState state)
    {
        return getMove(state);
    }

}
