import com.sun.org.apache.xerces.internal.impl.xpath.XPath;

import java.util.*;

public abstract class MetaHeuristic {

    float calculteCostFunction(Architecture architecture, List<List<Integer>> solution)
    {
        float result = 0.0f;

        final float bandwidthCoeff = 0.0f;
        final float rlCoeff = 0.0f;
        final float totalLengthCoeff = 0.0f;
        final float deadlineCoeff = 0.0f;
        final float overlappingCoeff = 0.0f;

        // Calculate free bandwidth of each link
        ArrayList<Link> linkBuffer = architecture.getLinks();
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
                    Link currentLink = architecture.getLink(solution.get(streamID).get(nodeID - 1),solution.get(streamID).get(nodeID));
                    linkCapacity.put(currentLink, linkCapacity.get(currentLink) - (architecture.getStreambyID(streamID).getSize()
                            / architecture.getStreambyID(streamID).getPeriod()));
                }
            }
        }
        for(Link link:linkCapacity.keySet())
        {
            result += linkCapacity.get(link) * bandwidthCoeff;
        }



        // Calculate overlapping (number of common links of Streams with common start and end Node)
        HashMap<Integer,Integer> streamOverlaps = new HashMap<Integer,Integer>();
        for(Stream currentStream:architecture.getStreams())
        {
            streamOverlaps.put(currentStream.getId(),0);
        }
        ArrayList<Stream> streams = architecture.getStreams();
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
}
