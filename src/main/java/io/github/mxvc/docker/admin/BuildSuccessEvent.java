package io.github.mxvc.docker.admin;

import io.github.mxvc.docker.admin.entity.BuildLog;
import io.github.mxvc.base.BaseEvent;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class BuildSuccessEvent extends BaseEvent {


    BuildLog buildLog;
    String version;

    public BuildSuccessEvent(Object source) {
        super(source);
    }
}
