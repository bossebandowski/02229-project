import java.util.Arrays;
import java.util.List;

public abstract class MetaHeuristic {
    private Architecture a;

    public MetaHeuristic(Architecture a) {
        this.a = a;
    }

    public boolean isViable(List<List<Integer>> solution) {
        return this.deadlinesOk(solution);
    }

    private void calculateMaxLinkDelays(List<List<Integer>> solution) {
        float[] dataTransferred = new float[this.a.getLinks().size()];
        Arrays.fill(dataTransferred, 0f);
        int idx = 0;

        for (Stream s : this.a.getStreams()) {
            int size = s.getSize();
            for (int rep = 0; rep < s.getRl(); rep++) {
                List<Integer> route = solution.get(idx);

                int parent = route.get(0);
                int child;
                // get all links. calculate time it takes to pass the link and add to t
                for (int rId = 1; rId < route.size(); rId++) {
                    child = route.get(rId);
                    int linkId = this.a.getGraph()[parent][child];
                    dataTransferred[linkId] += size;
                    parent = child;
                }
                idx++;
            }
        }

        for (int i = 0; i < dataTransferred.length; i++) {
            Link l = this.a.getLinks().get(i);
            l.setC(dataTransferred[i]/l.getSpeed());
        }
    }

    private boolean deadlinesOk(List<List<Integer>> solution) {
        this.calculateMaxLinkDelays(solution);

        int idx = 0;
        // iterate over all streams
        for (Stream s : this.a.getStreams()) {
            // get stream deadline
            float deadline = (float) s.getDeadline();
            // iterate over all routes associated with the stream
            for (int rep = 0; rep < s.getRl(); rep++) {
                float t = 0f;
                List<Integer> route = solution.get(idx);
                int parent = route.get(0);
                int child;
                // get all links. calculate time it takes to pass the link and add to t
                for (int rId = 1; rId < route.size(); rId++) {
                    child = route.get(rId);
                    Link l = this.a.getLinks().get(this.a.getGraph()[parent][child]);
                    t += l.getC();
                    parent = child;
                }

                // if t is greater than the stream's deadline, then the check has been failed
                if (t > deadline) return false;
                // check next route
                idx++;
            }
        }
        return true;
    }
}
