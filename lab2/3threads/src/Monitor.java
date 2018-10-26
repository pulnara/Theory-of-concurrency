public class Monitor {
    private int numberToPrint;

    public Monitor(int numberToPrint) {
        this.numberToPrint = numberToPrint;
    }


    public int getNumberToPrint() {
        return numberToPrint;
    }

    public void incNumberToPrint() {
        this.numberToPrint = (this.numberToPrint % 3) + 1;
    }
}
