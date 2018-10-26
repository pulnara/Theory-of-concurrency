import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Buffer {
    private final int[] buffer;
    private int availablePortionSize;
    private int takePtr;
    private int putPtr;
    private DataKeeper dataKeeper;
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition PIERWSZYPROD;
    private final Condition RESZTAPROD;
    private final Condition PIERWSZYKONS;
    private final Condition RESZTAKONS;
    private boolean pk;
    private boolean pp;

    Buffer(int size, DataKeeper dataKeeper) {
        this.buffer = new int[size];
        this.availablePortionSize = this.takePtr = this.putPtr = 0;
        this.dataKeeper = dataKeeper;
        this.PIERWSZYKONS = lock.newCondition();
        this.PIERWSZYPROD = lock.newCondition();
        this.RESZTAKONS = lock.newCondition();
        this.RESZTAPROD = lock.newCondition();
        pk = pp = false;
    }

    public synchronized void put_naive(int portionSize) {
        long begin_wait = System.nanoTime();
        while (portionSize > (this.buffer.length - availablePortionSize)) {
            try {
                if (ProducerConsumer.remainingCons.get() == 0) {
                    return;
                }
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        long end_wait = System.nanoTime();

        dataKeeper.updateProducerEntry(portionSize, end_wait - begin_wait);

        for (int i = 0; i < portionSize; i++) {
            buffer[putPtr] = 1;
            putPtr = (putPtr + 1) % buffer.length;
            availablePortionSize++;
        }

        notifyAll();
    }

    public synchronized void take_naive(int portionSize) {
        long begin_wait = System.nanoTime();
        while (portionSize > availablePortionSize) {
            try {
                if (ProducerConsumer.remainingProds.get() == 0) {
                    return;
                }
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        long end_wait = System.nanoTime();

        dataKeeper.updateConsumerEntry(portionSize, end_wait - begin_wait);

        for (int i = 0; i < portionSize; i++) {
            buffer[takePtr] = 0;
            takePtr = (takePtr + 1) % buffer.length;
            availablePortionSize--;
        }

        notifyAll();
    }

    public void take_fair(int portionSize) {
        lock.lock();
//        System.out.println(String.format("BEGIN\tConsumer #%d - avaliable portion size: %d - want %d",
//                Thread.currentThread().getId(),
//                availablePortionSize, portionSize));

        long begin_wait = System.nanoTime();
        try {
            if (pk) {
                RESZTAKONS.await();
            }

            pk = true;
            while (portionSize > availablePortionSize) {
                if (ProducerConsumer.remainingProds.get() == 0) {
                    System.out.println(String.format("END\tThis was thread #%d - available portion size: %d",
                            Thread.currentThread().getId(),
                            availablePortionSize));
                    if (lock.isHeldByCurrentThread()) {
                        pk = false;
                        RESZTAKONS.signalAll();
                        PIERWSZYPROD.signalAll();
                        lock.unlock();
                    }
                    return;
                }
                PIERWSZYKONS.await();
            }
            long end_wait = System.nanoTime();
            dataKeeper.updateConsumerEntry(portionSize, end_wait - begin_wait);

//            System.out.println(String.format("WAKE\tConsumer #%d - I woke up", Thread.currentThread().getId()));

            for (int i = 0; i < portionSize; i++) {
                buffer[takePtr] = 0;
                takePtr = (takePtr + 1) % buffer.length;
                availablePortionSize--;
            }
            pk = false;
            RESZTAKONS.signalAll();
            PIERWSZYPROD.signalAll();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }

//        System.out.println(String.format("END\tThis was thread #%d - available portion size: %d",
//                Thread.currentThread().getId(),
//                availablePortionSize));
    }

    public void put_fair(int portionSize) {
        lock.lock();
//        System.out.println(String.format("BEGIN\tProducer #%d - free space size: %d - want %d", Thread.currentThread().getId(),
//                (this.buffer.length - availablePortionSize), portionSize));
        try {
            long begin_wait = System.nanoTime();

            if (pp) {
                RESZTAPROD.await();
            }

            pp = true;
            while (portionSize > (this.buffer.length - availablePortionSize)) {

                if (ProducerConsumer.remainingCons.get() == 0) {
                    System.out.println("!!!!!!!!!!!!!!!");
                    if (lock.isHeldByCurrentThread()) {
                        pp = false;
                        PIERWSZYKONS.signalAll();
                        RESZTAPROD.signalAll();
                        lock.unlock();
                    }
                    System.out.println(String.format("END\tThis was thread #%d - available portion size: %d",
                            Thread.currentThread().getId(),
                            availablePortionSize));
                    return;
                }
                PIERWSZYPROD.await();
            }
            long end_wait = System.nanoTime();
            dataKeeper.updateConsumerEntry(portionSize, end_wait - begin_wait);

//            System.out.println(String.format("WAKE\tProducer #%d - I woke up - waited %d",
//                    Thread.currentThread().getId(), end_wait - begin_wait));

            for (int i = 0; i < portionSize; i++) {
                buffer[putPtr] = 1;
                putPtr = (putPtr + 1) % buffer.length;
                availablePortionSize++;
            }

            pp = false;
            PIERWSZYKONS.signalAll();
            RESZTAPROD.signalAll();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
//        System.out.println(String.format("END\tProducer #%d - free space size: %d - available portion size: %d",
//                Thread.currentThread().getId(),
//                (this.buffer.length - availablePortionSize), availablePortionSize));
    }
}



