package rps.gui;

// Project imports
import rps.bll.game.*;
import rps.bll.player.*;

// Java imports
import java.util.Scanner;

/**
 * Console implementation of the RPS game
 *
 * @author smsj
 */
public class ConsoleApp {

    /**
     * Starts the game
     */
    public void startGame() {
        System.out.println("Welcome to the classic Rock, Paper and Scissor game!");
        Scanner keyboard = new Scanner(System.in);

        System.out.print("Please type in your player name: ");
        String playerName = keyboard.next();

        IPlayer human = new Player(playerName, PlayerType.Human);
        IPlayer bot = new Player(getBotName(), PlayerType.AI);

        System.out.println("Your opponent is " + bot.getPlayerName());
        System.out.println("Starting game.... good luck both!");

        GameManager ge = new GameManager(human, bot);

        // --- GAME LOOP ---
        while (true) {
            String playerMove = getPlayerMove();

            if (playerMove.equalsIgnoreCase("exit"))
                break;

            ge.playRound(Move.valueOf(playerMove));

            // Print KUN sidste runde
            Result lastResult = null;
            for (Result r : ge.getGameState().getHistoricResults()) {
                lastResult = r;
            }

            if (lastResult != null) {
                System.out.println(getResultAsString(lastResult));
            }
        }

        // --- PRINT RESULTATER EFTER EXIT ---
        System.out.println("\nFinal results:");
        ge.getGameState().getHistoricResults().forEach((result) -> {
            System.out.println(getResultAsString(result));
        });

        System.out.println("\nThanks for playing!");
    }

    /**
     * Famous robots...
     */
    private String getBotName() {
        return "BotHuset";
    }

    /**
     * Ask the human player to make a move...
     */
    public String getPlayerMove() {
        Scanner keyboard = new Scanner(System.in);
        String input;
        boolean inputOK;

        do {
            inputOK = false;
            System.out.println();
            System.out.print("Choose Your Weapon (Rock/R, Paper/P or Scissor/S) or Exit/E to quit the game: ");
            input = keyboard.next();

            if (input.equalsIgnoreCase("rock") || input.equalsIgnoreCase("r") ||
                    input.equalsIgnoreCase("paper") || input.equalsIgnoreCase("p") ||
                    input.equalsIgnoreCase("scissor") || input.equalsIgnoreCase("s") ||
                    input.equalsIgnoreCase("exit") || input.equalsIgnoreCase("e")) {

                inputOK = true;

                if (input.equalsIgnoreCase("r")) input = "Rock";
                else if (input.equalsIgnoreCase("p")) input = "Paper";
                else if (input.equalsIgnoreCase("s")) input = "Scissor";
                else if (input.equalsIgnoreCase("e")) input = "Exit";
            }
            else {
                System.out.println("Invalid input. Try again :)");
            }
        }
        while (!inputOK);

        return input;
    }

    /**
     * Provides a custom formatted string representation of a Result
     */
    public String getResultAsString(Result result) {
        String statusText = result.getType() == ResultType.Win ? "wins over " : "ties ";

        return "Round #" + result.getRoundNumber() +
                ": " + result.getWinnerPlayer().getPlayerName() +
                " (" + result.getWinnerMove() + ") " +
                statusText + result.getLoserPlayer().getPlayerName() +
                " (" + result.getLoserMove() + ")!";
    }
}