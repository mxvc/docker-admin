package cn.moon.docker.sdk.callback;

import com.github.dockerjava.api.async.ResultCallbackTemplate;
import com.github.dockerjava.api.model.PullResponseItem;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

@Slf4j
@Getter
public class PullImageCallback extends ResultCallbackTemplate<PullImageCallback, PullResponseItem> {

    private final String logFileId;
    private String error;
    LogBuffer logBuffer = new LogBuffer(log);

    public PullImageCallback(String logFileId) {
        this.logFileId = logFileId;
    }

    @Override
    public void onNext(PullResponseItem item) {
        MDC.put("logFileId", this.logFileId);
        if (item.isErrorIndicated()) {
            this.error = item.getError();
        }
        logBuffer.tryLog(item);
    }

}
