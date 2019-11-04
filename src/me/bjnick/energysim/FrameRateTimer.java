package me.bjnick.energysim;

public abstract class FrameRateTimer extends Thread {

    float desiredUPS = 90;
    float desiredFPS = 60;

    volatile boolean running = true;
    boolean updating = true;

    boolean drawOnUpdate = false;

    long timeStarted;

    float elapsedSimulationTime;

    float instantFPS = 0;
    float instantUPS = 0;

    @Override
    public void run() {

        timeStarted = System.nanoTime();
        long lastFrame = timeStarted;
        long lastUpdate = timeStarted;

        elapsedSimulationTime = 0;

        while (running) {

            long now = System.nanoTime();
            boolean shouldDraw = false;

            float deltaUpdate = (now - lastUpdate) / 1000000000f;

            if (deltaUpdate > 1f / desiredUPS) {
                if (updating) {
                    update(deltaUpdate);
                    elapsedSimulationTime += deltaUpdate;
                    shouldDraw = true;
                    instantUPS = 1f / deltaUpdate;
                }
                lastUpdate = now;
            }

            now = System.nanoTime();

            float deltaFrame = (now - lastFrame) / 1000000000f;

            if ((shouldDraw || !drawOnUpdate) && deltaFrame > 1f / desiredFPS) {
                render();
                lastFrame = now;
                instantFPS = 1f / deltaFrame;
            }
        }
    }

    public abstract void update(float delta);

    public abstract void render();

}
