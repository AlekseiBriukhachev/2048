package com.aleksei.game2048;

import java.util.*;

//Model – obsahuje herni logiku a uklada hraci pole.
public class Model {

    //Ve tride Model je vytvoreno privet static final pole FIELD_WIDTH s hodnotou 4.
    private static final int FIELD_WIDTH = 4;

    //Ve tride Model jsou deklarovane a inicializovane private pole previousStates,
    // previousScores, isSaveNeeded.
    private Stack<Tile[][]> previousStates = new Stack<>();
    private Stack<Integer> previousScores = new Stack<>();
    private boolean isSaveNeeded = true;

    //Ve tride Model je vytvorena private hrací pole typu Tile[][].
    private Tile[][] gameTiles = new Tile[FIELD_WIDTH][FIELD_WIDTH];
    //Pole skore a maxTile typu int, ktere uklada aktualni skore a maximalni vahu desticky na hracim poli.
    public int score;
    public int maxTile;

    //Konstruktor bez parametru tridy Model vyplnuje pole hernich dlazdic s novymi objekty typu Tile.
    // Pole gameTiles musi mit rozmer FIELD_WIDTH x FIELD_WIDTH
    public Model() {
        //Konstruktor tridy Model musi obsahovat volani metody resetGameTiles.
        resetGameTiles();
    }

    //Metoda addTile zmeni hodnotu nahodne prazdne dlazdice v poli gameTiles na 2 nebo 4 s
    // pravdepodobnosti 0,9 a 0,1 v tomto poradi, coz se podiva na to, ktere dlazdice jsou prazdne,
    // a prípadne zmeni vahu jedne z nich. nahodne, o 2 nebo 4 (o 9 dvojek by melo odpovídat 1 ctyrce).
    // Nahodny objekt ze seznamu získame pomoci nasledujíciho vyrazu: (Velikost seznamu * randomNumberFromZeroToOne).
    // Pro vypocet hmotnosti nove dlazdice je vyraz (Math.random() < 0,9 ? 2 : 4).
    private void addTile() {
        List<Tile> emptyTiles = getEmptyTiles();
        if (emptyTiles.size() != 0) {
            Tile tile = emptyTiles.get((int) (Math.random() * emptyTiles.size()));
            tile.value = (Math.random() < 0.9 ? 2 : 4);
        }
    }

    //Metoda getEmptyTiles vrati seznam prazdnych dlazdic v poli gameTiles.
    private List<Tile> getEmptyTiles() {
        List<Tile> emptyTilesList = new ArrayList<>();
        for (int x = 0; x < FIELD_WIDTH; x++) {
            for (int y = 0; y < FIELD_WIDTH; y++) {
                if (gameTiles[x][y].isEmpty()) {
                    emptyTilesList.add(gameTiles[x][y]);
                }
            }
        }
        return emptyTilesList;
    }

    //Metoda resetGameTiles naplni pole gameTiles novymi dlazdicemi a zmeni hodnotu dvou z nich dvema volanimi metody addTile.
    public void resetGameTiles() {
        for (int x = 0; x < FIELD_WIDTH; x++) {
            for (int y = 0; y < FIELD_WIDTH; y++) {
                gameTiles[x][y] = new Tile();
            }
        }
        addTile();
        addTile();
        this.score = 0;
        this.maxTile = 0;
    }

    //Zmensit dlazdice tak, aby vsechny prazdne dlazdice byly vpravo, tzn. radek {4, 2, 0, 4} se zmeni na radek {4, 2, 4, 0}
    private boolean compressTiles(Tile[] tiles) {
        boolean isCompressed = false;
        int checkPosition = 0;
        for (int i = 0; i < tiles.length; i++) {
            if (!tiles[i].isEmpty()) {
                if (i != checkPosition) {
                    tiles[checkPosition] = tiles[i];
                    tiles[i] = new Tile();
                    isCompressed = true;
                }
                checkPosition++;
            }
        }
        //
        //Metoda compressTiles musi vratit hodnotu true, pokud provedla zmeny ve vstupnim poli.
        return isCompressed;
    }

    //
    //Slouceni dlazdic stejne nominalni hodnoty, tj. radek {4, 4, 2, 0} se zmeni na radek
    // {8, 2, 0, 0}. Radek {4, 4, 4, 4} se zmeni na {8, 8, 0, 0} a {4, 4, 4, 0} na {8, 4, 0, 0}.
    private boolean mergeTiles(Tile[] tiles) {
        boolean isMerged = false;
        for (int i = 0; i < tiles.length - 1; i++) {
            if (!tiles[i].isEmpty() && tiles[i].value == tiles[i + 1].value) {
                tiles[i].value *= 2;
                tiles[i + 1].value = 0;
                compressTiles(tiles);
                isMerged = true;
                //
                //Pokud je splnena podminka slouceni dlazdic, zda je nova hodnota vetsi nez
                // maximalni hodnota, a v pripade potreby zmena hodnoty pole maxTile.
                if (tiles[i].value > this.maxTile) {
                    this.maxTile = tiles[i].value;
                }
                //Skore se zvysuje po kazdem slouceni, napriklad pokud je aktualni skore 20 a radek {4, 4, 4, 0}
                // byl sloucen, skore by se mělo zvysit o 8.
                this.score += tiles[i].value;
            }
        }
        //Metoda mergeTiles musi vratit hodnotu true, pokud provedla zmeny ve vstupnim poli.
        return isMerged;
    }

    //
    //
    //Metoda left() posouva dlazdice doleva, coz zavola metody compressTiles a mergeTiles pro kazdy radek
    // pole gameTiles a v pripade potreby prida jednu dlazdici pomoci metody addTile.
    public void left() {
        if (isSaveNeeded) saveState(gameTiles);
        boolean isMovedLeft = false;
        for (int i = 0; i < gameTiles.length; i++) {
            if (compressTiles(gameTiles[i]) | mergeTiles(gameTiles[i])) {
                isMovedLeft = true;
            }
        }
        if (isMovedLeft) addTile();
        isSaveNeeded = true;
    }


    //Metoda up() posouva prvky pole gameTiles nahoru podle pravidel hry a v pripade potreby prida dlazdice pomoci metody addTile.
    public void up() {
        //Metoda up() musi ulozit aktualni stav hry a jednou skorovat do príslusneho steku
        saveState(gameTiles);
        rotateClockwise();
        rotateClockwise();
        rotateClockwise();
        left();
        rotateClockwise();
    }

    //Metoda down() posuva prvky pole gameTiles dolu podle pravidel hry a v pripade potreby prida dlazdici pomoci metody addTile.
    public void down() {
        //Metoda down() musi ulozit aktualni stav hry a jednou skorovat do príslusneho steku
        saveState(gameTiles);
        rotateClockwise();
        left();
        rotateClockwise();
        rotateClockwise();
        rotateClockwise();
    }

    //Metoda right() posuva prvky pole gameTiles doprava podle pravidel hry a v pripade potreby prida dlazdici pomoci metody addTile.
    public void right() {
        // Metoda right() musi ulozit aktualni stav hry a jednou skorovat do príslusneho steku
        saveState(gameTiles);
        rotateClockwise();
        rotateClockwise();
        left();
        rotateClockwise();
        rotateClockwise();
    }

    //Otaceni poli o 90st
    private void rotateClockwise() {
        Tile[][] rotatedGameTiles = new Tile[FIELD_WIDTH][FIELD_WIDTH];
        for (int x = 0; x < FIELD_WIDTH; x++) {
            for (int y = 0; y < FIELD_WIDTH; y++) {
                rotatedGameTiles[x][y] = gameTiles[gameTiles.length - y - 1][x];
            }
        }
        gameTiles = rotatedGameTiles;
    }

    public Tile[][] getGameTiles() {
        return gameTiles;
    }

    //Metoda canMove vrati hodnotu true, pokud je moznr provest tah v aktualni pozici tak, aby se zmenil stav hraciho pole.
    // Jinak - false.
    public boolean canMove() {
        if (!getEmptyTiles().isEmpty()) return true;
        for (int x = 0; x < FIELD_WIDTH; x++) {
            for (int y = 0; y < FIELD_WIDTH; y++) {
                if (x < FIELD_WIDTH - 1 && gameTiles[x][y].value == gameTiles[x + 1][y].value) return true;
                else if (y < FIELD_WIDTH - 1 && gameTiles[x][y].value == gameTiles[x][y + 1].value) return true;
            }
        }
        return false;
    }

    //Metoda saveState s jednim parametrem typu Tile[][] ulozi aktualni stav hry a pocet stacku pomoci metody push a
    // nastavi priznak isSaveNeeded na false.
    private void saveState(Tile[][] tiles) {
        Tile[][] savedTiles = new Tile[FIELD_WIDTH][FIELD_WIDTH];
        for (int x = 0; x < FIELD_WIDTH; x++) {
            for (int y = 0; y < FIELD_WIDTH; y++) {
                savedTiles[x][y] = new Tile(tiles[x][y].value);
            }
        }
        previousStates.push(savedTiles);
        previousScores.push(score);
        isSaveNeeded = false;
    }

    //metoda rollback nastavi aktualni stav hry na posledni ve stecku pomoci metody pop.
    public void rollback() {
        //Metoda rollback musi obnovit pole skore a hernich dlazdic z jejich prislusnych stecku, pokud nejsou prazdne.
        if (previousScores.size() != 0 && previousStates.size() != 0) {
            gameTiles = previousStates.pop();
            score = previousScores.pop();
        }
    }

    void randomMove() {
        //Metodu randomMove, ktera náhodne zavola jednu z metod presunu.
        int n = ((int) (Math.random() * 100)) % 4;
        //Toto cislo obsahuje pseudonahodne cele cislo v rozsahu [0..3],
        // pro kazde z nich muze se volat jednu z metod doleva, doprava, nahoru, dolu.
        switch (n) {
            case 0 -> left();
            case 1 -> right();
            case 2 -> up();
            case 3 -> down();
        }
    }

    public boolean hasBoardChanged() {
        //
        //boolean hasBoardChanged – vrati hodnotu true, pokud se hmotnost kamenu v poli hernich dlazdic lisi
        // od hmotnosti kamenu v hornim poli predchoziho stecku. Horni prvek nesmi byt odstranen ze stecku!
        Tile[][] tilesUpperArray = previousStates.peek();
        for (int x = 0; x < FIELD_WIDTH; x++) {
            for (int y = 0; y < FIELD_WIDTH; y++) {
                if (gameTiles[x][y].value != tilesUpperArray[x][y].value) return true;
            }
        }
        return false;
    }

    //MoveEfficiency getMoveEfficiency(Move move) - vezme jeden parametr typu pohybu a vrati objekt typu MoveEfficiency
    // popisujici efektivitu udalaneho pohybu.
    public MoveEfficiency getMoveEfficiency(Move move) {
        //Metoda getMoveEfficiency musi vratit prijatou efektivitu presunu jako parametr.
        move.move();
        //Pokud tah predany metode getMoveEfficiency nezmeni hraci pole, mel by byt vracen objekt s poctem prazdnych
        // bunek rovnym -1.
        if (!hasBoardChanged()) {
            rollback();
            return new MoveEfficiency(-1, 0, move);
        }
        int numberOfEmptyTiles = 0;
        for (int x = 0; x < FIELD_WIDTH; x++) {
            for (int y = 0; y < FIELD_WIDTH; y++) {
                if (gameTiles[x][y].isEmpty()) numberOfEmptyTiles++;
            }
        }
        MoveEfficiency moveEfficiency = new MoveEfficiency(numberOfEmptyTiles, score, move);
        rollback();
        return moveEfficiency;
    }

    public void autoMove() {
        //Lokalni PriorityQueue<MoveEfficiency> s parametrem Collections.reverseOrder()
        // (takze maximalni prvek je vzdy na zacatku fronty) a velikostí rovnou ctyrem.
        PriorityQueue<MoveEfficiency> priorityQueue = new PriorityQueue<>(4, Collections.reverseOrder());
        //Naplnme PriorityQueue ctyrmi objekty typu MoveEfficiency (jeden pro kazdou moznost presunu).
        priorityQueue.add(getMoveEfficiency(this::down));
        priorityQueue.add(getMoveEfficiency(this::up));
        priorityQueue.add(getMoveEfficiency(this::left));
        priorityQueue.add(getMoveEfficiency(this::right));
        //Vezmeme horni prvek a provedeme pohyb s nim spojeny.
        priorityQueue.peek().getMove().move();

    }
}
