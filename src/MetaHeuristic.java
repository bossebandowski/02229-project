import com.sun.org.apache.xerces.internal.impl.xpath.XPath;

import java.util.*;

import java.util.Arrays;
import java.util.List;

public abstract class MetaHeuristic {

    private Architecture a;

    public MetaHeuristic(Architecture a) {
        this.a = a;
    }

    float calculateCostFunction(List<List<Integer>> solution) {
        float result = 0.0f;

        final float bandwidthCoeff = 0.0f;
        final float rlCoeff = 0.0f;
        final float totalLengthCoeff = 0.0f;
        final float deadlineCoeff = 0.0f;
        final float overlappingCoeff = 0.0f;

        // Calculate free bandwidth of each link
        ArrayList<Link> linkBuffer = this.a.getLinks();
        HashMap<Link,Float> linkCapacity = new HashMap<Link,Float>();
        for(Link link:linkBuffer)
        {
            linkCapacity.put(link, link.getSpeed());
        }
        for (Integer streamID = 0; streamID < solution.size(); streamID++)
        {
            for(Integer nodeID = 0; nodeID < solution.get(streamID).size(); nodeID++)
            {
                if(nodeID > 0)
                {
                    Link currentLink = this.a.getLink(solution.get(streamID).get(nodeID - 1),solution.get(streamID).get(nodeID));
                    linkCapacity.put(currentLink, linkCapacity.get(currentLink) - (this.a.getStreambyID(streamID).getSize()
                            / this.a.getStreambyID(streamID).getPeriod()));
                }
            }
        }
        for(Link link:linkCapacity.keySet())
        {
            result += linkCapacity.get(link) * bandwidthCoeff;
        }



        // Calculate overlapping (number of common links of Streams with common start and end Node)
        HashMap<Integer,Integer> streamOverlaps = new HashMap<Integer,Integer>();
        for(Stream currentStream:this.a.getStreams())
        {
            streamOverlaps.put(currentStream.getId(),0);
        }
        ArrayList<Stream> streams = this.a.getStreams();
        int k = 0;
        for(int i = 0; i < streams.size(); i++)
        {
            Stream currentStream = streams.get(i);
            ArrayList<Integer> usedLinks = new ArrayList<Integer>();
            int currentRl = currentStream.getRl();
            int currentStreamStart = k;
            for(; k < currentStreamStart + currentRl; k++)
            {
                for(int j = 0; j < solution.get(k).size(); j++)
                {
                    if(!usedLinks.contains(solution.get(k).get(j)))
                    {
                        usedLinks.add(solution.get(k).get(j));
                    }
                    else
                    {
                        streamOverlaps.put(i,streamOverlaps.get(i) + 1);
                    }
                }
            }
            result -= overlappingCoeff * streamOverlaps.get(i);
        }

        // Calculate total length of routes
        ArrayList<Integer> lengths = new ArrayList<Integer>();
        for (Integer streamID = 0; streamID < solution.size(); streamID++)
        {
            lengths.add(solution.get(streamID).size());
            // Todo: How is the length affects the cost function
        }




        return  result;
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
