import java.util.Scanner;

public class ProductosMedios {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Pedir al usuario las semillas iniciales
        System.out.print("Introduce la primera semilla: ");
        int seed1 = scanner.nextInt();
        
        System.out.print("Introduce la segunda semilla: ");
        int seed2 = scanner.nextInt();
        
        System.out.print("Introduce la cantidad de números a generar: ");
        int n = scanner.nextInt();

        System.out.print("Introduce la cantidad de dígitos en cada número generado: ");
        int digits = scanner.nextInt();
        
        // Generar números pseudoaleatorios
        int[] randomNumbers = productosMedios(seed1, seed2, n, digits);

        // Mostrar los números generados
        System.out.println("Números pseudoaleatorios generados:");
        for (int i = 0; i < randomNumbers.length; i++) {
            System.out.println("Número " + (i+1) + ": " + randomNumbers[i]);
        }
    }

    public static int[] productosMedios(int seed1, int seed2, int n, int digits) {
        int[] randomNumbers = new int[n];

        for (int i = 0; i < n; i++) {
            // Multiplicar las dos semillas
            long producto = (long) seed1 * seed2;
            System.out.println("\nMultiplicación de " + seed1 + " * " + seed2 + " = " + producto);

            // Convertir el producto a cadena para extraer dígitos, asegurando que tenga al menos 2*digits caracteres
            String productoStr = String.valueOf(producto);

            // Si el producto tiene menos dígitos, agregar ceros a la izquierda
            if (productoStr.length() < 2 * digits) {
                productoStr = String.format("%0" + (2 * digits) + "d", producto);
            }
            System.out.println("Producto con ceros añadidos: " + productoStr);

            // Extraer los dígitos centrales
            int mid = productoStr.length() / 2;
            String nextSeedStr = productoStr.substring(mid - digits / 2, mid + digits / 2);
            System.out.println("Dígitos centrales extraídos: " + nextSeedStr);

            // Convertir de nuevo a entero y agregar a la lista de números aleatorios
            int nextSeed = Integer.parseInt(nextSeedStr);
            randomNumbers[i] = nextSeed;

            // Actualizar las semillas
            System.out.println("Nueva semilla 1: " + seed2);
            System.out.println("Nueva semilla 2: " + nextSeed);

            seed1 = seed2;
            seed2 = nextSeed;
        }

        return randomNumbers;
    }
}
