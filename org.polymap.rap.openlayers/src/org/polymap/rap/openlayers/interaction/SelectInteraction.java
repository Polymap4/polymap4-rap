/*
 * polymap.org 
 * Copyright (C) 2009-2018, Polymap GmbH. All rights reserved.
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
package org.polymap.rap.openlayers.interaction;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.json.JSONObject;

import com.google.common.collect.Lists;

import org.polymap.core.runtime.config.Concern;
import org.polymap.core.runtime.config.Config2;
import org.polymap.core.runtime.config.Immutable;

import org.polymap.rap.openlayers.base.OlEvent;
import org.polymap.rap.openlayers.base.OlEventPayload;
import org.polymap.rap.openlayers.base.OlPropertyConcern;
import org.polymap.rap.openlayers.layer.VectorLayer;

/**
 * Interaction for selecting vector features. By default, selected features are
 * styled differently, so this interaction can be used for visual highlighting, as
 * well as selecting features for other actions, such as modification or output.
 * There are three ways of controlling which features are selected: using the browser
 * event as defined by the condition and optionally the toggle, add/remove, and multi
 * options; a layers filter; and a further feature filter using the filter option.
 * 
 * Selected features are added to an internal unmanaged layer.
 * 
 * @see <a href="http://openlayers.org/en/master/apidoc/ol.interaction.Select.html">
 *      OpenLayers Doc</a>
 * @author <a href="http://stundzig.it">Steffen Stundzig</a>
 */
public class SelectInteraction
        extends Interaction {

    /**
     * Event types of {@link SelectInteraction}.
     */
    public enum Event {
        SELECT( "select" );

        private String name;

        private Event( String name ) {
            this.name = name;
        }

        public String toString() {
            return name;
        }
    }

    /**
     * The feature ID of an {@link Event#SELECT} event. 
     * @see Event#SELECT
     */
    public static class DrawendEventPayload
            extends OlEventPayload {

        public static Optional<DrawendEventPayload> findIn( OlEvent ev ) {
            return Optional.ofNullable( ev.properties().optString( "selected" ) != null 
                    ? new DrawendEventPayload( ev.properties() ) : null );
        }

        // receive ****************************************
        
        private JSONObject          json;

        protected DrawendEventPayload( JSONObject json ) {
            this.json = json;
        }

        /** The selected feature ID. */
        public String selected() {
            return json.getString( "selected" );
        }
        
        // send *******************************************
        
        public DrawendEventPayload() {
        }
        
        @Override
        public List<Variable> variables() {
            return Lists.newArrayList(
                    new Variable( "selected", "theEvent.selected != null ? theEvent.selected.map( function(feature) {return feature.get(\"id\");}) : {}" ) );
        }
    }

    // instance *******************************************
    
    @Immutable
    @Concern(OlPropertyConcern.class)
    public Config2<SelectInteraction, Collection<VectorLayer>> layers;


    public SelectInteraction( VectorLayer... layers) {
        super( "ol.interaction.Select" );
        this.layers.set( Arrays.asList( layers ) );
    }

    
    /**
     * @param event One of the {@link Event} types.
     * @param payload {@link SelectEventPayload} or any other appropriate payload.
     */
    @Override
    public void addEventListener( Object event, Object annotated, OlEventPayload... payload ) {
        super.addEventListener( event, annotated, payload );
    }


    /**
     * @param event One of the {@link Event} types.
     */
    @Override
    public void removeEventListener( Object event, Object annotated ) {
        super.removeEventListener( event, annotated );
    }


}
