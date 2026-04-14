package com.client.legitskillissue.utils;

import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S03PacketTimeUpdate;

public class TpsTracker {

    public static final TpsTracker INSTANCE = new TpsTracker();

    // Lưu thời điểm nhận S03PacketTimeUpdate gần nhất
    private long lastPacketTime = -1;

    // Circular buffer chỉ chứa các mẫu TPS thực tế đã đo được
    private static final int SAMPLE_SIZE = 10;
    private final float[] samples = new float[SAMPLE_SIZE];
    private int count = 0;   // tổng số mẫu đã ghi (không reset)
    private int head  = 0;   // vị trí ghi tiếp theo trong buffer

    public TpsTracker() {
        reset();
    }

    public void reset() {
        lastPacketTime = -1;
        count = 0;
        head  = 0;
        // Không fill mặc định — chưa có dữ liệu thì không giả vờ có
        for (int i = 0; i < SAMPLE_SIZE; i++) samples[i] = 0f;
    }

    public void onPacketReceive(Packet<?> packet) {
        if (!(packet instanceof S03PacketTimeUpdate)) return;

        long now = System.currentTimeMillis();

        if (lastPacketTime != -1) {
            float elapsed = (now - lastPacketTime) / 1000.0f; // giây

            // S03 được gửi mỗi 20 tick phía server
            // TPS thực = 20 / elapsed (vì 20 tick mất `elapsed` giây)
            // Clamp [0, 20] — không thể vượt 20 TPS
            float tps = Math.min(20.0f, 20.0f / elapsed);

            samples[head] = tps;
            head  = (head + 1) % SAMPLE_SIZE;
            count++;
        }

        lastPacketTime = now;
    }

    /**
     * Trả về TPS trung bình từ các mẫu thực tế đã đo.
     * Nếu chưa có đủ dữ liệu (< 2 packet), trả về 20.0 (chưa biết).
     */
    public float getTps() {
        int available = Math.min(count, SAMPLE_SIZE);
        if (available == 0) return 20.0f; // chưa có dữ liệu

        float sum = 0f;
        for (int i = 0; i < available; i++) {
            sum += samples[i];
        }
        return sum / available;
    }
}
