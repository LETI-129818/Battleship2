package battleship;

/**
 * Representa o registo de uma jogada para armazenamento.
 */
public class ShotRecord {
    private int row;
    private int column;
    private boolean hit;
    private String shipCategory;

    public ShotRecord() {
    }

    public ShotRecord(int row, int column, boolean hit, String shipCategory) {
        this.row = row;
        this.column = column;
        this.hit = hit;
        this.shipCategory = shipCategory;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    public boolean isHit() {
        return hit;
    }

    public void setHit(boolean hit) {
        this.hit = hit;
    }

    public String getShipCategory() {
        return shipCategory;
    }

    public void setShipCategory(String shipCategory) {
        this.shipCategory = shipCategory;
    }
}