package btree;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import btree.*;

public class Main {
    public static void main(String[] args) {
            BTree<Integer> arbol = new BTree<>(4); // orden 4 = máx 3 claves por nodo
            Scanner scanner = new Scanner(System.in);
            int opcion;
            
            do {
                System.out.println("==== MENÚ ÁRBOL B ====");
                System.out.println("1. Insertar clave");
                System.out.println("2. Eliminar clave");
                System.out.println("3. Buscar clave");
                System.out.println("4. Mostrar árbol");
                System.out.println("5. Salir");
                System.out.print("Ingrese una opción: ");
                opcion = scanner.nextInt();

                switch (opcion) {
                    case 1:
                        System.out.print("Ingrese la clave a insertar: ");
                        int claveInsertar = scanner.nextInt();
                        arbol.insert(claveInsertar);
                        System.out.println("Clave insertada.\n");
                        break;

                    case 2:
                        System.out.print("Ingrese la clave a eliminar: ");
                        int claveEliminar = scanner.nextInt();
                        arbol.remove(claveEliminar);
                        System.out.println("Clave eliminada (si existía).\n");
                        break;

                    case 3:
                        System.out.print("Ingrese la clave a buscar: ");
                        int claveBuscar = scanner.nextInt();
                        boolean encontrado = arbol.search(claveBuscar);
                        if (!encontrado)
                            System.out.println(claveBuscar + " no se encontró en el árbol.\n");
                        break;

                    case 4:
                        System.out.println("\n--- Estructura del Árbol B ---\n");
                        System.out.println(arbol);
                        break;

                    case 5:
                        System.out.println("Saliendo del programa...");
                        break;

                    default:
                        System.out.println("Opción inválida, intente nuevamente.\n");
                }

            } while (opcion != 5);

            scanner.close();
        }
    }



