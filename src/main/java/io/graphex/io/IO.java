package io.graphex.io;

import lombok.experimental.UtilityClass;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleDirectedGraph;
import org.jgrapht.nio.csv.CSVExporter;
import org.jgrapht.nio.csv.CSVFormat;
import org.jgrapht.nio.csv.CSVImporter;

import java.io.File;
import java.util.function.Function;

/**
 * Utility just to clean up main class.
 */
@UtilityClass
public class IO {

    public final String HOME_LABEL = "Home";

    /**
     * Simple function to load a graph from a CSV
     *
     * @param csvFileName path in the resources directory
     * @return a {@see Importer} for a SimpleDirectedGraph of Locations
     */
    public Importer<String, DefaultEdge> locationsFromResourceCSV(String csvFileName) {
        return () -> {
            new CSVImporter<String, DefaultEdge>() {

            };
            var target = new SimpleDirectedGraph<String, DefaultEdge>(DefaultEdge.class);
            var importer = new CSVImporter<String, DefaultEdge>(CSVFormat.EDGE_LIST);
            importer.setVertexFactory(Function.identity());
            importer.importGraph(target, IO.class.getClassLoader().getResourceAsStream(csvFileName));
            return target;
        };
    }

    /**
     * Creates an {@see Exporter} that captures a <pre>locationGraph</pre> to use as look up for name of vertex id.
     *
     * @param locationGraph the original graph of locations
     * @param root          page of the exporter graph
     * @param path          to write the csv file
     * @return Exporter
     */
    public Exporter<DefaultEdge, DefaultEdge> exportPagesCSV(Graph<String, DefaultEdge> locationGraph, DefaultEdge root, String path) {
        return (graph -> {
            var lineExporter = new CSVExporter<DefaultEdge, DefaultEdge>(CSVFormat.EDGE_LIST);
            lineExporter.setVertexIdProvider(e -> {
                boolean sourceIsHome = root.equals(e);
                return sourceIsHome ? HOME_LABEL :
                        String.format("travel-%s-to-%s", locationGraph.getEdgeSource(e), locationGraph.getEdgeTarget(e));
            });
            lineExporter.exportGraph(graph, new File(path));

        });

    }
}
