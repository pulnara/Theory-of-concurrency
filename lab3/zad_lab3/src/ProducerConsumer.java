import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class ProducerConsumer {
    private static int M;
    private int num_P_K;

//    public static int getRemainingProds() {
//        return remainingProds;
//    }
//
//    public static int getRemainingCons() {
//        return remainingCons;
//    }

    public static AtomicInteger remainingProds;
    public static AtomicInteger remainingCons;
    private Version algo_version;
    private Version portion_version;
    private DataKeeper dataKeeper;

    public ProducerConsumer(int M, int num_PK, Version algo_version, Version portion_version, DataKeeper dataKeeper) {
        ProducerConsumer.M = M;
        this.num_P_K  = num_PK;
        remainingCons = new AtomicInteger(num_PK);
        remainingProds = new AtomicInteger(num_PK);
        this.algo_version = algo_version;
        this.portion_version = portion_version;
        this.dataKeeper = dataKeeper;
    }

    public static void decreaseRemainingProds() {
        System.out.println(remainingProds.decrementAndGet() + " remaining prods");
        System.out.println(remainingProds + " remaining prods");
    }

    public static void decreaseRemainingCons() {
        System.out.println(remainingCons.decrementAndGet() + " remaining cons");
        System.out.println(remainingCons + " remaining cons");
    }

    private int get_equal_probability_portion_size(Random rand) {
        return rand.nextInt(ProducerConsumer.M) + 1;
    }

    public int get_portion_size() {
        Random rand = new Random();

        if (this.portion_version == Version.EQUAL_PORTIONS || rand.nextInt(199) % 13 == 0) {
            return get_equal_probability_portion_size(rand);
        }

        return rand.nextInt(M/2) + 1;
    }

    public void work() {
        Buffer buffer = new Buffer(2*M, dataKeeper);

        Producer producers[] = new Producer[num_P_K];
        Consumer consumers[] = new Consumer[num_P_K];

        for (int i = 0; i < num_P_K; i++) {
            producers[i] = new Producer(buffer, this, algo_version);
            consumers[i] = new Consumer(buffer, this, algo_version);
        }

        for (int i = 0; i < num_P_K; i++) {
            producers[i].start();
            consumers[i].start();
        }

        for (int i = 0; i < num_P_K; i++) {
            try {
                producers[i].join();
                consumers[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
