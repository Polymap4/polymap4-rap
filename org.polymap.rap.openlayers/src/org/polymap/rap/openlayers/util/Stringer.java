/* 
 * polymap.org
 * Copyright (C) 2013, Falko Bräutigam. All rights reserved.
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
package org.polymap.rap.openlayers.util;

import java.util.function.Function;

/**
 * Simple helper to build HTML/JavaScript code Strings.
 *
 * @author <a href="http://www.polymap.de">Falko Bräutigam</a>
 */
public class Stringer {

    public static final int     DEFAULT_CAPACITY = 1024;
    
    public static final Function<Object,String> DEFAULT_TOSTRING = o -> o.toString();
    
    /**
     * Constructs a new instance with the given separator and default capacity =
     * {@value #DEFAULT_CAPACITY}
     */
    public static Stringer on( String separator ) {
        return new Stringer().separator( separator );
    }

    /**
     * Constructs a new instance with default separator = "" and default capacity =
     * {@value #DEFAULT_CAPACITY}
     */
    public static Stringer defaults() {
        return new Stringer();
    }
    
    public static String join( Object first, Object... parts ) {
        return new Stringer().add( first, parts ).toString();
    }
    
    // instance *******************************************
    
    private StringBuilder       builder = new StringBuilder( DEFAULT_CAPACITY );
    
    private String              sep = "";
    
    private String              nullReplacement = null;

    private Function<Object,String> toString = DEFAULT_TOSTRING;
    
    @Override
    public String toString() {
        return builder.toString();
    }

    /**
     * Specifies the function to use to convert elements to {@link String}.
     */
    public Stringer toString( Function<Object,String> _toString ) {
        this.toString = _toString;
        return this;
    }
    
    public Stringer replaceNulls( String _nullReplacement ) {
        assert _nullReplacement != null;
        nullReplacement = _nullReplacement;
        return this;
    }
    
    /**
     * Specifies the separator to be used to separate the elements of
     * {@link #add(Object, Object...)}. The separator is added <b>between</b> the
     * elements.
     */
    public Stringer separator( String _sep ) {
        this.sep = _sep;
        return this;
    }
    
    /**
     * Adds the given elements to the string. Uses the last set {@link #separator(String)},
     * {@link #toString(Function)} and {@link #replaceNulls(String)}.
     */
    public Stringer add( Object first, Object... parts ) {
        append( first, true );
        for (Object part : parts) {
            append( part, false );
        }
        return this;
    }

    public Stringer add( Object[] parts ) {
        int c = 0;
        for (Object part : parts) {
            append( part, c++ == 0 );
        }
        return this;        
    }

    public Stringer add( Iterable parts ) {
        int c = 0;
        for (Object part : parts) {
            append( part, c++ == 0 );
        }
        return this;        
    }

    protected void append( Object part, boolean isFirst ) {
        if (part == null) {
            if (nullReplacement == null) {
                throw new IllegalArgumentException( "String part is null and no null replacement was given." );
            }
            else {
                part = nullReplacement;
            }
        }
        if (!isFirst /*builder.length() > 0*/ && sep.length() > 0) {
            builder.append( sep );
        }
        builder.append( toString.apply( part ) );
    }
    
}
