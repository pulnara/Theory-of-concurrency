public class Consumer extends Thread {
    private Buffer buffer;
    private ProducerConsumer pc;
    private Version algo_version;

    public Consumer(Buffer buffer, ProducerConsumer pc, Version algo_version) {
        this.buffer = buffer;
        this.pc = pc;
        this.algo_version = algo_version;
    }

    private void run_naive_consumer(int portionSize) {
        buffer.take_naive(portionSize);
    }

    private void run_fair_consumer(int portionSize) {
        buffer.take_fair(portionSize);

    }

    public void run() {
        for (int i = 0; i < 100; i++) {
            int portionSize = pc.get_portion_size();
            if (algo_version == Version.NAIVE) {
                run_naive_consumer(portionSize);
            } else {
                run_fair_consumer(portionSize);
            }
        }
        Buffer.decreaseRemainingCons();
        System.out.println(Thread.currentThread().getId() + " papa");
    }
}
