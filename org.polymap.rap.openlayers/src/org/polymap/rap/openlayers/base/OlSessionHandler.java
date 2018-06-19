/*
 * polymap.org 
 * Copyright 2009-2018, Polymap GmbH. All rights reserved.
 * 
 * This is free software; you can redistribute it and/or modify it under the terms of
 * the GNU Lesser General Public License as published by the Free Software
 * Foundation; either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 */
package org.polymap.rap.openlayers.base;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.lang.ref.WeakReference;

import org.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.eclipse.swt.widgets.Display;

import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.json.JsonValue;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.SingletonUtil;
import org.eclipse.rap.rwt.client.service.JavaScriptLoader;
import org.eclipse.rap.rwt.remote.AbstractOperationHandler;
import org.eclipse.rap.rwt.remote.Connection;
import org.eclipse.rap.rwt.remote.RemoteObject;

import org.polymap.core.runtime.event.EventHandler;

import org.polymap.rap.openlayers.OlPlugin;
import org.polymap.rap.openlayers.base.OlEventPayload.Variable;
import org.polymap.rap.openlayers.util.Stringer;

/**
 * Widget Provider holding a reference to the widget and generate client side object
 * hash / id's
 * 
 * @author Marcus -LiGi- B&uuml;schleb < mail: ligi (at) polymap (dot) de >
 * @author <a href="http://stundzig.it">Steffen Stundzig</a>
 */
public class OlSessionHandler {

    private final static Log log = LogFactory.getLog( OlSessionHandler.class );
    
    public static OlSessionHandler instance() {
        return SingletonUtil.getSessionInstance( OlSessionHandler.class );
    }
    
    // instance *******************************************

    /** Maps client side ID into references to created objects. */
    private Map<String,WeakReference<OlObject>> ref2obj = new HashMap();

    private volatile int        refCounter;

    private RemoteObject        remote;

    private List<RemoteCall>    calls = new ArrayList<RemoteCall>();

    private boolean             isRendered;


    private OlSessionHandler() {
        Connection connection = RWT.getUISession().getConnection();
        remote = connection.createRemoteObject( "org.polymap.rap.openlayers.OlWidget" );
        loadJavaScript();

        remote.set( "appearance", "composite" );
        remote.set( "overflow", "hidden" );

        remote.setHandler( new AbstractOperationHandler() {
            @Override 
            public void handleCall( String method, JsonObject properties ) {
//                log.warn( this + ".handleCall " + method + ";" + properties.toString() );
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
                            obj.handleEvent( new OlEvent( obj, method, new JSONObject( properties.toString() ) ) );
                        }
                    }
                }
            }
        });
    }


    protected void loadJavaScript() {
        // if not set as resource before, add this default css
        //OlPlugin.registerResource( "resources/css/bootstrap-3.3.4.min.css", "css/bootstrap.css" );
        OlPlugin.registerResource( "resources/css/ol-3.7.0.css", "css/ol.css" );
        OlPlugin.registerResource( "resources/css/progress.css", "css/progress.css" );

        // OlPlugin.registerResource( "resources/js/ol-3.7.0.debug.js", "js/ol.js" );
        OlPlugin.registerResource( "resources/js/progress.js", "js/progress.js" );
        OlPlugin.registerResource( "resources/js/ol-3.7.0.js", "js/ol.js" );
        OlPlugin.registerResource( "org/polymap/rap/openlayers/js/OlWrapper.js", "js/OlWrapper.js" );

        JavaScriptLoader jsLoader = RWT.getClient().getService( JavaScriptLoader.class );
        jsLoader.require( OlPlugin.resourceLocation( "js/progress.js" ) );
        jsLoader.require( OlPlugin.resourceLocation( "js/ol.js" ) );
        jsLoader.require( OlPlugin.resourceLocation( "js/OlWrapper.js" ) );
    }


    public void call( OlCommand command ) {
        callRemote( "call", command.getJson() );
    }


    /**
     * 
     *
     * @param src
     * @param event
     * @param annotated The {@link EventHandler annotated} event listener.
     * @param payload The map of variables to send back to server and the JS code
     *        that produces their values.
     */
    void registerEventListener( OlObject src, String event, Object annotated,
            OlEventPayload... payload ) {
        // FIXME call OlMap.getProperties() would cause 'TypeError: cyclic object value'
        Stringer code = src instanceof OlMap
                ? Stringer.on( "" ).add( "var result = {};" )
                : Stringer.on( "" ).add( "var result = that.objs['", src.getObjRef(), "'].getProperties();" );
        
        //command.add( "console.log('", event, "');" );
                
        code.add( "result['event_src_obj'] = '" + src.getObjRef() + "';" ); 
        for (OlEventPayload entry : payload) {
            entry.init( src );
            for (Variable variable : entry.variables()) {
                code.add( "result.", variable.name, " = ", variable.code, ";" );
            }
        }
        code.add( "rap.getRemoteObject(that).call( '", event, "', result);" );
        
        callRemote( "addListener", new JsonObject()
                .add( "src", src.getObjRef() )
                .add( "code", code.toString() )
                .add( "event", event )
                .add( "hashCode", Stringer.on( "_" ).add( event, annotated.hashCode() ).toString() ) );
    }


    void unregisterEventListener( OlObject src, String event, Object annotated ) {
        callRemote( "removeListener", new JsonObject()
                .add( "src", src.getObjRef() )
                .add( "hashCode", event + "_" + annotated.hashCode() ) );
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


    public String newReference( OlObject src ) {
        assert Display.getCurrent() != null;
        String newRef = Stringer.join( "ol", refCounter++ );
        if (ref2obj.put( newRef, new WeakReference( src ) ) != null) {
            throw new IllegalStateException( "objRef already added: " + newRef );
        }
        return newRef;
    }


    public OlObject getObject( String objRef ) {
         WeakReference<OlObject> reference = ref2obj.get( objRef );
         return reference != null ? reference.get() : null;
    }


    public void remove( String objRef ) {
        assert Display.getCurrent() != null;
        ref2obj.remove( objRef );
    }

}
