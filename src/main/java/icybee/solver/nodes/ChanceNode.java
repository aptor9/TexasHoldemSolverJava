package icybee.solver.nodes;

import icybee.solver.Card;
import icybee.solver.trainable.Trainable;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by huangxuefeng on 2019/10/7.
 * This file contians action node implementation
 */
public class ChanceNode extends GameTreeNode{
    // 如果一个chance node的game round是river，那么实际上它是一个介于turn和river之间的发牌节点
    List<GameTreeNode> childrens;

    Trainable trainable;

    int player;

    List<Card> cards;

    boolean donk;

    public ChanceNode(List<GameTreeNode> childrens, GameRound round, Double pot, GameTreeNode parent, List<Card> cards,List<Card> board, boolean donk){
        super(round,pot,parent);
        this.childrens = childrens;
        this.cards = cards.stream().filter(card -> {
            for (Card boardCard : board) {
                if (card.equals(boardCard)) {
                    return false;
                }
            }
            return true;
        }).collect(Collectors.toList());
        this.donk = donk;
        //if(childrens.size() != cards.size()) throw new RuntimeException("Card and childern length not match");
    }

//    public ChanceNode(List<GameTreeNode> childrens, GameRound round, Double pot, GameTreeNode parent, List<Card> cards){
//        this(childrens, round, pot, parent, cards, List.of());
//    }

    public ChanceNode(List<GameTreeNode> childrens, GameRound round, Double pot, GameTreeNode parent, List<Card> cards, List<Card> board){
        this(childrens, round, pot, parent, cards, board, false);
        //if(childrens.size() != cards.size()) throw new RuntimeException("Card and childern length not match");
    }

    public List<Card> getCards() {
        return cards;
    }

    public List<GameTreeNode> getChildrens() {
        return childrens;
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

    public void setChildrens(List<GameTreeNode> childrens) {
        this.childrens = childrens;
    }

    @Override
    public GameTreeNodeType getType() {
        return GameTreeNodeType.CHANCE;
    }

    @Override
    public JSONObject toJson() {
        List<Card> cards = this.getCards();
        List<GameTreeNode> childerns = this.getChildrens();
        if(cards.size() != childerns.size())
            throw new RuntimeException("length not match");
        JSONObject retjson = new JSONObject();
        List<String> card_strs = new ArrayList<>();
        for(Card card:cards)
            card_strs.add(card.toString());

        JSONObject dealcards = new JSONObject();
        for(int i = 0;i < cards.size();i ++){
            Card one_card = cards.get(i);
            GameTreeNode gameTreeNode = childerns.get(i);
            dealcards.put(one_card.toString(),gameTreeNode.toJson());
        }

        retjson.put("deal_cards",dealcards);
        retjson.put("deal_number",dealcards.length());
        retjson.put("node_type","chance_node");
        return retjson;
    }

    public static ChanceNode fromJson(JSONObject json) {
        return null;
    }

    public boolean isDonk() {
        return donk;
    }

}
