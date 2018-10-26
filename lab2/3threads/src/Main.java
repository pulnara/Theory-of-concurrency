public class Main {
    public static void main(String[] args) throws InterruptedException {

        Monitor monitor = new Monitor(1);

        NumThread thread1 = new NumThread(1, monitor);
        NumThread thread2 = new NumThread(2, monitor);
        NumThread thread3 = new NumThread(3, monitor);

        thread2.start();
        thread3.start();
        thread1.start();

        thread3.join();
        thread1.join();
        thread2.join();

    }
}
