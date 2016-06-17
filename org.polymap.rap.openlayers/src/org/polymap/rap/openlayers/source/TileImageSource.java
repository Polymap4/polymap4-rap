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
package org.polymap.rap.openlayers.source;

import org.eclipse.rap.rwt.widgets.WidgetUtil;
import org.polymap.rap.openlayers.base.OlMap;

/**
 * Base class for sources providing images divided into a tile grid.
 * 
 * @see <a href="http://openlayers.org/en/master/apidoc/ol.source.TileImage.html">
 *      OpenLayers Doc</a>
 * @author <a href="http://stundzig.it">Steffen Stundzig</a>
 */
public abstract class TileImageSource
        extends TileSource {

    public TileImageSource( String jsClassname ) {
        super( jsClassname );
    }

    @Override
    public void onSetMap( OlMap map ) {
        call( "this.objs['" + getObjRef() + "_keys'] = [];");
        call( "var progress=this.objs['" + WidgetUtil.getId( map.getControl() )
                + "p']; this.objs['" + getObjRef() + "_keys'].push( this.obj.on('tileloadstart', function() { progress.addLoading();}).key);" );
        call( "var progress=this.objs['" + WidgetUtil.getId( map.getControl() )
                + "p']; this.objs['" + getObjRef() + "_keys'].push(this.obj.on('tileloadend', function() { progress.addLoaded();}).key);" );
        call( "var progress=this.objs['" + WidgetUtil.getId( map.getControl() )
                + "p']; this.objs['" + getObjRef() + "_keys'].push(this.obj.on('tileloaderror', function() { progress.addLoaded();}).key);" );
        //call( "console.log(this.objs['" + getObjRef() + "_keys']);");
        super.onSetMap( map );
    }
    
    @Override
    public void onUnsetMap( OlMap map ) {
        //call( "console.log(this.objs['" + getObjRef() + "_keys']);");
        call( "var index; for( index in this.objs['" + getObjRef() + "_keys']) { var key = this.objs['" + getObjRef() + "_keys'][index]; this.obj.unByKey(key);/*console.log('deleted: ' + key);*/};");
        call( "delete this.objs['" + getObjRef() + "_keys'];");
        //call( "console.log(this.objs['" + getObjRef() + "_keys']);");
        super.onUnsetMap( map );
    }
}
