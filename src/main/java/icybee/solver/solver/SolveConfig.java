package icybee.solver.solver;

import icybee.solver.Card;
import icybee.solver.Config;
import icybee.solver.Deck;
import icybee.solver.SolverEnvironment;
import icybee.solver.compairer.Compairer;
import icybee.solver.ranges.PrivateCards;
import icybee.solver.trainable.CfrPlusTrainable;
import icybee.solver.trainable.DiscountedCfrTrainable;
import icybee.solver.utils.PrivateRangeConverter;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SolveConfig {
    private static Compairer compairer_holdem = null;
    private static Compairer compairer_shortdeck = null;
    private static Deck holdem_deck = null;
    private static Deck shortdeck_deck = null;

    static {
        try {
            System.out.println("loading holdem compairer dictionary...");
            String config_name = "resources/yamls/rule_holdem_simple.yaml";
            Config config = loadConfig(config_name);
            compairer_holdem = SolverEnvironment.compairerFromConfig(config, false);
            holdem_deck = SolverEnvironment.deckFromConfig(config);
            System.out.println("loading holdem compairer dictionary complete");

            System.out.println("loading shortdeck compairer dictionary...");
            config_name = "resources/yamls/rule_shortdeck_simple.yaml";
            config = loadConfig(config_name);
            compairer_shortdeck = SolverEnvironment.compairerFromConfig(config, false);
            shortdeck_deck = SolverEnvironment.deckFromConfig(config);
            System.out.println("loading shortdeck compairer dictionary complete");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    PrivateCards[][] ranges;
    PrivateCards[] range1;
    PrivateCards[] range2;
    int[] initial_board;
    long initial_board_long;
    Compairer compairer;
    Deck deck;
    int player_number;
    int iteration_number;
    boolean debug;
    int print_interval;
    String logfile;
    Class<?> trainer;
    MonteCarolAlg monteCarolAlg;

    Double stopExploitability;

    String oopRangeStr;
    String ipRangeStr;
    String initialBoardStr;
    int deckMode; // 0 is texas_holdem, 1 is shortdeck
    String iterationStr;
    String logIntervalStr;
    int algorithmMode; // 0 is DiscountedCfrTrainable, 1 is CfrPlusTrainable
    boolean monteCarloAlg;  // true == MonteCarolAlg.PUBLIC, false == MonteCarolAlg.NONE
    String stopExploitabilityStr;

    public SolveConfig(String oopRangeStr,
                       String ipRangeStr,
                       String initialBoardStr,
                       int deckMode, // 0 is texas_holdem, 1 is shortdeck
                       String iterationStr,
                       String logIntervalStr,
                       int algorithmMode, // 0 is DiscountedCfrTrainable, 1 is CfrPlusTrainable
                       boolean monteCarloAlg,  // true == MonteCarolAlg.PUBLIC, false == MonteCarolAlg.NONE
                       String stopExploitabilityStr) {
        this.oopRangeStr = oopRangeStr;
        this.ipRangeStr = ipRangeStr;
        this.initialBoardStr = initialBoardStr;
        this.deckMode = deckMode;
        this.iterationStr = iterationStr;
        this.logIntervalStr = logIntervalStr;
        this.algorithmMode = algorithmMode;
        this.monteCarloAlg = monteCarloAlg;
        this.stopExploitabilityStr = stopExploitabilityStr;

        String[] boardCards = initialBoardStr.split(",");

        int[] initialBoard = new int[boardCards.length];
        for(int i = 0;i < boardCards.length;i ++){
            initialBoard[i] = Card.strCard2int(boardCards[i]);
        }

        PrivateCards[] oopRange = PrivateRangeConverter.rangeStr2Cards(oopRangeStr,initialBoard);
        PrivateCards[] ipRange = PrivateRangeConverter.rangeStr2Cards(ipRangeStr,initialBoard);

        Compairer compairer =  deckMode == 0 ? compairer_holdem : compairer_shortdeck;
        Deck deck = deckMode == 0 ? holdem_deck : shortdeck_deck;

        this.initial_board_long = Card.boardInts2long(initialBoard);

        this.range1 = this.noDuplicateRange(oopRange, this.initial_board_long);
        this.range2 = this.noDuplicateRange(ipRange, this.initial_board_long);

        this.player_number = 2;
        this.ranges = new PrivateCards[this.player_number][];
        this.ranges[0] = this.range1;
        this.ranges[1] = this.range2;

        this.compairer = compairer;
        this.deck = deck;
        this.initial_board = initialBoard;
        this.iteration_number = Integer.parseInt(iterationStr);
        this.debug = false;
        this.print_interval = Integer.parseInt(logIntervalStr);
        this.logfile = null;
        this.trainer = algorithmMode == 0 ? DiscountedCfrTrainable.class : CfrPlusTrainable.class;
        this.monteCarolAlg = monteCarloAlg ? MonteCarolAlg.PUBLIC:MonteCarolAlg.NONE;
        this.stopExploitability = Double.valueOf(stopExploitabilityStr);
    }
    public SolveConfig(PrivateCards[] range1 ,
                       PrivateCards[] range2,
                       int[] initial_board,
                       Compairer compairer,
                       Deck deck,
                       int iteration_number,
                       boolean debug,
                       int print_interval,
                       String logfile,
                       Class<?> trainer,
                       MonteCarolAlg monteCarolAlg,
                       Double stopExploitability) {
        this.initial_board_long = Card.boardInts2long(initial_board);

        this.range1 = this.noDuplicateRange(range1, this.initial_board_long);
        this.range2 = this.noDuplicateRange(range2, this.initial_board_long);

        this.player_number = 2;
        this.ranges = new PrivateCards[this.player_number][];
        this.ranges[0] = this.range1;
        this.ranges[1] = this.range2;

        this.compairer = compairer;
        this.deck = deck;
        this.initial_board = initial_board;
        this.iteration_number = iteration_number;
        this.debug = debug;
        this.print_interval = print_interval;
        this.logfile = logfile;
        this.trainer = trainer;
        this.monteCarolAlg = monteCarolAlg;
        this.stopExploitability = stopExploitability;
    }

    private static Config loadConfig(String conf_name) {
        File file;
        for(String one_url: new String[]{conf_name,"src/test/" + conf_name}) {
            file = new File(one_url);
            Config config;
            try {
                config = new Config(file.getAbsolutePath());
            } catch (Exception e) {
                continue;
            }
            return config;
        }
        throw new RuntimeException("load config failed: cannot find config file");
    }

    public PrivateCards[] noDuplicateRange(PrivateCards[] private_range,long board_long){
        List<PrivateCards> range_array = new ArrayList<>();
        Map<Integer,Boolean> rangekv = new HashMap<>();
        for(PrivateCards one_range:private_range){
            if(one_range == null) throw new RuntimeException();
            if(rangekv.get(one_range.hashCode()) != null)
                throw new RuntimeException(String.format("duplicated key %d",one_range.toString()));
            rangekv.put(one_range.hashCode(),Boolean.TRUE);
            long hand_long = Card.boardInts2long(new int[]{
                    one_range.card1,
                    one_range.card2
            });
            if(!Card.boardsHasIntercept(hand_long,board_long)){
                range_array.add(one_range);
            }
        }
        PrivateCards[] ret = new PrivateCards[range_array.size()];
        range_array.toArray(ret);
        return ret;
    }

    public JSONObject toJson() {
        JSONObject json = new JSONObject();

        json.put("oop_range", oopRangeStr);
        json.put("ip_range", ipRangeStr);
        json.put("board", initialBoardStr);
        json.put("deck_mode", deckMode);
        json.put("iteration", iterationStr);
        json.put("log_interval", logIntervalStr);
        json.put("algorithm_mode", algorithmMode);
        json.put("monte_carlo_alg", monteCarloAlg);
        json.put("stop_exploitability", stopExploitabilityStr);
        return json;
    }

    public static SolveConfig fromJson(JSONObject json) {
        String oopRangeStr = json.getString("oop_range");
        String ipRangeStr = json.getString("ip_range");
        String initialBoardStr = json.getString("board");
        int deckMode = json.getInt("deck_mode");
        String iterationStr = json.getString("iteration");
        String logIntervalStr = json.getString("log_interval");
        int algorithmMode = json.getInt("algorithm_mode");
        boolean monteCarloAlg = json.getBoolean("monte_carlo_alg");
        String stopExploitabilityStr = json.getString("stop_exploitability");

        return new SolveConfig(
                oopRangeStr,
                ipRangeStr,
                initialBoardStr,
                deckMode,
                iterationStr,
                logIntervalStr,
                algorithmMode,
                monteCarloAlg,
                stopExploitabilityStr
        );
    }
}
