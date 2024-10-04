import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

public class mca extends JFrame {
    private JTextField txtModulo, txtSemillas, txtTamaño;
    private JTable tablaNumeros;
    private DefaultTableModel modeloTabla;
    private int modulo;
    private int[] semillas;
    private int tamaño;

    public mca() {
        setTitle("Método Congruencial Aditivo");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 700);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Panel de entradas
        JPanel panelEntradas = new JPanel();
        panelEntradas.setLayout(new GridLayout(4, 2, 10, 10));

        panelEntradas.add(new JLabel("Módulo (M):"));
        txtModulo = new JTextField();
        panelEntradas.add(txtModulo);

        panelEntradas.add(new JLabel("Semillas (separadas por comas):"));
        txtSemillas = new JTextField();
        panelEntradas.add(txtSemillas);

        panelEntradas.add(new JLabel("Tamaño de la secuencia (n):"));
        txtTamaño = new JTextField();
        panelEntradas.add(txtTamaño);

        JButton btnGenerar = new JButton("Generar Números");
        panelEntradas.add(btnGenerar);

        // Panel de la tabla
        JPanel panelTabla = new JPanel();
        modeloTabla = new DefaultTableModel();
        modeloTabla.addColumn("Números Enteros");
        modeloTabla.addColumn("Números Decimales");

        tablaNumeros = new JTable(modeloTabla);
        JScrollPane scrollTabla = new JScrollPane(tablaNumeros);
        panelTabla.add(scrollTabla);

        // Botón para generar el PDF
        JButton btnDescargarPDF = new JButton("Descargar en PDF");
        panelTabla.add(btnDescargarPDF);

        // Agregar los paneles a la ventana
        add(panelEntradas, BorderLayout.NORTH);
        add(panelTabla, BorderLayout.CENTER);

        // Acción del botón Generar Números
        btnGenerar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generarNumerosPseudoaleatorios();
            }
        });

        // Acción del botón Descargar PDF
        btnDescargarPDF.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    seleccionarYGuardarPDF();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        // Hacer visible la ventana
        setVisible(true);
    }

    private void generarNumerosPseudoaleatorios() {
        modeloTabla.setRowCount(0); // Limpiar la tabla

        try {
            modulo = Integer.parseInt(txtModulo.getText());
            tamaño = Integer.parseInt(txtTamaño.getText());

            // Leer las semillas ingresadas por el usuario
            String[] semillasStr = txtSemillas.getText().split(",");
            semillas = new int[semillasStr.length];

            // Validar y convertir las semillas a enteros
            for (int i = 0; i < semillasStr.length; i++) {
                semillas[i] = Integer.parseInt(semillasStr[i].trim());
                if (semillas[i] >= modulo) {
                    throw new NumberFormatException("Las semillas deben ser menores que el módulo.");
                }
            }

            if (semillas.length > tamaño) {
                throw new IllegalArgumentException("El número de semillas no puede ser mayor que el tamaño de la secuencia.");
            }

            int[] numeros = new int[tamaño];
            System.arraycopy(semillas, 0, numeros, 0, Math.min(semillas.length, tamaño));

            // Fórmula para generar números pseudoaleatorios
            for (int i = semillas.length; i < tamaño; i++) {
                numeros[i] = (numeros[i - 1] + numeros[i - semillas.length]) % modulo;
            }

            // Llenar la tabla con números enteros y decimales
            for (int i = 0; i < tamaño; i++) {
                double numeroDecimal = (double) numeros[i] / (modulo - 1);
                modeloTabla.addRow(new Object[]{numeros[i], String.format("%.6f", numeroDecimal)});
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Error en la entrada: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void seleccionarYGuardarPDF() throws Exception {
        // Crear un cuadro de diálogo para seleccionar el archivo
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Guardar como PDF");
        fileChooser.setSelectedFile(new File("NumerosPseudoaleatorios.pdf"));
        int userSelection = fileChooser.showSaveDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File archivoPDF = fileChooser.getSelectedFile();

            // Crear el PDF
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(archivoPDF));

            document.open();
            PdfPTable pdfTable = new PdfPTable(2); // Dos columnas para los números enteros y decimales
            pdfTable.addCell("Números Enteros");
            pdfTable.addCell("Números Decimales");

            for (int i = 0; i < modeloTabla.getRowCount(); i++) {
                pdfTable.addCell(modeloTabla.getValueAt(i, 0).toString());
                pdfTable.addCell(modeloTabla.getValueAt(i, 1).toString());
            }

            document.add(pdfTable);
            document.close();

            JOptionPane.showMessageDialog(null, "Archivo PDF guardado en: " + archivoPDF.getAbsolutePath());
        }
    }
}
