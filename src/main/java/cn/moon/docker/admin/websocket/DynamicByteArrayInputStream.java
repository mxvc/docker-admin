package cn.moon.docker.admin.websocket;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class DynamicByteArrayInputStream extends InputStream {

    private final BlockingQueue<Byte> dataQueue = new LinkedBlockingQueue<>();

    // 添加数据的方法
    public void addData(byte[] newData) {
        for (byte b : newData) {
            dataQueue.add(b);
        }
    }

    @Override
    public int read() throws IOException {
        try {
            Byte b = dataQueue.take(); // 如果队列为空，此方法会阻塞直到有新的数据
            return b & 0xFF; // 返回队列头部的字节
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt(); // 重新设置中断状态
            return -1;
        }
    }

    @Override
    public int available() throws IOException {
        int size = dataQueue.size();

        System.out.println("available:" + size);

        return size;
    }
}
