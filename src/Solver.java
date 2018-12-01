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

    public void solve(){
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
    }

    private void gaussJordanStep(int activeRow){
        Double[] K = getAllK(activeRow);
    }

    private Double[] getAllK(int activeRow) throws ExecutionException, InterruptedException {
        Double[] K = new Double[equations.n];
        List<Future<Double>> futures = new LinkedList<>();
        ThreadPoolExecutor kDetermineExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(equations.n);
        for(int i = 0; i < equations.n; i++){
            final int finalI = i;
            Future<Double> f = kDetermineExecutor.submit(() -> {
                return equations.A[finalI][activeRow] / equations.A[finalI][activeRow];
            });
            futures.add(f);
        }
        int i = 0;
        for(Future<Double> future : futures){
            K[i] = future.get();
            i++;
        }
        return K;
    }
}
