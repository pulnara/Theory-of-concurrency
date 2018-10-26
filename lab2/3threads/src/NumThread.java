public class NumThread extends Thread {
    private int num;
    private final Monitor monitor;

    public NumThread(int num, Monitor monitor) {
        this.num = num;
        this.monitor = monitor;
    }

    public void run() {
        try {
            for (int i = 0; i < 10; i++) {
                synchronized(monitor) {
                    while (this.num != monitor.getNumberToPrint()) {
                        monitor.wait();
                    }
                    System.out.println(this.num);
                    monitor.incNumberToPrint();
                    monitor.notifyAll();
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            e.printStackTrace();
        }
    }
}
