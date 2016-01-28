/*
 * polymap.org Copyright (C) 2009-2015 Polymap GmbH. All rights reserved.
 * 
 * This is free software; you can redistribute it and/or modify it under the terms of
 * the GNU Lesser General Public License as published by the Free Software
 * Foundation; either version 2.1 of the License, or (at your option) any later
 * version.
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 */
package org.polymap.rap.demo;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.polymap.rap.openlayers.base.OlFeature;
import org.polymap.rap.openlayers.base.OlMap;
import org.polymap.rap.openlayers.format.GeoJSONFormat;
import org.polymap.rap.openlayers.geom.PointGeometry;
import org.polymap.rap.openlayers.geom.PolygonGeometry;
import org.polymap.rap.openlayers.layer.Layer;
import org.polymap.rap.openlayers.layer.TileLayer;
import org.polymap.rap.openlayers.layer.VectorLayer;
import org.polymap.rap.openlayers.source.MapQuestSource;
import org.polymap.rap.openlayers.source.VectorSource;
import org.polymap.rap.openlayers.style.CircleStyle;
import org.polymap.rap.openlayers.style.FillStyle;
import org.polymap.rap.openlayers.style.Font;
import org.polymap.rap.openlayers.style.StrokeStyle;
import org.polymap.rap.openlayers.style.Style;
import org.polymap.rap.openlayers.style.TextStyle;
import org.polymap.rap.openlayers.types.Attribution;
import org.polymap.rap.openlayers.types.Color;
import org.polymap.rap.openlayers.types.Coordinate;
import org.polymap.rap.openlayers.types.Projection;
import org.polymap.rap.openlayers.types.Projection.Units;
import org.polymap.rap.openlayers.view.View;

import com.google.common.collect.Lists;

/**
 * 
 * @author <a href="http://stundzig.it">Steffen Stundzig</a>
 *
 */
public class LayerSwitchingTab extends DemoTab {

    private VectorSource source;

    private OlMap map;

    private List<Layer<?>> layers;

    public LayerSwitchingTab() {
        super("LayerMoving");
    }

    private final static Log log = LogFactory.getLog(LayerSwitchingTab.class);

    @Override
    protected void createDemoControls(Composite parent) {
        map = new OlMap(parent, SWT.MULTI | SWT.WRAP | SWT.BORDER,
                new View().projection.put(new Projection("EPSG:3857", Units.m)).zoom.put(12).center
                        .put(new Coordinate(1401845.7269824906, 6666952.61751981)));

        layers = Lists.newArrayList();
        layers.add(new TileLayer().source.put(new MapQuestSource(MapQuestSource.Type.osm)));

        // map.addLayer( new ImageLayer().source
        // .put( new ImageWMSSource().url.put(
        // "http://ows.terrestris.de/osm/service/" ).params
        // .put( new ImageWMSSource.RequestParams().layers.put( "OSM-WMS" ) )
        // ).opacity
        // .put( 0.5f ) );

        source = new VectorSource().format.put(new GeoJSONFormat()).attributions
                .put(Arrays.asList(new Attribution("Steffen Stundzig")));

        layers.add(new VectorLayer().style
                .put(new Style().fill.put(new FillStyle().color.put(new Color(0, 0, 255, 0.1f))).stroke
                        .put(new StrokeStyle().color.put(new Color("red")).width.put(1f))).source.put(source));
        addFeatures();

        layers.forEach(layer -> map.addLayer(layer));
    }

    private void addFeatures() {
        OlFeature feature = new OlFeature();
        feature.name.set("Point");
        feature.labelPoint.set(map.view.get().center.get());
        feature.geometry.set(new PointGeometry(map.view.get().center.get()));
        feature.style.put(new Style().text.put(new TextStyle().text.put("MY MESSAGE").font
                .put(new Font().family.put(Font.Family.CourierNew).weight.put(Font.Weight.bold).size.put(24)).stroke
                        .put(new StrokeStyle().color.put(new Color("green")).width.put(2f))).image
                                .put(new CircleStyle(5.0f).fill.put(new FillStyle().color.put(new Color("red")))));

        OlFeature feature2 = new OlFeature();
        feature2.name.set("Polygon");
        feature2.labelPoint.set(map.view.get().center.get());
        feature2.geometry.set(new PolygonGeometry(new Coordinate(-12393642.369994164, 9388521.61821717),
                new Coordinate(-13351395.095766516, 7297102.400714279),
                new Coordinate(-10888602.372351898, 7688021.880621362),
                new Coordinate(-12393642.369994164, 9388521.61821717)));
        feature2.style.put(new Style().fill.put(new FillStyle().color.put(new Color("red"))).stroke
                .put(new StrokeStyle().color.put(new Color("blue")).width.put(10f)));
        source.addFeatures(feature, feature2);
    }

    @Override
    protected void createStyleControls(Composite parent) {
        Composite group = new Composite(parent, SWT.NONE);
        group.setLayout(new GridLayout(2, false));

        new Label(group, SWT.NONE).setText("");

        Button center = new Button(group, SWT.BORDER);
        center.setText("Switch Layer order");
        center.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                layers.forEach(layer -> map.removeLayer(layer));
                Collections.reverse(layers);
                layers.forEach(layer -> map.addLayer(layer));
            }
        });

    }
}
