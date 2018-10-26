
public class Main {

    public static void main(String[] argv) {

        if (argv.length != 2) {
            System.out.println("Invalid number of parameters given.");
            System.out.println("Valid parameters:");
            System.out.println("<M> <PK>");
            System.exit(1);
        }


        int M = Integer.parseInt(argv[0]);
        int num_P_K = Integer.parseInt(argv[1]);

        DataKeeper dataKeeper = new DataKeeper(M);

        ////////////////////////////////////////////////////////////
        Version algo_version = Version.NAIVE;
        Version portion_version = Version.EQUAL_PORTIONS;

        ProducerConsumer pc_naive_equal = new ProducerConsumer(M, num_P_K, algo_version, portion_version, dataKeeper);
        pc_naive_equal.work();
        dataKeeper.manageResults(num_P_K, M, algo_version, portion_version);
        ////////////////////////////////////////////////////////////


        ////////////////////////////////////////////////////////////
        portion_version = Version.NOT_EQUAL_PORTIONS;
        dataKeeper = new DataKeeper(M);

        ProducerConsumer pc_naive_not_equal = new ProducerConsumer(M, num_P_K, algo_version, portion_version, dataKeeper);
        pc_naive_not_equal.work();
        dataKeeper.manageResults(num_P_K, M, algo_version, portion_version);
        ////////////////////////////////////////////////////////////


        ////////////////////////////////////////////////////////////
        algo_version = Version.FAIR;
        portion_version = Version.EQUAL_PORTIONS;
        dataKeeper = new DataKeeper(M);

        ProducerConsumer pc_fair_equal = new ProducerConsumer(M, num_P_K, algo_version, portion_version, dataKeeper);
        pc_fair_equal.work();
        dataKeeper.manageResults(num_P_K, M, algo_version, portion_version);
        ////////////////////////////////////////////////////////////

        ////////////////////////////////////////////////////////////
        portion_version = Version.NOT_EQUAL_PORTIONS;
        dataKeeper = new DataKeeper(M);

        ProducerConsumer pc_fair_not_equal = new ProducerConsumer(M, num_P_K, algo_version, portion_version, dataKeeper);
        pc_fair_not_equal.work();
        dataKeeper.manageResults(num_P_K, M, algo_version, portion_version);
        ////////////////////////////////////////////////////////////


        System.out.println();
        System.out.printf("Udalo sie!!!!!");
    }
}
