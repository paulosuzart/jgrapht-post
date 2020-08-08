package io.graphex.alg.scoring;

import org.jgrapht.Graph;
import org.jgrapht.alg.interfaces.VertexScoringAlgorithm;

import java.util.AbstractMap.SimpleEntry;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

/**
 * Defines a score of <pre>E</pre> to be the sum of the scores (as defined in <pre>decorated</pre>) of <pre>V</pre> in a <pre>delegate</pre> graph.
 *
 * @param <V> The type of the vertex of the original graph
 * @param <E> The type of the edge of the original graph
 * @param <S> the type of the scoring
 */
public class SumScoresDecorator<V, E, S extends Number> implements VertexScoringAlgorithm<E, S> {

    private final Graph<V, E> delegate;
    private final VertexScoringAlgorithm<V, S> decorated;
    private final Graph<E, ?> graph;
    private final BiFunction<S, S, S> adder;

    private SumScoresDecorator(Graph<V, E> delegate,
                               VertexScoringAlgorithm<V, S> decorated,
                               Graph<E, ?> graph,
                               BiFunction<S, S, S> adder) {
        this.delegate = delegate;
        this.decorated = decorated;
        this.graph = graph;
        this.adder = adder;
    }

    @Override
    public Map<E, S> getScores() {
        return graph.vertexSet().stream()
                .map(e -> new SimpleEntry<>(e, getVertexScore(e)))
                .collect(Collectors.toMap(SimpleEntry::getKey, SimpleEntry::getValue));
    }

    @Override
    public S getVertexScore(E e) {
        var source = decorated.getVertexScore(delegate.getEdgeSource(e));
        var target = decorated.getVertexScore(delegate.getEdgeTarget(e));
        return adder.apply(source, target);
    }

    /**
     * Instantiates a new <pre>SumScoresDecorator</pre>
     */
    public static <V, E> SumScoresDecorator<V, E, Double> doubleSumScoreDecorator(Graph<V, E> delegate,
                                                                                  VertexScoringAlgorithm<V, Double> decorated, Graph<E, ?> graph) {
        return new SumScoresDecorator<>(delegate, decorated, graph, Double::sum);
    }


}
