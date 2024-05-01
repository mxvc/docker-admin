package cn.moon.docker.admin.websocket;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.URLUtil;
import cn.moon.docker.admin.entity.Host;
import cn.moon.docker.admin.service.HostService;
import cn.moon.docker.sdk.DockerSdkManager;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.command.AttachContainerCmd;
import com.github.dockerjava.api.command.ExecCreateCmdResponse;
import com.github.dockerjava.api.model.Frame;
import com.github.dockerjava.core.async.ResultCallbackTemplate;
import com.github.dockerjava.core.command.AttachContainerResultCallback;
import com.github.dockerjava.core.command.ExecStartResultCallback;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;
import cn.moon.logview.TailFile;

import javax.annotation.Resource;
import javax.naming.ldap.StartTlsRequest;
import java.io.*;
import java.net.URLDecoder;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;


@Slf4j
@Component
public class TerminalHandler extends AbstractWebSocketHandler {


    @Resource
    HostService hostService;

    @Resource
    DockerSdkManager dockerManager;
    private DynamicByteArrayInputStream is;




    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        System.out.println(payload);

        if(payload.equals("\r")){
            payload  ="\r\n";
        }

        is.addData(payload.getBytes(StandardCharsets.UTF_8));
        send(session, payload);
    }



    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        log.info("打开连接 {} {}", session.getId(), session.getUri());

        is = new DynamicByteArrayInputStream();
        is.addData("ls -l\n".getBytes(StandardCharsets.UTF_8));
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
        is.addData("ls -l\n".getBytes(StandardCharsets.UTF_8));


        client.execStartCmd(id)
                .withDetach(false)
                .withTty(true)
                .withStdIn(is)
                .exec(new ExecStartResultCallback() {

                    @Override
                    public void onNext(Frame frame) {
                        byte[] bytes = frame.getPayload();

                        send(session, new String(bytes));
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


    }

    private static void send(WebSocketSession session, String str) {
        try {
            session.sendMessage(new TextMessage(str));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        log.info("关闭连接 {} {}", session.getId(), status);
    }


}