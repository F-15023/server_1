import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;

public class HttpRequestHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    Channel channel;

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

        RouteRequest routeRequest =  new RouteRequest().parseRouteRequestFromJsonStrong(contentString);

        DefaultFullHttpResponse response = new DefaultFullHttpResponse(httpRequest.protocolVersion(), HttpResponseStatus.OK);
        response.content().writeBytes(responceString.getBytes(StandardCharsets.UTF_8));
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    private RouteRequest parseContent(String jsonContent, String apiMethod) throws Exception {
        switch (apiMethod) {
            case "/route":
                return new RouteRequest().parseRouteRequestFromJsonStrong(jsonContent);
            default:
                throw new Exception("Unknown api method! Check request!");
        }
    }


}
