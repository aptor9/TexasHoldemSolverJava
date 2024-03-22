package icybee.solver;

import icybee.solver.compairer.Compairer;
import icybee.solver.exceptions.BoardNotFoundException;
import icybee.solver.ranges.PrivateCards;
import icybee.solver.solver.*;
import icybee.solver.trainable.DiscountedCfrTrainable;
import icybee.solver.utils.PrivateRangeConverter;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertTrue;

/**
 *
 * Unit test
 */
public class TexasHoldemSolverTest
{
    /**
     * Rigorous Test :-)
     */
    static Compairer compairer = null;
    static Deck deck = null;

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

    @Before
    public void loadEnvironmentsTest()
    {
        String config_name = "yamls/rule_holdem_simple.yaml";
        //String config_name = "yamls/rule_shortdeck_turnriversolver.yaml";
        //String config_name = "yamls/rule_shortdeck_turnsolver.yaml";
        //String config_name = "yamls/rule_shortdeck_turnsolver_withallin.yaml";
        //String config_name = "yamls/rule_shortdeck_flopsolver.yaml";
        Config config = this.loadConfig(config_name);

        if(TexasHoldemSolverTest.compairer == null) {
            try {
                TexasHoldemSolverTest.compairer = SolverEnvironment.compairerFromConfig(config);
                TexasHoldemSolverTest.deck = SolverEnvironment.deckFromConfig(config);
            } catch (Exception e) {
                e.printStackTrace();
                assertTrue(false);
            }
        }

    }

    @Test
    public void cardCompairLGTest(){
        try {
            List<Card> board = Arrays.asList(
                    new Card("6c"),
                    new Card("6d"),
                    new Card("7c"),
                    new Card("7d"),
                    new Card("8s")
            );
            List<Card> private1 = Arrays.asList(
                    new Card("6h"),
                    new Card("6s")
            );
            List<Card> private2 = Arrays.asList(
                    new Card("9c"),
                    new Card("9s")
            );

            Compairer.CompairResult cr = TexasHoldemSolverTest.compairer.compair(private1,private2,board);
            System.out.println(cr);
            assertTrue(cr == Compairer.CompairResult.LARGER);
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }

    @Test
    public void cardCompairEQTest(){
        try{
            List<Card> board = Arrays.asList(
                    new Card("6c"),
                    new Card("6d"),
                    new Card("7c"),
                    new Card("7d"),
                    new Card("8s")
            );
            List<Card> private1 = Arrays.asList(
                    new Card("8h"),
                    new Card("7s")
            );
            List<Card> private2 = Arrays.asList(
                    new Card("8d"),
                    new Card("7h")
            );

            Compairer.CompairResult cr = TexasHoldemSolverTest.compairer.compair(private1,private2,board);
            System.out.println(cr);
            assertTrue(cr == Compairer.CompairResult.EQUAL);
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }

    @Test
    public void cardCompairSMTest(){
        try{
            List<Card> board = Arrays.asList(
                    new Card("6c"),
                    new Card("6d"),
                    new Card("7c"),
                    new Card("7d"),
                    new Card("8s")
            );
            List<Card> private1 = Arrays.asList(
                    new Card("6h"),
                    new Card("7s")
            );
            List<Card> private2 = Arrays.asList(
                    new Card("8h"),
                    new Card("7h")
            );

            Compairer.CompairResult cr = TexasHoldemSolverTest.compairer.compair(private1,private2,board);
            System.out.println(cr);
            assertTrue(cr == Compairer.CompairResult.SMALLER);
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }

    @Test
    public void getRankTest(){
        List<Card> board = Arrays.asList(
                new Card("8d"),
                new Card("9d"),
                new Card("9s"),
                new Card("Jd"),
                new Card("Jh")
        );
        List<Card> private_cards = Arrays.asList(
                new Card("6h"),
                new Card("7s")
        );

        int rank = TexasHoldemSolverTest.compairer.get_rank(private_cards,board);
        System.out.println(rank);
        assertTrue(rank > 0);
    }

    @Test
    public void printTreeTest(){
        String config_name = "yamls/rule_holdem_simple.yaml";
        //String config_name = "yamls/rule_shortdeck_turnriversolver.yaml";
        //String config_name = "yamls/rule_shortdeck_turnsolver.yaml";
        //String config_name = "yamls/rule_shortdeck_turnsolver_withallin.yaml";
        //String config_name = "yamls/rule_shortdeck_flopsolver.yaml";
        Config config = this.loadConfig(config_name);
        GameTree game_tree = SolverEnvironment.gameTreeFromConfig(config, TexasHoldemSolverTest.deck);
        System.out.println("The game tree :");
        try {
            game_tree.printTree(-1);
        }catch(Exception e){
            e.printStackTrace();
            assertTrue(false);
        }
    }

    @Test
    public void printTreeLimitDepthTest(){
        String config_name = "yamls/rule_holdem_simple.yaml";
        //String config_name = "yamls/rule_shortdeck_turnriversolver.yaml";
        //String config_name = "yamls/rule_shortdeck_turnsolver.yaml";
        //String config_name = "yamls/rule_shortdeck_turnsolver_withallin.yaml";
        //String config_name = "yamls/rule_shortdeck_flopsolver.yaml";
        Config config = this.loadConfig(config_name);
        GameTree game_tree = SolverEnvironment.gameTreeFromConfig(config, TexasHoldemSolverTest.deck);
        System.out.println("The depth limit game tree :");
        try {
            game_tree.printTree(2);
        }catch(Exception e){
            e.printStackTrace();
            assertTrue(false);
        }
    }

    @Test
    public void cardConvertTest(){
        System.out.println("cardConvertTest");
        try {
            Card card = new Card("6c");
            int card_int = Card.card2int(card);

            Card card_rev = new Card(Card.intCard2Str(card_int));
            int card_int_rev = Card.card2int(card_rev);
            System.out.println(card_int);
            System.out.println(card_int_rev);
            assert(card_int == card_int_rev);
        }catch (Exception e){
            e.printStackTrace();
            assertTrue(false);
        }
        System.out.println("end of cardConvertTest");
    }

    @Test
    public void cardsIntegerConvertTest(){
        Card[] board = {
                new Card("6c"),
                new Card("6d"),
                new Card("7c"),
                new Card("7d"),
                new Card("8s"),
                new Card("6h"),
                new Card("7s")
        };
        try {
            long board_int = Card.boardCards2long(board);
            Card[] board_cards = Card.long2boardCards(board_int);
            long board_int_rev = Card.boardCards2long(board_cards);

            for(Card i:board)
                System.out.println(i.getCard());
            System.out.println();
            for(Card i:board_cards)
                System.out.println(i.getCard());

            System.out.println(board_int);
            System.out.println(board_int_rev);
            assert(board_int == board_int_rev);
        }catch (Exception e){
            e.printStackTrace();
            assertTrue(false);
        }
    }

    @Test
    public void cardsIntegerConvertNETest(){
        Card[] board1 = {
                new Card("6c"),
                new Card("6d"),
                new Card("7c"),
                new Card("7d"),
                new Card("8s"),
                new Card("6h"),
                new Card("7s")
        };
        Card[] board2 = {
                new Card("6c"),
                new Card("6d"),
                new Card("7c"),
                new Card("7d"),
                new Card("9s"),
                new Card("6h"),
                new Card("7s")
        };
        try {
            long board_int1 = Card.boardCards2long(board1);
            long board_int2 = Card.boardCards2long(board2);
            assertTrue(board_int1 != board_int2);

        }catch (Exception e){
            e.printStackTrace();
            assertTrue(false);
        }
    }

    @Test
    public void compaierEquivlentTest(){
        System.out.println("compaierEquivlentTest");
        List<Card> board1_public = Arrays.asList(
                new Card("6c"),
                new Card("6d"),
                new Card("7c"),
                new Card("7d"),
                new Card("8s")
        );
        List<Card> board1_private = Arrays.asList(
                new Card("6h"),
                new Card("7s")
        );
        int[] board2_public = {
                (new Card("6c").getCardInt()),
                (new Card("6d").getCardInt()),
                (new Card("7c").getCardInt()),
                (new Card("7d").getCardInt()),
                (new Card("8s").getCardInt()),
        };
        int[] board2_private = {
                (new Card("6h").getCardInt()),
                (new Card("7s").getCardInt())
        };
        try {
            long board_int1 = compairer.get_rank(board1_private,board1_public);
            long board_int2 = compairer.get_rank(board2_private,board2_public);
            System.out.println(board_int1);
            System.out.println(board_int2);
            assertTrue(board_int1 == board_int2);

        }catch (Exception e){
            e.printStackTrace();
            assertTrue(false);
        }
        System.out.println("end compaierEquivlentTest");
    }

    @Test
    public void cfrSolverTest() throws Exception{
        System.out.println("solverTest");

        String config_name = "yamls/rule_holdem_simple.yaml";
        //String config_name = "yamls/rule_shortdeck_turnriversolver.yaml";
        //String config_name = "yamls/rule_shortdeck_turnsolver.yaml";
        //String config_name = "yamls/rule_shortdeck_turnsolver_withallin.yaml";
        //String config_name = "yamls/rule_shortdeck_flopsolver.yaml";
        Config config = this.loadConfig(config_name);
        GameTree game_tree = SolverEnvironment.gameTreeFromConfig(config, TexasHoldemSolverTest.deck);

//        String player1RangeStr = "AA,KK,QQ,JJ,TT,99,88,77,66,AK,AQ,AJ,AT,A9,A8,A7,A6,KQ,KJ,KT,K9,K8,K7,K6,QJ,QT,Q9,Q8,Q7,Q6,JT,J9,J8,J7,J6,T9,T8,T7,T6,98,97,96,43,64,84";
//        String player2RangeStr = "AA,KK,QQ,JJ,TT,99,88,77,66,AK,AQ,AJ,AT,A9,A8,A7,A6,KQ,KJ,KT,K9,K8,K7,K6,QJ,QT,Q9,Q8,Q7,Q6,JT,J9,J8,J7,J6,T9,T8,T7,T6,98,97,96,43,64,84";

        String player1RangeStr = "Jc5c: 0.0041958,Jc6c: 0.00598499964327,Jc9c: 0.049461,Jd5d: 0.1258428,Jd6d: 0.102221193907,Js5s: 0.193959388439,Js6s: 0.6145279,Js7s: 0.0628723,Js9c: 0.0515676969263,Js9h: 0.0008844,Qc9c: 0.000944,Qc9h: 0.0677125,QcTc: 0.29744,QcTd: 0.1696662,QcTh: 0.1379181,QcTs: 0.168507589956,QcJc: 0.283859983081,QcJd: 0.5903636,QcJs: 0.443982873537,Qd9c: 0.000227099986464,Qd9h: 0.0290965,QdTc: 0.1243321,QdTd: 0.14159999156,QdTh: 0.0923404,QdTs: 0.1176243,QdJc: 0.0901529892529,QdJd: 0.729537956516,QdJs: 0.377906477475,Qh9c: 0.0001673,Qh9h: 0.133007992072,QhTc: 0.1110481,QhTd: 0.118914,QhTh: 0.184664,QhTs: 0.1160136,QhJc: 0.139595991679,QhJd: 0.5613431,QhJs: 0.4186267,Qs9c: 0.000240099985689,Qs9h: 0.0290975,QsTc: 0.0833066950345,QsTd: 0.0730891,QsTh: 0.0385236,QsTs: 0.126947992433,QsJc: 0.1291171,QsJd: 0.5395333,QsJs: 0.587,Kc9h: 0.0002944,KcJc: 0.004,KcJd: 0.39564,KcJs: 0.279459983343,Kd9h: 0.0005876,KdJc: 0.00156689990661,KdJd: 0.402999975979,KdJs: 0.191539988583,Kh9h: 0.002,KhJc: 0.0046959,KhJd: 0.3940653,KhJs: 0.280245,Ks9c: 0.0002944,Ks9h: 0.00151340018041,KsTc: 0.0207962,KsTs: 0.01176,KsJc: 0.0086264,KsJd: 0.4219077,KsJs: 0.385999976993,Ac3c: 0.98699994117,Ac4c: 0.994,Ac5c: 0.940000043972,Ac5d: 0.2736264,Ac5h: 0.270325,Ac5s: 0.274999983609,Ac6c: 0.96603384242,Ac7c: 0.932000111103,Ac7d: 0.36963,Ac7h: 0.37,Ac7s: 0.369999977946,Ac8c: 0.881,Ac8d: 0.51867,Ac8h: 0.528346168508,Ac8s: 0.562174966492,Ac9c: 0.452547,Ac9h: 0.4013982,AcTc: 0.727272656651,AcTd: 0.722985,AcTh: 0.69943495831,AcTs: 0.64056,Ad2d: 0.9960031,Ad3d: 0.999999940395,Ad4d: 0.999999940395,Ad5c: 0.208175,Ad5d: 0.988000117779,Ad5h: 0.269775,Ad5s: 0.274999983609,Ad6d: 0.997,Ad7c: 0.296369982335,Ad7d: 0.993000118375,Ad7h: 0.369999977946,Ad7s: 0.369999977946,Ad8c: 0.433919974136,Ad8d: 0.981000041528,Ad8h: 0.504710469917,Ad8s: 0.54579,Ad9c: 0.36736,Ad9h: 0.407539975709,AdTc: 0.73319,AdTd: 0.411,AdTh: 0.7732251,AdTs: 0.748141755407,Ah2h: 0.9950061,Ah3h: 0.999999940395,Ah4h: 0.999999940395,Ah5c: 0.1837,Ah5d: 0.263725,Ah5h: 0.9850001,Ah5s: 0.274999983609,Ah6h: 0.997,Ah7c: 0.29008,Ah7d: 0.36963,Ah7h: 0.98699994117,Ah7s: 0.369999977946,Ah8c: 0.434050874129,Ah8d: 0.52545,Ah8h: 0.9532859,Ah8s: 0.546355,Ah9c: 0.5904,Ah9h: 0.793999952674,AhTc: 0.730889056436,AhTd: 0.7520301,AhTh: 0.489999970794,AhTs: 0.738684955971,AhJs: 0.0348749979213";
        String player2RangeStr = "3d3c: 0.0485786,3h3c: 0.0657394,3h3d: 0.082119,3s3c: 0.0000326,3s3d: 0.0000651,3s3h: 0.0001535,4d4c: 0.043524,4h4c: 0.062384,4h4d: 0.05412,5c4c: 0.0478918,5d4d: 0.0647833,5d5c: 0.000969,5h4h: 0.0548842,5h5c: 0.001342,5h5d: 0.001496,5s4s: 0.0302064,5s5d: 0.00012,5s5h: 0.000256,6c5c: 0.0960206,6d5d: 0.0879368,6h5h: 0.0906694,6s5s: 0.0898128,7c5c: 0.1437005,7c6c: 0.248976,7d5d: 0.1154251,7d6d: 0.268502,7h5h: 0.1164711,7h6h: 0.302237,7h7d: 0.000028,7s5s: 0.1097276,7s6s: 0.1356,8c6c: 0.153594,8c7c: 0.001,8d6d: 0.16058,8d7d: 0.051,8d8c: 0.005896,8h6h: 0.193328,8h7h: 0.053,8h8c: 0.006176,8h8d: 0.093086,8s6s: 0.10788,8s7s: 0.015984,8s8c: 0.002688,8s8d: 0.01965,8s8h: 0.0265,9c7c: 0.000239,9c8c: 0.005363,9c8d: 0.0009564,9c8h: 0.0016472,9c8s: 0.0002378,9h6h: 0.001464,9h7h: 0.00072,9h8c: 0.002307,9h8d: 0.0021506,9h8h: 0.08652,9h8s: 0.0004756,9h9c: 0.000804,Tc6c: 0.050619,Tc7c: 0.116883,Tc8c: 0.188961,Tc8d: 0.0664541,Tc8h: 0.062231,Tc8s: 0.0568936,Tc9c: 0.082615,Tc9h: 0.095823,Td6d: 0.070325,Td7d: 0.170829,Td8c: 0.0560771,Td8d: 0.19261,Td8h: 0.0568415,Td8s: 0.052422,Td9c: 0.054168,Td9h: 0.079275,Th6h: 0.072496,Th7h: 0.181818,Th8c: 0.0528316,Th8d: 0.0603856,Th8h: 0.207836,Th8s: 0.0485322,Th9c: 0.059534,Th9h: 0.058597,Ts6s: 0.0262088,Ts7s: 0.183632,Ts8c: 0.0549143,Ts8d: 0.0609652,Ts8h: 0.0547734,Ts8s: 0.217321,Ts9c: 0.056317,Ts9h: 0.068228,Jc9c: 0.139125,Jc9h: 0.27028,Jd9c: 0.141546,Jd9h: 0.2544456,JdJc: 0.41,Js9c: 0.092742,Js9h: 0.198543,JsJc: 0.401,JsJd: 0.3866131,Qc3c: 0.092162,Qc4c: 0.07383,Qc5c: 0.050616,Qc6c: 0.000984,Qc7c: 0.00144,Qc8c: 0.122,Qc8d: 0.036515,Qc8h: 0.043055,Qc8s: 0.050685,QcTc: 0.144672,QcTd: 0.106752,QcTh: 0.085169,QcTs: 0.089076,Qd2d: 0.1144092,Qd3d: 0.07266,Qd4d: 0.054528,Qd5d: 0.023529,Qd6d: 0.006308,Qd7d: 0.010527,Qd8c: 0.0981,Qd8d: 0.222,Qd8h: 0.03597,Qd8s: 0.04469,QdTc: 0.103416,QdTd: 0.114912,QdTh: 0.089091,QdTs: 0.101936,Qh2h: 0.1064217,Qh3h: 0.06929,Qh4h: 0.057409,Qh5h: 0.031208,Qh6h: 0.003737,Qh7h: 0.00981,Qh8c: 0.095375,Qh8d: 0.02834,Qh8h: 0.217,Qh8s: 0.05559,QhTc: 0.082867,QhTd: 0.096232,QhTh: 0.156055,QhTs: 0.088006,Qs3s: 0.00322,Qs4s: 0.00266,Qs5s: 0.01056,Qs6s: 0.000296,Qs7s: 0.00065,Qs8c: 0.128075,Qs8d: 0.07085,Qs8h: 0.06431,Qs8s: 0.229,QsTc: 0.0825,QsTd: 0.100016,QsTh: 0.083076,QsTs: 0.14036,Kc3c: 0.127296,Kc4c: 0.095504,Kc5c: 0.109005,Kc6c: 0.00714,Kc7c: 0.03408,Kc7d: 0.0150446,Kc7h: 0.0147793,Kc7s: 0.0002576,Kc8c: 0.12927,Kc8d: 0.1204847,Kc8h: 0.1590003,Kc8s: 0.0417831,KcTc: 0.007956,KcTd: 0.040397,KcTh: 0.02013,KcTs: 0.00133,Kd2d: 0.004324,Kd3d: 0.087216,Kd4d: 0.060634,Kd5d: 0.050375,Kd6d: 0.0065,Kd7c: 0.0055448,Kd7d: 0.127335,Kd7h: 0.018183,Kd7s: 0.0012012,Kd8c: 0.0842325,Kd8d: 0.18625,Kd8h: 0.1815978,Kd8s: 0.0706352,KdTc: 0.02304,KdTd: 0.034959,KdTh: 0.02625,KdTs: 0.008476,Kh2h: 0.00091,Kh3h: 0.086432,Kh4h: 0.061908,Kh5h: 0.05418,Kh6h: 0.008085,Kh7c: 0.0049795,Kh7d: 0.0134224,Kh7h: 0.127908,Kh7s: 0.0007328,Kh8c: 0.0749026,Kh8d: 0.1202603,Kh8h: 0.253464,Kh8s: 0.057089,KhTc: 0.00832,KhTd: 0.038406,KhTh: 0.03528,KhTs: 0.00266,Ks3s: 0.020453,Ks4s: 0.018997,Ks5s: 0.00711,Ks6s: 0.00342,Ks7c: 0.012011,Ks7d: 0.0175286,Ks7h: 0.0228507,Ks7s: 0.014322,Ks8c: 0.1031187,Ks8d: 0.1485693,Ks8h: 0.2174901,Ks8s: 0.109863,KsTc: 0.027048,KsTd: 0.059899,KsTh: 0.03772,KsTs: 0.026726,Ac7c: 0.024167,Ac7d: 0.0039,Ac7h: 0.001488,Ac7s: 0.039,Ac8c: 0.18232,Ac8d: 0.204022,Ac8h: 0.16497,Ac8s: 0.19845,Ac9c: 0.260154,Ac9h: 0.397824,AcTc: 0.220337,AcTd: 0.2285425,AcTh: 0.222777,AcTs: 0.222777,AcJc: 0.045916,AcJd: 0.067034,AcJs: 0.083655,AcQc: 0.097647,AcQd: 0.096444,AcQh: 0.0954755,AcQs: 0.1056224,AcKc: 0.131604,AcKd: 0.121756,AcKh: 0.126746,AcKs: 0.138583,Ad2d: 0.000124,Ad6d: 0.008257,Ad7c: 0.000672,Ad7d: 0.015517,Ad7h: 0.004446,Ad7s: 0.055596,Ad8c: 0.1638,Ad8d: 0.185969,Ad8h: 0.171339,Ad8s: 0.195951,Ad9c: 0.246156,Ad9h: 0.398278,AdTc: 0.218348,AdTd: 0.17658,AdTh: 0.2310729,AdTs: 0.224503,AdJc: 0.0417,AdJd: 0.137547,AdJs: 0.0785714,AdQc: 0.076648,AdQd: 0.119196,AdQh: 0.080256,AdQs: 0.095811,AdKc: 0.101694,AdKd: 0.1655025,AdKh: 0.0977063,AdKs: 0.111552,Ah2h: 0.000183,Ah6h: 0.019256,Ah7c: 0.000486,Ah7d: 0.000729,Ah7h: 0.004617,Ah7s: 0.055671,Ah8c: 0.152468,Ah8d: 0.167832,Ah8h: 0.167606,Ah8s: 0.176715,Ah9c: 0.2288,Ah9h: 0.425,AhTc: 0.221832,AhTd: 0.242,AhTh: 0.213075,AhTs: 0.233007,AhJc: 0.074672,AhJd: 0.076912,AhJs: 0.0968,AhQc: 0.0651,AhQd: 0.061929,AhQh: 0.171513,AhQs: 0.075614,AhKc: 0.0818362,AhKd: 0.0698601,AhKh: 0.18463,AhKs: 0.0926286";


//         String player1RangeStr = "87";
//         String player2RangeStr = "87";
        /*
        case 'c': return 0; // 梅花
        case 'd': return 1; // 方块
        case 'h': return 2; // 红桃
        case 's': return 3; // 黑桃
         */

        int[] initialBoard = new int[]{
                Card.strCard2int("As"),
                Card.strCard2int("Jh"),
                Card.strCard2int("9d"),
                Card.strCard2int("2c"),
                Card.strCard2int("9s")
        };

        PrivateCards[] player1Range = PrivateRangeConverter.rangeStr2Cards(player1RangeStr,initialBoard);
        PrivateCards[] player2Range = PrivateRangeConverter.rangeStr2Cards(player2RangeStr,initialBoard);
        //PrivateCards[] player1Range = new PrivateCards[]{new PrivateCards(Card.strCard2int("As"),Card.strCard2int("Ad"),1)};

        /*
        PrivateCards[] player1Range = new PrivateCards[]{
                new PrivateCards(Card.strCard2int("As"),Card.strCard2int("Ad"),1)
                ,new PrivateCards(Card.strCard2int("8h"),Card.strCard2int("7d"),1)
        };
        PrivateCards[] player2Range = new PrivateCards[]{
                //new PrivateCards(Card.strCard2int("6d"),Card.strCard2int("8s"),1)
                new PrivateCards(Card.strCard2int("6d"),Card.strCard2int("7d"),1)
                ,new PrivateCards(Card.strCard2int("6s"),Card.strCard2int("6d"),1)
        };
         */

        String logfile_name = "src/test/resources/outputs/outputs_log.txt";
        Solver solver = new CfrPlusRiverSolver(game_tree);
        SolveConfig solveConfig = new SolveConfig(
                player1Range
                , player2Range
                , initialBoard
                , TexasHoldemSolverTest.compairer
                , TexasHoldemSolverTest.deck
                ,100
                ,false
                , 10
                ,logfile_name
                , DiscountedCfrTrainable.class
                ,MonteCarolAlg.NONE,
                0.0);
        solver.train(solveConfig);

        String strategy_json = solver.getTree().dumps(false).toString(4);

        String strategy_fname = "src/test/resources/outputs/outputs_river_strategy.json";

        File output_file = new File(strategy_fname);
        FileWriter writer = new FileWriter(output_file);
        writer.write(strategy_json);
        writer.flush();
        writer.close();

        System.out.println("end solverTest");
    }

    @Test
    public void cfrHoldemFlopSolverTest() throws Exception{
        System.out.println("start holdem_flop_solver_test");

        // String config_name = "yamls/rule_holdem_simple.yaml";
        //String config_name = "yamls/rule_shortdeck_turnriversolver.yaml";
        //String config_name = "yamls/rule_shortdeck_turnsolver.yaml";
        //String config_name = "yamls/rule_shortdeck_turnsolver_withallin.yaml";
        //String config_name = "yamls/rule_shortdeck_flopsolver.yaml";
        String config_name = "yamls/rule_holdem_flop.yaml";
        Config config = this.loadConfig(config_name);
        GameTree game_tree = SolverEnvironment.gameTreeFromConfig(config, TexasHoldemSolverTest.deck);

//        String player1RangeStr = "AA,KK,QQ,JJ,TT,99,88,77,66,AK,AQ,AJ,AT,A9,A8,A7,A6,KQ,KJ,KT,K9,K8,K7,K6,QJ,QT,Q9,Q8,Q7,Q6,JT,J9,J8,J7,J6,T9,T8,T7,T6,98,97,96,43,64,84";
//        String player2RangeStr = "AA,KK,QQ,JJ,TT,99,88,77,66,AK,AQ,AJ,AT,A9,A8,A7,A6,KQ,KJ,KT,K9,K8,K7,K6,QJ,QT,Q9,Q8,Q7,Q6,JT,J9,J8,J7,J6,T9,T8,T7,T6,98,97,96,43,64,84";
        String player1RangeStr = "2d2c: 1,2h2c: 1,2h2d: 1,2s2c: 1,2s2d: 1,2s2h: 1,3d3c: 1,3h3c: 1,3h3d: 1,3s3c: 1,3s3d: 1,3s3h: 1,4c3c: 1,4d3d: 1,4d4c: 1,4h3h: 1,4h4c: 1,4h4d: 1,4s3s: 1,4s4c: 1,4s4d: 1,4s4h: 1,5c3c: 1,5c4c: 1,5d3d: 1,5d4d: 1,5d5c: 1,5h3h: 1,5h4h: 1,5h5c: 1,5h5d: 1,5s3s: 1,5s4s: 1,5s5c: 1,5s5d: 1,5s5h: 1,6c4c: 1,6c5c: 1,6d4d: 1,6d5d: 1,6d6c: 1,6h4h: 1,6h5h: 1,6h6c: 1,6h6d: 1,6s4s: 1,6s5s: 1,6s6c: 1,6s6d: 1,6s6h: 1,7c4c: 0.365,7c5c: 1,7c6c: 1,7d4d: 0.365,7d5d: 1,7d6d: 1,7d7c: 0.885,7h4h: 0.365,7h5h: 1,7h6h: 1,7h7c: 0.885,7h7d: 0.885,7s4s: 0.365,7s5s: 1,7s6s: 1,7s7c: 0.885,7s7d: 0.885,7s7h: 0.885,8c5c: 0.705,8c6c: 1,8c7c: 1,8d5d: 0.705,8d6d: 1,8d7d: 1,8d8c: 0.09,8h5h: 0.705,8h6h: 1,8h7h: 1,8h8c: 0.09,8h8d: 0.09,8s5s: 0.705,8s6s: 1,8s7s: 1,8s8c: 0.09,8s8d: 0.09,8s8h: 0.09,9c6c: 1,9c7c: 1,9c8c: 1,9h6h: 1,9h7h: 1,9h8h: 1,9s6s: 1,9s7s: 1,9s8s: 1,Tc6c: 0.47,Tc7c: 1,Tc8c: 1,Tc9c: 1,Tc9h: 0.345,Tc9s: 0.345,Td6d: 0.47,Td7d: 1,Td8d: 1,Td9c: 0.345,Td9h: 0.345,Td9s: 0.345,Th6h: 0.47,Th7h: 1,Th8h: 1,Th9c: 0.345,Th9h: 1,Th9s: 0.345,Ts6s: 0.47,Ts7s: 1,Ts8s: 1,Ts9c: 0.345,Ts9h: 0.345,Ts9s: 1,Jc5c: 0.28,Jc6c: 0.665,Jc7c: 0.545,Jc8c: 0.865,Jc9c: 1,Jc9h: 0.205,Jc9s: 0.205,JcTc: 0.955,JcTd: 0.6,JcTh: 0.6,JcTs: 0.6,Jd5d: 0.28,Jd6d: 0.665,Jd7d: 0.545,Jd8d: 0.865,Jd9c: 0.205,Jd9h: 0.205,Jd9s: 0.205,JdTc: 0.6,JdTd: 0.955,JdTh: 0.6,JdTs: 0.6,Js5s: 0.28,Js6s: 0.665,Js7s: 0.545,Js8s: 0.865,Js9c: 0.205,Js9h: 0.205,Js9s: 1,JsTc: 0.6,JsTd: 0.6,JsTh: 0.6,JsTs: 0.955,Qc2c: 0.74,Qc3c: 0.33,Qc4c: 0.5,Qc5c: 0.735,Qc6c: 1,Qc7c: 1,Qc8c: 1,Qc9c: 1,Qc9h: 0.265,Qc9s: 0.265,QcTc: 1,QcTd: 0.615,QcTh: 0.615,QcTs: 0.615,QcJc: 1,QcJd: 0.71,QcJs: 0.71,Qd2d: 0.74,Qd3d: 0.33,Qd4d: 0.5,Qd5d: 0.735,Qd6d: 1,Qd7d: 1,Qd8d: 1,Qd9c: 0.265,Qd9h: 0.265,Qd9s: 0.265,QdTc: 0.615,QdTd: 1,QdTh: 0.615,QdTs: 0.615,QdJc: 0.71,QdJd: 1,QdJs: 0.71,Qh2h: 0.74,Qh3h: 0.33,Qh4h: 0.5,Qh5h: 0.735,Qh6h: 1,Qh7h: 1,Qh8h: 1,Qh9c: 0.265,Qh9h: 1,Qh9s: 0.265,QhTc: 0.615,QhTd: 0.615,QhTh: 1,QhTs: 0.615,QhJc: 0.71,QhJd: 0.71,QhJs: 0.71,Qs2s: 0.74,Qs3s: 0.33,Qs4s: 0.5,Qs5s: 0.735,Qs6s: 1,Qs7s: 1,Qs8s: 1,Qs9c: 0.265,Qs9h: 0.265,Qs9s: 1,QsTc: 0.615,QsTd: 0.615,QsTh: 0.615,QsTs: 1,QsJc: 0.71,QsJd: 0.71,QsJs: 1,Kc2c: 0.7,Kc3c: 0.33,Kc4c: 0.97,Kc5c: 1,Kc6c: 1,Kc7c: 1,Kc8c: 1,Kc9c: 1,Kc9h: 0.295,Kc9s: 0.295,KcTc: 0.56,KcTd: 0.785,KcTh: 0.785,KcTs: 0.785,KcJc: 1,KcJd: 0.785,KcJs: 0.785,KcQc: 0.235,KcQd: 0.87,KcQh: 0.87,KcQs: 0.87,Kd2d: 0.7,Kd3d: 0.33,Kd4d: 0.97,Kd5d: 1,Kd6d: 1,Kd7d: 1,Kd8d: 1,Kd9c: 0.295,Kd9h: 0.295,Kd9s: 0.295,KdTc: 0.785,KdTd: 0.56,KdTh: 0.785,KdTs: 0.785,KdJc: 0.785,KdJd: 1,KdJs: 0.785,KdQc: 0.87,KdQd: 0.235,KdQh: 0.87,KdQs: 0.87,Kh2h: 0.7,Kh3h: 0.33,Kh4h: 0.97,Kh5h: 1,Kh6h: 1,Kh7h: 1,Kh8h: 1,Kh9c: 0.295,Kh9h: 1,Kh9s: 0.295,KhTc: 0.785,KhTd: 0.785,KhTh: 0.56,KhTs: 0.785,KhJc: 0.785,KhJd: 0.785,KhJs: 0.785,KhQc: 0.87,KhQd: 0.87,KhQh: 0.235,KhQs: 0.87,Ks2s: 0.7,Ks3s: 0.33,Ks4s: 0.97,Ks5s: 1,Ks6s: 1,Ks7s: 1,Ks8s: 1,Ks9c: 0.295,Ks9h: 0.295,Ks9s: 1,KsTc: 0.785,KsTd: 0.785,KsTh: 0.785,KsTs: 0.56,KsJc: 0.785,KsJd: 0.785,KsJs: 1,KsQc: 0.87,KsQd: 0.87,KsQh: 0.87,KsQs: 0.235,Ac2c: 1,Ac3c: 1,Ac4c: 1,Ac5c: 1,Ac5d: 0.275,Ac5h: 0.275,Ac5s: 0.275,Ac6c: 1,Ac7c: 1,Ac7d: 0.37,Ac7h: 0.37,Ac7s: 0.37,Ac8c: 1,Ac8d: 0.565,Ac8h: 0.565,Ac8s: 0.565,Ac9c: 1,Ac9h: 0.82,Ac9s: 0.82,AcTc: 1,AcTd: 0.785,AcTh: 0.785,AcTs: 0.785,AcJd: 0.775,AcJs: 0.775,Ad2d: 1,Ad3d: 1,Ad4d: 1,Ad5c: 0.275,Ad5d: 1,Ad5h: 0.275,Ad5s: 0.275,Ad6d: 1,Ad7c: 0.37,Ad7d: 1,Ad7h: 0.37,Ad7s: 0.37,Ad8c: 0.565,Ad8d: 1,Ad8h: 0.565,Ad8s: 0.565,Ad9c: 0.82,Ad9h: 0.82,Ad9s: 0.82,AdTc: 0.785,AdTd: 1,AdTh: 0.785,AdTs: 0.785,AdJc: 0.775,AdJs: 0.775,Ah2h: 1,Ah3h: 1,Ah4h: 1,Ah5c: 0.275,Ah5d: 0.275,Ah5h: 1,Ah5s: 0.275,Ah6h: 1,Ah7c: 0.37,Ah7d: 0.37,Ah7h: 1,Ah7s: 0.37,Ah8c: 0.565,Ah8d: 0.565,Ah8h: 1,Ah8s: 0.565,Ah9c: 0.82,Ah9h: 1,Ah9s: 0.82,AhTc: 0.785,AhTd: 0.785,AhTh: 1,AhTs: 0.785,AhJc: 0.775,AhJd: 0.775,AhJs: 0.775";
        String player2RangeStr = "3d3c: 0.775,3h3c: 0.775,3h3d: 0.775,3s3c: 0.775,3s3d: 0.775,3s3h: 0.775,4d4c: 1,4h4c: 1,4h4d: 1,4s4c: 1,4s4d: 1,4s4h: 1,5c4c: 0.28,5d4d: 0.28,5d5c: 1,5h4h: 0.28,5h5c: 1,5h5d: 1,5s4s: 0.28,5s5c: 1,5s5d: 1,5s5h: 1,6c5c: 0.405,6d5d: 0.405,6d6c: 1,6h5h: 0.405,6h6c: 1,6h6d: 1,6s5s: 0.405,6s6c: 1,6s6d: 1,6s6h: 1,7c5c: 0.44,7c6c: 1,7d5d: 0.44,7d6d: 1,7d7c: 1,7h5h: 0.44,7h6h: 1,7h7c: 1,7h7d: 1,7s5s: 0.44,7s6s: 1,7s7c: 1,7s7d: 1,7s7h: 1,8c6c: 1,8c7c: 1,8d6d: 1,8d7d: 1,8d8c: 1,8h6h: 1,8h7h: 1,8h8c: 1,8h8d: 1,8s6s: 1,8s7s: 1,8s8c: 1,8s8d: 1,8s8h: 1,9c6c: 1,9c7c: 1,9c8c: 1,9c8d: 0.29,9c8h: 0.29,9c8s: 0.29,9h6h: 1,9h7h: 1,9h8c: 0.29,9h8d: 0.29,9h8h: 1,9h8s: 0.29,9h9c: 1,9s6s: 1,9s7s: 1,9s8c: 0.29,9s8d: 0.29,9s8h: 0.29,9s8s: 1,9s9c: 1,9s9h: 1,Tc6c: 1,Tc7c: 1,Tc8c: 1,Tc8d: 0.365,Tc8h: 0.365,Tc8s: 0.365,Tc9c: 1,Tc9h: 1,Tc9s: 1,Td6d: 1,Td7d: 1,Td8c: 0.365,Td8d: 1,Td8h: 0.365,Td8s: 0.365,Td9c: 1,Td9h: 1,Td9s: 1,TdTc: 1,Th6h: 1,Th7h: 1,Th8c: 0.365,Th8d: 0.365,Th8h: 1,Th8s: 0.365,Th9c: 1,Th9h: 1,Th9s: 1,ThTc: 1,ThTd: 1,Ts6s: 1,Ts7s: 1,Ts8c: 0.365,Ts8d: 0.365,Ts8h: 0.365,Ts8s: 1,Ts9c: 1,Ts9h: 1,Ts9s: 1,TsTc: 1,TsTd: 1,TsTh: 1,Jc4c: 0.295,Jc5c: 1,Jc6c: 1,Jc7c: 1,Jc8c: 1,Jc8d: 0.055,Jc8h: 0.055,Jc8s: 0.055,Jc9c: 1,Jc9h: 1,Jc9s: 1,JcTc: 1,JcTd: 1,JcTh: 1,JcTs: 1,Jd4d: 0.295,Jd5d: 1,Jd6d: 1,Jd7d: 1,Jd8c: 0.055,Jd8d: 1,Jd8h: 0.055,Jd8s: 0.055,Jd9c: 1,Jd9h: 1,Jd9s: 1,JdTc: 1,JdTd: 1,JdTh: 1,JdTs: 1,JdJc: 1,Js4s: 0.295,Js5s: 1,Js6s: 1,Js7s: 1,Js8c: 0.055,Js8d: 0.055,Js8h: 0.055,Js8s: 1,Js9c: 1,Js9h: 1,Js9s: 1,JsTc: 1,JsTd: 1,JsTh: 1,JsTs: 1,JsJc: 1,JsJd: 1,Qc2c: 0.425,Qc3c: 1,Qc4c: 1,Qc5c: 1,Qc6c: 1,Qc7c: 1,Qc8c: 1,Qc8d: 0.545,Qc8h: 0.545,Qc8s: 0.545,Qc9c: 1,Qc9h: 1,Qc9s: 1,QcTc: 1,QcTd: 1,QcTh: 1,QcTs: 1,QcJc: 1,QcJd: 1,QcJs: 1,Qd2d: 0.425,Qd3d: 1,Qd4d: 1,Qd5d: 1,Qd6d: 1,Qd7d: 1,Qd8c: 0.545,Qd8d: 1,Qd8h: 0.545,Qd8s: 0.545,Qd9c: 1,Qd9h: 1,Qd9s: 1,QdTc: 1,QdTd: 1,QdTh: 1,QdTs: 1,QdJc: 1,QdJd: 1,QdJs: 1,QdQc: 1,Qh2h: 0.425,Qh3h: 1,Qh4h: 1,Qh5h: 1,Qh6h: 1,Qh7h: 1,Qh8c: 0.545,Qh8d: 0.545,Qh8h: 1,Qh8s: 0.545,Qh9c: 1,Qh9h: 1,Qh9s: 1,QhTc: 1,QhTd: 1,QhTh: 1,QhTs: 1,QhJc: 1,QhJd: 1,QhJs: 1,QhQc: 1,QhQd: 1,Qs2s: 0.425,Qs3s: 1,Qs4s: 1,Qs5s: 1,Qs6s: 1,Qs7s: 1,Qs8c: 0.545,Qs8d: 0.545,Qs8h: 0.545,Qs8s: 1,Qs9c: 1,Qs9h: 1,Qs9s: 1,QsTc: 1,QsTd: 1,QsTh: 1,QsTs: 1,QsJc: 1,QsJd: 1,QsJs: 1,QsQc: 1,QsQd: 1,QsQh: 1,Kc2c: 1,Kc3c: 1,Kc4c: 1,Kc5c: 1,Kc6c: 1,Kc7c: 1,Kc7d: 0.095,Kc7h: 0.095,Kc7s: 0.095,Kc8c: 1,Kc8d: 0.645,Kc8h: 0.645,Kc8s: 0.645,Kc9c: 1,Kc9h: 1,Kc9s: 1,KcTc: 1,KcTd: 1,KcTh: 1,KcTs: 1,KcJc: 1,KcJd: 1,KcJs: 1,KcQc: 1,KcQd: 1,KcQh: 1,KcQs: 1,Kd2d: 1,Kd3d: 1,Kd4d: 1,Kd5d: 1,Kd6d: 1,Kd7c: 0.095,Kd7d: 1,Kd7h: 0.095,Kd7s: 0.095,Kd8c: 0.645,Kd8d: 1,Kd8h: 0.645,Kd8s: 0.645,Kd9c: 1,Kd9h: 1,Kd9s: 1,KdTc: 1,KdTd: 1,KdTh: 1,KdTs: 1,KdJc: 1,KdJd: 1,KdJs: 1,KdQc: 1,KdQd: 1,KdQh: 1,KdQs: 1,KdKc: 1,Kh2h: 1,Kh3h: 1,Kh4h: 1,Kh5h: 1,Kh6h: 1,Kh7c: 0.095,Kh7d: 0.095,Kh7h: 1,Kh7s: 0.095,Kh8c: 0.645,Kh8d: 0.645,Kh8h: 1,Kh8s: 0.645,Kh9c: 1,Kh9h: 1,Kh9s: 1,KhTc: 1,KhTd: 1,KhTh: 1,KhTs: 1,KhJc: 1,KhJd: 1,KhJs: 1,KhQc: 1,KhQd: 1,KhQh: 1,KhQs: 1,KhKc: 1,KhKd: 1,Ks2s: 1,Ks3s: 1,Ks4s: 1,Ks5s: 1,Ks6s: 1,Ks7c: 0.095,Ks7d: 0.095,Ks7h: 0.095,Ks7s: 1,Ks8c: 0.645,Ks8d: 0.645,Ks8h: 0.645,Ks8s: 1,Ks9c: 1,Ks9h: 1,Ks9s: 1,KsTc: 1,KsTd: 1,KsTh: 1,KsTs: 1,KsJc: 1,KsJd: 1,KsJs: 1,KsQc: 1,KsQd: 1,KsQh: 1,KsQs: 1,KsKc: 1,KsKd: 1,KsKh: 1,Ac2c: 1,Ac3c: 1,Ac3d: 1,Ac3h: 1,Ac3s: 1,Ac4c: 1,Ac4d: 1,Ac4h: 1,Ac4s: 1,Ac5c: 1,Ac5d: 1,Ac5h: 1,Ac5s: 1,Ac6c: 1,Ac6d: 1,Ac6h: 1,Ac6s: 1,Ac7c: 1,Ac7d: 1,Ac7h: 1,Ac7s: 1,Ac8c: 1,Ac8d: 1,Ac8h: 1,Ac8s: 1,Ac9c: 1,Ac9h: 1,Ac9s: 1,AcTc: 1,AcTd: 1,AcTh: 1,AcTs: 1,AcJc: 1,AcJd: 1,AcJs: 1,AcQc: 1,AcQd: 1,AcQh: 1,AcQs: 1,AcKc: 1,AcKd: 1,AcKh: 1,AcKs: 1,Ad2d: 1,Ad3c: 1,Ad3d: 1,Ad3h: 1,Ad3s: 1,Ad4c: 1,Ad4d: 1,Ad4h: 1,Ad4s: 1,Ad5c: 1,Ad5d: 1,Ad5h: 1,Ad5s: 1,Ad6c: 1,Ad6d: 1,Ad6h: 1,Ad6s: 1,Ad7c: 1,Ad7d: 1,Ad7h: 1,Ad7s: 1,Ad8c: 1,Ad8d: 1,Ad8h: 1,Ad8s: 1,Ad9c: 1,Ad9h: 1,Ad9s: 1,AdTc: 1,AdTd: 1,AdTh: 1,AdTs: 1,AdJc: 1,AdJd: 1,AdJs: 1,AdQc: 1,AdQd: 1,AdQh: 1,AdQs: 1,AdKc: 1,AdKd: 1,AdKh: 1,AdKs: 1,AdAc: 1,Ah2h: 1,Ah3c: 1,Ah3d: 1,Ah3h: 1,Ah3s: 1,Ah4c: 1,Ah4d: 1,Ah4h: 1,Ah4s: 1,Ah5c: 1,Ah5d: 1,Ah5h: 1,Ah5s: 1,Ah6c: 1,Ah6d: 1,Ah6h: 1,Ah6s: 1,Ah7c: 1,Ah7d: 1,Ah7h: 1,Ah7s: 1,Ah8c: 1,Ah8d: 1,Ah8h: 1,Ah8s: 1,Ah9c: 1,Ah9h: 1,Ah9s: 1,AhTc: 1,AhTd: 1,AhTh: 1,AhTs: 1,AhJc: 1,AhJd: 1,AhJs: 1,AhQc: 1,AhQd: 1,AhQh: 1,AhQs: 1,AhKc: 1,AhKd: 1,AhKh: 1,AhKs: 1,AhAc: 1,AhAd: 1";

        // String player1RangeStr = "87";
        // String player2RangeStr = "87";
        /*
        case 'c': return 0; // 梅花
        case 'd': return 1; // 方块
        case 'h': return 2; // 红桃
        case 's': return 3; // 黑桃
         */

        int[] initialBoard = new int[]{
                Card.strCard2int("As"),
                Card.strCard2int("Jh"),
                Card.strCard2int("9d")
        };

        PrivateCards[] player1Range = PrivateRangeConverter.rangeStr2Cards(player1RangeStr,initialBoard);
        PrivateCards[] player2Range = PrivateRangeConverter.rangeStr2Cards(player2RangeStr,initialBoard);
        //PrivateCards[] player1Range = new PrivateCards[]{new PrivateCards(Card.strCard2int("As"),Card.strCard2int("Ad"),1)};

        /*
        PrivateCards[] player1Range = new PrivateCards[]{
                new PrivateCards(Card.strCard2int("As"),Card.strCard2int("Ad"),1)
                ,new PrivateCards(Card.strCard2int("8h"),Card.strCard2int("7d"),1)
        };
        PrivateCards[] player2Range = new PrivateCards[]{
                //new PrivateCards(Card.strCard2int("6d"),Card.strCard2int("8s"),1)
                new PrivateCards(Card.strCard2int("6d"),Card.strCard2int("7d"),1)
                ,new PrivateCards(Card.strCard2int("6s"),Card.strCard2int("6d"),1)
        };
         */

        String logfile_name = "src/test/resources/outputs/outputs_log.txt";
        Solver solver = new CfrPlusRiverSolver(game_tree
        );
        SolveConfig solveConfig = new SolveConfig(
                player1Range
                , player2Range
                , initialBoard
                , TexasHoldemSolverTest.compairer
                , TexasHoldemSolverTest.deck
                ,100
                ,false
                , 10
                ,logfile_name
                , DiscountedCfrTrainable.class
                ,MonteCarolAlg.NONE
                , 0.0
        );
        solver.train(solveConfig);

        String strategy_json = solver.getTree().dumps(false).toString();

        String strategy_fname = "src/test/resources/outputs/outputs_flop_strategy.json";

        File output_file = new File(strategy_fname);
        FileWriter writer = new FileWriter(output_file);
        writer.write(strategy_json);
        writer.flush();
        writer.close();

        System.out.println("end holdem_flop_solver_test");
    }

    @Test
    public void cfrTurnSolverTest() throws BoardNotFoundException,Exception{
        System.out.println("solverTest");

        //String config_name = "yamls/rule_holdem_simple.yaml";
    //String config_name = "yamls/rule_shortdeck_turnriversolver.yaml";
        String config_name = "yamls/rule_shortdeck_turnsolver.yaml";
        //String config_name = "yamls/rule_shortdeck_turnsolver_withallin.yaml";
        //String config_name = "yamls/rule_shortdeck_flopsolver.yaml";
        Config config = this.loadConfig(config_name);
        GameTree game_tree = SolverEnvironment.gameTreeFromConfig(config, TexasHoldemSolverTest.deck);

        String player1RangeStr = "AA,KK,QQ,JJ,TT,99,88,77,66,AK,AQ,AJ,AT,A9,A8,A7,A6,KQ,KJ,KT,K9,K8,K7,K6,QJ,QT,Q9,Q8,Q7,Q6,JT,J9,J8,J7,J6,T9,T8,T7,T6,98,97,96,43,64,84";
        String player2RangeStr = "AA,KK,QQ,JJ,TT,99,88,77,66,AK,AQ,AJ,AT,A9,A8,A7,A6,KQ,KJ,KT,K9,K8,K7,K6,QJ,QT,Q9,Q8,Q7,Q6,JT,J9,J8,J7,J6,T9,T8,T7,T6,98,97,96,43,64,84";

        // String player1RangeStr = "87";
        // String player2RangeStr = "87";
        /*
        case 'c': return 0; // 梅花
        case 'd': return 1; // 方块
        case 'h': return 2; // 红桃
        case 's': return 3; // 黑桃
         */

        int[] initialBoard = new int[]{
                Card.strCard2int("Kd"),
                Card.strCard2int("Jd"),
                Card.strCard2int("Td"),
                Card.strCard2int("7s"),
        };

        PrivateCards[] player1Range = PrivateRangeConverter.rangeStr2Cards(player1RangeStr,initialBoard);
        PrivateCards[] player2Range = PrivateRangeConverter.rangeStr2Cards(player2RangeStr,initialBoard);

        String logfile_name = "src/test/resources/outputs/outputs_log.txt";
        Solver solver = new CfrPlusRiverSolver(game_tree);
        SolveConfig solveConfig = new SolveConfig(
                player1Range
                , player2Range
                , initialBoard
                , TexasHoldemSolverTest.compairer
                , TexasHoldemSolverTest.deck
                ,100
                ,false
                , 10
                ,logfile_name
                , DiscountedCfrTrainable.class
                ,MonteCarolAlg.NONE
                , 0.0
        );
        solver.train(solveConfig);

        String strategy_json = solver.getTree().dumps(false).toString(4);

        String strategy_fname = "src/test/resources/outputs/outputs_turn_strategy.json";

        File output_file = new File(strategy_fname);
        FileWriter writer = new FileWriter(output_file);
        writer.write(strategy_json);
        writer.flush();
        writer.close();

        System.out.println("end solverTest");
    }

    @Test
    public void cfrMonteCarloTurnSolverTest() throws BoardNotFoundException,Exception{
        System.out.println("solverTest");

        //String config_name = "yamls/rule_holdem_simple.yaml";
        //String config_name = "yamls/rule_shortdeck_turnriversolver.yaml";
        String config_name = "yamls/rule_shortdeck_turnsolver.yaml";
        //String config_name = "yamls/rule_shortdeck_turnsolver_withallin.yaml";
        //String config_name = "yamls/rule_shortdeck_flopsolver.yaml";
        Config config = this.loadConfig(config_name);
        GameTree game_tree = SolverEnvironment.gameTreeFromConfig(config, TexasHoldemSolverTest.deck);

        String player1RangeStr = "AA,KK,QQ,JJ,TT,99,88,77,66,AK,AQ,AJ,AT,A9,A8,A7,A6,KQ,KJ,KT,K9,K8,K7,K6,QJ,QT,Q9,Q8,Q7,Q6,JT,J9,J8,J7,J6,T9,T8,T7,T6,98,97,96,43,64,84";
        String player2RangeStr = "AA,KK,QQ,JJ,TT,99,88,77,66,AK,AQ,AJ,AT,A9,A8,A7,A6,KQ,KJ,KT,K9,K8,K7,K6,QJ,QT,Q9,Q8,Q7,Q6,JT,J9,J8,J7,J6,T9,T8,T7,T6,98,97,96,43,64,84";

        // String player1RangeStr = "87";
        // String player2RangeStr = "87";
        /*
        case 'c': return 0; // 梅花
        case 'd': return 1; // 方块
        case 'h': return 2; // 红桃
        case 's': return 3; // 黑桃
         */

        int[] initialBoard = new int[]{
                Card.strCard2int("Kd"),
                Card.strCard2int("Jd"),
                Card.strCard2int("Td"),
                Card.strCard2int("7s"),
        };

        PrivateCards[] player1Range = PrivateRangeConverter.rangeStr2Cards(player1RangeStr,initialBoard);
        PrivateCards[] player2Range = PrivateRangeConverter.rangeStr2Cards(player2RangeStr,initialBoard);

        String logfile_name = "src/test/resources/outputs/outputs_log.txt";
        Solver solver = new CfrPlusRiverSolver(game_tree);
        SolveConfig solveConfig = new SolveConfig(
                player1Range
                , player2Range
                , initialBoard
                , TexasHoldemSolverTest.compairer
                , TexasHoldemSolverTest.deck
                ,100
                ,false
                , 10
                ,logfile_name
                , DiscountedCfrTrainable.class
                ,MonteCarolAlg.PUBLIC
                , 0.0
        );
        solver.train(solveConfig);

        String strategy_json = solver.getTree().dumps(false).toString(4);

        String strategy_fname = "src/test/resources/outputs/outputs_strategy.json";

        File output_file = new File(strategy_fname);
        FileWriter writer = new FileWriter(output_file);
        writer.write(strategy_json);
        writer.flush();
        writer.close();

        System.out.println("end solverTest");
    }

    //@Test
    public void cfrFlopSolverTest() throws BoardNotFoundException,Exception{
        System.out.println("solverTest");

        //String config_name = "yamls/rule_holdem_simple.yaml";
        //String config_name = "yamls/rule_shortdeck_turnriversolver.yaml";
        //String config_name = "yamls/rule_shortdeck_turnsolver.yaml";
        //String config_name = "yamls/rule_shortdeck_turnsolver_withallin.yaml";
        String config_name = "yamls/rule_shortdeck_flopsolver.yaml";
        Config config = this.loadConfig(config_name);
        GameTree game_tree = SolverEnvironment.gameTreeFromConfig(config, TexasHoldemSolverTest.deck);

        String player1RangeStr = "AA,KK,QQ,JJ,TT,99,88,77,66,AK,AQ,AJ,AT,A9,A8,A7,A6,KQ,KJ,KT,K9,K8,K7,K6,QJ,QT,Q9,Q8,Q7,Q6,JT,J9,J8,J7,J6,T9,T8,T7,T6,98,97,96,43,64,84";
        //String player2RangeStr = "AA,KK,QQ,JJ,TT,99,88,77,66,AK,AQ,AJ,AT,A9,A8,A7,A6,KQ,KJ,KT,K9,K8,K7,K6,QJ,QT,Q9,Q8,Q7,Q6,JT,J9,J8,J7,J6,T9,T8,T7,T6,98,97,96,43,64,84";
        String player2RangeStr = "KT,K9,K8,K7,K6,QJ,QT,Q9,Q8,Q7,Q6,JT,J9,J8,J7,J6,T9,T8,T7,T6,98,97,96,43,64,84";

        // String player1RangeStr = "87";
        // String player2RangeStr = "87";
        /*
        case 'c': return 0; // 梅花
        case 'd': return 1; // 方块
        case 'h': return 2; // 红桃
        case 's': return 3; // 黑桃
         */

        int[] initialBoard = new int[]{
                Card.strCard2int("Kd"),
                Card.strCard2int("Jd"),
                Card.strCard2int("Td"),
        };

        PrivateCards[] player1Range = PrivateRangeConverter.rangeStr2Cards(player1RangeStr,initialBoard);
        PrivateCards[] player2Range = PrivateRangeConverter.rangeStr2Cards(player2RangeStr,initialBoard);

        String logfile_name = "src/test/resources/outputs/outputs_log.txt";
        Solver solver = new CfrPlusRiverSolver(game_tree);
        SolveConfig solveConfig = new SolveConfig(
                player1Range
                , player2Range
                , initialBoard
                , TexasHoldemSolverTest.compairer
                , TexasHoldemSolverTest.deck
                ,100
                ,false
                , 10
                ,logfile_name
                , DiscountedCfrTrainable.class
                ,MonteCarolAlg.NONE
                , 0.0
        );
        solver.train(solveConfig);

        String strategy_json = solver.getTree().dumps(false).toString(4);

        String strategy_fname = "src/test/resources/outputs/outputs_strategy.json";

        File output_file = new File(strategy_fname);
        FileWriter writer = new FileWriter(output_file);
        writer.write(strategy_json);
        writer.flush();
        writer.close();

        System.out.println("end solverTest");
    }

    //@Test
    public void cfrFlopSolverPcsTest() throws BoardNotFoundException,Exception{
        System.out.println("solverTest");

        //String config_name = "yamls/rule_holdem_simple.yaml";
        //String config_name = "yamls/rule_shortdeck_turnriversolver.yaml";
        //String config_name = "yamls/rule_shortdeck_turnsolver.yaml";
        //String config_name = "yamls/rule_shortdeck_turnsolver_withallin.yaml";
        String config_name = "yamls/rule_shortdeck_flopsolver.yaml";
        Config config = this.loadConfig(config_name);
        GameTree game_tree = SolverEnvironment.gameTreeFromConfig(config, TexasHoldemSolverTest.deck);

        String player1RangeStr = "AA,KK,QQ,JJ,TT,99,88,77,66,AK,AQ,AJ,AT,A9,A8,A7,A6,KQ,KJ,KT,K9,K8,K7,K6,QJ,QT,Q9,Q8,Q7,Q6,JT,J9,J8,J7,J6,T9,T8,T7,T6,98,97,96,43,64,84";
        String player2RangeStr = "AA,KK,QQ,JJ,TT,99,88,77,66,AK,AQ,AJ,AT,A9,A8,A7,A6,KQ,KJ,KT,K9,K8,K7,K6,QJ,QT,Q9,Q8,Q7,Q6,JT,J9,J8,J7,J6,T9,T8,T7,T6,98,97,96,43,64,84";
        //String player2RangeStr = "KT,K9,K8,K7,K6,QJ,QT,Q9,Q8,Q7,Q6,JT,J9,J8,J7,J6,T9,T8,T7,T6,98,97,96,43,64,84";

        // String player1RangeStr = "87";
        // String player2RangeStr = "87";
        /*
        case 'c': return 0; // 梅花
        case 'd': return 1; // 方块
        case 'h': return 2; // 红桃
        case 's': return 3; // 黑桃
         */

        int[] initialBoard = new int[]{
                Card.strCard2int("Kd"),
                Card.strCard2int("Jd"),
                Card.strCard2int("Td"),
        };

        PrivateCards[] player1Range = PrivateRangeConverter.rangeStr2Cards(player1RangeStr,initialBoard);
        PrivateCards[] player2Range = PrivateRangeConverter.rangeStr2Cards(player2RangeStr,initialBoard);

        String logfile_name = "src/test/resources/outputs/outputs_log.txt";
        Solver solver = new CfrPlusRiverSolver(game_tree);
        SolveConfig solveConfig = new SolveConfig(
                player1Range
                , player2Range
                , initialBoard
                , TexasHoldemSolverTest.compairer
                , TexasHoldemSolverTest.deck
                ,100
                ,false
                , 10
                ,logfile_name
                , DiscountedCfrTrainable.class
                ,MonteCarolAlg.PUBLIC
                , 0.0
        );
        solver.train(solveConfig);

        String strategy_json = solver.getTree().dumps(false).toString(4);

        String strategy_fname = "src/test/resources/outputs/outputs_strategy.json";

        File output_file = new File(strategy_fname);
        FileWriter writer = new FileWriter(output_file);
        writer.write(strategy_json);
        writer.flush();
        writer.close();

        System.out.println("end solverTest");
    }

    @Test
    public void parrallelCfrTurnSolverTest() throws BoardNotFoundException,Exception{
        System.out.println("solverTest");

        //String config_name = "yamls/rule_holdem_simple.yaml";
        //String config_name = "yamls/rule_shortdeck_turnriversolver.yaml";
        String config_name = "yamls/rule_shortdeck_turnsolver.yaml";
        //String config_name = "yamls/rule_shortdeck_turnsolver_withallin.yaml";
        //String config_name = "yamls/rule_shortdeck_flopsolver.yaml";
        Config config = this.loadConfig(config_name);
        GameTree game_tree = SolverEnvironment.gameTreeFromConfig(config, TexasHoldemSolverTest.deck);

        String player1RangeStr = "AA,KK,QQ,JJ,TT,99,88,77,66,AK,AQ,AJ,AT,A9,A8,A7,A6,KQ,KJ,KT,K9,K8,K7,K6,QJ,QT,Q9,Q8,Q7,Q6,JT,J9,J8,J7,J6,T9,T8,T7,T6,98,97,96,43,64,84";
        //String player2RangeStr = "AA,KK,QQ,JJ,TT,99,88,77,66,AK,AQ,AJ,AT,A9,A8,A7,A6,KQ,KJ,KT,K9,K8,K7,K6,QJ,QT,Q9,Q8,Q7,Q6,JT,J9,J8,J7,J6,T9,T8,T7,T6,98,97,96,43,64,84";
        String player2RangeStr = "KT,K9,K8,K7,K6,QJ,QT,Q9,Q8,Q7,Q6,JT,J9,J8,J7,J6,T9,T8,T7,T6,98,97,96,43,64:1.0,84:4.5";

        // String player1RangeStr = "87";
        // String player2RangeStr = "87";
        /*
        case 'c': return 0; // 梅花
        case 'd': return 1; // 方块
        case 'h': return 2; // 红桃
        case 's': return 3; // 黑桃
         */

        int[] initialBoard = new int[]{
                Card.strCard2int("Kd"),
                Card.strCard2int("Jd"),
                Card.strCard2int("Td"),
        };

        PrivateCards[] player1Range = PrivateRangeConverter.rangeStr2Cards(player1RangeStr,initialBoard);
        PrivateCards[] player2Range = PrivateRangeConverter.rangeStr2Cards(player2RangeStr,initialBoard);

        String logfile_name = "src/test/resources/outputs/outputs_log.txt";
        Solver solver = new ParallelCfrPlusSolver(game_tree
                ,2
                ,1
                ,0
                , 1
                , 0
        );
        SolveConfig solveConfig = new SolveConfig(
                player1Range
                , player2Range
                , initialBoard
                , TexasHoldemSolverTest.compairer
                , TexasHoldemSolverTest.deck
                ,100
                ,false
                , 10
                ,logfile_name
                , DiscountedCfrTrainable.class
                ,MonteCarolAlg.NONE
                , 0.0
        );
        solver.train(solveConfig);

        String strategy_json = solver.getTree().dumps(false).toString(4);

        String strategy_fname = "src/test/resources/outputs/outputs_strategy.json";

        File output_file = new File(strategy_fname);
        FileWriter writer = new FileWriter(output_file);
        writer.write(strategy_json);
        writer.flush();
        writer.close();

        System.out.println("end solverTest");
    }
}
