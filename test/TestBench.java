import router.*;

import java.io.FileWriter;
import java.io.IOException;

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
    private static final String runtime = "5";
    private static final String[] nns = {"-1", "0", "1"};
    private static final String[] mhs = {"sa", "ga"};
    private static final String[] sbs = {"rnd", "astar", "bfs"};
    private static final int runs = 5;

    static void runtime_test() throws IOException {
        FileWriter scores = new FileWriter(scores_path,true);
        String out;
        for (String mh : mhs) {
            if (mh.equals("sa")) {
                for (String sb : sbs) {
                    for (String nn : nns) {
                        for (String file : input_files) {
                            for (int i = 0; i < runs; i++) {
                                out = String.join("_", "../data/res", mh, sb, nn, file.substring(8,11), String.valueOf(i));
                                scores.append(out + ";" + i + "\n");
                                System.out.println(out);
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
        System.out.println("hello from tb");
        System.out.println("Runtime test");
        runtime_test();
    }
}


