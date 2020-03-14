package dev.acardosi.libertas;

public class Card {
    private String line1;
    private String line2;
    private String thumbnail;

    public Card(String line1, String line2, String thumbnail) {
        this.line1 = line1;
        this.line2 = line2;
        this.thumbnail = thumbnail;
    }

    public String getLine1() {
        return line1;
    }

    public String getLine2() {
        return line2;
    }

    public String getThumbnail() {
        return thumbnail;
    }


}