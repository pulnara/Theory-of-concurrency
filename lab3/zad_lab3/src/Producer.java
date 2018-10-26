public class Producer extends Thread {
    private Buffer buffer;
    private ProducerConsumer pc;
    private Version algo_version;

    public Producer(Buffer buffer, ProducerConsumer pc, Version algo_version) {
        this.buffer = buffer;
        this.pc = pc;
        this.algo_version = algo_version;
    }

    private void run_naive_producer(int portionSize) {
        buffer.put_naive(portionSize);
    }

    private void run_fair_producer(int portionSize) {
        buffer.put_fair(portionSize);

    }

    public void run() {
        for (int i = 0; i < 10; i++) {
            int portionSize = pc.get_portion_size();
            if (algo_version == Version.NAIVE) {
                run_naive_producer(portionSize);
            } else {
                run_fair_producer(portionSize);
            }
        }
        Buffer.decreaseRemainingProds();
        System.out.println(Thread.currentThread().getId() + " papa");
    }
}

