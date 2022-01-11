package com.first;

import jade.core.AID;
import jade.core.behaviours.TickerBehaviour;
import java.util.HashMap;
import jade.domain.AMSService;
import jade.domain.FIPAAgentManagement.AMSAgentDescription;
import jade.domain.FIPAAgentManagement.SearchConstraints;
import jade.lang.acl.ACLMessage;
import jade.wrapper.ContainerController;

import java.util.concurrent.TimeUnit;

public class FindAverage extends TickerBehaviour {
    private final DefaultAgent agent;

    FindAverage(DefaultAgent agent, long period) {
        super(agent, period);
        this.setFixedPeriod(true);
        this.agent = agent;
    }

    @Override
    protected void onTick() {
        DefaultAgent currentAgent = this.agent;
        System.out.println("Agent " + currentAgent.getLocalName() + ": TICK = " + getTickCount());

        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);

        for (String agent : currentAgent.linkedAgents) {
            AID destination = new AID(agent, AID.ISLOCALNAME);
            msg.addReceiver(destination);
        }

        try {
            msg.setContentObject(currentAgent.sharingMap);
        } catch (Exception e) {
            e.printStackTrace();
        }

        currentAgent.send(msg);
        currentAgent.sharingMap.clear();
        currentAgent.receivedContent.clear();

        int i = currentAgent.linkedAgents.length;
        while (i > 0) {
            ACLMessage msgRes = currentAgent.receive();
            if (msgRes != null) {
                i--;
                try {
                    @SuppressWarnings("unchecked")
                    HashMap<Integer, Double> content = (HashMap<Integer, Double>) msgRes.getContentObject();
                    if (content != null) {
                        currentAgent.receivedContent.putAll(content);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        // for testing
//        System.out.println("receivedContent: " + currentAgent.receivedContent + " id: " + currentAgent.id);

        currentAgent.receivedContent.forEach((k, v) -> {
            if (!currentAgent.map.containsKey(k)) {
                currentAgent.map.put(k, v);
                currentAgent.sharingMap.put(k, v);
            }
        });
        // for testing
//        System.out.println("sharingMap:  " + currentAgent.sharingMap + " id: " + currentAgent.id);

        if (currentAgent.sharingMap.size() == 0){
            try {
                TimeUnit.SECONDS.sleep(currentAgent.id - 1);
            } catch(InterruptedException e){
                System.out.println("AVG Done\n________________");
                Thread.currentThread().interrupt();
            }
            AMSAgentDescription [] agents = null;
            ContainerController cont = currentAgent.getContainerController();

            try {
                SearchConstraints cc = new SearchConstraints();
                cc.setMaxResults((long) -1);
                agents = AMSService.search( myAgent, new AMSAgentDescription(), cc );
            } catch (Exception e) {
                e.printStackTrace();
            }

            assert agents != null;
            for (AMSAgentDescription agent: agents) {
                AID agentID = agent.getName();
                if (agentID != currentAgent.getAID()) {
                    try {
                        cont.getAgent(agentID.getLocalName()).kill();
                    } catch(Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            getAVG();
            currentAgent.doDelete();
            this.stop();
        }
    }

    private void getAVG(){
        DefaultAgent currentAgent = this.agent;
        double sum = currentAgent.map.values().stream().reduce(0.0, Double::sum);

        System.out.println("Result: " + sum / currentAgent.map.size() + "\n" +
                "Agent: " + currentAgent.id + "\n" +
                "Agent map: " + currentAgent.map);
    }
}