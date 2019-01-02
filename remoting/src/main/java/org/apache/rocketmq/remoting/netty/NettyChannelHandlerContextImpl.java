package org.apache.rocketmq.remoting.netty;/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import java.net.SocketAddress;
import org.apache.rocketmq.logging.InternalLogger;
import org.apache.rocketmq.logging.InternalLoggerFactory;
import org.apache.rocketmq.remoting.RemotingChannel;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.apache.rocketmq.remoting.protocol.RemotingCommand;

public class NettyChannelHandlerContextImpl implements RemotingChannel {
    public static final String ROCKETMQ_REMOTING = "RocketmqRemoting";

    private static final InternalLogger log = InternalLoggerFactory.getLogger(ROCKETMQ_REMOTING);

    private final ChannelHandlerContext channelHandlerContext;

    public NettyChannelHandlerContextImpl(ChannelHandlerContext channelHandlerContext) {
        this.channelHandlerContext = channelHandlerContext;
    }

    @Override
    public SocketAddress localAddress() {
        return channelHandlerContext.channel().localAddress();
    }

    @Override
    public SocketAddress remoteAddress() {
        return channelHandlerContext.channel().remoteAddress();
    }

    @Override
    public boolean isWritable() {
        return channelHandlerContext.channel().isWritable();
    }

    @Override
    public boolean isActive() {
        return channelHandlerContext.channel().isActive();
    }

    @Override
    public void close() {
        final String addrRemote = RemotingHelper.parseChannelRemoteAddr(channelHandlerContext.channel());
        channelHandlerContext.close().addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                log.info("CloseChannel: close the connection to remote address[{}] result: {}", addrRemote,
                    future.isSuccess());
            }
        });
    }

    @Override
    public void reply(final RemotingCommand command) {
        channelHandlerContext.writeAndFlush(command);
    }

    public ChannelHandlerContext getChannelHandlerContext() {
        return channelHandlerContext;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        final NettyChannelHandlerContextImpl that = (NettyChannelHandlerContextImpl) o;

        return channelHandlerContext != null ? channelHandlerContext.equals(that.channelHandlerContext) : that.channelHandlerContext == null;

    }

    @Override
    public int hashCode() {
        return channelHandlerContext != null ? channelHandlerContext.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "NettyChannelHandlerContextImpl [channel=" + channelHandlerContext + "]";
    }
}