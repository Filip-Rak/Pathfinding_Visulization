package kosmo.pathfinding;

import javafx.application.Platform;
import javafx.scene.control.Label;

public class Execution
{
    // Attributes
    private final double MAX_SPEED = 5;
    private final double MIN_SPEED = 0.1;
    private double speed = 1;
    private final long BASE_DELAY = 100;
    private volatile long timeToWait = BASE_DELAY;
    private boolean paused = true;
    private boolean ceaseExecution = true;
    private boolean isRunning = false;
    private boolean isRefreshed = true;
    private Label speedLabel;

    // Singleton
    private static Execution instance;

    // Constructor
    private Execution() {}

    // Methods
    public static Execution get()
    {
        if(instance == null)
            instance = new Execution();

        return instance;
    }

    public void Wait()
    {
        long startTime = System.currentTimeMillis();
        long waitedTime = 0;

        try
        {
            while ((waitedTime < timeToWait || paused) && !ceaseExecution && isRunning)
            {
                Thread.sleep(10);  // Sleep for short intervals to remain responsive
                waitedTime = System.currentTimeMillis() - startTime;

                // Break will happen if the speed is decreased far enough
                if ((!paused && (timeToWait - waitedTime < 0)) || ceaseExecution) break;
            }
        }
        catch (InterruptedException e)
        {
            System.out.println("Waiting was interrupted.");
        }
    }

    // Getters
    public double getSpeed()
    {
        return this.speed;
    }

    public boolean isPaused()
    {
        return paused;
    }

    // Setters
    public void setSpeed(double speed)
    {
        this.speed = Math.clamp(speed, MIN_SPEED, MAX_SPEED);

        timeToWait = (long)((float)(BASE_DELAY) / this.speed);
        speedLabel.setText(String.format("%.2f", this.speed) + "x");
    }

    public void ceaseExecution()
    {
        if(!isRunning)
            return;

        this.ceaseExecution = true;
        Platform.runLater(() -> OutputConsole.get().writeLn("WRAPPING UP THE EXECUTION"));
    }

    public void startPoint()
    {
        this.ceaseExecution = false;
        this.isRunning = true;
        this.isRefreshed = false;
    }

    public void stopPoint()
    {
        this.isRunning = false;
    }

    public void setSpeedText(Label speedLabel)
    {
        this.speedLabel = speedLabel;
    }

    public void setPaused(boolean paused)
    {
        this.paused = paused;
    }

    public void setRefreshed(boolean refreshed)
    {
        isRefreshed = refreshed;
    }

    // Getters
    public boolean isRunning()
    {
        return this.isRunning;
    }

    public boolean isRefreshed()
    {
        return isRefreshed;
    }

    public double getMaxSpeed()
    {
        return MAX_SPEED;
    }

    public double getMinSpeed()
    {
        return MIN_SPEED;
    }
}
