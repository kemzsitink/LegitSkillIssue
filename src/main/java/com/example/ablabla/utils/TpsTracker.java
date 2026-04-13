package com.example.ablabla.utils;

import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S03PacketTimeUpdate;

import java.util.Arrays;

public class TpsTracker {

    public static final TpsTracker INSTANCE = new TpsTracker();

    private final float[] tickRates = new float[20];
    private int nextIndex = 0;
    private long timeLastTimeUpdate;

    public TpsTracker() {
        reset();
    }

    public void reset() {
        Arrays.fill(tickRates, 20.0f);
        timeLastTimeUpdate = -1;
    }

    public void onPacketReceive(Packet<?> packet) {
        if (packet instanceof S03PacketTimeUpdate) {
            if (timeLastTimeUpdate != -1) {
                float timeElapsed = (float) (System.currentTimeMillis() - timeLastTimeUpdate) / 1000.0F;
                tickRates[nextIndex % tickRates.length] = Math.max(0.0F, Math.min(20.0F, 20.0F / timeElapsed));
                nextIndex++;
            }
            timeLastTimeUpdate = System.currentTimeMillis();
        }
    }

    public float getTps() {
        int numTicks = 0;
        float sumTickRates = 0.0f;
        for (float tickRate : tickRates) {
            if (tickRate > 0) {
                sumTickRates += tickRate;
                numTicks++;
            }
        }
        return numTicks > 0 ? sumTickRates / numTicks : 0.0f;
    }
}
