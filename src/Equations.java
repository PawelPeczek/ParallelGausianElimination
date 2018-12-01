public class Equations {

    public final int n;
    public final Double[][] A;
    public final Double[] b;

    public Equations(int n) {
        this.n = n;
        A = new Double[n][n];
        b = new Double[n];
    }

    @Override
    public String toString(){
        StringBuilder result = new StringBuilder();
        result.append(n).append("\n");
        for(int i = 0; i < n; i++){
            for(int j = 0; j < n; j++){
                result.append(A[i][j]);
                if(j != n - 1){
                    result.append(" ");
                }
            }
            result.append("\n");
        }
        for(int i = 0; i < n; i++){
            result.append(b[i]);
            if(i != n - 1){
                result.append(" ");
            }
        }
        return result.toString();
    }
}
