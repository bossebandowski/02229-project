import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

class TestBench {
    private static final String[] inputFiles = {"../data/TC0_example.app_network_description",
            "../data/TC1_check_red.app_network_description",
            "../data/TC2_check_bw.app_network_description",
            "../data/TC3_medium.app_network_description",
            "../data/TC4_split_and_merge.app_network_description",
            "../data/TC5_large1.app_network_description",
            "../data/TC6_large2.app_network_description",
            "../data/TC7_huge.app_network_description"};
    private static final String scoresPath = "../data/scores.csv";
    private static final String timesPath = "../data/times.csv";
    private static final String runtime = "1";
    private static final String[] nns = {"-1", "0", "1"};
    private static final String[] mhs = {"sa", "ga"};
    private static final String[] sbs = {"rnd", "astar", "bfs"};
    private static final Map<String, Float> targetScores = new HashMap<>();
    private static final int runs = 2;
    private static final String runtimeBound = "5";
    private static final float targetFactor = 1.01f;

    static void runtimeTest() throws IOException {
        TreeMap<Long, Float> res;
        FileWriter costs = new FileWriter(scoresPath,false);
        costs.append(String.join(";", "mh", "sb", "nn", "file", "iteration", "cost\n"));
        String scoreLine;
        String out;
        float score;
        for (String mh : mhs) {
            if (mh.equals("sa")) {
                for (String sb : sbs) {
                    for (String nn : nns) {
                        for (String file : inputFiles) {
                            for (int i = 0; i < runs; i++) {
                                out = String.join("_", "../solutions/scores/res", mh, sb, nn, file.substring(8,11), String.valueOf(i), ".xml");
                                System.out.println(out + " ... ");
                                res = router.PathPlanner.run(file, out, sb, mh, Integer.parseInt(runtime), Integer.parseInt(nn), 0);
                                score = res.firstEntry().getValue();
                                scoreLine = String.join(";", mh, sb, nn, file.substring(8,11), String.valueOf(i), score + "\n");
                                costs.append(scoreLine);
                                System.out.printf("Score: %.2f\n", score);
                            }
                        }
                    }
                }
            } else {
                for (String file : inputFiles) {
                    for (int i = 0; i < runs; i++) {
                        out = String.join("_", "../solutions/scores/res", mh, file.substring(8,11), String.valueOf(i), ".xml");
                        System.out.println(out + " ... ");
                        res = router.PathPlanner.run(file, out, "rnd", mh, Integer.parseInt(runtime), 0, 0);
                        score = res.firstEntry().getValue();
                        scoreLine = String.join(";", mh, "", "", file.substring(8,11), String.valueOf(i), score + "\n");
                        costs.append(scoreLine);
                        System.out.printf("Score: %.2f\n", score);
                    }
                }
            }
        }
        costs.flush();
        costs.close();
    }

    static void calculateTargetScores() {
        for (String file : inputFiles) {
            targetScores.put(file.substring(8,11), Float.MAX_VALUE);
        }

        String line;
        try (BufferedReader br = new BufferedReader(new FileReader(scoresPath))) {
            while ((line = br.readLine()) != null) {
                try {
                    String[] res = line.split(";");
                    if (Float.parseFloat(res[5]) < targetScores.get(res[3])) {
                        targetScores.put(res[3], Float.parseFloat(res[5]));
                    }
                } catch (NumberFormatException ignored) {
                    // just the header, ignore
                }

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        for (String key : targetScores.keySet()) {
            System.out.printf(key + ": %.2f\n", targetScores.get(key));
        }

        for (String file : inputFiles) {
            targetScores.put(file.substring(8,11), targetScores.get(file.substring(8,11)) * targetFactor);
        }

        targetScores.remove("TC2");

        for (String key : targetScores.keySet()) {
            System.out.printf(key + ": %.2f\n", targetScores.get(key));
        }
    }

    static void targetScoreTest() throws IOException {
        calculateTargetScores();

        TreeMap<Long, Float> res;
        FileWriter times = new FileWriter(timesPath,false);
        times.append(String.join(";", "mh", "sb", "nn", "file", "iteration", "time\n"));
        String scoreLine;
        String out;
        long time;
        for (String mh : mhs) {
            if (mh.equals("sa")) {
                for (String sb : sbs) {
                    for (String nn : nns) {
                        for (String file : inputFiles) {
                            if (!(file.startsWith("TC2", 8))) {
                                for (int i = 0; i < runs; i++) {
                                    out = String.join("_", "../solutions/times/res", mh, sb, nn, file.substring(8,11), String.valueOf(i), ".xml");
                                    System.out.println(out + " ... ");
                                    res = router.PathPlanner.run(file, out, sb, mh, Integer.parseInt(runtimeBound), Integer.parseInt(nn), targetScores.get(file.substring(8, 11)));
                                    time = res.firstEntry().getKey();
                                    scoreLine = String.join(";", mh, sb, nn, file.substring(8,11), String.valueOf(i), time + "\n");
                                    times.append(scoreLine);
                                    System.out.printf("Time: %.2fs\n", (float) (time/1000));
                                }
                            }
                        }
                    }
                }
            } else {
                for (String file : inputFiles) {
                    if (!(file.startsWith("TC2", 8))) {
                        for (int i = 0; i < runs; i++) {
                            out = String.join("_", "../solutions/times/res", mh, file.substring(8,11), String.valueOf(i), ".xml");
                            System.out.println(out + " ... ");
                            res = router.PathPlanner.run(file, out, "rnd", mh, Integer.parseInt(runtimeBound), 0, targetScores.get(file.substring(8, 11)));
                            time = res.firstEntry().getKey();
                            scoreLine = String.join(";", mh, "", "", file.substring(8,11), String.valueOf(i), time + "\n");
                            times.append(scoreLine);
                            System.out.printf("Time: %.2fs\n", (float) (time/1000));
                        }
                    }
                }
            }
        }
        times.flush();
        times.close();
    }

    public static void main(String[] args) throws IOException {
        System.out.println("Runtime test");
        System.out.println("============");
        System.out.println("============");
        runtimeTest();
        targetScoreTest();
    }
}


