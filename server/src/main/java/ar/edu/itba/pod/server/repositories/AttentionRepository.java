package ar.edu.itba.pod.server.repositories;

import ar.edu.itba.pod.grpc.common.*;

import java.util.*;

public class AttentionRepository {
    private HashMap<Integer,AttentionResponse> startedAttentions;
    private List<AttentionResponse> finishedAttentions;

    public AttentionRepository(){
        startedAttentions = new HashMap<>();
        finishedAttentions = new ArrayList<>();
    }
    public AttentionResponse startAttention (AttentionResponse request){
        startedAttentions.put(request.getRoom(),request);
        return request;
    }

    public AttentionResponse existAttention(Attention attention) {
        AttentionResponse a = startedAttentions.get(attention.getRoom());
        if (a!= null && a.getDoctor().equals(attention.getDoctor()) && a.getPatient().equals(attention.getPatient()) && a.getRoom() == attention.getRoom())
            return a;
        return null;
    }

    public AttentionResponse finishAttention (AttentionResponse attentionResponse){
        startedAttentions.remove(attentionResponse);
        finishedAttentions.add(attentionResponse);
        return attentionResponse;
    }

    public AttentionResponse getStartedAttention(Integer key) {
        return startedAttentions.get(key);
    }
}
