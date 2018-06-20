/*
 * polymap.org and individual contributors as indicated by the @authors tag.
 * Copyright (C) 2009-2015 All rights reserved.
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
import java.util.Collection;
import java.util.List;

import org.json.JSONArray;

import org.polymap.core.runtime.Lazy;
import org.polymap.core.runtime.LockedLazyInit;
import org.polymap.core.runtime.PlainLazyInit;
import org.polymap.core.runtime.config.Config;
import org.polymap.core.runtime.config.ConfigurationFactory;
import org.polymap.core.runtime.event.EventHandler;
import org.polymap.core.runtime.event.EventManager;
import org.polymap.core.runtime.event.TypeEventFilter;

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

    protected String           jsClassname = UNKNOWN_CLASSNAME;

    protected String           objRef;

    protected Lazy<OlSessionHandler> osh = new LockedLazyInit( () -> OlSessionHandler.instance() );
    
    /** The listeners added via {@link #addEventListener(Object, OlEventPayload, OlEventListener)}. */
    private List<OlEventListener> strongRefs = new ArrayList( 3 );


    protected OlObject( String jsClassname ) {
        this.jsClassname = jsClassname;

        // initialize Property instances
        ConfigurationFactory.inject( this );
    }


    /**
     * Should only be used for testing.
     */
    void setOsh( OlSessionHandler osh ) {
        this.osh = new PlainLazyInit( () -> osh );
    }

    
    /**
     * This method is called in order to check {@link #isCreated()} and then
     * eventually call {@link #doCreate()}.
     */
    protected final void lazyCreate() {
        //assert Display.getCurrent() != null : "Not Thread safe, call from Display Thread!";        
        assert jsClassname != null && !jsClassname.equals( UNKNOWN_CLASSNAME ) : "jsClassname must be set, but is " + jsClassname;
        if (!isCreated()) {
            doCreate();
        }        
    }

    
    /**
     * This method is called in order to actually create the object instance on the
     * client side. The default implementation uses the values of all {@link Config}
     * members for building an options object. Sub-classes may override to implement
     * other behaviour.
     */
    protected void doCreate() {
        String options = OlPropertyConcern.propertiesAsJson( this );
        createWithCode( Stringer.join( "new ", jsClassname, "(", options, ")" ) );
    }


    /**
     * Builds JS code like:
     * <pre>
     * new {@link #jsClassname}({@link #argToString(Object)}(options[0]),...)
     * </pre>
     */
    protected void createWithOptions( Object... options ) {
        createWithCode( Stringer.defaults().add( "new ", jsClassname, "(" )
                .toString( o -> argToString( o ) ).separator( "," ).add( options )
                .toString( Stringer.DEFAULT_TOSTRING ).add( ")" ).toString() );
    }


    protected void createWithCode( String code ) {
        objRef = osh.get().newReference( this );
        osh.get().call( new OlCommand( getJSObjRef() + "=" + code + ";" ) );
    }


    protected boolean isCreated() {
        return objRef != null;
    }


    public void call( String code ) {
        osh.get().call( new OlCommand( Stringer.join( "this.obj=", getJSObjRef(), "; ", code ) ) );
    }


    /**
     * Executes the specified function in the JS client part.
     * 
     * @param function The name of the function.
     */
    public void call( String function, Object... args ) {
        call( Stringer.defaults().add( "this.obj.", function, '(' )
                .toString( arg -> argToString( arg ) ).separator( "," ).add( args )
                .toString( Stringer.DEFAULT_TOSTRING ).add( ");" ).toString() );
    }


    /**
     * Adds code that sets the value of the given attribute.
     * 
     * @param attr The name of the attribute.
     */
    public void setAttribute( String attr, Object arg ) {
        call( "set", attr, arg );
    }


    protected String argToString( Object arg ) {
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
        if (arg instanceof Jsonable) {
            return ((Jsonable)arg).toJson().toString();
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
     */
    public String getObjRef() {
        lazyCreate();
        return objRef;
    }


    public String getJSObjRef() {
        lazyCreate();
        return Stringer.on( "" ).replaceNulls( "null" ).add( "this.objs['", objRef, "']" ).toString();
    }


    public void dispose() {
        osh.get().remove( getObjRef() );
        call( "delete " + getJSObjRef() + ";" );
    }

    @Override
    protected void finalize() throws Throwable {
        dispose();
    }

    
    /**
     * Registers an event listener for this object.
     *
     * @param event A {@link String} or {@link Enum} that describes the
     *        {@link OlEvent#name()} of the event. Appropriate Enum types are defined
     *        by sub-classes.
     * @param annotated The {@link EventHandler annotated} event listener,
     *        <b>weakly</b> referenced by {@link EventManager}.
     * @param payload
     */
    protected void addEventListener( Object event, Object annotated, OlEventPayload... payload ) {
        String eventName = event instanceof String
                ? (String)event
                : ((Enum)event).toString();
        
        EventManager.instance().subscribe( annotated, TypeEventFilter.ifType( OlEvent.class, ev ->
                ev.name().equals( eventName ) && 
                ev.getSource().equals( OlObject.this ) ) );
        
        osh.get().registerEventListener( this, eventName, annotated, payload );
    }

    
    /**
     * Registers an event listener for this object.
     * <p/>
     * This method is meant to support in-place event listener implementations and
     * calls {@link #addEventListener(Object, Object, OlEventPayload...)} to actually
     * register the listener.
     * 
     * @see #addEventListener(Object, Object, OlEventPayload...)
     */
    public void addEventListener( Object event, OlEventPayload payload, OlEventListener listener ) {
        addEventListener( event, listener, payload );
        strongRefs.add( listener );
    }

    /**
     * 
     *
     * @param event A {@link String} or {@link Enum} that describes the
     *        {@link OlEvent#name()} of the event. Appropriate Enum types are defined
     *        by sub-classes.
     * @param annotated The {@link EventHandler annotated} event listener,
     *        <em>weakly</em> referenced by {@link EventManager}.
     * @param event
     */
    protected void removeEventListener( Object event, Object annotated ) {
        String eventName = event instanceof String
                ? (String)event
                : ((Enum)event).toString();
        EventManager.instance().unsubscribe( annotated );
        osh.get().unregisterEventListener( this, eventName, annotated );
        
        if (annotated instanceof OlEventListener) {
            strongRefs.remove( annotated );
        }
    }


    void handleEvent( OlEvent event ) {
        EventManager.instance().publish( event );
    }
}
