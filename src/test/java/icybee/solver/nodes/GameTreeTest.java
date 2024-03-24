package icybee.solver.nodes;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flipkart.zjsonpatch.JsonDiff;
import icybee.solver.*;
import icybee.solver.compairer.Compairer;
import icybee.solver.ranges.PrivateCards;
import icybee.solver.solver.CfrPlusRiverSolver;
import icybee.solver.solver.MonteCarolAlg;
import icybee.solver.solver.SolveConfig;
import icybee.solver.solver.Solver;
import icybee.solver.trainable.DiscountedCfrTrainable;
import icybee.solver.utils.PrivateRangeConverter;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertTrue;

public class GameTreeTest {
    Compairer compairer = null;
    Deck deck = null;

    Config config = null;

    Config loadConfig(String conf_name){
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource(conf_name).getFile());

        Config config = null;
        try {
            config = new Config(file.getAbsolutePath());
        }catch(Exception e){
            throw new RuntimeException();
        }
        return config;
    }

    public void initialize(String configFile) {
        config = loadConfig(configFile);
        if(compairer == null) {
            try {
                compairer = SolverEnvironment.compairerFromConfig(config);
                deck = SolverEnvironment.deckFromConfig(config);
            } catch (Exception e) {
                e.printStackTrace();
                assertTrue(false);
            }
        }
    }

    @Test
    public void toJsonTest() throws Exception {
        initialize("yamls/rule_holdem_simple.yaml");

        String strategy_fname = "src/test/resources/inputs/river_strategy.json";

        GameTree game_tree = SolverEnvironment.gameTreeFromConfig(config, deck);

        String player1RangeStr = "Jc5c: 0.0041958,Jc6c: 0.00598499964327,Jc9c: 0.049461,Jd5d: 0.1258428,Jd6d: 0.102221193907,Js5s: 0.193959388439,Js6s: 0.6145279,Js7s: 0.0628723,Js9c: 0.0515676969263,Js9h: 0.0008844,Qc9c: 0.000944,Qc9h: 0.0677125,QcTc: 0.29744,QcTd: 0.1696662,QcTh: 0.1379181,QcTs: 0.168507589956,QcJc: 0.283859983081,QcJd: 0.5903636,QcJs: 0.443982873537,Qd9c: 0.000227099986464,Qd9h: 0.0290965,QdTc: 0.1243321,QdTd: 0.14159999156,QdTh: 0.0923404,QdTs: 0.1176243,QdJc: 0.0901529892529,QdJd: 0.729537956516,QdJs: 0.377906477475,Qh9c: 0.0001673,Qh9h: 0.133007992072,QhTc: 0.1110481,QhTd: 0.118914,QhTh: 0.184664,QhTs: 0.1160136,QhJc: 0.139595991679,QhJd: 0.5613431,QhJs: 0.4186267,Qs9c: 0.000240099985689,Qs9h: 0.0290975,QsTc: 0.0833066950345,QsTd: 0.0730891,QsTh: 0.0385236,QsTs: 0.126947992433,QsJc: 0.1291171,QsJd: 0.5395333,QsJs: 0.587,Kc9h: 0.0002944,KcJc: 0.004,KcJd: 0.39564,KcJs: 0.279459983343,Kd9h: 0.0005876,KdJc: 0.00156689990661,KdJd: 0.402999975979,KdJs: 0.191539988583,Kh9h: 0.002,KhJc: 0.0046959,KhJd: 0.3940653,KhJs: 0.280245,Ks9c: 0.0002944,Ks9h: 0.00151340018041,KsTc: 0.0207962,KsTs: 0.01176,KsJc: 0.0086264,KsJd: 0.4219077,KsJs: 0.385999976993,Ac3c: 0.98699994117,Ac4c: 0.994,Ac5c: 0.940000043972,Ac5d: 0.2736264,Ac5h: 0.270325,Ac5s: 0.274999983609,Ac6c: 0.96603384242,Ac7c: 0.932000111103,Ac7d: 0.36963,Ac7h: 0.37,Ac7s: 0.369999977946,Ac8c: 0.881,Ac8d: 0.51867,Ac8h: 0.528346168508,Ac8s: 0.562174966492,Ac9c: 0.452547,Ac9h: 0.4013982,AcTc: 0.727272656651,AcTd: 0.722985,AcTh: 0.69943495831,AcTs: 0.64056,Ad2d: 0.9960031,Ad3d: 0.999999940395,Ad4d: 0.999999940395,Ad5c: 0.208175,Ad5d: 0.988000117779,Ad5h: 0.269775,Ad5s: 0.274999983609,Ad6d: 0.997,Ad7c: 0.296369982335,Ad7d: 0.993000118375,Ad7h: 0.369999977946,Ad7s: 0.369999977946,Ad8c: 0.433919974136,Ad8d: 0.981000041528,Ad8h: 0.504710469917,Ad8s: 0.54579,Ad9c: 0.36736,Ad9h: 0.407539975709,AdTc: 0.73319,AdTd: 0.411,AdTh: 0.7732251,AdTs: 0.748141755407,Ah2h: 0.9950061,Ah3h: 0.999999940395,Ah4h: 0.999999940395,Ah5c: 0.1837,Ah5d: 0.263725,Ah5h: 0.9850001,Ah5s: 0.274999983609,Ah6h: 0.997,Ah7c: 0.29008,Ah7d: 0.36963,Ah7h: 0.98699994117,Ah7s: 0.369999977946,Ah8c: 0.434050874129,Ah8d: 0.52545,Ah8h: 0.9532859,Ah8s: 0.546355,Ah9c: 0.5904,Ah9h: 0.793999952674,AhTc: 0.730889056436,AhTd: 0.7520301,AhTh: 0.489999970794,AhTs: 0.738684955971,AhJs: 0.0348749979213";
        String player2RangeStr = "3d3c: 0.0485786,3h3c: 0.0657394,3h3d: 0.082119,3s3c: 0.0000326,3s3d: 0.0000651,3s3h: 0.0001535,4d4c: 0.043524,4h4c: 0.062384,4h4d: 0.05412,5c4c: 0.0478918,5d4d: 0.0647833,5d5c: 0.000969,5h4h: 0.0548842,5h5c: 0.001342,5h5d: 0.001496,5s4s: 0.0302064,5s5d: 0.00012,5s5h: 0.000256,6c5c: 0.0960206,6d5d: 0.0879368,6h5h: 0.0906694,6s5s: 0.0898128,7c5c: 0.1437005,7c6c: 0.248976,7d5d: 0.1154251,7d6d: 0.268502,7h5h: 0.1164711,7h6h: 0.302237,7h7d: 0.000028,7s5s: 0.1097276,7s6s: 0.1356,8c6c: 0.153594,8c7c: 0.001,8d6d: 0.16058,8d7d: 0.051,8d8c: 0.005896,8h6h: 0.193328,8h7h: 0.053,8h8c: 0.006176,8h8d: 0.093086,8s6s: 0.10788,8s7s: 0.015984,8s8c: 0.002688,8s8d: 0.01965,8s8h: 0.0265,9c7c: 0.000239,9c8c: 0.005363,9c8d: 0.0009564,9c8h: 0.0016472,9c8s: 0.0002378,9h6h: 0.001464,9h7h: 0.00072,9h8c: 0.002307,9h8d: 0.0021506,9h8h: 0.08652,9h8s: 0.0004756,9h9c: 0.000804,Tc6c: 0.050619,Tc7c: 0.116883,Tc8c: 0.188961,Tc8d: 0.0664541,Tc8h: 0.062231,Tc8s: 0.0568936,Tc9c: 0.082615,Tc9h: 0.095823,Td6d: 0.070325,Td7d: 0.170829,Td8c: 0.0560771,Td8d: 0.19261,Td8h: 0.0568415,Td8s: 0.052422,Td9c: 0.054168,Td9h: 0.079275,Th6h: 0.072496,Th7h: 0.181818,Th8c: 0.0528316,Th8d: 0.0603856,Th8h: 0.207836,Th8s: 0.0485322,Th9c: 0.059534,Th9h: 0.058597,Ts6s: 0.0262088,Ts7s: 0.183632,Ts8c: 0.0549143,Ts8d: 0.0609652,Ts8h: 0.0547734,Ts8s: 0.217321,Ts9c: 0.056317,Ts9h: 0.068228,Jc9c: 0.139125,Jc9h: 0.27028,Jd9c: 0.141546,Jd9h: 0.2544456,JdJc: 0.41,Js9c: 0.092742,Js9h: 0.198543,JsJc: 0.401,JsJd: 0.3866131,Qc3c: 0.092162,Qc4c: 0.07383,Qc5c: 0.050616,Qc6c: 0.000984,Qc7c: 0.00144,Qc8c: 0.122,Qc8d: 0.036515,Qc8h: 0.043055,Qc8s: 0.050685,QcTc: 0.144672,QcTd: 0.106752,QcTh: 0.085169,QcTs: 0.089076,Qd2d: 0.1144092,Qd3d: 0.07266,Qd4d: 0.054528,Qd5d: 0.023529,Qd6d: 0.006308,Qd7d: 0.010527,Qd8c: 0.0981,Qd8d: 0.222,Qd8h: 0.03597,Qd8s: 0.04469,QdTc: 0.103416,QdTd: 0.114912,QdTh: 0.089091,QdTs: 0.101936,Qh2h: 0.1064217,Qh3h: 0.06929,Qh4h: 0.057409,Qh5h: 0.031208,Qh6h: 0.003737,Qh7h: 0.00981,Qh8c: 0.095375,Qh8d: 0.02834,Qh8h: 0.217,Qh8s: 0.05559,QhTc: 0.082867,QhTd: 0.096232,QhTh: 0.156055,QhTs: 0.088006,Qs3s: 0.00322,Qs4s: 0.00266,Qs5s: 0.01056,Qs6s: 0.000296,Qs7s: 0.00065,Qs8c: 0.128075,Qs8d: 0.07085,Qs8h: 0.06431,Qs8s: 0.229,QsTc: 0.0825,QsTd: 0.100016,QsTh: 0.083076,QsTs: 0.14036,Kc3c: 0.127296,Kc4c: 0.095504,Kc5c: 0.109005,Kc6c: 0.00714,Kc7c: 0.03408,Kc7d: 0.0150446,Kc7h: 0.0147793,Kc7s: 0.0002576,Kc8c: 0.12927,Kc8d: 0.1204847,Kc8h: 0.1590003,Kc8s: 0.0417831,KcTc: 0.007956,KcTd: 0.040397,KcTh: 0.02013,KcTs: 0.00133,Kd2d: 0.004324,Kd3d: 0.087216,Kd4d: 0.060634,Kd5d: 0.050375,Kd6d: 0.0065,Kd7c: 0.0055448,Kd7d: 0.127335,Kd7h: 0.018183,Kd7s: 0.0012012,Kd8c: 0.0842325,Kd8d: 0.18625,Kd8h: 0.1815978,Kd8s: 0.0706352,KdTc: 0.02304,KdTd: 0.034959,KdTh: 0.02625,KdTs: 0.008476,Kh2h: 0.00091,Kh3h: 0.086432,Kh4h: 0.061908,Kh5h: 0.05418,Kh6h: 0.008085,Kh7c: 0.0049795,Kh7d: 0.0134224,Kh7h: 0.127908,Kh7s: 0.0007328,Kh8c: 0.0749026,Kh8d: 0.1202603,Kh8h: 0.253464,Kh8s: 0.057089,KhTc: 0.00832,KhTd: 0.038406,KhTh: 0.03528,KhTs: 0.00266,Ks3s: 0.020453,Ks4s: 0.018997,Ks5s: 0.00711,Ks6s: 0.00342,Ks7c: 0.012011,Ks7d: 0.0175286,Ks7h: 0.0228507,Ks7s: 0.014322,Ks8c: 0.1031187,Ks8d: 0.1485693,Ks8h: 0.2174901,Ks8s: 0.109863,KsTc: 0.027048,KsTd: 0.059899,KsTh: 0.03772,KsTs: 0.026726,Ac7c: 0.024167,Ac7d: 0.0039,Ac7h: 0.001488,Ac7s: 0.039,Ac8c: 0.18232,Ac8d: 0.204022,Ac8h: 0.16497,Ac8s: 0.19845,Ac9c: 0.260154,Ac9h: 0.397824,AcTc: 0.220337,AcTd: 0.2285425,AcTh: 0.222777,AcTs: 0.222777,AcJc: 0.045916,AcJd: 0.067034,AcJs: 0.083655,AcQc: 0.097647,AcQd: 0.096444,AcQh: 0.0954755,AcQs: 0.1056224,AcKc: 0.131604,AcKd: 0.121756,AcKh: 0.126746,AcKs: 0.138583,Ad2d: 0.000124,Ad6d: 0.008257,Ad7c: 0.000672,Ad7d: 0.015517,Ad7h: 0.004446,Ad7s: 0.055596,Ad8c: 0.1638,Ad8d: 0.185969,Ad8h: 0.171339,Ad8s: 0.195951,Ad9c: 0.246156,Ad9h: 0.398278,AdTc: 0.218348,AdTd: 0.17658,AdTh: 0.2310729,AdTs: 0.224503,AdJc: 0.0417,AdJd: 0.137547,AdJs: 0.0785714,AdQc: 0.076648,AdQd: 0.119196,AdQh: 0.080256,AdQs: 0.095811,AdKc: 0.101694,AdKd: 0.1655025,AdKh: 0.0977063,AdKs: 0.111552,Ah2h: 0.000183,Ah6h: 0.019256,Ah7c: 0.000486,Ah7d: 0.000729,Ah7h: 0.004617,Ah7s: 0.055671,Ah8c: 0.152468,Ah8d: 0.167832,Ah8h: 0.167606,Ah8s: 0.176715,Ah9c: 0.2288,Ah9h: 0.425,AhTc: 0.221832,AhTd: 0.242,AhTh: 0.213075,AhTs: 0.233007,AhJc: 0.074672,AhJd: 0.076912,AhJs: 0.0968,AhQc: 0.0651,AhQd: 0.061929,AhQh: 0.171513,AhQs: 0.075614,AhKc: 0.0818362,AhKd: 0.0698601,AhKh: 0.18463,AhKs: 0.0926286";


        int[] initialBoard = new int[]{
                Card.strCard2int("As"),
                Card.strCard2int("Jh"),
                Card.strCard2int("9d"),
                Card.strCard2int("2c"),
                Card.strCard2int("9s")
        };

        PrivateCards[] player1Range = PrivateRangeConverter.rangeStr2Cards(player1RangeStr,initialBoard);
        PrivateCards[] player2Range = PrivateRangeConverter.rangeStr2Cards(player2RangeStr,initialBoard);

        String logfile_name = "src/test/resources/outputs/outputs_log.txt";
        Solver solver = new CfrPlusRiverSolver(game_tree);
        SolveConfig solveConfig = new SolveConfig(
                player1Range
                , player2Range
                , initialBoard
                , compairer
                , deck
                ,100
                ,false
                , 10
                ,logfile_name
                , DiscountedCfrTrainable.class
                , MonteCarolAlg.NONE,
                0.0);
        solver.train(solveConfig);

        JSONObject json1 = game_tree.toJson();
        JSONObject json2 = game_tree.dumps(false);
        ObjectMapper jackson = new ObjectMapper();
        JsonNode beforeNode = jackson.readTree(json1.toString());
        JsonNode afterNode = jackson.readTree(json2.toString());
        JsonNode patchNode = JsonDiff.asJson(beforeNode, afterNode);
        Assert.assertEquals("[]", patchNode.toString());
    }
}
