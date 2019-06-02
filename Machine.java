package src.assignment1;

/*
 * author Jack Hosking
 * Student ID: 16932920
 */

import java.util.Random;

public class Machine {

    private static Random random = new Random();

    private boolean isRunning;
    private int minTemp, maxTemp;
    private int currentTemp = 22;
    private Cooler connectedCooler;

    public Machine(int minTemp, int maxTemp) {
        this.minTemp = minTemp;
        this.maxTemp = maxTemp;
    }

    public void startMachine() {
        if (!this.isRunning) {
            this.isRunning = true;
            new Thread(() -> run()).start();
        }
    }

    public boolean isRunning() {
        return this.isRunning;
    }

    public void stopMachine() {
        this.isRunning = false;
    }

    public synchronized int getCurrentTemp() {
            return this.currentTemp;

    }

    public int getMinTemp() {
        return this.minTemp;
    }

    public int getMaxTemp() {
        return this.maxTemp;
    }

    public boolean connectCooler(Cooler cooler) {
        if (this.connectedCooler == null) {
            this.connectedCooler = cooler;
            return true;
        }
        return false;
    }

    public synchronized boolean isCoolerConnected() {
        return connectedCooler != null;
    }

    public synchronized void disconnectCooler() {
        this.connectedCooler = null;
    }

    public void run() {
        while (this.isRunning) {
            if (connectedCooler != null) {
                this.currentTemp -= connectedCooler.getCoolingFactor();
            } else {
                this.currentTemp += random.nextInt(6);
            }
            // this is just spam
            // System.out.println("Machine: " + this.currentTemp + " : " + this.minTemp + " : " + this.maxTemp);
            if (this.currentTemp <= minTemp || this.currentTemp >= maxTemp) {
                this.isRunning = false;
                System.out.println("MACHINE OVERHEATED AND DIED.");
                throw new MachineTemperatureException();
            }
            try {
                Thread.sleep(200);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
