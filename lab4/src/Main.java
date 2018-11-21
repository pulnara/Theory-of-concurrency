import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.*;
import static java.lang.Math.min;
import static java.lang.System.exit;

public class Main {
    private static int MAX_ITER;
    private static final double ZOOM = 150;
    private static int k;

    private static void count(Mandelbrot pic, int n) {
        for (int y = 0; y < pic.getHeight(); y++) {
            for (int x = n * pic.getWidth() / (k*10); x < min(pic.getWidth(), (n+1) * pic.getWidth() / (k*10)); x++) {
                double zx, zy;
                zx = zy = 0;
                double cX = (x - 400) / ZOOM;
                double cY = (y - 300) / ZOOM;
                int iter = MAX_ITER;
                while (zx * zx + zy * zy < 4 && iter > 0) {
                    double tmp = zx * zx - zy * zy + cX;
                    zy = 2.0 * zx * zy + cY;
                    zx = tmp;
                    iter--;
                }
                pic.I.setRGB(x, y, iter | (iter << 8));
            }
        }
    }

    public static class PartCounterCallable implements Callable {
        private Mandelbrot pic;
        private int n;

        PartCounterCallable(Mandelbrot pic, int n) {
            this.pic = pic;
            this.n = n;
        }

        @Override
        public Object call() throws Exception {
            count(this.pic, this.n);
            return null;
        }
    }

    public static class PartCounterRunnable implements Runnable {
        private Mandelbrot pic;
        private int n;

        PartCounterRunnable(Mandelbrot pic, int n) {
            this.pic = pic;
            this.n = n;
        }

        @Override
        public void run() {
            count(this.pic, this.n);
        }
    }

    private static void runFutureVariant(ExecutorService pool, String title) throws ExecutionException, InterruptedException {
        Mandelbrot mandelbrotFuture = new Mandelbrot(title);
        Set<Future<Integer>> set = new HashSet<Future<Integer>>();
        long begin_time = System.nanoTime();
        for (int i = 0; i < k*10; i++) {
            Callable<Integer> callable = new PartCounterCallable(mandelbrotFuture, i);
            Future<Integer> future = pool.submit(callable);
            set.add(future);
        }
        for (Future<Integer> future : set) {
            future.get();
        }
        long end_time = System.nanoTime();
        mandelbrotFuture.print();
        System.out.println(String.format("%s\t\t%d ms", title, (end_time - begin_time)/1000000));
    }

    private static void runExecutorVariant(ExecutorService executor, String title) {
        Mandelbrot mandelbrotExecute = new Mandelbrot(title);
        long begin_time = System.nanoTime();
        for (int i = 0; i < k*10; i++) {
            executor.execute(new PartCounterRunnable(mandelbrotExecute, i));
        }
        executor.shutdown();
        while (!executor.isTerminated()) {}
        long end_time = System.nanoTime();
        mandelbrotExecute.print();
        System.out.println(String.format("%s\t\t%d ms", title, (end_time - begin_time)/1000000));
    }

    private static void performAllImplementations() throws ExecutionException, InterruptedException {
        ExecutorService pool = Executors.newSingleThreadExecutor();
        runFutureVariant(pool, "Mandelbrot - Future - SingleThreadExecutor");
        runExecutorVariant(pool, "Mandelbrot - Execute - SingleThreadExecutor");

        pool = Executors.newFixedThreadPool(k);
        runFutureVariant(pool, "Mandelbrot - Future - FixedThreadPool");
        runExecutorVariant(pool, "Mandelbrot - Execute - FixedThreadPool");

        pool = Executors.newCachedThreadPool();
        runFutureVariant(pool, "Mandelbrot - Future - CachedThreadPool");
        runExecutorVariant(pool, "Mandelbrot - Execute - CachedThreadPool");

        pool = Executors.newWorkStealingPool(k);
        runFutureVariant(pool, "Mandelbrot - Future - WorkStealingPool");
        runExecutorVariant(pool, "Mandelbrot - Execute - WorkStealingPool");
    }

    public static void runForGivenk(int k) throws ExecutionException, InterruptedException {
        MAX_ITER = 570;
        System.out.println(String.format("\nMAX_ITER: %d", MAX_ITER));
        System.out.println(String.format("k: %d", k));
        System.out.println();
        performAllImplementations();

        MAX_ITER = 700;
        System.out.println(String.format("\nMAX_ITER: %d", MAX_ITER));
        System.out.println(String.format("k: %d", k));
        System.out.println();
        performAllImplementations();

        MAX_ITER = 1000;
        System.out.println(String.format("\nMAX_ITER: %d", MAX_ITER));
        System.out.println(String.format("k: %d", k));
        System.out.println();
        performAllImplementations();
    }


    public static void main(String[] args) throws ExecutionException, InterruptedException {
        if (args.length != 1) {
            System.out.println("Invalid number of parameters. Please, provide number of threads.");
            exit(1);
        }

        k = Integer.valueOf(args[0]);
        runForGivenk(k);

        k = 1000;
        runForGivenk(k);
    }
}
