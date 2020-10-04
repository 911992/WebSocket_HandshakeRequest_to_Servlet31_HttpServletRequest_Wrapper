/*
 * Copyright (c) 2020, https://github.com/911992 All rights reserved.
 * License BSD 3-Clause (https://opensource.org/licenses/BSD-3-Clause)
 */

 /*
WebSocket_HandshakeRequest_to_Servlet31_HttpServletRequest_Wrapper
File: HandshakeRequest_HttpServletRequest.java
Created on: Oct 4, 2020 5:57:37 AM
    @author https://github.com/911992
 
History:
    initial version: 0.1.0(20201003)
 */
package github_911992.lib.jsr356.wrapper.servlet31;

import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.websocket.server.HandshakeRequest;

/**
 * Wrapper for WebSocket {@link HandshakeRequest} to relative
 * {@link HttpServletRequest} type.
 * <p>
 * <b>NOTE: </b> <u>limited</u> functionalities are provided by this wrap type.
 * Since there is no way to access the related(possible) servlet context and
 * real underlying LL request.<br>
 * Unimplemented methods will throw {@link UnsupportedOperationException}
 * exception.
 * </p>
 * <p>
 * This is <b>highly</b> recommended to use
 * {@link HandshakeRequest_HttpServletRequest#wrapperForHandshakeRequest(javax.websocket.server.HandshakeRequest)}
 * over instancing.<br>
 * Since some container <i>may</i> implement the {@link HandshakeRequest} as a
 * {@link HttpServletRequest} too.
 * </p>
 * <p>
 * The main reason behind this wrapper class is allowing the dev deal with
 * user's session, and accessing cookies of the client.
 * </p>
 * <p>
 * <b>NOTE:</b> Only session, and request header, params are accessible by this
 * wrapper. <b>No</b> any server-side request attributes/context is
 * accessible(such as {@link #getAttribute(java.lang.String)}.
 * </p>
 * <p>Usage:</p>
 * <pre>
 * public class Ws_Handshake_Handler extends ServerEndpointConfig.Configurator{
 * ...
 *  &#64;Override
 *  public void modifyHandshake(ServerEndpointConfig sec,
 *                              HandshakeRequest request,
 *                              HandshakeResponse response) {
 *      HandshakeRequest_HttpServletRequest _http_servlet_req 
 *                = HandshakeRequest_HttpServletRequest.wrapperForHandshakeRequest(request);
 *      //or new HandshakeRequest_HttpServletRequest(request);
 *      //using _http_servlet_req...
 *  }
 * ...
 * }
 * </pre>
 * 
 * @author https://github.com/911992
 */
public class HandshakeRequest_HttpServletRequest extends Null_HttpServletRequest{

    /**
     * The instance of related websocket handshake.
     */
    private HandshakeRequest websocketHandshakeRequest;

    /**
     * @return the associated {@link HandshakeRequest} to this instance.
     */
    public HandshakeRequest getWebsocketHandshakeRequest() {
        return websocketHandshakeRequest;
    }

    /**
     * Sets the working {@link HandshakeRequest}, also processes it for caching
     * the cookies related to this handshake request.
     * <p>
     * <b>Note:</b> in case this method should be overiden, either call the this
     * actual impl, or call {@link #processHandshakeRequest()} for processing
     * cookies.
     * </p>
     *
     * @param arg_websocketHandshakeRequest the handshake request should be wrapped
     * (must not be {@code null})
     * @see #processHandshakeRequest()
     */
    public void setWebsocketHandshakeRequest(HandshakeRequest arg_websocketHandshakeRequest) {
        this.websocketHandshakeRequest = arg_websocketHandshakeRequest;
        processHandshakeRequest();
    }

    /**
     * Checks if the given {@link HandshakeRequest} is actually a concreted
     * {@link HttpServletRequest} to cast and return, or an instance of
     * {@link HandshakeRequest_HttpServletRequest} should be created.
     * <p>
     * Since a container <i>may</i> implement(treat) both handshake and actual
     * request as one type, so returning the actual concreted
     * {@link HttpServletRequest} instance has higher priority, over a wrapping
     * type.
     * </p>
     *
     * @param arg_handshake_req the non-{@code null} {@link HandshakeRequest}
     * should be wrapped.
     * @return the same given {@code arg_handshake_req} if it's also a
     * {@link HttpServletRequest}, or a new instance of
     * {@link HandshakeRequest_HttpServletRequest}
     */
    public static HttpServletRequest wrapperForHandshakeRequest(HandshakeRequest arg_handshake_req) {
        if ((arg_handshake_req instanceof HttpServletRequest)) {
            return (HttpServletRequest) arg_handshake_req;
        }
        return new HandshakeRequest_HttpServletRequest(arg_handshake_req);
    }

    /**
     * Default constructor.
     * <p>
     * During instancing, method {@link #setWebsocketHandshakeRequest(javax.websocket.server.HandshakeRequest)
     * } is called, same cookie processing and cache should be done during
     * instancing.
     * </p>
     * <p>
     * <b>Note:</b> in case method {@link #setWebsocketHandshakeRequest(javax.websocket.server.HandshakeRequest)
     * } is overiden, make sure method {@link #processHandshakeRequest() } is
     * called before any cookie access is appreciated.
     * </p>
     *
     * @param arg_websocketHandshakeRequest non-{@code null}
     * {@link HandshakeRequest} should be wrapped.
     */
    public HandshakeRequest_HttpServletRequest(HandshakeRequest arg_websocketHandshakeRequest) {
        setWebsocketHandshakeRequest(arg_websocketHandshakeRequest);
    }

    /**
     * Holds the cookeis related to request handshake in cache way.
     *
     * @see #processHandshakeRequest()
     */
    private Cookie[] cookies;

    /**
     * search for all {@code Cookie} in headers, and process the content as
     * cookies.
     * <p>
     * Results are supposed to be saved(cached) in {@link #cookies} variable
     * </p>
     * <p>
     * If the handshake request has no any cookie(including session tracker
     * cookie), then the {@link #cookies} will be {@code null}
     * </p>
     */
    protected void processHandshakeRequest() {
        cookies = null;
        if (websocketHandshakeRequest == null) {
            return;
        }
        List<String> _cooks_list = websocketHandshakeRequest.getHeaders().get("Cookie");
        if (_cooks_list != null) {
            ArrayList<Cookie> _cs = new ArrayList<>(5);
            for (String _chstr : _cooks_list) {
                String[] _cstr = _chstr.split("\\;\\s{1}");
                _cs.ensureCapacity(_cs.size() + _cstr.length);
                for (String _1c : _cstr) {
                    String[] _cv = _1c.split("\\=",2);
                    String _cname = _cv[0];
                    String _cval;
                    if (_cv.length == 2) {
                        _cval = _cv[1];
                    } else {
                        _cval = null;
                    }
                    Cookie _c = new Cookie(_cname, _cval);
                    _cs.add(_c);
                }
            }
            cookies = new Cookie[_cs.size()];
            _cs.toArray(cookies);
        }
    }

    /*-----------------
    HttpServletRequest - begin
    -----------------*/
    /**
     * Sample and expected datetime format, based on RFC5322.
     */
    private final SimpleDateFormat default_date_parser = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);

    /**
     * Returns the cached processed cookies.
     * <p>
     * If the result is {@code null}, then either handshake request has no any
     * cookies, or this wrapper object has not been asked to processing/caching
     * cookies of related handshake request
     * </p>
     *
     * @return cookies related to this handshake request, if {@code null} then
     * this type is in either bad-state, or the request has no any cookies
     * associated.
     * @see #processHandshakeRequest()
     */
    @Override
    public Cookie[] getCookies() {
        return cookies;
    }

    /**
     * {@inheritDoc }
     * <p>
     * <b>Note: </b>please mind, only the datetime format has specified in
     * RFC5322 is used for parsing the header value.
     * </p>
     *
     * @throws IllegalArgumentException if the value related to the header is
     * not a valid/expected format
     * @see #default_date_parser
     */
    @Override
    public long getDateHeader(String arg_header_name) {
        String _head_val = getHeader(arg_header_name);
        if (_head_val == null) {
            return -1;
        }
        try {
            return default_date_parser.parse(_head_val).getTime();
        } catch (Exception e) {
        }
        throw new IllegalArgumentException(String.format("Unsupported date format for header value (%s). Expecting format follow RFC5322", _head_val));
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String getHeader(String arg_header_name) {
        List<String> _vals = websocketHandshakeRequest.getHeaders().get(arg_header_name);
        if (_vals == null || _vals.size() == 0) {
            return null;
        }
        return _vals.get(0);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Enumeration<String> getHeaders(String arg_header_name) {
        List<String> _vals = websocketHandshakeRequest.getHeaders().get(arg_header_name);
        if (_vals == null) {
            return null;
        }
        return Collections.enumeration(_vals);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Enumeration<String> getHeaderNames() {
        Set<String> _head_names = websocketHandshakeRequest.getHeaders().keySet();
        return Collections.enumeration(_head_names);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public int getIntHeader(String arg_header_name) {
        String _head_val = getHeader(arg_header_name);
        if (_head_val == null) {
            return -1;
        }
        return Integer.parseInt(_head_val);
    }

    /**
     * {@inheritDoc }
     * <p>
     * <b>NOTE: </b> this method returns constant {@code "GET"} value,
     * regardless if the request was made on which method.
     * </p>
     * <p>
     * Please refer to RFC 6455 Section 4.1
     * </p>
     */
    @Override
    public String getMethod() {
        //as RFC 6455 Section 4.1
        return "GET";
    }

    /**
     * Forward the call to {@link HandshakeRequest#getQueryString()}.
     *
     * @return the {@code getQueryString()} value returned by associated
     * {@link HandshakeRequest} of this instance
     */
    @Override
    public String getQueryString() {
        return websocketHandshakeRequest.getQueryString();
    }

    /**
     * {@inheritDoc }
     * Forward the call to
     * {@link HandshakeRequest#isUserInRole(java.lang.String)}.
     *
     * @param arg_rule_name the rule name should be checked
     * @return the {@code isUserInRole()} value returned by associated
     * {@link HandshakeRequest} of this instance
     */
    @Override
    public boolean isUserInRole(String arg_rule_name) {
        return websocketHandshakeRequest.isUserInRole(arg_rule_name);
    }

    /**
     * {@inheritDoc }
     * Forward the call to {@link HandshakeRequest#getUserPrincipal() }.
     *
     * @return the {@code getUserPrincipal()} value returned by associated
     * {@link HandshakeRequest} of this instance
     */
    @Override
    public Principal getUserPrincipal() {
        return websocketHandshakeRequest.getUserPrincipal();
    }

    /**
     * {@inheritDoc }
     * Forward the call to {@link HandshakeRequest#getRequestURI() }.
     *
     * @return the {@code getRequestURI()} value returned by associated
     * {@link HandshakeRequest} of this instance
     */
    @Override
    public String getRequestURI() {
        return websocketHandshakeRequest.getRequestURI().toString();
    }

    /**
     * Grabs the session object related to associated {@link HandshakeRequest#getHttpSession() }.
     * <p>
     * The session object should be non-{@code null}, and type of
     * {@link HttpSession}, otherwise {@code null} will be returned, or an
     * exception where session creation({@code arg_create}) is asked
     * </p>
     * <p>
     * <b>NOTE:</b> Since there is no way to access underlying servlet context, so asking for creating a session(when missed) is not possible.
     * <br>
     * If the user asked for creating one({@code arg_create} as {@code true}, or calling {@link #getSession()}), and there is no any session available, then {@link ClassCastException} will be thrown.
     * </p>
     * @param arg_create specifies if the session should be created if not exist
     * (must be {@code false})
     * @return the servlet session of the related handshake request if
     * applicable, otherwise {@code null}
     * @throws ClassCastException when the related http session is not type of
     * {@link HttpSession}, and non-{@code null}. Probably because the container
     * is not a servlet one.
     * @throws UnsupportedOperationException when there is no({@code null}) any session associated to the handshake request, and user asked to created one({@code arg_create} as {@code true}) which is not possible
     * @see HandshakeRequest#getHttpSession() 
     */
    @Override
    public HttpSession getSession(boolean arg_create) {
        Object _sess = websocketHandshakeRequest.getHttpSession();
        if ((_sess instanceof HttpSession)) {
            return (HttpSession) _sess;
        } else if (_sess != null) {
            throw new ClassCastException(String.format("The associated session type(%s) of websocket handshake is not the expected servlet %s", _sess.getClass().getName(), HttpSession.class.getName()));
        } else if (arg_create) {
            throw new UnsupportedOperationException("Not supported yet. No access to servlet context to ask for a new session creation!");
        }
        return null;
    }

    /**
     * {@inheritDoc }
     * <p>
     * <b>Note:</b> Please call {@link #getSession(boolean) } instead.<br>
     * Calling this could result a {@link UnsupportedOperationException}
     * exception when there is no a session, and asked to be created.
     * </p>
     * @throws ClassCastException when the related http session is not type of
     * {@link HttpSession}, and non-{@code null}. Probably because the container
     * is not a servlet one.
     * @throws UnsupportedOperationException when there is no({@code null}) any session associated to the handshake request
     */
    @Override
    @java.lang.Deprecated()
    public HttpSession getSession() {
        return getSession(true);
    }

    /**
     * {@inheritDoc }
     * @see HandshakeRequest#getParameterMap() 
     */
    @Override
    public String getParameter(String arg_param_name) {
        List<String> _pvals = websocketHandshakeRequest.getParameterMap().get(arg_param_name);
        if (_pvals == null) {
            return null;
        }
        if (_pvals.size() == 0) {
            return "";
        }
        String _res = _pvals.get(0);
//        return _res == null ? "" : _res;
        return _res;
    }

    /**
     * {@inheritDoc }
     * @see HandshakeRequest#getParameterMap() 
     */
    @Override
    public Enumeration<String> getParameterNames() {
        Set<String> _pnames = websocketHandshakeRequest.getParameterMap().keySet();
        return Collections.enumeration(_pnames);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String[] getParameterValues(String arg_param_name) {
        List<String> _pvals = websocketHandshakeRequest.getParameterMap().get(arg_param_name);
        if (_pvals == null) {
            return null;
        }
        String[] _res = new String[_pvals.size()];
        _pvals.toArray(_res);
        return _res;
    }

    /**
     * {@inheritDoc }
     * @see HandshakeRequest#getParameterMap() 
     */
    @Override
    public Map<String, String[]> getParameterMap() {
        HashMap<String, String[]> _res = new HashMap<>(websocketHandshakeRequest.getParameterMap().size());
        for (String _name : websocketHandshakeRequest.getParameterMap().keySet()) {
            List<String> _vals = websocketHandshakeRequest.getParameterMap().get(_name);
            String[] _val_arr = new String[_vals.size()];
            _vals.toArray(_val_arr);
            _res.put(_name, _val_arr);
        }
        return _res;
    }

    /**
     * {@inheritDoc }
     * @see HandshakeRequest#getRequestURI()  
     */
    @Override
    public String getScheme() {
        return websocketHandshakeRequest.getRequestURI().getScheme();
    }

    /*-----------------
    HttpServletRequest - end
    -----------------*/
}
