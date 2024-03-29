package icybee.solver.nodes;

import icybee.solver.Card;
import icybee.solver.trainable.Trainable;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by huangxuefeng on 2019/10/7.
 * This file contians action node implementation
 */
public class ActionNode extends GameTreeNode{

    List<GameActions> actions;
    List<GameTreeNode> childrens;

    List<Card> boardCards;

    Trainable trainable;

    int player;

    public ActionNode(List<GameActions> actions, List<GameTreeNode> childrens, int player, GameRound round,Double pot,GameTreeNode parent){
        this(actions, childrens, player, round, pot, parent, List.of());
    }

    public ActionNode(List<GameActions> actions, List<GameTreeNode> childrens, int player, GameRound round,Double pot,GameTreeNode parent, List<Card> boardCards){
        super(round,pot,parent);
        assert(actions.size() == childrens.size());
        this.actions = actions;
        this.childrens = childrens;
        this.player = player;
        this.boardCards = boardCards;
    }

    public List<GameActions> getActions() {
        return actions;
    }

    public List<GameTreeNode> getChildrens() {
        return childrens;
    }

    public void setActions(List<GameActions> actions) {
        this.actions = actions;
    }

    public void setChildrens(List<GameTreeNode> childrens) {
        this.childrens = childrens;
    }


    public int getPlayer() {
        return player;
    }

    public Trainable getTrainable() {
        return trainable;
    }

    public void setTrainable(Trainable trainable) {
        this.trainable = trainable;
    }

    @Override
    public GameTreeNodeType getType() {
        return GameTreeNodeType.ACTION;
    }

    @Override
    public JSONObject toJson() {
        JSONObject retjson = new JSONObject();

        List<String> actions_str = new ArrayList<>();
        for(GameActions one_action:this.getActions()) actions_str.add(one_action.toString());

        retjson.put("round", round.toString());
        retjson.put("actions",actions_str);
        retjson.put("player",this.getPlayer());

        JSONObject childrens = null;

        for(int i = 0;i < this.getActions().size();i ++){
            GameActions one_action = this.getActions().get(i);
            GameTreeNode one_child = this.getChildrens().get(i);

            JSONObject one_json = one_child.toJson();
            if(one_json != null){
                if(childrens == null) childrens = new JSONObject();
                childrens.put(one_action.toString(),one_json);
            }
        }
        if(childrens != null) {
            retjson.put("childrens", childrens);
        }
        JSONObject strategy = this.getTrainable().dumps(false);
        for(String key : strategy.keySet())
        {
            retjson.put(key, strategy.get(key));
        }
        retjson.put("node_type","action_node");
        return retjson;
    }

    public static ActionNode fromJson(JSONObject json) {
        return null;
//        JSONObject strategy = json.getJSONObject("strategy");
//        for (String sKey : strategy.keySet()) {
//
//        }
//        return strategy;
    }

}
