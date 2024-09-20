package ar.edu.itba.pod.server.repositories;

import ar.edu.itba.pod.grpc.common.*;

import java.util.*;

public class AttentionRepository {
    private List<AttentionResponse> startedAttentions;
    private List<AttentionResponse> finishedAttentions;

    public AttentionRepository(){
        startedAttentions = new ArrayList<>();
        finishedAttentions = new ArrayList<>();
    }
    public AttentionResponse startAttention (AttentionResponse request){
        startedAttentions.add(request);
        return request;
    }

    public AttentionResponse existAttention(Attention attention){
        for(AttentionResponse a :startedAttentions){
            if(a.getDoctor().equals(attention.getDoctor()) && a.getPatient().equals(attention.getPatient()) && a.getRoom() == attention.getRoom())
                return a;
        }
        return null;
    }

    public AttentionResponse finishAttention (AttentionResponse attentionResponse){
        startedAttentions.remove(attentionResponse);
        finishedAttentions.add(attentionResponse);
        return attentionResponse;
    }
}
