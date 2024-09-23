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
        if(!startedAttentions.containsKey(attentionResponse.getRoom()) ||
                !startedAttentions.get(attentionResponse.getRoom()).getDoctor().equals(attentionResponse.getDoctor())
        ){
            return null;
        }
        startedAttentions.remove(attentionResponse.getRoom());
        finishedAttentions.add(attentionResponse);
        return attentionResponse;
    }

    public AttentionResponse getStartedAttention(Integer key) {
        return startedAttentions.get(key);
    }

    public List<AttentionResponse> getFinishedAttentions(){
        return finishedAttentions;
    }

    public List<AttentionResponse> getFinishedAttentionsByRoom(int room){
        List<AttentionResponse> list = new ArrayList<>();
        for(AttentionResponse a: finishedAttentions){
            if(a.getRoom() == room){
                list.add(a);
            }
        }
        return list;
    }
}
