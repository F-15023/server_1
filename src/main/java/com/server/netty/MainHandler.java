package com.server.netty;

import com.server.routing.RouteRequest;
import com.server.routing.Router;
import io.netty.channel.*;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;

import java.nio.charset.StandardCharsets;

public class MainHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    Channel channel;
    Router router;

    public MainHandler(Router router) {
        this.router = router;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Client connected: " + ctx);
        channel = ctx.channel();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("Error!");
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest httpRequest) throws Exception {
        String apiMethod = httpRequest.uri();
        String contentString = httpRequest.content().toString(StandardCharsets.UTF_8);
        String responseString = getResponceString(apiMethod, contentString);

        DefaultFullHttpResponse response = new DefaultFullHttpResponse(httpRequest.protocolVersion(), HttpResponseStatus.OK);
        response.content().writeBytes(responseString.getBytes(StandardCharsets.UTF_8));
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    private String getResponceString(String apiMethod, String contentString) throws Exception {
        switch (apiMethod) {
            case "/route":
                return getRoute(contentString);
            default:
                throw new Exception("Unknown api method! Check request!");
        }
    }

    private String getRoute(String contentString) throws Exception {
        RouteRequest routeRequest = new RouteRequest().parseRouteRequestFromJsonStrong(contentString);
        return router.getRouteInWKT4326(routeRequest.getPoints());
    }
}
