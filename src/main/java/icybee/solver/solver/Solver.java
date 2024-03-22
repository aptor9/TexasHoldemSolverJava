package icybee.solver.solver;

import icybee.solver.Card;
import icybee.solver.Deck;
import icybee.solver.GameTree;
import icybee.solver.RiverRangeManager;
import icybee.solver.compairer.Compairer;
import icybee.solver.ranges.PrivateCards;
import icybee.solver.ranges.PrivateCardsManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ForkJoinPool;

/**
 * Created by huangxuefeng on 2019/10/9.
 * contains an abstract class Solver for cfr or other things.
 */
public abstract class Solver {

    GameTree tree;
    SolveConfig solveConfig;

    protected RiverRangeManager rrm;
    protected PrivateCardsManager pcm;

    public Solver(GameTree tree) {
        this.tree = tree;
    }
    public GameTree getTree() {
        return tree;
    }

    public abstract void train(SolveConfig solveConfig) throws Exception;
}
