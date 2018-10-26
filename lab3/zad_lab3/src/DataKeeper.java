import java.util.ArrayList;

public class DataKeeper {
    class Entry {
        public long getNanoseconds_wait_time_sum() {
            return nanoseconds_wait_time_sum;
        }

        public int getThreads_counter() {
            return threads_counter;
        }

        long nanoseconds_wait_time_sum;
        int threads_counter;

        Entry () {
            this.nanoseconds_wait_time_sum = 0;
            this.threads_counter = 0;
        }

        public void addTime(long time) {
            this.nanoseconds_wait_time_sum += time;
            this.threads_counter += 1;
        }
    }

    private Entry[] consumerMeasurements;
    private Entry[] producerMeasurements;

    public DataKeeper(int M) {
        this.consumerMeasurements = new Entry[M];
        this.producerMeasurements = new Entry[M];
        for (int i = 0; i < M; i++) {
            consumerMeasurements[i] = new Entry();
            producerMeasurements[i] = new Entry();
        }
    }

    public void updateConsumerEntry(int portionSize, long time) {
        consumerMeasurements[portionSize-1].addTime(time);
    }

    public void updateProducerEntry(int portionSize, long time) {
        producerMeasurements[portionSize-1].addTime(time);
    }

    public void manageResults( int num_PK, int M, Version algo_version, Version portion_version) {
        ChartCreator chartCreator = new ChartCreator();
        for (int i = 0; i < this.producerMeasurements.length; i++) {
            if (this.producerMeasurements[i].getThreads_counter() != 0) {
                float avg_time = (float) this.producerMeasurements[i].getNanoseconds_wait_time_sum() / (float)this.producerMeasurements[i].getThreads_counter() / 1000000;
                chartCreator.addValue(avg_time, i);
            }
        }
        chartCreator.create_chart(num_PK, M, "Prod", algo_version, portion_version);


        chartCreator = new ChartCreator();
        for (int i = 0; i < this.consumerMeasurements.length; i++) {
            if (this.consumerMeasurements[i].getThreads_counter() != 0) {
                float avg_time = (float) this.consumerMeasurements[i].getNanoseconds_wait_time_sum() / (float)this.consumerMeasurements[i].getThreads_counter() / 1000000;
                chartCreator.addValue(avg_time, i);
            }
        }
        chartCreator.create_chart(num_PK, M, "Kons", algo_version, portion_version);


    }

//    public void printTable() {
//
//        for (int i = 0; i < this.measurements.length; i++) {
//            System.out.println(i+1 + " " + this.measurements[i].getNanoseconds_wait_time_sum() + " " + this.measurements[i].getThreads_counter());
//        }
//
//    }

}
