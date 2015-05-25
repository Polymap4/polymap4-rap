/*
 * polymap.org Copyright (C) 2009-2014, Polymap GmbH. All rights reserved.
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
package org.polymap.rap.openlayers.style;

import org.polymap.core.runtime.config.Concern;
import org.polymap.core.runtime.config.Mandatory;
import org.polymap.core.runtime.config.Property2;
import org.polymap.rap.openlayers.base.OlObject;
import org.polymap.rap.openlayers.base.OlPropertyConcern;

/**
 * Container for vector feature rendering styles. Any changes made to the style or
 * its children will not take effect until the feature, layer or FeatureOverlay that
 * uses the style is re-rendered.
 * 
 * @see href="http://openlayers.org/en/master/apidoc/ol.style.Style.html
 * @author <a href="http://www.polymap.de">Falko Br�utigam</a>
 * @author <a href="http://stundzig.it">Steffen Stundzig</a>
 */
public class Style
        extends OlObject {

    @Concern(OlPropertyConcern.class)
    public Property2<Style,StrokeStyle> stroke;

    @Concern(OlPropertyConcern.class)
    public Property2<Style,ImageStyle>  image;

    @Concern(OlPropertyConcern.class)
    public Property2<Style,FillStyle>   fill;

    @Concern(OlPropertyConcern.class)
    public Property2<Style,TextStyle>   text;


    public Style() {
        super( "ol.style.Style" );
    }

}
