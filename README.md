# WebSocket_HandshakeRequest_to_Servlet31_HttpServletRequest_Wrapper
Wrapping WebSocket HandshakeRequest to Servlet 3.1 HttpServletRequest
## Revision History
Latest: v0.1.0 (Oct 4, 2020)  
Please refer to [release_note.md](./release_note.md) file  

## Requirements
0. Java 1.7 or later
1. Servlet 3.1 (may could work for lower version, using manual builds)
2. WebSocket JSR 356

## Overview
A simple(and limited) implementation to allow easier work with WebSocket `HandshakeRequest` as a `HttpServletRequest` on a servlet container(like tomcat,...).

It's actually more than a simple wrapping type, rather a lib.

## Sample Usage
Simply, instancing a `HandshakeRequest_HttpServletRequest` during WebSocket handshaking. Considering following example:
```java
/*the WS end-point (after handshake). Mind the configurator*/
@ServerEndpoint(value="/endpoint_path",configurator = My_EndPoint_Configurator.class)
public class My_EndPoint {
    //... implementing the required methods...
}

/*The handshake handler(aka configurator), by extending class javax.websocket.server.ServerEndpointConfig.Configurator*/
public class My_EndPoint_Configurator extends ServerEndpointConfig.Configurator{
//override the modifyHandshake method
//modifyHandshake method, where handshake begins
@Override
    public void modifyHandshake(ServerEndpointConfig sec, HandshakeRequest request, HandshakeResponse response) {
        /*wrap the HandshakeRequest to HttpServletRequest*/
        HttpServletRequest _http_req = HandshakeRequest_HttpServletRequest.wrapperForHandshakeRequest(request);
        /*grabbing the session(if available)*/
        HttpSession _the_session = _req.getSession(false); //ALWAYS pass false
        if(_the_session != null){
            //using the http session
        }else{
            //there is no session! maybe close the ws? whatever
        }
        //getting request cookies, params and headers also possible... (nothing more)
    }
}
```
*code snipped0: sample usage*

## Limitations
Since there is no access to underlying low-level IO, and servlet context, and relative method calls will result as `UnsupportedOperationException` exception.

## Utilizing The Artifact
As maven
```xml
<dependency>
  <groupId>com.github.911992</groupId>
  <artifactId>WebSocket_HandshakeRequest_to_Servlet31_HttpServletRequest_Wrapper</artifactId>
  <version>0.1.0</version>
</dependency>
```
*code snippet 1: maven dependency*

## 