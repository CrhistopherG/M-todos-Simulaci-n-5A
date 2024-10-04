import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

// Clase principal AlgoritmoBlumBlum
public class AlgoritmoBlumBlum {
    private String nombre;
    private String codigo;
    private ArrayList<Parametro<?>> parametros;
    private ArrayList<NumAleatorio> numeros;
    private String[] columnas;

    public AlgoritmoBlumBlum(String nombre, String codigo, String[] columnas, Parametro<?>... parametros) {
        this.nombre = nombre;
        this.codigo = codigo;
        this.parametros = new ArrayList<>();
        this.columnas = columnas;

        for (Parametro<?> parametro : parametros) {
            this.parametros.add(parametro);
        }
        this.numeros = new ArrayList<>();
    }

    // Método para generar números aleatorios
    public double generarNumAleatorio(double seed) {
        Long p = (Long) parametros.get(0).validar();
        Long q = (Long) parametros.get(1).validar();
        Long m = (Long) parametros.get(3).validar();

        if (!esPrimo(p) || !esPrimo(q)) {
            throw new IllegalArgumentException("p y q deben ser primos.");
        }

        if (p % 4 != 3 || q % 4 != 3) {
            throw new IllegalArgumentException("p y q deben cumplir p % 4 == 3 y q % 4 == 3.");
        }

        double xiSquared = seed * seed;
        double modM = xiSquared % m;
        double normalized = modM / (m - 1);

        Map<String, Object> mapaValores = new HashMap<>();
        mapaValores.put(columnas[1], seed);
        mapaValores.put(columnas[2], xiSquared);
        mapaValores.put(columnas[3], modM);
        mapaValores.put(columnas[4], normalized);

        numeros.add(new NumAleatorio(normalized, mapaValores));
        return normalized;
    }

    // Validación de la semilla
    public static boolean seeds(Long seed, long m) {
        return seed.compareTo(m) < 0 && Math.sqrt(seed.doubleValue()) % 1 == 0;
    }

    // Método para verificar si un número es primo
    public static boolean esPrimo(Long p) {
        if (p < 2) return false;
        if (p == 2) return true;
        if (p % 2 == 0) return false;

        int top = (int) Math.sqrt(p) + 1;
        for (int i = 3; i < top; i += 2) {
            if (p % i == 0) {
                return false;
            }
        }
        return true;
    }

    // Exportar los números generados a un archivo
    public File exportarNumeros() {
        File archivo = new File("numeros.txt");
        try (FileWriter writer = new FileWriter(archivo)) {
            for (NumAleatorio num : numeros) {
                writer.write(num.getValor() + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return archivo;
    }

    // Método para imprimir la tabla de resultados
    public void imprimirTabla(double seed) {
        System.out.println("------------------------------------------------------");
        System.out.printf("| %-10s | %-15s | %-15s | %-15s | %-15s |\n", columnas[0], columnas[1], columnas[2], columnas[3], columnas[4]);
        System.out.println("------------------------------------------------------");

        double xi = seed;

        for (int i = 1; i <= 10; i++) {
            xi = generarNumAleatorio(xi);
            NumAleatorio num = numeros.get(i - 1);
            Map<String, Object> valores = num.getValores();

            System.out.printf("| %-10d | %-15s | %-15s | %-15s | %-15.4f |\n",
                    i, valores.get(columnas[1]).toString(), valores.get(columnas[2]).toString(),
                    valores.get(columnas[3]).toString(), (double) valores.get(columnas[4]));
        }

        System.out.println("------------------------------------------------------");
    }

    // Clase Parametro
    public static abstract class Parametro<T> {
        private String nombre;
        private T valor;

        public Parametro(String nombre, T valor) {
            this.nombre = nombre;
            this.valor = valor;
        }

        public abstract T validar();

        public String getNombre() {
            return nombre;
        }

        public T getValor() {
            return valor;
        }
    }

    // Clase NumAleatorio para almacenar los números generados
    public static class NumAleatorio {
        private double valor;
        private Map<String, Object> valores;

        public NumAleatorio(double valor, Map<String, Object> valores) {
            this.valor = valor;
            this.valores = valores;
        }

        public double getValor() {
            return valor;
        }

        public Map<String, Object> getValores() {
            return valores;
        }
    }

    // Método Main para ejecutar el algoritmo
    public static void main(String[] args) {
        // Definir las columnas para la tabla
        String[] columnas = {"Iteración", "Xi", "Xi^2", "mod M", "Xi / M-1"};

        // Solicitar al usuario los valores iniciales
        Long p = 419L; // Ingresar valor para p
        Long q = 479L; // Ingresar valor para q
        double seed = 8065; // Ingresar valor de la semilla

        // Validar que p y q sean válidos
        if (!esPrimo(p)) {
            System.out.println(p + " no es primo.");
            return;
        }
        if (!esPrimo(q)) {
            System.out.println(q + " no es primo.");
            return;
        }

        // Calcular m como el producto de p y q
        Long m = p * q;

        // Crear los parámetros
        ArrayList<Parametro<?>> parametros = new ArrayList<>();
        parametros.add(new Parametro<Long>("p", p) {
            @Override
            public Long validar() {
                return p;
            }
        });
        parametros.add(new Parametro<Long>("q", q) {
            @Override
            public Long validar() {
                return q;
            }
        });
        parametros.add(new Parametro<Double>("x0", seed) {
            @Override
            public Double validar() {
                return seed; 
            }
        });
        parametros.add(new Parametro<Long>("m", m) {
            @Override
            public Long validar() {
                return m;
            }
        });

        // Crear objeto AlgoritmoBlumBlum
        AlgoritmoBlumBlum bbs = new AlgoritmoBlumBlum("Blum Blum Shub", "BBS", columnas, parametros.toArray(new Parametro[0]));

        // Generar y mostrar números aleatorios
        bbs.imprimirTabla(seed);
    }
}
