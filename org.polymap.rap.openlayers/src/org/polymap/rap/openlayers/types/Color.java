/*
 * polymap.org Copyright 2015, Polymap GmbH. All rights reserved.
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
package org.polymap.rap.openlayers.types;

import org.polymap.core.runtime.config.Check;
import org.polymap.core.runtime.config.Config2;
import org.polymap.core.runtime.config.ConfigurationFactory;
import org.polymap.core.runtime.config.NumberRangeValidator;

import org.polymap.rap.openlayers.base.Jsonable;

/**
 * A color represented as a short array [red, green, blue, alpha]. red, green, and
 * blue should be integers in the range 0..255 inclusive. alpha should be a float in
 * the range 0..1 inclusive.
 * 
 * @see <a href="http://openlayers.org/en/v3.20.1/apidoc/ol.html#Color">OpenLayers Doc</a>
 * @author <a href="http://www.polymap.de">Falko Bräutigam</a>
 * @author <a href="http://stundzig.it">Steffen Stundzig</a>
 */
public class Color
        implements Jsonable {

    @Check(value = NumberRangeValidator.class, args = { "0", "255" })
    public Config2<Color,Integer> red;

    @Check(value = NumberRangeValidator.class, args = { "0", "255" })
    public Config2<Color,Integer> green;

    @Check(value = NumberRangeValidator.class, args = { "0", "255" })
    public Config2<Color,Integer> blue;

    @Check(value = NumberRangeValidator.class, args = { "0", "1" })
    public Config2<Color,Float>   alpha;

    public Config2<Color,String>  name;


    public Color( String color ) {
        ConfigurationFactory.inject( this );
        this.name.set( color );
    }


    public Color( int r, int g, int b ) {
        this( r, g, b, 1 );
    }


    public Color( int r, int g, int b, float a ) {
        ConfigurationFactory.inject( this );
        this.red.set( r );
        this.green.set( g );
        this.blue.set( b );
        this.alpha.set( a );
    }


    @Override
    public Object toJson() {
        return "'"
                + (name.get() != null ? name.get() : "rgba(" + red.get() + "," + green.get() + ","
                        + blue.get() + "," + alpha.get() + ")") + "'";
    }

}
