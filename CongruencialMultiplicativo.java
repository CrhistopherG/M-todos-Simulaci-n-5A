import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class CongruencialMultiplicativo extends JFrame {

    public CongruencialMultiplicativo() {
        setTitle("Congruencial Multiplicativo");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 510);
        setResizable(true);
        setLocationRelativeTo(null);

        JPanel CM = new JPanel();
        CM.setLayout(new BoxLayout(CM, BoxLayout.Y_AXIS));

        JPanel panelTexto = new JPanel();
        panelTexto.setLayout(new FlowLayout(FlowLayout.CENTER));
        panelTexto.setPreferredSize(new Dimension(30, 30));

        JLabel labelTexto = new JLabel("Método Congruencial Multiplicativo");
        panelTexto.add(labelTexto);

        JPanel datos = new JPanel();
        datos.setLayout(new GridLayout(6, 2, 10, 10));

        JLabel valor_Inicial = new JLabel("Valor inicial (X0)", SwingConstants.LEFT);
        JTextField contenido_inicial = new JTextField(10);
        datos.add(valor_Inicial);
        datos.add(contenido_inicial);

        JLabel valor_k = new JLabel("Valor multiplicador (k)");
        JTextField contenido_k = new JTextField(10);
        datos.add(valor_k);
        datos.add(contenido_k);

        JLabel valor_g = new JLabel("Exponente (g)");
        JTextField contenido_g = new JTextField(10);
        datos.add(valor_g);
        datos.add(contenido_g);

        JButton boton_generar = new JButton("Generar números");
        JButton boton_regresar = new JButton("Regresar");
        datos.add(boton_generar);
        datos.add(boton_regresar);

        JPanel panelTabla = new JPanel();
        DefaultTableModel modelotabla = new DefaultTableModel(new Object[]{"n", "Xn", "Resultado", "No. Pseudoaleatorio"}, 0);
        JTable tablaNumeros = new JTable(modelotabla);
        JScrollPane scrollPane = new JScrollPane(tablaNumeros);
        panelTabla.add(scrollPane);
        tablaNumeros.setBorder(BorderFactory.createLineBorder(Color.gray, 2));
        tablaNumeros.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        CM.add(panelTexto);
        CM.add(datos);
        CM.add(Box.createRigidArea(new Dimension(0, 20)));
        CM.add(scrollPane);

        boton_regresar.addActionListener(e -> {
            Menu menu = new Menu();
            menu.setVisible(true);
            dispose();
        });

        boton_generar.addActionListener(e -> {
            try {
                int X0 = Integer.parseInt(contenido_inicial.getText());
                int k = Integer.parseInt(contenido_k.getText());
                int g = Integer.parseInt(contenido_g.getText());

                // Validar las restricciones
                if (X0 % 2 == 0) {
                    JOptionPane.showMessageDialog(null, "X0 debe ser un número impar.");
                    return;
                }
                if (k < 0) {
                    JOptionPane.showMessageDialog(null, "k debe ser un número entero no negativo.");
                    return;
                }
                if (g < 0) {
                    JOptionPane.showMessageDialog(null, "g debe ser un número entero no negativo.");
                    return;
                }

                // Calcular m y a
                int m = (int) Math.pow(2, g);
                int a = (5 + 8 * k); // Usar la fórmula adecuada
                modelotabla.setRowCount(0);

                List<Object[]> resultados = generarNumeros(X0, a, m);

                for (Object[] fila : resultados) {
                    modelotabla.addRow(fila);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Por favor, introduce valores válidos.");
            }
        });
        JPanel panelPDF = new JPanel();
        JButton boton_guardarPDF = new JButton("Guardar en PDF");
        panelPDF.add(boton_guardarPDF);

        // Agregar el ActionListener al botón
        boton_guardarPDF.addActionListener(e -> {
        try {
            seleccionarYGuardarPDF(modelotabla);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error al guardar el archivo PDF: " + ex.getMessage());
        }
        });

        CM.add(panelPDF);
        add(CM);
        setVisible(true);
    }



    private List<Object[]> generarNumeros(int X0, int a, int m) {
        List<Object[]> datos = new ArrayList<>();
        List<Integer> valoresGenerados = new ArrayList<>();
        int xn = X0;
        int contador = 1;
    
        while (true) {
            int resultado = (a * xn) % m;
            double noPseudoaleatorio = (double) resultado / (m - 1);
            String noPseudoaleatorioFormateado = String.format("%.5f", noPseudoaleatorio);
    
            // Agregar la fila a la lista
            datos.add(new Object[]{contador, xn, resultado, noPseudoaleatorioFormateado});
    
            // Comprobar si el valor ya se generó
            if (valoresGenerados.contains(resultado)) {
                // Agregar uno más antes de salir
                datos.add(new Object[]{contador + 1, resultado, (a * resultado) % m, String.format("%.5f", (double) ((a * resultado) % m) / (m - 1))});
                break;
            }
    
            // Guardar el valor generado
            valoresGenerados.add(resultado);
            xn = resultado;
            contador++;
        }
    
        return datos;
    }
    
    private void seleccionarYGuardarPDF(DefaultTableModel modelotabla) throws Exception {
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
            PdfPTable pdfTable = new PdfPTable(4); // Cuatro columnas para los datos de la tabla
            pdfTable.addCell("n");
            pdfTable.addCell("Xn");
            pdfTable.addCell("Resultado");
            pdfTable.addCell("No. Pseudoaleatorio");
    
            // Llenar la tabla PDF con los datos de la JTable
            for (int i = 0; i < modelotabla.getRowCount(); i++) {
                for (int j = 0; j < modelotabla.getColumnCount(); j++) {
                    pdfTable.addCell(modelotabla.getValueAt(i, j).toString());
                }
            }
    
            document.add(pdfTable);
            document.close();
    
            JOptionPane.showMessageDialog(null, "Archivo PDF guardado en: " + archivoPDF.getAbsolutePath());
        }
    }
    
    
    


}
