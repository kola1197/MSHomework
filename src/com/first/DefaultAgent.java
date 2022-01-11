package com.first;

import jade.core.Agent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class DefaultAgent extends Agent {
    public String[] linkedAgents;
    public Integer id;
    public HashMap<Integer, Double> map;
    public HashMap<Integer, Double> receivedContent;
    public HashMap<Integer, Double> sharingMap;

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
        this.map = new HashMap<>() {{ put(id, (double)id); }};
        this.receivedContent = new HashMap<>();
        this.sharingMap = new HashMap<>() {{ put(id, (double)id); }};

        System.out.println("Agent #" + id);

        addBehaviour(new FindAverage(this, 2000));
    }
}
