package snakepackage;

import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JFrame;

import enums.GridSize;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import static java.lang.Double.NEGATIVE_INFINITY;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 * @author jd-
 *
 */
public class SnakeApp {

    private static SnakeApp app;
    public static final int MAX_THREADS = 8;
    Snake[] snakes = new Snake[MAX_THREADS];
    private static final Cell[] spawn = {
        new Cell(1, (GridSize.GRID_HEIGHT / 2) / 2),
        new Cell(GridSize.GRID_WIDTH - 2,
        3 * (GridSize.GRID_HEIGHT / 2) / 2),
        new Cell(3 * (GridSize.GRID_WIDTH / 2) / 2, 1),
        new Cell((GridSize.GRID_WIDTH / 2) / 2, GridSize.GRID_HEIGHT - 2),
        new Cell(1, 3 * (GridSize.GRID_HEIGHT / 2) / 2),
        new Cell(GridSize.GRID_WIDTH - 2, (GridSize.GRID_HEIGHT / 2) / 2),
        new Cell((GridSize.GRID_WIDTH / 2) / 2, 1),
        new Cell(3 * (GridSize.GRID_WIDTH / 2) / 2,
        GridSize.GRID_HEIGHT - 2)};
    private JFrame frame;
    private static Board board;
    int nr_selected = 0;
    Thread[] thread = new Thread[MAX_THREADS];
    private static final Object SYNCRO = new Object();
    private int firstdead = -1;
    private String muerte = "";

    public SnakeApp() {
        JButton star, pause, resume;
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        frame = new JFrame("The Snake Race");
        frame.setLayout(new BorderLayout());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // frame.setSize(618, 640);
        frame.setSize(GridSize.GRID_WIDTH * GridSize.WIDTH_BOX + 17,
                GridSize.GRID_HEIGHT * GridSize.HEIGH_BOX + 40);
        frame.setLocation(dimension.width / 2 - frame.getWidth() / 2,
                dimension.height / 2 - frame.getHeight() / 2);
        board = new Board();

        frame.add(board, BorderLayout.CENTER);

        final JPanel actionsBPabel = new JPanel();
        actionsBPabel.setLayout(new FlowLayout());

        actionsBPabel.add(star = new JButton("star"));
        actionsBPabel.add(pause = new JButton("pause"));
        actionsBPabel.add(resume = new JButton("resume"));
        star.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                for (int i = 0; i < thread.length; i++) {
                    if (!thread[i].isAlive()) {
                        thread[i].start();
                    }

                }
            }

        });
        pause.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                double max;
                int sna = 0;
                max = NEGATIVE_INFINITY;
                for (int i = 0; i < snakes.length; i++) {
                    snakes[i].setPausa(true);
                    if (snakes[i].getBody().size() > max) {
                        max = snakes[i].getBody().size();
                        sna = i;
                    }
                }
                if (firstdead == -1) {
                    muerte = "no dead";
                } else {
                    muerte = "SNAKE NUMBER " + String.valueOf(firstdead);
                }
                
                JOptionPane.showMessageDialog(null, "the longest snake is the number: " + (sna + 1) + "\nFirst dead: " + muerte);
            }
        });

        resume.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent ae) {
                synchronized (SYNCRO) {
                    SYNCRO.notifyAll();
                }
            }

        });
        frame.add(actionsBPabel, BorderLayout.SOUTH);

    }

    public static void main(String[] args) {
        app = new SnakeApp();
        app.init();
    }

    private void init() {

        for (int i = 0; i != MAX_THREADS; i++) {

            snakes[i] = new Snake(i + 1, spawn[i], i + 1);
            snakes[i].addObserver(board);
            thread[i] = new Thread(snakes[i]);

        }

        frame.setVisible(true);

        while (true) {
            int x = 0;
            for (int i = 0; i != MAX_THREADS; i++) {
                if (snakes[i].isSnakeEnd() == true) {
                    if (x == 0) {
                        firstdead = i + 1;
                    }
                    x++;
                }
            }
            if (x == MAX_THREADS) {
                break;
            }
        }

        System.out.println("Thread (snake) status:");
        for (int i = 0; i != MAX_THREADS; i++) {
            System.out.println("[" + i + "] :" + thread[i].getState());
        }

    }

    public static SnakeApp getApp() {
        return app;
    }

    public static Object getSYNCRO() {
        return SYNCRO;
    }

}
