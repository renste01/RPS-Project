package rps.gui.controller;

// Java imports
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.net.URL;
import java.util.ResourceBundle;

// Project imports
import rps.bll.game.*;
import rps.bll.player.*;

/**
 * Controller for the retro-styled RPS game UI.
 */
public class GameViewController implements Initializable {

    // ── Score labels ──────────────────────────────────────────────────────
    @FXML private Label lblPlayerScore;
    @FXML private Label lblBotScore;
    @FXML private Label lblTieScore;

    // ── Arena labels ──────────────────────────────────────────────────────
    @FXML private Label lblPlayerMove;
    @FXML private Label lblPlayerMoveText;
    @FXML private Label lblBotMove;
    @FXML private Label lblBotMoveText;
    @FXML private Label lblResult;

    // ── Stats labels ──────────────────────────────────────────────────────
    @FXML private Label lblRounds;
    @FXML private Label lblWinRate;
    @FXML private Label lblStreak;

    // ── History table ─────────────────────────────────────────────────────
    @FXML private TableView<HistoryRow> tblHistory;
    @FXML private TableColumn<HistoryRow, Integer> colRound;
    @FXML private TableColumn<HistoryRow, String>  colPlayerMove;
    @FXML private TableColumn<HistoryRow, String>  colBotMove;
    @FXML private TableColumn<HistoryRow, String>  colOutcome;

    // ── Game state ────────────────────────────────────────────────────────
    private GameManager gameManager;
    private IPlayer human;
    private IPlayer bot;

    private int playerWins  = 0;
    private int botWins     = 0;
    private int ties        = 0;
    private int currentStreak = 0;
    private int longestStreak = 0;

    private final ObservableList<HistoryRow> historyRows = FXCollections.observableArrayList();

    // ── Emoji helpers ─────────────────────────────────────────────────────
    private static String moveEmoji(Move move) {
        return switch (move) {
            case Rock    -> "✊";
            case Paper   -> "✋";
            case Scissor -> "✌";
        };
    }

    private static String moveName(Move move) {
        return switch (move) {
            case Rock    -> "ROCK";
            case Paper   -> "PAPER";
            case Scissor -> "SCISSOR";
        };
    }

    // ── Initialise ────────────────────────────────────────────────────────
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        human = new Player("Player", PlayerType.Human);
        bot   = new BotHuset();
        gameManager = new GameManager(human, bot);

        // Wire up table columns
        colRound.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("round"));
        colPlayerMove.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("playerMove"));
        colBotMove.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("botMove"));
        colOutcome.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("outcome"));

        tblHistory.setItems(historyRows);

        // Color the outcome column
        colOutcome.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    if (item.contains("WIN"))       setStyle("-fx-text-fill: #00e676; -fx-alignment: CENTER;");
                    else if (item.contains("LOSS")) setStyle("-fx-text-fill: #ff4f4f; -fx-alignment: CENTER;");
                    else                            setStyle("-fx-text-fill: #f5c842; -fx-alignment: CENTER;");
                }
            }
        });
    }

    // ── Button handlers ───────────────────────────────────────────────────
    @FXML
    private void onRock()    { play(Move.Rock);    }

    @FXML
    private void onPaper()   { play(Move.Paper);   }

    @FXML
    private void onScissor() { play(Move.Scissor); }

    // ── Core play logic ───────────────────────────────────────────────────
    private void play(Move humanMove) {
        Result result = gameManager.playRound(humanMove);

        Move botMove = result.getWinnerPlayer().getPlayerName().equals(bot.getPlayerName())
                ? result.getWinnerMove()
                : result.getLoserMove();

        // Update arena
        lblPlayerMove.setText(moveEmoji(humanMove));
        lblPlayerMoveText.setText(moveName(humanMove));
        lblBotMove.setText(moveEmoji(botMove));
        lblBotMoveText.setText(moveName(botMove));

        // Determine outcome from the human's perspective
        String outcomeText;
        if (result.getType() == ResultType.Tie) {
            outcomeText = "TIE!";
            ties++;
            currentStreak = 0;
        } else if (result.getWinnerPlayer().getPlayerName().equals(human.getPlayerName())) {
            outcomeText = "YOU WIN!";
            playerWins++;
            currentStreak++;
            if (currentStreak > longestStreak) longestStreak = currentStreak;
        } else {
            outcomeText = "YOU LOSE!";
            botWins++;
            currentStreak = 0;
        }

        lblResult.setText(outcomeText);

        // Scores
        lblPlayerScore.setText(String.valueOf(playerWins));
        lblBotScore.setText(String.valueOf(botWins));
        lblTieScore.setText(String.valueOf(ties));

        // Stats
        int totalRounds = playerWins + botWins + ties;
        lblRounds.setText(String.valueOf(totalRounds));
        int winPct = totalRounds == 0 ? 0 : (int) Math.round((playerWins * 100.0) / totalRounds);
        lblWinRate.setText(winPct + "%");
        lblStreak.setText(String.valueOf(longestStreak));

        // History row — show from human's perspective
        String playerMoveStr = moveName(humanMove);
        String botMoveStr    = moveName(botMove);
        String histOutcome   = result.getType() == ResultType.Tie ? "TIE"
                : result.getWinnerPlayer().getPlayerName().equals(human.getPlayerName()) ? "WIN" : "LOSS";

        historyRows.add(0, new HistoryRow(result.getRoundNumber(), playerMoveStr, botMoveStr, histOutcome));
    }

    // ── History row model ─────────────────────────────────────────────────
    public static class HistoryRow {
        private final int    round;
        private final String playerMove;
        private final String botMove;
        private final String outcome;

        public HistoryRow(int round, String playerMove, String botMove, String outcome) {
            this.round      = round;
            this.playerMove = playerMove;
            this.botMove    = botMove;
            this.outcome    = outcome;
        }

        public int    getRound()      { return round;      }
        public String getPlayerMove() { return playerMove; }
        public String getBotMove()    { return botMove;    }
        public String getOutcome()    { return outcome;    }
    }
}