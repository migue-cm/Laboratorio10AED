package btree;

import java.util.ArrayList;

public class BNode<E extends Comparable<E>> {
    protected ArrayList<E> keys;
    protected ArrayList<BNode<E>> childs;
    protected int count;
    protected int idNode;

    private static int idNodeCounter = 0;

    public BNode(int n) {
        this.keys = new ArrayList<>(n);
        this.childs = new ArrayList<>(n + 1); // En un B-tree, puede haber n+1 hijos para n claves.
        this.count = 0;
        this.idNode = ++idNodeCounter;

        // Inicializamos las listas con nulls
        for (int i = 0; i < n; i++) {
            this.keys.add(null);
        }
        for (int i = 0; i < n + 1; i++) {
            this.childs.add(null);
        }
    }

    // Verifica si el nodo está lleno
    public boolean nodeFull(int n) {
        return count == n;
    }

    // Verifica si el nodo está vacío
    public boolean nodeEmpty() {
        return count == 0;
    }

    /**
     * Busca una clave en el nodo.
     * Si la encuentra, retorna true y la posición.
     * Si no la encuentra, retorna false y la posición donde debería bajar.
     */
    public SearchResult searchNode(E key) {
        int pos = 0;

        while (pos < count && keys.get(pos) != null && key.compareTo(keys.get(pos)) > 0) {
            pos++;
        }

        if (pos < count && keys.get(pos) != null && key.compareTo(keys.get(pos)) == 0) {
            return new SearchResult(true, pos);
        } else {
            return new SearchResult(false, pos);
        }
    }

    // Devuelve las claves del nodo junto con el ID
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Node ID: ").append(idNode).append(" | Keys: [");
        for (int i = 0; i < count; i++) {
            sb.append(keys.get(i));
            if (i < count - 1) sb.append(", ");
        }
        sb.append("]");
        return sb.toString();
    }

    // Clase auxiliar para devolver los resultados de búsqueda
    public static class SearchResult {
        public boolean found;
        public int position;

        public SearchResult(boolean found, int position) {
            this.found = found;
            this.position = position;
        }
    }
}

