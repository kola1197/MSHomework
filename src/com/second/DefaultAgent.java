package com.second;

import jade.core.AID;
import jade.core.Agent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class DefaultAgent extends Agent {
    public String[] linkedAgents;
    public Integer id;
    public HashMap<AID, Double> neighboringNodes;
    public Double receivedContent;
    public Double sharingValue;

    private static ArrayList<String[]> linkGraph = new ArrayList<>(Arrays.asList(
            new String[] {"3"},
            new String[] {"3"},
            new String[] {"1", "2", "4"},
            new String[] {"3", "5"},
            new String[] {"4"}
            ));

    @Override
    protected void setup() {
        this.id = Integer.parseInt(getAID().getLocalName());
        this.linkedAgents = linkGraph.get(id - 1);
        this.neighboringNodes = new HashMap<>();
        this.receivedContent = 0.0;
        this.sharingValue = 5.0;//(double) id;

        System.out.println("Agent #" + id);

        addBehaviour(new FindAverage(this, 2000));
    }
}
