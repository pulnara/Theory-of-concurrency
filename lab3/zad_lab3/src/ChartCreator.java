import java.io.*;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.chart.ChartUtilities;

public class ChartCreator {
    final DefaultCategoryDataset dataset;

    public ChartCreator() {
        dataset = new DefaultCategoryDataset();
    }

    public void addValue(float value, int M) {
        dataset.setValue(value , "Wielkosc porcji" , Integer.toString(M));

    }

    public void create_chart(int num_P_K, int M, String who, Version algo_version, Version portion_version) {

        JFreeChart barChart = ChartFactory.createBarChart(
                String.format("Konsumenci\nM = %d\nP = K = %d", M, num_P_K),
                "Wielkosc porcji", "t [ms]",
                dataset,PlotOrientation.VERTICAL,
                false, true, false);

        int width = 2000;    /* Width of the image */
        int height = 900;   /* Height of the image */
        String filename = String.format("%s_M%d_PK%d_%s_%s.jpeg", who, M, num_P_K, algo_version.name(),portion_version.name());
        File BarChart = new File( filename );
        try {
            ChartUtilities.saveChartAsJPEG( BarChart , barChart , width , height );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
