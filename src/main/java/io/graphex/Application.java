package io.graphex;

import io.graphex.alg.TopPages;
import io.graphex.alg.scoring.SumScoresDecorator;
import io.graphex.io.IO;
import org.jgrapht.Graph;
import org.jgrapht.alg.scoring.BetweennessCentrality;
import org.jgrapht.alg.transform.LineGraphConverter;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleDirectedGraph;
import org.jgrapht.nio.DefaultAttribute;
import org.jgrapht.nio.dot.DOTExporter;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

import static io.graphex.alg.scoring.SumScoresDecorator.doubleSumScoreDecorator;

public class Application {

    public static void main(String[] args) {
        try {
            var importer = IO.locationsFromResourceCSV("data.csv");
            var locationsGraph = importer.doImport();

            var lineConverter = new LineGraphConverter<String, DefaultEdge, DefaultEdge>(locationsGraph);

            var pagesGraph = new SimpleDirectedGraph<DefaultEdge, DefaultEdge>(DefaultEdge.class);
            lineConverter.convertToLineGraph(pagesGraph);

            var scores = new BetweennessCentrality<>(locationsGraph, true);

            var pageScores = doubleSumScoreDecorator(locationsGraph, scores, pagesGraph);

            var rootPage = new DefaultEdge();

            new TopPages(pagesGraph, pageScores, rootPage, 3);

            IO.exportPagesCSV(locationsGraph, rootPage, "/tmp/out.csv").doExport(pagesGraph);

            vizPages(locationsGraph, pagesGraph, pageScores, rootPage);
            vizLocations(locationsGraph);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void vizLocations(org.jgrapht.Graph<String, DefaultEdge> locationsGraph) {
        DOTExporter<String, DefaultEdge> exporter = new DOTExporter<>();
        exporter.setVertexIdProvider(e -> e);
        exporter.exportGraph(locationsGraph, new File("/home/paul/Desktop/g.dot"));
        System.out.println(locationsGraph);
    }

    private static void vizPages(Graph<String, DefaultEdge> locationsGraph, SimpleDirectedGraph<DefaultEdge, DefaultEdge> pagesGraph, SumScoresDecorator<String, DefaultEdge, Double> pageScores, DefaultEdge rootPage) {
        DOTExporter<DefaultEdge, DefaultEdge> exporter2 = new DOTExporter<>();
        exporter2.setVertexIdProvider(page -> {
            boolean isHome = rootPage.equals(page);
            return isHome ? "Home" :
                    String.format("%s_%s", locationsGraph.getEdgeSource(page),
                            locationsGraph.getEdgeTarget(page));
        });
        exporter2.setVertexAttributeProvider(page -> {
            boolean isHome = rootPage.equals(page);
            if (isHome) {
                return Map.of("color", DefaultAttribute.createAttribute("grey"));
            }
            var score = pageScores.getVertexScore(page);
            return Map.of("xlabel", DefaultAttribute.createAttribute(new BigDecimal(score).setScale(2, RoundingMode.CEILING).doubleValue()));
        });
        exporter2.exportGraph(pagesGraph, new File("/tmp/g1.dot"));
    }
}
