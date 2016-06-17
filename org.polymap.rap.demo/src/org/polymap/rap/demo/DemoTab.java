/*
 * polymap.org
 * Copyright (C) 2009-2015 Polymap GmbH. All rights reserved.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 */
package org.polymap.rap.demo;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.polymap.rap.openlayers.base.OlMap;
import org.polymap.rap.openlayers.layer.ImageLayer;
import org.polymap.rap.openlayers.source.ImageWMSSource;
import org.polymap.rap.openlayers.source.WMSRequestParams;
import org.polymap.rap.openlayers.types.Coordinate;
import org.polymap.rap.openlayers.types.Projection;
import org.polymap.rap.openlayers.types.Projection.Units;
import org.polymap.rap.openlayers.view.View;

/**
 * 
 * @author <a href="http://stundzig.it">Steffen Stundzig</a>
 *
 */
public abstract class DemoTab {

    private Composite demoComposite;

    protected Composite styleComposite;

    private final String name;

    private Composite form;

    public DemoTab(String name) {
        this.name = name;
    }

    public String name() {
        return name;
    }

    public String getId() {
        String id = this.getClass().getSimpleName();
        if (id.endsWith("Tab")) {
            id = id.substring(0, id.length() - 3);
        }
        return id;
    }

    public Composite createContents(Composite parent) {
        if (form == null) {
            form = createSashForm(parent);
        }
        return form;
    }

    protected abstract void createStyleControls(Composite parent);

    protected abstract void createDemoControls(Composite parent);

    private Composite createSashForm(Composite parent) {
        SashForm horSashForm = new SashForm(parent, SWT.HORIZONTAL);
        createLeft(horSashForm);
        createRight(horSashForm);
        horSashForm.setWeights(new int[] { 80, 20 });
        return horSashForm;
    }

    private void createLeft(Composite parent) {
        demoComposite = new Composite(parent, SWT.NONE);
        demoComposite.setLayoutData(createLayoutDataForContentArea());
        demoComposite.setLayout(new FillLayout());
        createDemoControls(demoComposite);
    }

    private void createRight(Composite parent) {
        styleComposite = new Composite(parent, SWT.NONE);
        styleComposite.setLayoutData(createLayoutDataForContentArea());
        styleComposite.setLayout(new RowLayout(SWT.VERTICAL));
        createStyleControls(styleComposite);
    }

    private static Object createLayoutDataForContentArea() {
        FormData formData = new FormData();
        formData.top = new FormAttachment(0, 35);
        formData.left = new FormAttachment(0, 5);
        formData.right = new FormAttachment(100, -5);
        formData.bottom = new FormAttachment(100, -5);
        return formData;
    }

    protected OlMap defaultMap(Composite parent) {
        OlMap map = new OlMap(parent, SWT.MULTI | SWT.WRAP | SWT.BORDER,
                new View().projection.put(new Projection("EPSG:3857", Units.m)).zoom.put(12).center
                        .put(new Coordinate(1401845.7269824906, 6666952.61751981)));

        // map.addLayer( new TileLayer().source.put( new MapQuestSource(
        // MapQuestSource.Type.osm ) ) );

        map.addLayer(
                new ImageLayer().source.put(new ImageWMSSource().url.put("http://ows.terrestris.de/osm/service/").params
                        .put(new WMSRequestParams().layers.put("OSM-WMS"))).opacity.put(0.5f));

        map.view.get().addPropertyChangeListener(event -> {
            StatusBar.getInstance().addInfo(parent, name() + ": " + event.properties().toString());
        });

        return map;
    }
}
