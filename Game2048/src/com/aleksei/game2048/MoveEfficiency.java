package com.aleksei.game2048;

public class MoveEfficiency implements Comparable<MoveEfficiency> {
    //Trida MoveEfficiency popisuje efektivitu pohybu. Aby bylo mozne porovnat efektivitu ruznych tahu,
    // je nutne implementovat podporu pro rozhrani Comparable<MoveEfficiency> ve tride MoveEfficiency.
    private int numberOfEmptyTiles;
    private int score;
    private Move move;

    public MoveEfficiency(int numberOfEmptyTiles, int score, Move move) {
        this.numberOfEmptyTiles = numberOfEmptyTiles;
        this.score = score;
        this.move = move;
    }

    public Move getMove() {
        return move;
    }

    @Override
    public int compareTo(MoveEfficiency o) {
        int result = Integer.compare(numberOfEmptyTiles, o.numberOfEmptyTiles);
        if (result != 0) return result;
        return Integer.compare(score, o.score);
    }
}
