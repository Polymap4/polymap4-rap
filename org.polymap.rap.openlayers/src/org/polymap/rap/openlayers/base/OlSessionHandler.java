/*
 * polymap.org Copyright 2009, Polymap GmbH, and individual contributors as indicated
 * by the @authors tag.
 * 
 * This is free software; you can redistribute it and/or modify it under the terms of
 * the GNU Lesser General Public License as published by the Free Software
 * Foundation; either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along
 * with this software; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF site:
 * http://www.fsf.org.
 */
package org.polymap.rap.openlayers.base;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.json.JsonValue;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.SingletonUtil;
import org.eclipse.rap.rwt.client.service.JavaScriptLoader;
import org.eclipse.rap.rwt.remote.AbstractOperationHandler;
import org.eclipse.rap.rwt.remote.Connection;
import org.eclipse.rap.rwt.remote.RemoteObject;
import org.polymap.rap.openlayers.OlPlugin;
import org.polymap.rap.openlayers.base.OlEventListener.PayLoad;
import org.polymap.rap.openlayers.util.Stringer;

/**
 * Widget Provider holding a reference to the widget and generate client side object
 * hash / id's
 * 
 * @author Marcus -LiGi- B&uuml;schleb < mail: ligi (at) polymap (dot) de >
 * @author <a href="http://stundzig.it">Steffen Stundzig</a>
 */
public class OlSessionHandler {

    private final static Log     log   = LogFactory.getLog( OlSessionHandler.class );

    /**
     * HashMap to hold references to created objects as value with the client side id
     * as key
     */
    private Map<String,OlObject> ref2obj;

    public Vector<OlCommand>     cmdsBeforeRemoteWasPresent;

    private int                  referenceCounter;

    private final RemoteObject   remote;

    private List<RemoteCall>     calls = new ArrayList<RemoteCall>();

    private boolean              isRendered;


    private OlSessionHandler() {
        ref2obj = new HashMap<String,OlObject>();
        cmdsBeforeRemoteWasPresent = new Vector<OlCommand>();

        Connection connection = RWT.getUISession().getConnection();
        remote = connection.createRemoteObject( "org.polymap.rap.openlayers.OlWidget" );
        loadJavaScript();

        remote.setHandler( operationHandler );
        remote.set( "appearance", "composite" );
        remote.set( "overflow", "hidden" );
    }

    private final AbstractOperationHandler operationHandler = new AbstractOperationHandler() {

        private static final long serialVersionUID = 1L;


        @Override
        public void handleCall( String method, JsonObject properties ) {
            log.warn( this + ".handleCall " + method + ";" + properties.toString() );
            if ("handleOnRender".equals( method )) {
                isRendered = true;
                for (RemoteCall call : calls) {
                    callRemote( call.method, call.json );
                }
                calls.clear();
            }
            else {
                JsonValue objRefJS = properties.get( "event_src_obj" );
                if (objRefJS != null) {
                    String objRef = objRefJS.asString();
                    properties.remove( "event_src_obj" );
                    OlObject obj = getObject( objRef );
                    if (obj != null) {
                        obj.handleEvent(new OlEvent( obj, method, properties ));
                    }
                }
            }
        }
    };


    private void loadJavaScript() {

        // if not set as resource before, add this default css
        OlPlugin.registerResource( "resources/css/bootstrap-3.3.4.min.css", "css/bootstrap.css" );
        OlPlugin.registerResource( "resources/css/ol-3.7.0.css", "css/ol.css" );
        OlPlugin.registerResource( "resources/css/ol3-loadingpanel.css", "css/ol.css" );

//        OlPlugin.registerResource( "resources/js/ol-3.7.0.debug.js", "js/ol.js" );
        OlPlugin.registerResource( "resources/js/ol-3.7.0.js", "js/ol.js" );
        OlPlugin.registerResource( "org/polymap/rap/openlayers/js/OlWrapper.js",
                "js/OlWrapper.js" );
        OlPlugin.registerResource( "resources/js/ol3-loadingpanel.js", "js/ol3-loadingpanel.js" );

        JavaScriptLoader jsLoader = RWT.getClient().getService( JavaScriptLoader.class );
        jsLoader.require( OlPlugin.resourceLocation( "js/ol.js" ) );
        jsLoader.require( OlPlugin.resourceLocation( "js/OlWrapper.js" ) );
        jsLoader.require( OlPlugin.resourceLocation( "js/ol3-loadingpanel.js" ) );
    }


    // remote call

    public void call( OlCommand command ) {
        callRemote( "call", command.getJson() );
    }
//
//    public void call( String method, JsonObject json ) {
//        callRemote( method, json );
//    }


    void registerEventListener( OlObject src, String event, OlEventListener listener,
            PayLoad payload ) {
        Stringer payloadStringer = new Stringer();
        if (payload != null) {
            payload.values().forEach(
                    value -> payloadStringer.add( "result.", value.key(), " = ", value.value(), ";" ) );
        }
        // FIXME call OlMap.getProperties() would cause 'TypeError: cyclic object value'
        String command = null;
        if(src instanceof OlMap) {
            command = "var result = {};";
        } else {
            command = new Stringer(
                    "var result = that.objs['", src.getObjRef(), "'].getProperties();").toString();
        }
        command = new Stringer(
                // "console.log('", event, "');",
                command ,
                "result['event_src_obj'] = '" + src.getObjRef() + "';", payloadStringer,
                "rap.getRemoteObject(that).call( '", event, "', result);" ).toString();
        callRemote( "addListener",
                new JsonObject().add( "src", src.getObjRef() ).add( "code", command )
                        .add( "event", event )
                        .add( "hashCode", event + "_" + listener.hashCode() ) );
    }


    void unregisterEventListener( OlObject src, String event, OlEventListener listener ) {
        callRemote( "removeListener", new JsonObject().add( "src", src.getObjRef() )
                .add( "hashCode", event + "_" + listener.hashCode() ) );
    }

    private class RemoteCall {

        String     method;

        JsonObject json;


        public RemoteCall( String method, JsonObject json ) {
            this.method = method;
            this.json = json;
        }
    }


    void callRemote( String method, JsonObject json ) {
        if (isRendered) {
            log.debug( "callRemote: " + method + " with " + json.toString().replaceAll( "\\\\\"", "'" ) );
            remote.call( method, json );
        }
        else {
            calls.add( new RemoteCall( method, json ) );
        }
    }


    public synchronized static OlSessionHandler getInstance() {
        return SingletonUtil.getSessionInstance( OlSessionHandler.class );
    }


    public synchronized String generateReference( OlObject src ) {
        String newRef = "ol" + referenceCounter++;
        if (ref2obj.put( newRef, src ) != null) {
            throw new IllegalStateException( "objRef already added: " + newRef );
        }
        return newRef;
    }


    public OlObject getObject( String objRef ) {
        return ref2obj.get( objRef );
    }


    public void remove( String objRef ) {
        ref2obj.remove( objRef );
    }

}
