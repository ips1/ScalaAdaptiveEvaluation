package evaluation.matrixmul;

import org.jlinalg.MatrixMultiplication;

/**
 * Created by Petr Kubat on 7/3/17.
 */
public class CustomizedMatrixMultiplication extends MatrixMultiplication {
    static {
        STRASSEN_ORIGINAL_TRUNCATION_POINT = 10;
        STRASSEN_WINOGRAD_TRUNCATION_POINT = 10;
        STRASSEN_BODRATO_TRUNCATION_POINT = 10;
    }
}
