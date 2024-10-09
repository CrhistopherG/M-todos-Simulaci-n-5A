import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.util.HashSet;
import java.util.Set;

public class GeneradorNumerosAleatoriosGUI {

    public static void main(String[] args) {
        // Crear la ventana principal
        JFrame frame = new JFrame("Generador de Números Aleatorios");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(700, 400);
        
        // Crear componentes
        JLabel labelX0 = new JLabel("Semilla X₀");
        JLabel labelX1 = new JLabel("Semilla X₁");
        JLabel labelMaxIteraciones = new JLabel("Máx. Iteraciones");

        JTextField textX0 = new JTextField(5);
        JTextField textX1 = new JTextField(5);
        JTextField textMaxIteraciones = new JTextField(5); // Campo para max iteraciones
        
        JButton botonGenerar = new JButton("Generar");
        JButton botonExportar = new JButton("Exportar");
        JButton botonMenu = new JButton("Menú");
        JButton botonCerrar = new JButton("Cerrar");
        
        // Crear tabla con las nuevas columnas
        String[] columnas = {"Xn", "X1", "X2", "Resultado", "Xn+1", "Ri"};
        DefaultTableModel model = new DefaultTableModel(null, columnas);  // Modelo de la tabla
        JTable tabla = new JTable(model);  // Se pasa el modelo al JTable
        JScrollPane scrollPane = new JScrollPane(tabla);
        
        // Panel para los campos de entrada y etiquetas
        JPanel panelEntrada = new JPanel();
        panelEntrada.setLayout(new GridLayout(3, 2, 10, 10)); // 3 filas, 2 columnas con espacio entre componentes
        panelEntrada.add(labelX0);
        panelEntrada.add(textX0);
        panelEntrada.add(labelX1);
        panelEntrada.add(textX1);
        panelEntrada.add(labelMaxIteraciones);
        panelEntrada.add(textMaxIteraciones); // Añadir campo para max iteraciones
        
        // Panel para los botones
        JPanel panelBotones = new JPanel();
        panelBotones.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panelBotones.add(botonGenerar);
        panelBotones.add(botonExportar);
        panelBotones.add(botonMenu);
        panelBotones.add(botonCerrar);
        
        // Añadir componentes al frame principal
        frame.setLayout(new BorderLayout());
        frame.add(panelEntrada, BorderLayout.NORTH);
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.add(panelBotones, BorderLayout.SOUTH);
        
        // Acción al hacer clic en "Generar"
        botonGenerar.addActionListener(e -> {
            // Obtener los valores de las semillas
            int semillaX0 = Integer.parseInt(textX0.getText());
            int semillaX1 = Integer.parseInt(textX1.getText());
            int maxIteraciones = Integer.parseInt(textMaxIteraciones.getText()); // Obtener iteraciones del nuevo campo

            Set<String> riSet = new HashSet<>();
            model.setRowCount(0); // Limpiar la tabla antes de generar nuevos datos

            int i = 0;
            String primerRi = null; // Variable para almacenar el primer Ri
            boolean repetido = false; // Bandera para saber si Ri se repitió

            while (i < maxIteraciones) { // Generar hasta que el primer Ri se repita o se alcance el límite
                long resultado = (long) semillaX0 * semillaX1;  // Multiplicar X0 y X1
                String resultadoStr = Long.toString(resultado);

                // Verificar el número de dígitos del resultado y aplicar las reglas
                String digitosCentrales;
                if (resultadoStr.length() == 5) {
                    // Si tiene 5 dígitos, agregar un cero a la izquierda
                    resultadoStr = "0" + resultadoStr;
                    digitosCentrales = resultadoStr.substring(1, 5);  // Obtener los 4 dígitos centrales
                } else if (resultadoStr.length() == 6) {
                    // Si tiene 6 dígitos, quitar el primero y el último
                    digitosCentrales = resultadoStr.substring(1, 5);  // Quitar el primero y el último dígito
                } else if (resultadoStr.length() == 7) {
                    // Si tiene 7 dígitos, agregar un cero a la izquierda
                    resultadoStr = "0" + resultadoStr;
                    digitosCentrales = resultadoStr.substring(2, 6);  // Quitar los primeros dos y los últimos dos dígitos
                } else if (resultadoStr.length() > 4) {
                    // Si tiene más de 4 dígitos, tomar los 4 centrales
                    digitosCentrales = resultadoStr.substring(2, 6);
                } else {
                    // Si tiene 4 o menos dígitos, usar el número completo
                    digitosCentrales = resultadoStr;
                }
                
                // Ri será "0." seguido de los 4 dígitos centrales
                String ri = "0." + digitosCentrales;

                // Verificar si el Ri ya se ha generado
                if (i == 0) {
                    primerRi = ri; // Almacena el primer Ri
                } else if (ri.equals(primerRi)) {
                    repetido = true; // Se encontró repetición del primer Ri
                    break;
                }

                // Añadir el Ri al conjunto
                riSet.add(ri);

                // Añadir los valores generados a la tabla
                model.addRow(new Object[]{"X" + i, semillaX0, semillaX1, resultado, digitosCentrales, ri});

                // Actualizar las semillas para la siguiente iteración
                semillaX0 = semillaX1;  // X1 se convierte en la nueva Xn
                semillaX1 = Integer.parseInt(digitosCentrales);  // Xn+1 se convierte en la nueva X1
                i++;
            }

            // Mostrar si se encontró repetición o si se alcanzó el límite
            if (repetido) {
                JOptionPane.showMessageDialog(frame, "¡Se encontró una repetición en Ri!\n" +
                        "El primer Ri: " + primerRi + "\nNúmero de iteraciones antes de la repetición: " + (i - 1));
            } else {
                JOptionPane.showMessageDialog(frame, "Se alcanzó el límite de " + maxIteraciones + " iteraciones sin encontrar repetición.\nPuede que haya un ciclo infinito.");
            }
        });
        
        // Acción al hacer clic en "Exportar"
        botonExportar.addActionListener(e -> {
            try {
                // Crear el documento PDF
                Document documento = new Document();
                String pdfPath = "tabla_generada.pdf";
                PdfWriter.getInstance(documento, new FileOutputStream(pdfPath));
                documento.open();

                // Añadir el contenido de la tabla al PDF
                PdfPTable pdfTable = new PdfPTable(tabla.getColumnCount());
                
                // Añadir las cabeceras de la tabla
                for (int j = 0; j < tabla.getColumnCount(); j++) {
                    pdfTable.addCell(tabla.getColumnName(j));
                }

                // Añadir las filas de la tabla
                for (int rows = 0; rows < tabla.getRowCount(); rows++) {
                    for (int cols = 0; cols < tabla.getColumnCount(); cols++) {
                        pdfTable.addCell(tabla.getValueAt(rows, cols).toString());
                    }
                }

                // Añadir la tabla al documento
                documento.add(pdfTable);
                documento.close();

                // Notificar al usuario y ofrecer abrir el archivo PDF
                int response = JOptionPane.showConfirmDialog(frame, "Tabla exportada a PDF correctamente.\n¿Desea abrir el archivo?", "Exportación completa", JOptionPane.YES_NO_OPTION);
                if (response == JOptionPane.YES_OPTION) {
                    Desktop.getDesktop().open(new File(pdfPath)); // Abrir el PDF
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(frame, "Error al exportar el PDF.");
            }
        });
        
        // Mostrar la ventana
        frame.setVisible(true);
    }
}
