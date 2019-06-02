package src.assignment1;

/*
 * author Jack Hosking
 * Student ID: 16932920
 */

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

public class FactoryControlGUI extends JPanel implements ActionListener {

    private JButton startMachines, stopMachines;
    private DrawPanel drawPanel;


    private static final int MAX_MACHINES = 20;
    private static final int MAX_COOLERS = 3;
    private static final int RECT_WIDTH = 20;
    private static final int OPTIMAL_ZONE_MIN = 140;
    private static final int OPTIMAL_ZONE_MAX = 440;
    private static final int TOTAL_ZONE = 540;
    private static final int X_1 = 10, X_2 = 750;


    private List<Machine> machines = new ArrayList<>();
    private List<MonitoringCooler> coolers = new ArrayList<>();


    public FactoryControlGUI() {
        super(new BorderLayout());

        drawPanel = new DrawPanel();
        add(drawPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        startMachines = new JButton("Start Machines");
        startMachines.addActionListener(this);
        buttonPanel.add(startMachines);

        stopMachines = new JButton("Stop Machines");
        stopMachines.addActionListener(this);
        buttonPanel.add(stopMachines);

        add(buttonPanel, BorderLayout.SOUTH);

        for (int i = 0; i < MAX_MACHINES; i++) {
            machines.add(new Machine(0, 250));
        }

        for (int i = 0; i < MAX_COOLERS; i++) {
            coolers.add(new MonitoringCooler(machines, 25));
        }

        Timer timer = new Timer(25, this);
        timer.start();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if (source == startMachines) {
            System.out.println("Current number of machines: " + machines.size());
            System.out.println("Current number of coolers:" + coolers.size());
            System.out.println("Starting to monitor their temperatures...");
            for (Machine m : machines) {
                if(m.getCurrentTemp() < m.getMaxTemp() && m.getCurrentTemp() > m.getMinTemp()){
                    m.startMachine();
                }
            }

            //coolers can be started here, or in the factorycontrolgui
            for (MonitoringCooler cooler : coolers) {
                cooler.startCooler();
            }
        }

        if (source == stopMachines) {
            System.out.println("HAULTING MACHINES");
            for (Machine m : machines) {
                m.stopMachine();
            }
        }
        drawPanel.repaint();
    }

    public void drawTempLines(Graphics g) {
        g.setColor(Color.black);
        g.drawLine(X_1, TOTAL_ZONE, X_2, TOTAL_ZONE);
        g.drawString("0", X_2 + 5, TOTAL_ZONE);

        g.setColor(Color.blue);
        g.drawLine(X_1, OPTIMAL_ZONE_MAX, X_2, OPTIMAL_ZONE_MAX);
        g.drawString("50", X_2 + 5, OPTIMAL_ZONE_MAX);

        g.setColor(Color.black);
        g.drawLine(X_1, OPTIMAL_ZONE_MAX - OPTIMAL_ZONE_MIN, X_2, OPTIMAL_ZONE_MAX - OPTIMAL_ZONE_MIN);
        g.drawString("125", X_2 + 5, OPTIMAL_ZONE_MAX - OPTIMAL_ZONE_MIN);

        g.setColor(Color.red);
        g.drawLine(X_1, OPTIMAL_ZONE_MIN, X_2, OPTIMAL_ZONE_MIN);
        g.drawString("200", X_2 + 5, OPTIMAL_ZONE_MIN);


        g.setColor(Color.black);
        g.drawLine(X_1, 40, X_2, 40);
        g.drawString("250", X_2 + 5, 40);
    }


    private class DrawPanel extends JPanel {

        public DrawPanel() {
            super();
            setPreferredSize(new Dimension(800, 600));
            setBackground(Color.WHITE);
        }

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            int rect_x = 10;
            g.setFont(new Font("TimesRoman", Font.PLAIN, 24));

            drawTempLines(g);

            for (Machine m : machines) {
                int currentTemp = TOTAL_ZONE - (m.getCurrentTemp() * 2);

                if (currentTemp <= TOTAL_ZONE && currentTemp > OPTIMAL_ZONE_MAX) {
                    g.setColor(Color.blue);
                } else if (currentTemp <= OPTIMAL_ZONE_MAX && currentTemp > OPTIMAL_ZONE_MIN) {
                    // optimal temperature
                    g.setColor(Color.orange);
                } else if (currentTemp <= OPTIMAL_ZONE_MIN && currentTemp > 40) {
                    g.setColor(Color.red);
                } else {
                    g.setColor(Color.black);
                }

                g.fillRect(rect_x, currentTemp, RECT_WIDTH, m.getCurrentTemp() * 2);

                if (m.isCoolerConnected()) {
                    g.setColor(Color.red);
                    g.drawString("+", rect_x + 5, TOTAL_ZONE + 20);
                } else {
                    g.setColor(Color.blue);
                    g.drawString("-", rect_x + 5, TOTAL_ZONE + 20);
                }

                rect_x += 35;
            }
        }
    }


    public static void main(String[] args) {
        JFrame frame = new JFrame("Factory Machine Control");

        // kill all threads when frame closes
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(new FactoryControlGUI());
        frame.pack();

        // position the frame in the middle of the screen
        Toolkit tk = Toolkit.getDefaultToolkit();
        Dimension screenDimension = tk.getScreenSize();
        Dimension frameDimension = frame.getSize();
        frame.setLocation((screenDimension.width - frameDimension.width) / 2,
                (screenDimension.height - frameDimension.height) / 2);
        frame.setVisible(true);
        // now display something while the main thread is still alive
    }
}
