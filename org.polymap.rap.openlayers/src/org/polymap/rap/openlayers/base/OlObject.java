/*
 * polymap.org and individual contributors as indicated by the @authors tag.
 * Copyright (C) 2009-2015 
 * All rights reserved.
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

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.polymap.core.runtime.config.Config;
import org.polymap.core.runtime.config.ConfigurationFactory;
import org.polymap.rap.openlayers.base.OlEventListener.PayLoad;
import org.polymap.rap.openlayers.base.OlPropertyConcern.Unquoted;
import org.polymap.rap.openlayers.types.Coordinate;
import org.polymap.rap.openlayers.util.Stringer;

/**
 * Client Side OpenLayers Object Base Class holding a reference to the widget and
 * keeps track of changes to the object
 * 
 * @author Marcus -LiGi- B&uuml;schleb < mail: ligi (at) polymap (dot) de >
 * @author <a href="http://www.polymap.de">Falko Bräutigam</a>
 * @author <a href="http://mapzone.io">Steffen Stundzig</a>
 */
public abstract class OlObject {

    public static final String UNKNOWN_CLASSNAME = "_unknown_";

    protected String           jsClassname       = UNKNOWN_CLASSNAME;

    protected String           objRef;


    protected OlObject( String jsClassname ) {
        this.jsClassname = jsClassname;

        // initialize Property instances
        ConfigurationFactory.inject( this );
    }


    /**
     * Create the JS instance on the client side using the values of all
     * {@link Config} members.
     */
    protected void create() {
        if (jsClassname == null || jsClassname.equals( UNKNOWN_CLASSNAME )) {
            throw new IllegalArgumentException( "jsClassname must be set, but is " + jsClassname );
        }

        String options = OlPropertyConcern.propertiesAsJson( this );
        create( jsClassname, options );
    }


    protected void create( String jsClassname, String options ) {
        create( new Stringer( "new ", jsClassname, "(", options, ")" ).toString() );
    }

    private OlSessionHandler osh;


    private OlSessionHandler osh() {
        if (osh == null) {
            osh = OlSessionHandler.getInstance();
        }
        return osh;
    }


    /**
     * Should only be used for testing.
     */
    void setOsh( OlSessionHandler osh ) {
        this.osh = osh;
    }


    protected void create( String code ) {
        objRef = osh().generateReference( this );
        osh().call( new OlCommand( getJSObjRef() + "=" + code + ";" ) );
    }


    private void lazyCreate() {
        if (objRef == null) {
            create();
        }
    }


    public void call( String code ) {
        osh().call( new OlCommand( new Stringer( "this.obj=", getJSObjRef(), "; ", code ).toString() ) );
    }


    /**
     * Executes the specified function in the JS client part.
     * 
     * @param function The name of the function.
     */
    public void call( String function, Object... args ) {
        StringBuilder buf = new StringBuilder( 128 ).append( "this.obj." ).append( function ).append( '(' );

        for (int i = 0; i < args.length; i++) {
            if (i > 0) {
                buf.append( ',' );
            }
            Object arg = args[i];
            buf.append( argToString( arg ) );
        }
        call( buf.append( ");" ).toString() );
    }


    /**
     * Adds code that sets the value of the given attribute.
     * 
     * @param attr The name of the attribute.
     */
    public void setAttribute( String attr, Object arg ) {
        call( "set", attr, arg );
    }


    private String argToString( Object arg ) {
        if (arg instanceof OlObject) {
            return ((OlObject)arg).getJSObjRef();
        }
        if (arg instanceof Number || arg instanceof Boolean) {
            return arg.toString();
        }
        if (arg instanceof String) {
            return '\'' + (String)arg + '\'';
        }
        if (arg instanceof Enum) {
            return '\'' + ((Enum)arg).name() + '\'';
        }
        if (arg instanceof Unquoted) {
            return ((Unquoted)arg).toJSONString();
        }
        if (arg instanceof JSONArray) {
            return ((JSONArray)arg).toString();
        }
        if (arg instanceof Coordinate) {
            return ((Coordinate)arg).toJson().toString();
        }
        if (arg instanceof Collection) {
            return OlPropertyConcern.propertyAsJson( arg ).toString();
        }
        throw new IllegalArgumentException( "Unknown arg type: " + arg.getClass() + ": " + arg );
    }


    /**
     * return the current reference number without any JS around. If the objRef is
     * null, the OlObject starts its creation lify cycle.
     * 
     * @return
     */
    public String getObjRef() {
        lazyCreate();
        return objRef;
    }


    boolean isCreated() {
        return objRef != null;
    }


    public String getJSObjRef() {
        lazyCreate();
        return new Stringer().replaceNulls( "null" ).add( "this.objs['", objRef, "']" ).toString();
    }


    public void dispose() {
        // TODO remove also all controls and other stuff of this map
        osh().remove( getObjRef() );
        call( getJSObjRef() + "=null;" );
    }

    private Map<String,Set<OlEventListener>> eventListeners = new HashMap<String,Set<OlEventListener>>();


    protected void addEventListener( final String event, OlEventListener listener, PayLoad payload ) {
        Set<OlEventListener> listeners = eventListeners.get( event );
        if (listeners == null) {
            listeners = new HashSet<OlEventListener>();
            eventListeners.put( event, listeners );
        }
        listeners.add( listener );

        osh().registerEventListener( this, event, listener, payload );
    }


    protected void removeEventListener( final String event, OlEventListener listener ) {
        Set<OlEventListener> listeners = eventListeners.get( event );
        if (listeners != null) {
            listeners.remove( listener );
        }
        osh().unregisterEventListener( this, event, listener );
    }


    void handleEvent( OlEvent event ) {
        for (OlEventListener l : eventListeners.get( event.name() )) {
            l.handleEvent( event );
        }
    }
}
