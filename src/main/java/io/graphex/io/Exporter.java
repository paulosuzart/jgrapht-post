package io.graphex.io;

import org.jgrapht.Graph;

@FunctionalInterface
public interface Exporter<V, E> {
    void doExport(Graph<V, E> graph);
}
