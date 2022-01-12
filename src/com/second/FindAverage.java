package com.second;

import jade.core.AID;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;

public class FindAverage extends TickerBehaviour {
    private final DefaultAgent agent;

    FindAverage(DefaultAgent agent, long period) {
        super(agent, period);
        this.setFixedPeriod(true);
        this.agent = agent;
    }

    private final double brokeProbability = 0.5;

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
            msg.setContentObject(currentAgent.sharingValue + 2 * Math.random() - 1);
        } catch (Exception e) {
            e.printStackTrace();
        }

        currentAgent.send(msg);
        currentAgent.neighboringNodes.clear();

        while (currentAgent.neighboringNodes.size() != currentAgent.linkedAgents.length) {
            ACLMessage msgRes = currentAgent.receive();
            if (msgRes != null) {
                try {
                    Double content = (Double) msgRes.getContentObject();
                    AID agentSender = msgRes.getSender();

                    // probability of flash link between 3 and 4: 40 %
                    if (Math.random() < brokeProbability &&
                            (agentSender.getLocalName().equals("4") && currentAgent.id.equals("3") ||
                                    agentSender.getLocalName().equals("3") && currentAgent.id.equals("4"))) {
                        currentAgent.neighboringNodes.put(agentSender, 0.0);
                    } else if (content != null) {
                        currentAgent.neighboringNodes.put(agentSender, (currentAgent.sharingValue - content) * 0.1);
                    }
                    System.out.println("Agent " + currentAgent.getLocalName()+": Content " + content);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        currentAgent.neighboringNodes.forEach((k, v) -> {
            currentAgent.sharingValue -= v;
        });

        if (getTickCount() == 50) {
            System.out.println("Agent: " + currentAgent.getLocalName() + "\n" +
                    "TICK: " + getTickCount() + "\n" +
                    "Value: " + currentAgent.sharingValue);

            currentAgent.doDelete();
            this.stop();
        }
    }
}