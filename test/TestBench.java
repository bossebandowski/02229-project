import router.*;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

class TestBench {
    private static final String[] input_files = {"../data/TC0_example.app_network_description",
            "../data/TC1_check_red.app_network_description",
            "../data/TC2_check_bw.app_network_description",
            "../data/TC3_medium.app_network_description",
            "../data/TC4_split_and_merge.app_network_description",
            "../data/TC5_large1.app_network_description",
            "../data/TC6_large2.app_network_description",
            "../data/TC7_huge.app_network_description"};
    private static final String scores_path = "../data/scores.csv";
    private static final String runtime = "10";
    private static final String[] nns = {"-1", "0", "1"};
    private static final String[] mhs = {"sa", "ga"};
    private static final String[] sbs = {"rnd", "astar", "bfs"};
    private static final int runs = 5;

    static void runtime_test() throws IOException {
        FileWriter scores = new FileWriter(scores_path,false);
        scores.append(String.join(";", "mh", "sb", "nn", "file", "iteration", "score\n"));
        String scoreLine;
        String out;
        float score;
        for (String mh : mhs) {
            if (mh.equals("sa")) {
                for (String sb : sbs) {
                    for (String nn : nns) {
                        for (String file : input_files) {
                            for (int i = 0; i < runs; i++) {
                                out = String.join("_", "../solutions/res", mh, sb, nn, file.substring(8,11), String.valueOf(i), ".xml");
                                System.out.println(out + " ... ");
                                score = router.PathPlanner.run(file, out, sb, mh, Integer.parseInt(runtime), Integer.parseInt(nn));
                                scoreLine = String.join(";", mh, sb, nn, file.substring(8,11), String.valueOf(i), score + "\n");
                                scores.append(scoreLine);
                                System.out.printf("Score: %.2f\n", score);
                            }
                        }
                    }
                }
            } else {
                System.out.println("Automated testing for GA not implemented yet");
            }
        }
        scores.flush();
        scores.close();
    }

    public static void main(String[] args) throws IOException {
        System.out.println("Runtime test");
        System.out.println("============");
        System.out.println("============");
        runtime_test();
    }
}


