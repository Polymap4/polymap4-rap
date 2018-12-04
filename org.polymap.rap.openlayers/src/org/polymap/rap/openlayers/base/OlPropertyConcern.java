/*
 * polymap.org Copyright (C) 2015, Falko Bräutigam. All rights reserved.
 * 
 * This is free software; you can redistribute it and/or modify it under the terms of
 * the GNU Lesser General Public License as published by the Free Software
 * Foundation; either version 3.0 of the License, or (at your option) any later
 * version.
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 */
package org.polymap.rap.openlayers.base;

import java.util.Arrays;
import java.util.Collection;

import java.lang.reflect.Field;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONString;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.polymap.core.runtime.config.Config;
import org.polymap.core.runtime.config.ConfigurationException;
import org.polymap.core.runtime.config.DefaultPropertyConcern;

/**
 * Synchronizes the value of a {@link Config} of an {@link OlObject},
 * {@link Jsonable} or {@link Collection} thereof with the property of the JavaScript
 * object.
 * <p/>
 * Provides static methods the build JSON representation of the properties of an
 * {@link OlObject}.
 * 
 * @see OlProperty
 * @see OlSetter
 * @see OlPropertyAndSetter
 * @author <a href="http://www.polymap.de">Falko Bräutigam</a>
 */
public class OlPropertyConcern
        extends DefaultPropertyConcern<Object> {

    private static final Log log = LogFactory.getLog( OlPropertyConcern.class );

    
    @Override
    public synchronized Object doSet( Object obj, Config<Object> prop, Object value ) {
        //log.info( obj.getClass().getSimpleName() + "." + prop.info().getName() + " = " + value );

        OlSetter sa = prop.info().getAnnotation( OlSetter.class );
        OlPropertyAndSetter psa = prop.info().getAnnotation( OlPropertyAndSetter.class );
        OlProperty pa = prop.info().getAnnotation( OlProperty.class );
        assert Arrays.asList( sa, pa, psa ).stream().filter( e -> e!=null ).count() <= 1;  // mutual exclusive

        OlObject olobj = (OlObject)obj;

        // setter
        if (sa != null) {
            olobj.call( sa.value(), value );
        }
        // propertyAndSetter
        else if (psa != null) {
            if (olobj.isCreated()) {
                olobj.call( psa.setter(), value );
            }
        }
        // property
        else {
            // if object is not yet created then the property
            // is given to the ctor by #propertiesAsJson()
            if (olobj.isCreated()) {
                Object jsonValue = propertyAsJson( value );
                String propName = pa != null ? pa.value() : prop.info().getName();
                ((OlObject)obj).setAttribute( propName, jsonValue );
            }
        }
        return value;
    }

    //
    // @Override
    // public Object doGet( Object obj, Property prop, Object value ) {
    // log.info( obj.getClass().getSimpleName() + "." + prop.info().getName() + " = "
    // + value );
    //
    //// OlMethodProperty setter = prop.info().getAnnotation( OlMethodProperty.class
    // );
    //// if (setter != null) {
    //// ((OlObject)obj).execute( setter.value(), value );
    //// }
    //// else {
    // // is the object created as JS on the client already?
    // // if it is created, we must call the setter of this obj
    // if (((OlObject)obj).getObjRef() != null) {
    //
    // Object jsonValue = propertyAsJson( prop, value );
    //
    // OlProperty a = prop.info().getAnnotation( OlProperty.class );
    // String propName = a != null ? a.value() : prop.info().getName();
    // Object res = ((OlObject)obj).getAttribute( propName );
    // } else {
    // return super.doGet( obj, prop, value );
    // }
    // // }
    // // return value;
    // }


    /**
     * Creates a JSON representation of all {@link Config} members of an
     * {@link OlObject}.
     */
    public static String propertiesAsJson( Object obj ) {
        JSONObject json = new JSONObject();
        try {
            for (Class cl = obj.getClass(); cl != null; cl = cl.getSuperclass()) {
                for (Field f : cl.getDeclaredFields()) {
                    if (Config.class.isAssignableFrom( f.getType() )) {
                        f.setAccessible( true );
                        Config prop = (Config)f.get( obj );
                        Object value = prop.get();
                        if (value != null) {
                            String name = f.getName();
                            OlProperty a = f.getAnnotation( OlProperty.class );
                            if (a != null) {
                                name = a.value();
                            }
                            OlPropertyAndSetter a2 = f.getAnnotation( OlPropertyAndSetter.class );
                            if (a2 != null) {
                                name = a2.property();
                            }
                            Object jsonValue = propertyAsJson( value );
                            json.put( name, jsonValue );
                        }
                    }
                }
            }
            return json.toString();
        }
        catch (Exception e) {
            throw new ConfigurationException( e );
        }
    }


    /**
     * 
     */
    public static Object propertyAsJson( Object value ) {
        // log.info( prop.info().getName() + ": " + value );
        if (value instanceof Jsonable) {
            return new Unquoted( ((Jsonable)value).toJson().toString() );
        }
        else if (value instanceof OlObject) {
            return new Unquoted( ((OlObject)value).getJSObjRef() );
        }
        else if (value instanceof Collection) {
            JSONArray result = new JSONArray();
            ((Collection<Object>)value).forEach( item -> {
                result.put( propertyAsJson( item ) );
            } );
            return result;
        }
        else {
            return value;
        }
    }


    /**
     * 
     */
    public static class Unquoted
            implements JSONString {

        private String value;

        public Unquoted( String value ) {
            this.value = value;
        }

        @Override
        public String toJSONString() {
            return value;
        }
    }
}
