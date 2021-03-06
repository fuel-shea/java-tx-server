package com.fuelpowered;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;

/**
 * Based on code from: http://netty.io/4.0/xref/io/netty/example/http/websocketx/server/WebSocketServer.html
 */
public class WebSocketFrameHandler extends SimpleChannelInboundHandler<WebSocketFrame> {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketFrameHandler.class);
    private static final MongoDatabase db = MongoGetter.getDB();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, WebSocketFrame frame) throws Exception {
        // ping and pong frames already handled

        if (frame instanceof TextWebSocketFrame) {
            long gumCount = countGums();
            updateGumCount(gumCount);

            String request = ((TextWebSocketFrame) frame).text();
            logger.info("{} received {} -- db count={}", ctx.channel(), request, gumCount);

            String response = gumCount + " :: " + request.toUpperCase(Locale.CANADA);
            ctx.channel().writeAndFlush(new TextWebSocketFrame(response));
        } else {
            String message = "unsupported frame type: " + frame.getClass().getName();
            throw new UnsupportedOperationException(message);
        }
    }

    protected long countGums() {
        return db.getCollection("game_user_maps").count();
    }

    protected void updateGumCount(long gumCount) {
        db.getCollection("gum_counts").findOneAndUpdate(
                new BasicDBObject("name", "experimentalCount"),
                new BasicDBObject("$set", new BasicDBObject("count", gumCount)),
                new FindOneAndUpdateOptions().upsert(true)
        );
    }
}
