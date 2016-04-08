/*
 * polymap.org Copyright (C) 2016 individual contributors as indicated by
 * the @authors tag. All rights reserved.
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
package org.polymap.rap.openlayers.graph;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.widgets.Display;
import org.gephi.graph.GraphControllerImpl;
import org.gephi.graph.api.Edge;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.GraphModel;
import org.gephi.graph.api.Node;
import org.gephi.io.exporter.api.ExportController;
import org.gephi.io.exporter.impl.ExportControllerImpl;
import org.gephi.io.importer.api.Container;
import org.gephi.io.importer.api.ContainerLoader;
import org.gephi.io.importer.api.EdgeDirection;
import org.gephi.io.importer.api.EdgeDraft;
import org.gephi.io.importer.api.NodeDraft;
import org.gephi.io.importer.impl.ImportContainerFactoryImpl;
import org.gephi.io.importer.impl.ImportControllerImpl;
import org.gephi.io.processor.plugin.AppendProcessor;
import org.gephi.io.processor.spi.Processor;
import org.gephi.layout.plugin.AutoLayout;
import org.gephi.layout.plugin.forceAtlas2.ForceAtlas2;
import org.gephi.project.api.ProjectController;
import org.gephi.project.api.Workspace;
import org.gephi.project.impl.ProjectControllerImpl;
import org.polymap.core.runtime.Polymap;
import org.polymap.rap.openlayers.base.OlFeature;
import org.polymap.rap.openlayers.base.OlMap;
import org.polymap.rap.openlayers.geom.LineStringGeometry;
import org.polymap.rap.openlayers.geom.PointGeometry;
import org.polymap.rap.openlayers.source.VectorSource;
import org.polymap.rap.openlayers.style.StrokeStyle;
import org.polymap.rap.openlayers.style.Style;
import org.polymap.rap.openlayers.types.Color;
import org.polymap.rap.openlayers.types.Coordinate;
import org.polymap.rap.openlayers.types.Extent;

import com.google.common.collect.Maps;

/**
 * Wraps a graph stream implementation to work easily with OlFeatures. Simple
 * construct a VectorSource in your map. After that create your OlFeatures and the
 * edges between them, by simply adding them to this class.
 * 
 * @author <a href="http://stundzig.it">Steffen Stundzig</a>
 */
public class OlFeatureGephiGraph {

    private final static Log            log              = LogFactory.getLog( OlFeatureGephiGraph.class );

    private static final double         GRAPHUNIT2COORD  = 100;

    private static final long           REFRESH_INTERVAL = 500;

    private final VectorSource          vector;

    private final Map<String,OlFeature> nodes            = Maps.newHashMap();

    private final Map<String,OlFeature> edges            = Maps.newHashMap();

    private final OlMap                 map;

    // private Graph graph;

    private final ProjectController     pc;

    private final Workspace             workspace;

    private Container                   container;

    private ImportControllerImpl        importController;

    private GraphModel                  graphModel;

    private ForceAtlas2                 layout;


    /**
     * Creates a graph which updates automatically all features in the vector source
     * and updates also the optimal extent in the map.
     * 
     * @param vector
     * @param map
     */
    public OlFeatureGephiGraph( final VectorSource vector, final OlMap map ) {
        this.vector = vector;
        this.map = map;
        pc = new ProjectControllerImpl();// .getDefault().lookup(
                                         // ProjectController.class );
        pc.newProject();
        workspace = pc.getCurrentWorkspace();
    }


    /**
     * Adds or updates the feature to the graph with a default weight of 1.
     * 
     * @param feature
     */
    public void addOrUpdateNode( final OlFeature feature ) {
        addOrUpdateNode( feature, 1 );
    }


    /**
     * Adds or updates the feature to the graph with a specified weight.
     * 
     * @param feature
     * @param weight should be greater than 0
     */
    public void addOrUpdateNode( final OlFeature feature, int weight ) {
        if (!nodes.containsKey( feature.id.get() )) {
            nodes.put( feature.id.get(), feature );
            addNode( feature.id.get() );
            if (feature.geometry.get() == null
                    || !PointGeometry.class.isAssignableFrom( feature.geometry.get().getClass() )) {
                Coordinate coordinate = new Coordinate( 0.0, 0.0 );
                feature.geometry.set( new PointGeometry( coordinate ) );
            }
            vector.addFeature( feature );
        }
        // getNode( feature.id.get() ).addAttribute( "layout.weight", weight );
    }


    /**
     * Creates or updates an undirected edge between both features with a default
     * weight of 1 and a default stroke style with a black line and zIndex 0.
     * 
     * If an edge is added twice, than the weight of both are added.
     * 
     * @param src
     * @param target
     */
    public void addOrUpdateEdge( final OlFeature src, final OlFeature target ) {
        addOrUpdateEdge( src, target,
                new Style().stroke.put( new StrokeStyle().color.put( new Color( "black" ) ).width.put( 2f ) ).zIndex
                        .put( 0f ),
                1 );
    }


    /**
     * Creates or updates an undirected edge between both features with the specified
     * weight and the specified style.
     * 
     * If an edge is added twice, than the weight of both are added.
     * 
     * The id of the edge is constructed with src.id + "_" + target.id
     */
    public void addOrUpdateEdge( final OlFeature src, final OlFeature target,
            final Style style, int weight ) {
        final String edgeId = src.id.get() + "_" + target.id.get();
        OlFeature line = edges.get( edgeId );
        if (line == null) {
            line = new OlFeature( edgeId );
        }
        line.style.put( style );
        addOrUpdateEdge( line, src, target, weight );
    }


    /**
     * Creates or updates a predefined edge between both features with the specified
     * weight.
     * 
     * If an edge is added twice, than the weight of both are added.
     * 
     * <strong>The ID of the edge must be build with src.id + '_' +
     * target.id.</strong>
     */
    public void addOrUpdateEdge( final OlFeature edge, final OlFeature src,
            final OlFeature target, int weight ) {
        final String edgeId = edge.id.get();
        if (!edges.containsKey( edgeId )) {
            edge.geometry.set( new LineStringGeometry( ((PointGeometry)src.geometry.get()).coordinate.get(),
                    ((PointGeometry)target.geometry.get()).coordinate.get() ) );
            edges.put( edgeId, edge );
            addEdge( edgeId, src.id.get(), target.id.get() );
            vector.addFeature( edge );
            // getEdge( edgeId ).addAttribute( "layout.weight", weight );
        }
        else {
            // weight += getEdge( edgeId ).getAttribute( "layout.weight",
            // Integer.class );
            // getEdge( edgeId ).setAttribute( "layout.weight", weight );
        }
    }


    private NodeDraft getNode( String nodeId ) {
        return getContainer().getLoader().getNode( nodeId );
    }


    private EdgeDraft getEdge( String edgeId ) {
        return getContainer().getLoader().getEdge( edgeId );
    }


    private void addNode( String id ) {
        ContainerLoader loader = getContainer().getLoader();
        loader.addNode( loader.factory().newNodeDraft( id ) );
    }


    private void addEdge( String id, String srcId, String targetId ) {
        ContainerLoader loader = getContainer().getLoader();
        EdgeDraft edge = loader.factory().newEdgeDraft( id );
        edge.setSource( loader.getNode( srcId ) );
        edge.setTarget( loader.getNode( targetId ) );
        edge.setDirection( EdgeDirection.UNDIRECTED );
        loader.addEdge( edge );
    }


    private Container getContainer() {
        if (container == null) {
            container = new ImportContainerFactoryImpl().newContainer();
            importController = new ImportControllerImpl();
            graphModel = new GraphControllerImpl().getGraphModel( workspace );
            layout = new ForceAtlas2( null );
            layout.setGraphModel( graphModel );
            layout.resetPropertiesValues();
            layout.setOutboundAttractionDistribution( true );
            layout.setEdgeWeightInfluence( 1.0d );
            layout.setGravity( 1.0 );
            layout.setJitterTolerance( 1.0 );
            layout.setScalingRatio( 15.0 );
        }
        return container;
    }

    private Processor processor = new AppendProcessor();


    public void reload() {
        try {
            importController.process( container, processor, workspace );
            final Display display = Display.getCurrent();
            // Executors.newSingleThreadExecutor().execute( () -> {
            readCoordinates( graphModel, layout, display, 2000 );
            // } );
            // ExportController ec = new ExportControllerImpl();
            // try {
            // File gexf = new File("io_gexf.gexf");
            // System.out.println( "export to " + gexf.getAbsolutePath() );
            // ec.exportFile(gexf, workspace);
            // } catch (IOException ex) {
            // ex.printStackTrace();
            // return;
            // }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    // private Graph getInternalGraph() {
    // if (graph == null) {
    // graph = new MultiGraph( "" );
    // Viewer viewer = new Viewer( graph,
    // Viewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD );
    // Layout layout = new SpringBox( false );//
    // Eades84Layout();//HierarchicalLayout();//
    // // //new
    // // SpringBox( false );//
    // // LinLog(false);//.newLayoutAlgorithm();
    //// layout.setStabilizationLimit( 0.8d );
    // viewer.enableAutoLayout( layout );
    // final Display display = Display.getCurrent();
    // Executors.newSingleThreadExecutor().execute( () -> {
    // readCoordinates( viewer, layout, display, 50 );
    // } );
    // }
    // return graph;
    // }

    // private Map<String,List<Double>> nodeCoordinates = Maps.newHashMap();


    private void readCoordinates( final GraphModel graphModel, final ForceAtlas2 layout, final Display display,
            final int maxTime ) {
        // boolean firstRun = true;
        // layout.addAttributeSink( this );
        try {
            log.info( "run algo" );
            AutoLayout autolayout = new AutoLayout( maxTime, TimeUnit.MILLISECONDS );
            autolayout.addLayout( layout, 1.0f );
            autolayout.setGraphModel( graphModel );
            // Polymap.executorService().execute( () -> {
            // autolayout.execute();
            // log.info( "algo done" );
            // } );
            // layout.initAlgo();
            // for (int i = 0; i < maxSteps && layout.canAlgo(); i++) {
            // layout.goAlgo();
            // }
            // layout.endAlgo();
            // long end = System.currentTimeMillis() + maxTime;
            // while (System.currentTimeMillis() < end) {
            // log.info( "vor submit" );
            // Polymap.executorService().submit( () -> {
            // log.info( "vor display async" );
            display.asyncExec( () -> {
                autolayout.execute();
                log.info( "sending coordinates" );
                final Graph graph = graphModel.getUndirectedGraph();
                double minX = 10000;
                double minY = 10000;
                double maxX = -10000;
                double maxY = -10000;
                for (Node node : graph.getNodes()) {
                    Coordinate newCoordinate = new Coordinate( node.x() * GRAPHUNIT2COORD,
                            node.y() * GRAPHUNIT2COORD );
                    minX = Math.min( minX, newCoordinate.x() );
                    maxX = Math.max( maxX, newCoordinate.x() );
                    minY = Math.min( minY, newCoordinate.y() );
                    maxY = Math.max( maxY, newCoordinate.y() );
                    log.info( "sending coordinate " + node.getId() + ": " + newCoordinate.x() + ";" + newCoordinate.y() );
                    final OlFeature olFeature = nodes.get( node.getId() );
                    ((PointGeometry)olFeature.geometry.get()).coordinate.set( newCoordinate );
                    // Node node = getInternalGraph().getNode( entry.getKey()
                    // );
                    for (Edge edge : graph.getEdges( node )) {
                        OlFeature line = edges.get( edge.getId().toString() );
                        LineStringGeometry geometry = ((LineStringGeometry)line.geometry.get());
                        List<Coordinate> coordinates = geometry.coordinates.get();
                        coordinates.set(
                                (edge.getId().toString().startsWith( node.getId().toString() )) ? 0 : 1,
                                newCoordinate );
                        geometry.coordinates.set( coordinates );
                    }
                }
                Extent envelope = new Extent( minX, minY, maxX, maxY );
                map.view.get().fit( envelope, null );
                map.view.get().resolution.set( 600f );
//                map.view.get().resolution.set( new Double((Math.abs( maxX ) + Math.abs( minX )) / GRAPHUNIT2COORD).floatValue() );
                log.info( "setting extent to " + envelope.toJson() );
                // }
                log.info( "sending coordinates done." );
            } );
            // } ).get();
            // Thread.sleep( REFRESH_INTERVAL );
            // }
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException( e );
        }
    }
}