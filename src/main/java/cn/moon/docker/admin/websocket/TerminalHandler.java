package cn.moon.docker.admin.websocket;

import cn.hutool.core.date.DateUtil;
import cn.moon.docker.admin.entity.Host;
import cn.moon.docker.admin.service.HostService;
import cn.moon.docker.sdk.engine.DockerSdkManager;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.Frame;
import com.github.dockerjava.core.command.ExecStartResultCallback;
import jakarta.annotation.Resource;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Slf4j
@Component
public class TerminalHandler extends AbstractWebSocketHandler {


    @Resource
    HostService hostService;

    @Resource
    DockerSdkManager dockerManager;

    Map<String, OutputStream> inputStreamMap = new ConcurrentHashMap<>();

    Map<String, Closeable[]> callbackMap = new ConcurrentHashMap<>();



    @SneakyThrows
    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        log.info("打开连接 {} {}", session.getId(), session.getUri());

        send(session, "服务器连接成功\n");

        String query = session.getUri().getQuery();


        String[] arr = query.split("&");

        Map<String, String> params = new HashMap<>();
        for (String s : arr) {
            String[] kv = s.split("=");
            params.put(kv[0], kv[1]);
        }

        String hostId = params.get("hostId");
        String containerId = params.get("containerId");


        Host host = hostService.findOne(hostId);

        send(session, "连接容器[" + host.getName() + "]中...\n");

        DockerClient client = dockerManager.getClient(host);


        String id = client.execCreateCmd(containerId)
                .withCmd("/bin/sh")
                .withTty(true)
                .withAttachStdout(true)
                .withAttachStderr(true)
                .withAttachStdin(true)
                .exec().getId();


        PipedInputStream pipedInputStream = new PipedInputStream();
        inputStreamMap.put(session.getId(), new PipedOutputStream(pipedInputStream));


        ExecStartResultCallback callback = client.execStartCmd(id)
                .withDetach(false)
                .withTty(true)
                .withStdIn(pipedInputStream)
                .exec(new ExecStartResultCallback() {

                    @Override
                    public void onNext(Frame frame) {
                        byte[] bytes = frame.getPayload();
                        send(session, new String(bytes));
                        super.onNext(frame);
                    }

                    @Override
                    public void onStart(Closeable closeable) {
                        send(session, "连接容器成功 " + DateUtil.now() + "\n");
                    }


                    @Override
                    public void onError(Throwable throwable) {
                        System.out.println("onError");

                        throwable.printStackTrace();
                        send(session, "异常: " + throwable.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        System.out.println("onComplete");
                        send(session, "连接任务完成\n");
                    }

                    @Override
                    public void close() throws IOException {
                        System.out.println("close");
                        send(session, "close");
                    }
                });

        callbackMap.put(session.getId(), new Closeable[]{callback, client});

    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();

        OutputStream os = inputStreamMap.get(session.getId());
        if (os != null) {
            os.write(payload.getBytes(StandardCharsets.UTF_8));
        } else {
            send(session, "与容器的连接异常");
        }

    }



    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        log.info("关闭连接 {} {}", session.getId(), status);


        IOUtils.closeQuietly(inputStreamMap.get(session.getId()));
        IOUtils.closeQuietly(callbackMap.get(session.getId()));

        inputStreamMap.remove(session.getId());
        callbackMap.remove(session.getId());
    }

    @SneakyThrows
    private static void send(WebSocketSession session, String str) {
        session.sendMessage(new TextMessage(str));
    }

}
