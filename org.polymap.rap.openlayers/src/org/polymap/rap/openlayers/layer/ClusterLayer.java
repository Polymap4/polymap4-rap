/*
 * polymap.org
 * Copyright (C) 2009-2015, Polymap GmbH. All rights reserved.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 */
package org.polymap.rap.openlayers.layer;

import java.util.Arrays;
import java.util.function.Consumer;

import org.polymap.core.runtime.config.Mandatory;
import org.polymap.rap.openlayers.source.VectorSource;

/**
 * Vector data that is rendered client-side.
 * 
 * @see <a href="http://openlayers.org/en/master/apidoc/ol.layer.Vector.html">OpenLayers Doc</a>
 * @author <a href="http://www.polymap.de">Falko Bräutigam</a>
 */
public class ClusterLayer 
        extends Layer<VectorSource> {
    
    /**
     * Constructs a new instance.
     *
     * @param initializers Initialize at least all {@link Mandatory} properties.
     */
    public ClusterLayer( Consumer<ClusterLayer>... initializers ) {
        super( "ol.layer.Vector" );
        Arrays.asList( initializers ).forEach( initializer -> initializer.accept( this ) );
    }

    
    protected ClusterLayer( String jsClassname ) {
        super( jsClassname );
    }
    
    /* (non-Javadoc)
     * @see org.polymap.rap.openlayers.base.OlObject#create(java.lang.String, java.lang.String)
     */
    @Override
    protected void create( String jsClassname, String options ) {
        StringBuilder sb = new StringBuilder();
        sb.append("\"style\": function(feature, resolution) {");
        sb.append( "   var styleCache = {};" );
        sb.append("    var size = feature.get('features').length;");
        sb.append("    var style = styleCache[size];");
        sb.append("    if (!style) {");
        sb.append("      style = [new ol.style.Style({");
        sb.append("        image: new ol.style.Circle({");
        sb.append("          radius: 10,");
        sb.append("          stroke: new ol.style.Stroke({");
        sb.append("            color: '#fff'");
        sb.append("          }),");
        sb.append("          fill: new ol.style.Fill({");
        sb.append("            color: '#3399CC'");
        sb.append("          })");
        sb.append("        }),");
        sb.append("        text: new ol.style.Text({");
        sb.append("          text: size.toString(),");
        sb.append("          fill: new ol.style.Fill({");
        sb.append("            color: '#fff'");
        sb.append("          })");
        sb.append("        })");
        sb.append("      })];");
        sb.append("      styleCache[size] = style;");
        sb.append("    }");
        sb.append("    return style;");   
        sb.append("}");   
        StringBuilder sb2 = new StringBuilder(options);
        int index = options.lastIndexOf( "}" );
        sb2.insert( index, "," + sb.toString() );
        super.create( jsClassname, sb2.toString() );
    }
}
