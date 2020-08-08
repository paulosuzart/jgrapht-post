package io.graphex.alg;

import org.jgrapht.Graph;
import org.jgrapht.alg.interfaces.VertexScoringAlgorithm;
import org.jgrapht.graph.DefaultEdge;

import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Links <pre>root</pre> to at most <pre>totalLinks</pre> based on descending of <pre>scoring</pre>.
 */
public class TopPages {

    private final Graph<DefaultEdge, DefaultEdge> graph;
    private final VertexScoringAlgorithm<DefaultEdge, Double> scoring;
    private final DefaultEdge root;
    private final int totalLinks;

    public TopPages(Graph<DefaultEdge, DefaultEdge> graph,
                    VertexScoringAlgorithm<DefaultEdge, Double> scoring,
                    DefaultEdge root,
                    int totalLinks) {

        this.graph = graph;
        this.scoring = scoring;

        this.root = root;
        this.totalLinks = totalLinks;
        compute();
    }

    private void compute() {
        var topPages = scoring.getScores().entrySet().stream()
                .sorted(Collections.reverseOrder(Comparator.comparingDouble(Map.Entry::getValue)))
                .limit(totalLinks)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
        graph.addVertex(root);
        topPages.forEach(page -> graph.addEdge(root, page));

    }

}
