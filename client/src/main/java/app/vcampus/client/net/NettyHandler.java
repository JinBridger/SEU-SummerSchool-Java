package app.vcampus.client.net;

import app.vcampus.server.utility.Request;
import app.vcampus.server.utility.Response;
import com.google.gson.Gson;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

@Slf4j
public class NettyHandler extends ChannelInboundHandlerAdapter {
    private final Gson gson = new Gson();
    private final Map<UUID, Consumer<Response>> callbacks = new HashMap<>();
    private ChannelHandlerContext ctx;

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        log.info("[{}] Connected", ctx.channel().id());
        this.ctx = ctx;
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        log.info("[{}] Disconnected", ctx.channel().id());
        this.ctx = null;
    }

    @Override
    public void channelRead(@NonNull ChannelHandlerContext ctx, @NonNull Object msg) {
        ByteBuf in = (ByteBuf) msg;
        try {
            Response response = gson.fromJson(in.toString(CharsetUtil.UTF_8), Response.class);
            if (!callbacks.containsKey(response.getId())) {
                throw new IllegalStateException("Callback not found");
            }
            callbacks.get(response.getId()).accept(response);
        } catch (Exception e) {
            log.error("[{}] Exception: {}", ctx.channel().id(), e.getMessage());
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    public void sendRequest(Request request, Consumer<Response> callback) {
        if (ctx == null) {
            throw new IllegalStateException("Channel not connected");
        }

        callbacks.put(request.getId(), callback);
        ctx.writeAndFlush(Unpooled.copiedBuffer(gson.toJson(request), CharsetUtil.UTF_8));
    }

}
