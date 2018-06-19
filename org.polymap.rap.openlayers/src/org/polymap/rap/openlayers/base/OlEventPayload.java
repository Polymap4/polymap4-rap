/* 
 * polymap.org
 * Copyright (C) 2018, Falko Bräutigam. All rights reserved.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3.0 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 */
package org.polymap.rap.openlayers.base;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 *
 * @author <a href="http://www.polymap.de">Falko Bräutigam</a>
 */
public abstract class OlEventPayload {

    private static final Log log = LogFactory.getLog( OlEventPayload.class );

//    protected static <T extends OlEventPayload> Optional<T> findIn( OlEvent ev, Class<T> type ) {
//        try {
//            T instance = type.newInstance();
//            return Optional.ofNullable( instance.findIn( ev ) ? instance : null );
//        }
//        catch (InstantiationException | IllegalAccessException e) {
//            throw new RuntimeException( e );
//        }
//    }
    
    // instance *******************************************
    
    protected OlObject          belongsTo;
    
    void init( @SuppressWarnings( "hiding" ) OlObject belongsTo ) {
        this.belongsTo = belongsTo;
    }
    
    /**
     * 
     */
    protected String objRef() {
        return belongsTo.getObjRef();
    }

    /**
     * 
     */
    protected String jsObjRef() {
        return "that.objs['" + objRef() + "']";
    }
    
    /**
     * Defines the variables of the payload.
     * <p/>
     * Symbols that can be used in javascript code:
     * <ul>
     * <li>'theEvent' (or 'ev') -- The event object.
     * <li>'that' (or 'self') -- A reference to the object that ...?
     * </ul>
     */
    public abstract List<Variable> variables();
    
    
    /**
     * 
     */
    public static class Variable {
        public String       name;
        public String       code;
        
        public Variable( String name, String code ) {
            this.name = name;
            this.code = code;
        }
    }
    
}
