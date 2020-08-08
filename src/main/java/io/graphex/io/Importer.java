package io.graphex.io;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;

@FunctionalInterface
public interface Importer<V,E> {
    Graph<V,E> doImport();
}
