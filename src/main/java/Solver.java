import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.List;

public class Solver {

    private final Equations equations;

    public Solver(Equations equations) {
        this.equations = equations;
    }

    public void solve() throws ExecutionException, InterruptedException {
        for(int activeRow = 0; activeRow < equations.n; activeRow++){
            gaussJordanStep(activeRow);
        }
        normalizeSolution();
    }

    private void gaussJordanStep(int activeRow) throws ExecutionException, InterruptedException {
        findPivot(activeRow);
        Double[] K = getAllK(activeRow);
        Double[][] C = getAllC(activeRow, K);
        proceedUpdate(activeRow, C);
//        System.out.println(equations.toString());
    }

    private void findPivot(int activeRow) throws ExecutionException, InterruptedException {
        if(equations.A[activeRow][activeRow] != 0){
            return ;
        }
        int pivot = -1;
        for(int i = activeRow + 1; i < equations.n; i++){
            if(equations.A[i][activeRow] != 0){
                pivot = i;
                break;
            }
        }
        if(pivot == -1){
            throw new IllegalArgumentException("Equation system has no solution or infinite number of solutions.");
        }
        List<Future<?>> futures = new LinkedList<>();
        ThreadPoolExecutor rowCopyExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(equations.n - activeRow);
        for(int i = activeRow; i < equations.n; i++){
            final int finalI = i;
            final int finalPivot = pivot;
            Future<?> f = rowCopyExecutor.submit(() -> {
                Double mem = equations.A[activeRow][finalI];
                equations.A[activeRow][finalI] = equations.A[finalPivot][finalI];
                equations.A[finalPivot][finalI] = mem;
            });
            futures.add(f);
        }
        // await termination
        for(Future<?> future : futures){
            future.get();
        }
        rowCopyExecutor.shutdown();
    }

    private void normalizeSolution() throws ExecutionException, InterruptedException {
        List<Future<?>> futures = new LinkedList<>();
        ThreadPoolExecutor normalizeExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(equations.n);
        for(int i = 0; i < equations.n; i++){
            int finalI = i;
            Future<?> f = normalizeExecutor.submit(() -> {
                equations.b[finalI] /= equations.A[finalI][finalI];
                equations.A[finalI][finalI] = 1.0;
            });
            futures.add(f);
        }
        // await termination
        for(Future<?> future : futures){
            future.get();
        }
        normalizeExecutor.shutdown();
    }

    private Double[] getAllK(int activeRow) throws ExecutionException, InterruptedException {
        Double[] K = new Double[equations.n];
        List<Future<Double>> futures = new LinkedList<>();
        ThreadPoolExecutor kDetermineExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(equations.n);
        for(int i = 0; i < equations.n; i++){
            final int finalI = i;
            Future<Double> f = kDetermineExecutor.submit(() -> {
                if(equations.A[finalI][activeRow] == 0) {
                    return 0.0;
                } else {
                    return equations.A[activeRow][activeRow] / equations.A[finalI][activeRow];
                }
            });
            futures.add(f);
        }
        int i = 0;
        for(Future<Double> future : futures){
            K[i] = future.get();
            i++;
        }
        kDetermineExecutor.shutdown();
//        System.out.println("K:");
//        for(int j = 0; j < equations.n; j++){
//            System.out.print(K[j] + " ");
//        }
//        System.out.println();
        return K;
    }

    private Double[][] getAllC(int activeRow, Double[] K) throws ExecutionException, InterruptedException {
        Double[][] C = new Double[equations.n][equations.n];
        List<Future<Double>> futures = new ArrayList<>(equations.n * equations.n);
        ThreadPoolExecutor cDetermineExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(equations.n); // not n^2 not to burn CPU
        for(int r = 0; r < equations.n; r++){
            if(r == activeRow){
                continue;
            }
            for(int c = activeRow; c < equations.n; c++){
                int finalR = r;
                int finalC = c;
                Future<Double> f = cDetermineExecutor.submit(() -> K[finalR] * equations.A[finalR][finalC]);
                futures.add(f);
            }
        }

        // get results
        int i = 0;
        for(int r = 0; r < equations.n; r++){
            if(r == activeRow){
                continue;
            }
            for(int c = activeRow; c < equations.n; c++){
                C[r][c] = futures.get(i).get();
            }
        }
        cDetermineExecutor.shutdown();
        return C;
    }

    private void proceedUpdate(int activeRow, Double[][] C) throws ExecutionException, InterruptedException {
        List<Future<?>> futures = new LinkedList<>();
        ThreadPoolExecutor updateExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(equations.n); // not n^2 not to burn CPU
        for(int r = 0; r < equations.n; r++){
            if(r == activeRow){
                continue;
            }
            for(int c = activeRow; c < equations.n; c++){
                int finalR = r;
                int finalC = c;
                Future<Double> f = updateExecutor.submit(() -> equations.A[finalR][finalC] -= C[finalR][finalC]);
                futures.add(f);
            }
        }

        // await termination
        for(Future<?> future : futures){
            future.get();
        }
        updateExecutor.shutdown();
    }

}
