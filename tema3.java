import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;

public class tema3 extends JFrame implements ActionListener {

    JLabel nombre;
    JButton regresar;
    JLabel semilla, constante;
    JTextField datosemilla, datoconstante;
    JButton generar, limpiar;
    JTable resultadosTabla;
    DefaultTableModel model;
    JLabel interaccion;
    JTextField datos_inte;
    JButton exportar;

    public tema3() {
        setLayout(null);

        // Diseño de componentes
        semilla = new JLabel("Semilla Inicial: ");
        semilla.setFont(new Font("Arial", Font.PLAIN, 15));
        semilla.setBounds(70, 70, 140, 30);
        add(semilla);

        datosemilla = new JTextField("");
        datosemilla.setBounds(170, 70, 100, 30);
        datosemilla.setFont(new Font("Arial", Font.PLAIN, 15));
        add(datosemilla);

        interaccion = new JLabel("Numero: ");
        interaccion.setFont(new Font("Arial", Font.PLAIN, 15));
        interaccion.setBounds(320, 70, 100, 30);
        add(interaccion);

        datos_inte = new JTextField("");
        datos_inte.setBounds(390, 70, 80, 30);
        datos_inte.setFont(new Font("Arial", Font.PLAIN, 15));
        add(datos_inte);

        constante = new JLabel("Multiplicador constante: ");
        constante.setBounds(70, 130, 170, 30);
        constante.setFont(new Font("Arial", Font.PLAIN, 15));
        add(constante);

        datoconstante = new JTextField("");
        datoconstante.setBounds(240, 130, 100, 30);
        datoconstante.setFont(new Font("Arial", Font.PLAIN, 15));
        add(datoconstante);

        generar = new JButton("Generar");
        generar.setBounds(70, 190, 100, 30);
        generar.addActionListener(this);
        add(generar);

        limpiar = new JButton("Limpiar");
        limpiar.setBounds(300, 190, 100, 30);
        limpiar.addActionListener(this);
        add(limpiar);

        nombre = new JLabel("Algoritmo de Multiplicador Constante");
        nombre.setBounds(130, 15, 350, 30);
        nombre.setFont(new Font("Arial", Font.PLAIN, 18));
        add(nombre);

        regresar = new JButton("Regresar");
        regresar.setBounds(370, 450, 100, 30);
        regresar.addActionListener(this);
        add(regresar);

        // Inicialización de la tabla
        String[] columnNames = {"N", "Constante", "Semilla", "Resultado", "Digitos", "RI"};
        model = new DefaultTableModel(columnNames, 0);
        resultadosTabla = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(resultadosTabla);
        scrollPane.setBounds(70, 240, 400, 200);
        add(scrollPane);

        exportar = new JButton("Exportar");
        exportar.setBounds(70, 450, 100, 30);
        exportar.setFont(new Font("Arial", Font.PLAIN, 15));
        add(exportar);
        exportar.addActionListener(this);
    }

    public void actionPerformed(ActionEvent oliver) {
        if (oliver.getSource() == regresar) {
            MenuSimulacion2 ventana = new MenuSimulacion2();
            ventana.setTitle("Simulacion");
            ventana.setBounds(0, 0, 550, 450);
            ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            ventana.setVisible(true);
            ventana.setResizable(false);
            ventana.setLocationRelativeTo(null);
            this.dispose();
        } else if (oliver.getSource() == generar) {
            generarNumeros();
        } else if (oliver.getSource() == limpiar) {
            datosemilla.setText("");
            datoconstante.setText("");
            model.setRowCount(0); // Limpiar tabla
            datos_inte.setText("");
        } else if (oliver.getSource() == exportar) {
            try {
                seleccionarYGuardarPDF(model);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error al guardar el PDF: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void generarNumeros() {
        try {

            //empezamos a ver si los digitos pasan a 3 y entonces ci cumle la condicionq que pase al if
            String semilla = datosemilla.getText();
            String inte = datos_inte.getText();
            String constante = datoconstante.getText();
            if (semilla.length() >= 3) {
                if (constante.length() >= 3) {

                    int total;
                    int semillaInicial = Integer.parseInt(semilla);
                    int constanteMultiplicadora = Integer.parseInt(constante);
                    int interracion1 = Integer.parseInt(inte);

                    for (int i = 0; i < interracion1; i++) { // Generamos 10 resultados
                        total = semillaInicial * constanteMultiplicadora;

                        String totalStr = String.valueOf(total);

                        // Extraemos los 4 dígitos
                        String cuatroDigitos;
                        if (totalStr.length() > 4) {
                            int start = (totalStr.length() - 4) / 2;
                            cuatroDigitos = totalStr.substring(start, start + 4);
                        } else {
                            cuatroDigitos = totalStr;
                        }

                        double resultadoFinal = Integer.parseInt(cuatroDigitos) / 10000.0;
                        double ri = resultadoFinal; // Aquí puedes agregar lógica si necesitas calcular algo diferente

                        // Agregar fila a la tabla
                        model.addRow(new Object[]{i + 1, constanteMultiplicadora, semillaInicial, resultadoFinal, cuatroDigitos, ri});

                        // Actualizar la semilla para la próxima iteración
                        semillaInicial = Integer.parseInt(cuatroDigitos);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "ingresa tus datos correctos que sean mayr de 3 digitos y sin letras ");

                }
            } else {
                JOptionPane.showMessageDialog(this, "ingresa tus datos correctos que sean maypr de 3 digitos ");
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Por favor, ingresa un número válido!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void seleccionarYGuardarPDF(DefaultTableModel modelotabla) throws Exception {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Guardar como PDF");
        fileChooser.setSelectedFile(new File("Resultados.pdf"));
        int userSelection = fileChooser.showSaveDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File archivoPDF = fileChooser.getSelectedFile();

            // Crear el PDF
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(archivoPDF));

            document.open();
            PdfPTable pdfTable = new PdfPTable(modelotabla.getColumnCount()); // Número de columnas

            // Agregar los encabezados de la tabla
            for (int i = 0; i < modelotabla.getColumnCount(); i++) {
                pdfTable.addCell(modelotabla.getColumnName(i));
            }

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
