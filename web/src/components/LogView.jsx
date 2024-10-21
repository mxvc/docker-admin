import {LazyLog, ScrollFollow} from "react-lazylog";
import React from "react";
import {SysUtil} from "@tmgg/tmgg-base";

/**
 * https://mozilla-frontend-infra.github.io/react-lazylog/
 */
export default class extends React.Component {

    render() {
        const headers = SysUtil.getHeaders();
        let url = this.props.url;
        if (!url.startsWith("ws://")) {
            url = "ws://" + location.host +  url
            console.log('调整后的ws url')
        }


        return <ScrollFollow
            startFollowing={true}
            render={({follow, onScroll}) => (
                <LazyLog url={url}
                         height={500}
                         follow={follow}
                         fetchOptions={{credentials: 'include', ...headers}}
                         websocket={true}
                         onScroll={onScroll}/>
            )}
        />

    }
}
