package icybee.solver.gui;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.prefs.Preferences;

import icybee.solver.*;
import icybee.solver.compairer.Compairer;
import icybee.solver.ranges.PrivateCards;
import icybee.solver.solver.*;
import icybee.solver.trainable.CfrPlusTrainable;
import icybee.solver.trainable.DiscountedCfrTrainable;
import icybee.solver.utils.PrivateRangeConverter;
import org.json.JSONObject;

public class SolverGui {
    private static final String LAST_OPEN_FOLDER = "last_open_folder";

    private JPanel mainPanel;
    private JPanel left;
    private JTabbedPane tabbedPane1;
    private JPanel board;
    private JTextArea ooprange;
    private JButton selectOOPRangeButton;
    private JTextArea iprange;
    private JButton selectIPRangeButton;
    private JTextArea boardstr;
    private JButton selectBoardCardButton;
    private JButton buildTreeButton;
    private JButton showTreeButton;
    private JButton showResultButton;
    private JTextField raise_limit;
    private JTextField stacks;
    private JComboBox mode;
    private JTextArea log;
    private JButton startSolvingButton;
    private JTextField iteration;
    private JTextField exploitability;
    private JTextField log_interval;
    private JTextField pot;
    private JTextField threads;
    private JCheckBox mc;
    private JComboBox algorithm;
    private JButton clearLogButton;
    private JTextField flop_ip_bet;
    private JTextField flop_ip_raise;
    private JCheckBox flop_ip_allin;
    private JTextField turn_ip_bet;
    private JTextField turn_ip_raise;
    private JCheckBox turn_ip_allin;
    private JTextField river_ip_bet;
    private JTextField river_ip_raise;
    private JCheckBox river_ip_allin;
    private JTextField flop_oop_bet;
    private JTextField flop_oop_raise;
    private JCheckBox flop_oop_allin;
    private JTextField turn_oop_bet;
    private JTextField turn_oop_raise;
    private JTextField turn_oop_donk;
    private JCheckBox turn_oop_allin;
    private JTextField river_oop_bet;
    private JTextField river_oop_raise;
    private JTextField river_oop_donk;
    private JCheckBox river_oop_allin;
    private JButton copy;

    private JMenuBar menuBar;
    private JMenu fileMenu;
    private JMenuItem loadMenuItem;
    private JMenuItem saveMenuItem;

    private Compairer compairer_holdem = null;
    private Compairer compairer_shortdeck = null;
    private Deck holdem_deck = null;
    private Deck shortdeck_deck = null;
    GameTree game_tree;
    SolveConfig solveConfig;

    GameTreeBuildingSettings treeSettings;

    public static void main(String[] args) {
        new SolverGui();
    }

    public SolverGui() {
        JFrame frame = new JFrame("SolverGui");

        menuBar = new JMenuBar();
        fileMenu = new JMenu("File");
        menuBar.add(fileMenu);
        loadMenuItem = new JMenuItem("Load");
        saveMenuItem = new JMenuItem("Save");
        fileMenu.add(loadMenuItem);
        fileMenu.add(saveMenuItem);

        frame.setJMenuBar(menuBar);
        frame.setContentPane(mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);

        PrintStream printStream = new PrintStream(new CustomOutputStream(this.log));
        System.setOut(printStream);
        System.setErr(printStream);

        loadMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Preferences prefs = Preferences.userRoot().node(getClass().getName());
                String lastOpenDirectory = prefs.get(LAST_OPEN_FOLDER, new File(System.getProperty("user.dir")).getAbsolutePath());
                JFileChooser fileChooser = new JFileChooser(lastOpenDirectory);
                int result = fileChooser.showOpenDialog(frame);
                if (result == JFileChooser.APPROVE_OPTION) {
                    prefs.put(LAST_OPEN_FOLDER, fileChooser.getSelectedFile().getParent());

                    String loadFilename = fileChooser.getSelectedFile().getAbsolutePath();
                    File loadFile = new File(loadFilename);
                    if (!loadFile.exists()) {
                        JOptionPane.showMessageDialog(frame, "The selected file doesn't exist!");
                        return;
                    }

                    if(game_tree != null) {
                        int response = JOptionPane.showConfirmDialog(null, //
                                "After loading this train result file, your current game tree will be lost. Are you sure?", //
                                "Confirm", JOptionPane.YES_NO_OPTION, //
                                JOptionPane.QUESTION_MESSAGE);
                        if (response != JOptionPane.YES_OPTION) {
                            return;
                        }
                    }
                    new Thread() {
                        public void run() {
                            try {
                                loadTrainResult(loadFilename);
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            }
                        }
                    }.start();
                }
            }
        });

        saveMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Preferences prefs = Preferences.userRoot().node(getClass().getName());
                String lastOpenDirectory = prefs.get(LAST_OPEN_FOLDER, new File(System.getProperty("user.dir")).getAbsolutePath());
                JFileChooser fileChooser = new JFileChooser(lastOpenDirectory);
                int result = fileChooser.showSaveDialog(frame);
                if (result == JFileChooser.APPROVE_OPTION) {
                    prefs.put(LAST_OPEN_FOLDER, fileChooser.getSelectedFile().getParent());

                    if(game_tree == null) {
                        JOptionPane.showMessageDialog(frame, "Please build tree first");
                        return;
                    }

                    String outputFilename = fileChooser.getSelectedFile().getAbsolutePath();
                    File outputFile = new File(outputFilename);
                    if (outputFile.exists()) {
                        int response = JOptionPane.showConfirmDialog(null, //
                                "Are you sure you want to overwrite this file?", //
                                "Confirm", JOptionPane.YES_NO_OPTION, //
                                JOptionPane.QUESTION_MESSAGE);
                        if (response != JOptionPane.YES_OPTION) {
                            return;
                        }
                    }

                    new Thread() {
                        public void run() {
                            try {
                                saveTrainResult(outputFilename);
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            }
                        }
                    }.start();
                }
            }
        });

        // run initize
        new Thread(){
            public void run(){
                initialize();
            }
        }.start();

        // build trree
        buildTreeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Thread() {
                    public void run(){
                        onBuildTree();
                    }
                }.start();
            }
        });

        startSolvingButton.setEnabled(false);
        startSolvingButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(game_tree == null) {
                    JOptionPane.showMessageDialog(frame, "Please build tree first");
                    return;
                }
                new Thread(){
                    public void run(){
                        try {
                            solve();
                        }catch (Exception err){
                            err.printStackTrace();
                        }
                    }
                }.start();
            }
        });
        showTreeButton.setEnabled(false);
        showTreeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(game_tree == null){
                    JOptionPane.showMessageDialog(frame, "Please build tree first");
                }else {
                    game_tree.printTree(100);
                }
            }
        });
        clearLogButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                log.setText("");
            }
        });
        showResultButton.setEnabled(false);
        showResultButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(game_tree == null) {
                    JOptionPane.showMessageDialog(frame, "Please build tree first");
                    return;
                }
                SolverResult sr = new SolverResult(game_tree,game_tree.getRoot(),boardstr.getText());
                JFrame frame = new JFrame("SolverResult");
                frame.setContentPane(sr.resultPanel);
                frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                frame.pack();
                frame.setVisible(true);
            }
        });
        ooprange.setLineWrap(true);
        iprange.setLineWrap(true);
        selectOOPRangeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                RangeSelectorCallback rsc = new RangeSelectorCallback() {
                    @Override
                    void onFinish(String content) {
                        ooprange.setText(content);
                    }
                };
                JFrame frame = new JFrame("RangeSelector");
                RangeSelector rr = new RangeSelector(rsc,ooprange.getText(),mode.getSelectedIndex() == 0? RangeSelector.RangeType.HOLDEM: RangeSelector.RangeType.SHORTDECK,frame);
                frame.setContentPane(rr.range_selector_main_panel);
                frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                frame.pack();
                frame.setVisible(true);

            }
        });

        selectIPRangeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                RangeSelectorCallback rsc = new RangeSelectorCallback() {
                    @Override
                    void onFinish(String content) {
                        iprange.setText(content);
                    }
                };
                JFrame frame = new JFrame("RangeSelector");
                RangeSelector rr = new RangeSelector(rsc,iprange.getText(),mode.getSelectedIndex() == 0? RangeSelector.RangeType.HOLDEM: RangeSelector.RangeType.SHORTDECK,frame);
                frame.setContentPane(rr.range_selector_main_panel);
                frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                frame.pack();
                frame.setVisible(true);
            }
        });
        selectBoardCardButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                BoardSelectorCallback bsc = new BoardSelectorCallback() {
                    @Override
                    void onFinish(String content) {
                        boardstr.setText(content);
                    }
                };
                JFrame frame = new JFrame("BoardSelector");
                BoardSelector br = new BoardSelector(bsc,boardstr.getText(),mode.getSelectedIndex() == 0? RangeSelector.RangeType.HOLDEM: RangeSelector.RangeType.SHORTDECK,frame);
                frame.setContentPane(br.main_panel);
                frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                frame.pack();
                frame.setVisible(true);
            }
        });
        copy.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                flop_oop_allin.setSelected(flop_ip_allin.isSelected());
                flop_oop_raise.setText(flop_ip_raise.getText());
                flop_oop_bet.setText(flop_ip_bet.getText());

                turn_oop_allin.setSelected(turn_ip_allin.isSelected());
                turn_oop_raise.setText(turn_ip_raise.getText());
                turn_oop_bet.setText(turn_ip_bet.getText());

                river_oop_allin.setSelected(river_ip_allin.isSelected());
                river_oop_raise.setText(river_ip_raise.getText());
                river_oop_bet.setText(river_ip_bet.getText());
            }
        });
    }

    Config loadConfig(String conf_name){
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

    private void load_compairer() throws IOException {
        System.out.println("loading holdem compairer dictionary...");
        String config_name = "resources/yamls/rule_holdem_simple.yaml";
        Config config = this.loadConfig(config_name);
        this.compairer_holdem = SolverEnvironment.compairerFromConfig(config,false);
        this.holdem_deck = SolverEnvironment.deckFromConfig(config);
        System.out.println("loading holdem compairer dictionary complete");

        System.out.println("loading shortdeck compairer dictionary...");
        config_name = "resources/yamls/rule_shortdeck_simple.yaml";
        config = this.loadConfig(config_name);
        this.compairer_shortdeck = SolverEnvironment.compairerFromConfig(config,false);
        this.shortdeck_deck = SolverEnvironment.deckFromConfig(config);
        System.out.println("loading shortdeck compairer dictionary complete");
    }

    GameTreeBuildingSettings parseSettings(){
        GameTreeBuildingSettings.StreetSetting flop_ip = new GameTreeBuildingSettings.StreetSetting(
                flop_ip_bet.getText(),
                flop_ip_raise.getText(),
                null,
                flop_ip_allin.isSelected()
        );
        GameTreeBuildingSettings.StreetSetting turn_ip = new GameTreeBuildingSettings.StreetSetting(
                turn_ip_bet.getText(),
                turn_ip_raise.getText(),
                null,
                turn_ip_allin.isSelected()
        );
        GameTreeBuildingSettings.StreetSetting river_ip = new GameTreeBuildingSettings.StreetSetting(
                river_ip_bet.getText(),
                river_ip_raise.getText(),
                null,
                river_ip_allin.isSelected()
        );

        GameTreeBuildingSettings.StreetSetting flop_oop = new GameTreeBuildingSettings.StreetSetting(
                flop_oop_bet.getText(),
                flop_oop_raise.getText(),
                null,
                flop_oop_allin.isSelected()
        );
        GameTreeBuildingSettings.StreetSetting turn_oop = new GameTreeBuildingSettings.StreetSetting(
                turn_oop_bet.getText(),
                turn_oop_raise.getText(),
                turn_oop_donk.getText(),
                turn_oop_allin.isSelected()
        );
        GameTreeBuildingSettings.StreetSetting river_oop = new GameTreeBuildingSettings.StreetSetting(
                river_oop_bet.getText(),
                river_oop_raise.getText(),
                river_oop_donk.getText(),
                river_oop_allin.isSelected()
        );
        return new GameTreeBuildingSettings(flop_ip,turn_ip,river_ip,flop_oop,turn_oop,river_oop);
    }

    private void loadTrainResult(String filename) throws IOException {
        System.out.println("loading train result from " + filename);

        String content = Files.readString(Paths.get(filename));

        JSONObject trainResult = new JSONObject(content);
//        trainResult.put("game_tree", strategy);
        solveConfig = SolveConfig.fromJson(trainResult.getJSONObject("solve_config"));
        treeSettings = GameTreeBuildingSettings.fromJson(trainResult.getJSONObject("tree_settings"));

        System.out.println("load finished!");
    }

    private void saveTrainResult(String filename) throws IOException {
        System.out.println("saving train result to " + filename);

        JSONObject trainResult = new JSONObject();
        JSONObject strategy = game_tree.dumps(false);
        trainResult.put("game_tree", strategy);
        trainResult.put("solve_config", solveConfig.toJson());
        trainResult.put("tree_settings", treeSettings.toJson());
        File output_file = new File(filename);
        FileWriter writer = new FileWriter(output_file);
        writer.write(trainResult.toString());
        writer.flush();
        writer.close();

        System.out.println("save finished!");
    }

    private void onBuildTree(){
        System.out.println("building tree...");
        int mode = this.mode.getSelectedIndex();

        String board = boardstr.getText();
        int board_num = board.split(",").length;
        int round;
        if (board_num == 3){
            round = 2;
        }
        else if (board_num == 4){
            round = 3;
        }
        else if (board_num == 5){
            round = 4;
        }else throw new RuntimeException("board number not valid");

        treeSettings = parseSettings();
        if(mode == 0) {
            this.game_tree = SolverEnvironment.gameTreeFromParams(
                    this.holdem_deck,
                    Float.valueOf(this.pot.getText()) / 2,
                    Float.valueOf(this.pot.getText()) / 2,
                    round,
                    Integer.valueOf(raise_limit.getText()),
                    (float) 0.5,
                    (float) 1.0,
                    Float.valueOf(this.stacks.getText()) + Float.valueOf(this.pot.getText()) / 2,
                    treeSettings
            );
        }else if(mode == 1){
            this.game_tree = SolverEnvironment.gameTreeFromParams(
                    this.shortdeck_deck,
                    Float.valueOf(this.pot.getText()) / 2,
                    Float.valueOf(this.pot.getText()) / 2,
                    round,
                    Integer.valueOf(raise_limit.getText()),
                    (float) 0.5,
                    (float) 1.0,
                    Float.valueOf(this.stacks.getText()) + Float.valueOf(this.pot.getText()) / 2,
                    treeSettings
            );
        }else{
            throw new RuntimeException("game mode unknown");
        }
        System.out.println("build tree complete");

        showTreeButton.setEnabled(true);
        startSolvingButton.setEnabled(true);
        showResultButton.setEnabled(true);
    }

    private void initialize(){
        System.out.println("initializing...");
        try {
            load_compairer();
        }catch (java.io.IOException err){
            err.printStackTrace();
        }
        System.out.println("initialization complete");
    }

    private void solve() throws Exception{
        if (this.game_tree == null){
            System.out.println("please build tree first.");
            return;
        }
        System.out.println("solving...");
        this.solveConfig = new SolveConfig(
                iprange.getText()
                , ooprange.getText()
                , boardstr.getText()
                , mode.getSelectedIndex()
                , iteration.getText()
                , log_interval.getText()
                , algorithm.getSelectedIndex()
                , mc.isSelected()
                , exploitability.getText()
        );

        Solver solver = new ParallelCfrPlusSolver(game_tree
                , Integer.parseInt(threads.getText())
                ,1
                ,1
                , 1
                , 16
        );
        solver.train(solveConfig);

        /*
        String output_strategy_file = "out/demo.json";
        String strategy_json = solver.getTree().dumps(false).toJSONString();
        File output_file = new File(output_strategy_file);
        FileWriter writer = new FileWriter(output_file);
        writer.write(strategy_json);
        writer.flush();
        writer.close();
         */
        System.out.println("solve complete");
    }
}
