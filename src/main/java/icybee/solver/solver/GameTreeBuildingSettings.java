package icybee.solver.solver;

import icybee.solver.nodes.GameTreeNode;
import org.json.JSONObject;

public class GameTreeBuildingSettings {
    public static class StreetSetting{
        public float[] bet_sizes;
        public float[] raise_sizes;
        public float[] donk_sizes;
        public boolean allin;

        public String betSizes;
        public String raiseSizes;
        public String donkSizes;

        public StreetSetting(float[] bet_sizes, float[] raise_sizes, float[] donk_sizes, boolean allin) {
            this.bet_sizes = bet_sizes;
            this.raise_sizes = raise_sizes;
            this.donk_sizes = donk_sizes;
            this.allin = allin;
        }

        public StreetSetting(String betSizes, String raiseSizes, String donkSizes, boolean allin) {
            this.betSizes = betSizes;
            this.raiseSizes = raiseSizes;
            this.donkSizes = donkSizes;
            this.allin = allin;

            this.bet_sizes = parseBetSizes(betSizes);
            this.raise_sizes = parseBetSizes(raiseSizes);
            if (donkSizes != null) {
                this.donk_sizes = parseBetSizes(donkSizes);
            }
        }

        private static float[] parseBetSizes(String betstr){
            String[] bets_str = betstr.split(" ");
            float[] bet_sizes = new float[bets_str.length];
            for(int i = 0;i < bets_str.length;i ++){
                String one_bet_str = bets_str[i];
                if(one_bet_str.isEmpty())continue;
                boolean multiplier = false;
                if(one_bet_str.charAt(one_bet_str.length() - 1) == 'x'){
                    one_bet_str = one_bet_str.substring(0,one_bet_str.length() - 1);
                    multiplier = true;
                }
                if(multiplier) bet_sizes[i] = Float.parseFloat(one_bet_str) * 100;
                else bet_sizes[i] = Float.parseFloat(one_bet_str);
            }
            return bet_sizes;
        }

        public JSONObject toJson() {
            JSONObject json = new JSONObject();
            json.put("bet_sizes", betSizes);
            json.put("raise_sizes", raiseSizes);
            json.put("donk_sizes", donkSizes);
            json.put("allin", allin);
            return json;
        }

        public static StreetSetting fromJson(JSONObject json) {
            String betSizes = json.getString("bet_sizes");
            String raiseSizes = json.getString("raise_sizes");
            String donkSizes = null;
            if (json.has("donk_sizes")) {
                donkSizes = json.getString("donk_sizes");
            }
            boolean allin = json.getBoolean("allin");
            return new StreetSetting(betSizes, raiseSizes, donkSizes, allin);
        }
    }
    public StreetSetting flop_ip;
    public StreetSetting turn_ip;
    public StreetSetting river_ip;

    public StreetSetting flop_oop;
    public StreetSetting turn_oop;
    public StreetSetting river_oop;

    public GameTreeBuildingSettings(
            StreetSetting flop_ip,
            StreetSetting turn_ip,
            StreetSetting river_ip,
            StreetSetting flop_oop,
            StreetSetting turn_oop,
            StreetSetting river_oop) {
        this.flop_ip = flop_ip;
        this.turn_ip = turn_ip;
        this.river_ip = river_ip;
        this.flop_oop = flop_oop;
        this.turn_oop = turn_oop;
        this.river_oop = river_oop;
    }

    public StreetSetting getSettings(GameTreeNode.GameRound round,int player){
        if(!(player == 0 || player == 1)) throw new RuntimeException(String.format("player %s not known",player));
        if(round == GameTreeNode.GameRound.RIVER && player == 0) return this.river_ip;
        else if(round == GameTreeNode.GameRound.TURN && player == 0) return this.turn_ip;
        else if(round == GameTreeNode.GameRound.FLOP && player == 0) return this.flop_ip;
        else if(round == GameTreeNode.GameRound.RIVER && player == 1) return this.river_oop;
        else if(round == GameTreeNode.GameRound.TURN && player == 1) return this.turn_oop;
        else if(round == GameTreeNode.GameRound.FLOP && player == 1) return this.flop_oop;
        else throw new RuntimeException(String.format("player %s and round not known",player));
    }

    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("flop_ip_bet", flop_ip.toJson());
        json.put("turn_ip_bet", turn_ip.toJson());
        json.put("river_ip_bet", river_ip.toJson());
        json.put("flop_oop_bet", flop_oop.toJson());
        json.put("turn_oop_bet", turn_oop.toJson());
        json.put("river_oop_bet", river_oop.toJson());
        return json;
    }

    public static GameTreeBuildingSettings fromJson(JSONObject json) {
        return new GameTreeBuildingSettings(
                StreetSetting.fromJson(json.getJSONObject("flop_ip_bet")),
                StreetSetting.fromJson(json.getJSONObject("turn_ip_bet")),
                StreetSetting.fromJson(json.getJSONObject("river_ip_bet")),
                StreetSetting.fromJson(json.getJSONObject("flop_oop_bet")),
                StreetSetting.fromJson(json.getJSONObject("turn_oop_bet")),
                StreetSetting.fromJson(json.getJSONObject("river_oop_bet")));
    }
}
