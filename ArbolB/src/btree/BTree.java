package btree;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BTree<E extends Comparable<E>> {
	private BNode<E> root; 
	private int orden; 
	private boolean up; 
	private BNode<E> nDes;
	
	public BTree(int orden)
		{ this.orden = orden;
		this.root = null;
	}
	
	public boolean isEmpty() {
		return this.root == null;
	}
	
	public void insert(E cl)
		{ up = false; 
		E mediana; 
		BNode<E> pnew;
		mediana = push(this.root, cl);
		if (up) {
			pnew = new BNode<E>(this.orden); 
			pnew.count = 1; 
			pnew.keys.set(0, mediana); 
			pnew.childs.set(0, this.root); 
			pnew.childs.set(1, nDes); 
			this.root = pnew;
		
		}
	}
	
	//metodo search
	public boolean search(E cl) {
	    return searchRecursive(this.root, cl);
	}

	private boolean searchRecursive(BNode<E> current, E cl) {
	    if (current == null) {
	        return false;
	    }

	    for (int i = 0; i < current.count; i++) {
	        E key = current.keys.get(i);

	        if (key != null && cl.compareTo(key) == 0) {
	            System.out.println(cl + " se encuentra en el nodo " + current.idNode + " en la posición " + i);
	            return true;
	        }

	        // Si cl < key, ir al hijo izquierdo antes de esa clave
	        if (key != null && cl.compareTo(key) < 0) {
	            return searchRecursive(current.childs.get(i), cl);
	        }
	    }

	    // Si no encontró la clave en las claves del nodo, va al último hijo
	    return searchRecursive(current.childs.get(current.count), cl);
	}

	
	//metodo toString
	@Override
	public String toString() {
	    String s = "";
	    if (isEmpty())
	        s += "BTree is empty...";
	    else
	        s = writeTree(this.root);
	    return s;
	}

	private String writeTree(BNode<E> current) {
	    if (current == null) return "";

	    StringBuilder sb = new StringBuilder();

	    sb.append("Id.Nodo: ").append(current.idNode).append("\n");
	    sb.append("Claves Nodo: (");
	    for (int i = 0; i < current.count; i++) {
	        sb.append(current.keys.get(i));
	        if (i < current.count - 1)
	            sb.append(", ");
	    }
	    sb.append(")\n");

	    sb.append("Id.Hijos: [");
	    boolean hayHijos = false;
	    for (int i = 0; i <= current.count; i++) {
	        BNode<E> child = current.childs.get(i);
	        if (child != null) {
	            sb.append(child.idNode);
	            if (i < current.count && current.childs.get(i + 1) != null)
	                sb.append(", ");
	            hayHijos = true;
	        }
	    }
	    if (!hayHijos) sb.append("--");
	    sb.append("]\n\n");

	    // Recursivamente escribir los hijos
	    for (int i = 0; i <= current.count; i++) {
	        BNode<E> child = current.childs.get(i);
	        if (child != null) {
	            sb.append(writeTree(child));
	        }
	    }

	    return sb.toString();
	}
	
	//metodo remove
	public void remove(E cl) {
	    removeRecursive(this.root, cl);

	    // Si la raíz quedó vacía
	    if (root != null && root.count == 0) {
	        if (root.childs.get(0) != null) {
	            root = root.childs.get(0);
	        } else {
	            root = null;
	        }
	    }
	}
	
	private void removeRecursive(BNode<E> node, E cl) {
	    if (node == null) return;

	    int i = 0;
	    while (i < node.count && cl.compareTo(node.keys.get(i)) > 0) {
	        i++;
	    }

	    if (i < node.count && cl.compareTo(node.keys.get(i)) == 0) {
	        if (node.childs.get(i) == null) {
	            // Caso 1: en hoja
	            for (int j = i; j < node.count - 1; j++) {
	                node.keys.set(j, node.keys.get(j + 1));
	            }
	            node.keys.set(node.count - 1, null);
	            node.count--;
	        } else {
	            // Caso 2: nodo interno, tomar predecesor
	            BNode<E> pred = node.childs.get(i);
	            while (pred.childs.get(pred.count) != null) {
	                pred = pred.childs.get(pred.count);
	            }
	            E predKey = pred.keys.get(pred.count - 1);
	            node.keys.set(i, predKey);
	            removeRecursive(node.childs.get(i), predKey);
	        }
	    } else {
	        if (node.childs.get(i) == null) {
	            System.out.println("La clave no existe.");
	            return;
	        }

	        removeRecursive(node.childs.get(i), cl);

	        if (node.childs.get(i).count < (orden - 1) / 2) {
	            fixUnderflow(node, i);
	        }
	    }
	}
	
	
	public static BTree<Integer> building_BTree(String filePath) throws ItemNoFound, IOException {
	    BufferedReader reader = new BufferedReader(new FileReader(filePath));
	    String line;

	    // Leer orden del árbol
	    int orden = Integer.parseInt(reader.readLine().trim());
	    BTree<Integer> tree = new BTree<>(orden);

	    Map<Integer, BNode<Integer>> nodos = new HashMap<>();
	    Map<Integer, Integer> niveles = new HashMap<>();
	    int maxNivel = 0;

	    while ((line = reader.readLine()) != null) {
	        line = line.trim();
	        if (line.isEmpty()) continue;

	        String[] partes = line.split(",");
	        if (partes.length < 3) throw new ItemNoFound("Línea mal formada: " + line);

	        int nivel = Integer.parseInt(partes[0]);
	        int id = Integer.parseInt(partes[1]);

	        maxNivel = Math.max(maxNivel, nivel);

	        BNode<Integer> nodo = new BNode<>(orden);
	        nodo.idNode = id;

	        for (int i = 2; i < partes.length; i++) {
	            int clave = Integer.parseInt(partes[i]);
	            nodo.keys.set(i - 2, clave);
	            nodo.count++;
	        }

	        nodos.put(id, nodo);
	        niveles.put(id, nivel);
	    }

	    // Validaciones y enlace de nodos
	    for (Map.Entry<Integer, BNode<Integer>> entry : nodos.entrySet()) {
	        int id = entry.getKey();
	        BNode<Integer> nodo = entry.getValue();
	        int nivel = niveles.get(id);

	        // Validación de cantidad de claves (excepto raíz)
	        if (nivel != 0 && nodo.count < Math.ceil((orden - 1) / 2.0)) {
	            throw new ItemNoFound("Nodo " + id + " tiene menos claves que el mínimo permitido.");
	        }

	        // Validación de claves ordenadas
	        for (int i = 1; i < nodo.count; i++) {
	            if (nodo.keys.get(i - 1) > nodo.keys.get(i)) {
	                throw new ItemNoFound("Nodo " + id + " tiene claves desordenadas.");
	            }
	        }

	        // Conectar hijos solo si no está en el último nivel
	        if (nivel < maxNivel) {
	            List<BNode<Integer>> hijos = new ArrayList<>();

	            // Buscar hijos por niveles: los hijos deben tener nivel + 1
	            for (Map.Entry<Integer, BNode<Integer>> posibleHijo : nodos.entrySet()) {
	                if (niveles.get(posibleHijo.getKey()) == nivel + 1) {
	                    hijos.add(posibleHijo.getValue());
	                }
	            }

	            if (hijos.size() < nodo.count + 1) {
	                throw new ItemNoFound("Nodo " + id + " no tiene suficientes hijos.");
	            }

	            for (int i = 0; i <= nodo.count; i++) {
	                nodo.childs.set(i, hijos.get(i));
	            }
	        }
	    }

	    // Asumimos que el nodo con nivel 0 es la raíz
	    for (Map.Entry<Integer, Integer> e : niveles.entrySet()) {
	        if (e.getValue() == 0) {
	            tree.root = nodos.get(e.getKey());
	            break;
	        }
	    }

	    reader.close();
	    return tree;
	}

	private void fixUnderflow(BNode<E> parent, int idx) {
	    BNode<E> current = parent.childs.get(idx);
	    BNode<E> left = (idx > 0) ? parent.childs.get(idx - 1) : null;
	    BNode<E> right = (idx < parent.count) ? parent.childs.get(idx + 1) : null;

	    if (left != null && left.count > (orden - 1) / 2) {
	        // Redistribución con izquierda
	        for (int i = current.count; i > 0; i--) {
	            current.keys.set(i, current.keys.get(i - 1));
	            current.childs.set(i + 1, current.childs.get(i));
	        }
	        current.childs.set(1, current.childs.get(0));
	        current.keys.set(0, parent.keys.get(idx - 1));
	        current.childs.set(0, left.childs.get(left.count));
	        parent.keys.set(idx - 1, left.keys.get(left.count - 1));

	        left.count--;
	        current.count++;
	    } else if (right != null && right.count > (orden - 1) / 2) {
	        // Redistribución con derecha
	        current.keys.set(current.count, parent.keys.get(idx));
	        current.childs.set(current.count + 1, right.childs.get(0));
	        parent.keys.set(idx, right.keys.get(0));

	        for (int i = 0; i < right.count - 1; i++) {
	            right.keys.set(i, right.keys.get(i + 1));
	            right.childs.set(i, right.childs.get(i + 1));
	        }
	        right.childs.set(right.count - 1, right.childs.get(right.count));
	        right.count--;
	        current.count++;
	    } else if (left != null) {
	        // Fusión con izquierda
	        left.keys.set(left.count, parent.keys.get(idx - 1));
	        for (int i = 0; i < current.count; i++) {
	            left.keys.set(left.count + 1 + i, current.keys.get(i));
	        }
	        for (int i = 0; i <= current.count; i++) {
	            left.childs.set(left.count + 1 + i, current.childs.get(i));
	        }

	        left.count += current.count + 1;

	        for (int i = idx - 1; i < parent.count - 1; i++) {
	            parent.keys.set(i, parent.keys.get(i + 1));
	            parent.childs.set(i + 1, parent.childs.get(i + 2));
	        }
	        parent.keys.set(parent.count - 1, null);
	        parent.childs.set(parent.count, null);
	        parent.count--;
	    } else if (right != null) {
	        // Fusión con derecha
	        current.keys.set(current.count, parent.keys.get(idx));
	        for (int i = 0; i < right.count; i++) {
	            current.keys.set(current.count + 1 + i, right.keys.get(i));
	        }
	        for (int i = 0; i <= right.count; i++) {
	            current.childs.set(current.count + 1 + i, right.childs.get(i));
	        }

	        current.count += right.count + 1;

	        for (int i = idx; i < parent.count - 1; i++) {
	            parent.keys.set(i, parent.keys.get(i + 1));
	            parent.childs.set(i + 1, parent.childs.get(i + 2));
	        }
	        parent.keys.set(parent.count - 1, null);
	        parent.childs.set(parent.count, null);
	        parent.count--;
	    }
	}

	private E push(BNode<E> current,E cl){
		int pos[] = new int[1]; 
		E mediana;
		if(current == null){
		up = true; 
		nDes = null; 
		return cl;
	}
	else{
		boolean fl;
		BNode.SearchResult result = current.searchNode(cl);
		if(result.found){
			System.out.println("Item duplicado\n"); 
			up = false;
			return null;
		}
		mediana = push(current.childs.get(pos[0]),cl);
		if(up){
			if(current.nodeFull(this.orden-1))
				mediana = dividedNode(current,mediana,pos[0]);
			else{
				up = false;
				putNode(current,mediana,nDes,pos[0]);
			}
		}
		return mediana;
	}
	}
		
	private void putNode(BNode<E> current,E cl,BNode<E> rd,int k){
			int i;
			for(i = current.count-1; i >= k; i--)
				{ current.keys.set(i+1,current.keys.get(i));
				current.childs.set(i+2,current.childs.get(i+1));
			}
			current.keys.set(k,cl);
			current.childs.set(k+1,rd);
			current.count++;
	}
	
	private E dividedNode(BNode<E> current,E cl,int k){ BNode<E> rd = nDes;
		int i, posMdna;
		posMdna = (k <= this.orden/2) ? this.orden/2 : this.orden/2+1;
		nDes = new BNode<E>(this.orden);
		for(i = posMdna; i < this.orden-1; i++)
			{ 
			nDes.keys.set(i - posMdna, current.keys.get(i));
			nDes.childs.set(i-posMdna+1,current.childs.get(i+1));
		}
		nDes.count = (this.orden - 1) - posMdna;
		current.count = posMdna;
		if(k <= this.orden/2)
			putNode(current,cl,rd,k);
		else
			putNode(nDes,cl,rd,k-posMdna);
		E median = current.keys.get(current.count-1);
		nDes.childs.set(0,current.childs.get(current.count));
		current.count--;
		return median;
		}
	}	

