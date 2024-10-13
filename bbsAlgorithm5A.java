import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.Document;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.ScrollPane;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

//Clase para el algoritmo "Blum Blum Shub"
public class bbsAlgorithm5A extends JFrame{
    //ATRIBUTOS

    //p y q, números primos cuyo mod 4 es igual a 3
    int p;
    int q;
    //m, producto de p y q
    int m;

    //semilla, mayor que 0 y menor que m
    int seed;
    //semilla verdadera,
    /* Es igual al valor de Xi después de la primer
     * iteración del algoritmo, es necesaria ya que
     * a veces la semilla no es parte del ciclo.*/
    int true_seed;

    //iteraciones, número de iteraciones a generar
    int iterations;

    //parámetros_validos,
    /* Indica si todos los parametros son válidos, se
     * usa para validar los parámetros antes de la
     * generación.*/
    boolean parameters_valid = false;
    //números, ArrayList que guarda los números generados
    ArrayList<Double> numbers = new ArrayList<>();


    //COMPONENTES DE LA INTERFAZ

    //Panel
    JPanel panel = new JPanel(new BorderLayout(10, 10));

    //Campos de datos
    JTextField seed_input = new JTextField(8);
    JTextField p_input = new JTextField(8);
    JTextField q_input = new JTextField(8);
    JTextField m_input = new JTextField(8);
    JTextField i_input = new JTextField(8);

    //Botones
    JButton gen_button = new JButton("generar");
    JButton export_button = new JButton("exportar");
    JButton back_button = new JButton("regresar");

    //Tabla
    JTable table = new JTable();
    DefaultTableModel table_model = ((DefaultTableModel)table.getModel());

    //Color para parámetros inválidos
    Color error_tint = Color.getHSBColor(.0f, .3f, 1f);


    //LISTENERS

    //Listeners para los campos de texto
    DocumentListener doc_Listener = new DocumentListener() {
        @Override
        public void insertUpdate(DocumentEvent e) {changedUpdate(e);}
        @Override
        public void removeUpdate(DocumentEvent e) {changedUpdate(e);}

        //Eventos de cambio en los campos de texto
        @Override
        public void changedUpdate(DocumentEvent e) {
            try {
                Document event_doc = e.getDocument();
                //Recuperar contenido del campo de texto
                String doc_text = e.getDocument().getText(0, e.getDocument().getLength());

                //Validación del campo "semilla"
                if (event_doc == seed_input.getDocument()) {
                    try {
                        seed = Integer.parseInt(doc_text); //Convertir contenido a no. entero
                    } catch (Exception ex) {
                        seed = -1;
                    }
                    if (seed >= m || seed <= 0) { //Validar rango [1 - (m-1)]
                        parameters_valid = false; //Indicar valor inválido
                        seed_input.setBackground(error_tint);
                        throw new IllegalArgumentException();
                    } else {
                        parameters_valid = true; //Aceptar valor válido
                        seed_input.setBackground(Color.white);
                    }

                //Validación del campo "p"
                } else if (event_doc == p_input.getDocument()) {
                    try {
                        p = Integer.parseInt(doc_text);
                    } catch (Exception ex) {
                        p = -1;
                    }
                    if (!isPrime(p) || !(p % 4 == 3)) { //Validar que es primo y su mod 4 es igual a 3
                        parameters_valid = false;
                        p_input.setBackground(error_tint);
                        throw new IllegalArgumentException();
                    } else {
                        parameters_valid = true;
                        p_input.setBackground(Color.white);
                        m = p*q; //Calcular m
                        m_input.setText(String.valueOf(m));
                        seed_input.setText(String.valueOf(seed)); //Revalidar semilla
                    }

                //Validación del campo "p"
                } else if (event_doc == q_input.getDocument()) {
                    try {
                        q = Integer.parseInt(doc_text);                        
                    } catch (Exception ex) {
                        q = -1;
                    }
                    if (!isPrime(q) || !(q % 4 == 3)) {
                        parameters_valid = false;
                        q_input.setBackground(error_tint);
                        throw new IllegalArgumentException();
                    } else {
                        parameters_valid = true;
                        q_input.setBackground(Color.white);
                        m = p*q;
                        m_input.setText(String.valueOf(m));
                        seed_input.setText(String.valueOf(seed));
                    }

                //Validación del campo "iteraciones"
                } else if (event_doc == i_input.getDocument()) {
                    try {
                        iterations = Integer.parseInt(doc_text);
                    } catch (Exception ex) {
                        iterations = -1;
                    }
                    if (iterations < 0) { //Validar que sea mayor o igual a 0
                        parameters_valid = false;
                        i_input.setBackground(error_tint);
                        throw new IllegalArgumentException();
                    } else {
                        parameters_valid = true;
                        i_input.setBackground(Color.white);
                    }
                }
            } catch (Exception ex) { //Manejo de excepciones
                System.out.println(ex.getMessage());
            }
        }
    };

    //Listeners de botones
    ActionListener act_listener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {

            //Botón de generación
            if (e.getSource() == gen_button && parameters_valid) { //Verificar validez de los parámetros
                generate(); //Generar números

            //Botón de exportar
            } else if (e.getSource() == export_button && !numbers.isEmpty()) { //Verificar que haya números en la lista
                export(); //Exportar archivo

            //Botón regresar
            } else if (e.getSource() == back_button) {
                //REGRESAR AL MENÚ
            }
        }
    };

    //CONSTRUCTOR
    bbsAlgorithm5A() {

        //Configuración de la ventana
        super("Algoritmo Blum Blum Shub");
        setSize(800, 600);
        setContentPane(panel);

        //Creación de etiqueta de título
        JLabel title = new JLabel(getTitle());
        title.setFont(title.getFont().deriveFont(Font.PLAIN, 24));
        panel.add(title, BorderLayout.NORTH);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        //Creación de panel de entrada
        JPanel input_panel = new JPanel(new GridLayout(20, 1, 5, 5));


        //AGREGAR CAMPOS

        //Agregar campo "semilla"
        input_panel.add(new JLabel("semilla")); //Agregar etiqueta para el campo
        //Establecer tooltip con los requisitos del parámetro
        seed_input.setToolTipText("la semilla debe ser positiva y menor a m");
        seed_input.getDocument().addDocumentListener(doc_Listener); //Agregar listener
        input_panel.add(seed_input); //Agregar campo al panel de entrada

        //Agregar campo "p"
        input_panel.add(new JLabel("valor p"));
        p_input.setToolTipText("p debe ser primo, positivo y su modulo 4 (p % 4) igual a 3");
        p_input.getDocument().addDocumentListener(doc_Listener);
        input_panel.add(p_input);

        //Agregar campo "q"
        input_panel.add(new JLabel("valor q"));
        q_input.setToolTipText("q debe ser primo, positivo y su modulo 4 (p % 4) igual a 3");
        q_input.getDocument().addDocumentListener(doc_Listener);
        input_panel.add(q_input);

        //Agregar campo "m"
        input_panel.add(new JLabel("m"));
        m_input.setEditable(false);
        input_panel.add(m_input);

        //Agregar campo "iteraciones"
        input_panel.add(new JLabel("iteraciones"));
        i_input.setToolTipText("el número de iteraciones debe ser 0 o mayor, 0 genera hasta completar el periodo");
        i_input.getDocument().addDocumentListener(doc_Listener);
        input_panel.add(i_input);


        //AGREGAR BOTONES

        //Agregar botón "generar"
        gen_button.addActionListener(act_listener); //Agregar listener
        input_panel.add(gen_button);

        //Agregar botón "exportar"
        export_button.addActionListener(act_listener);
        input_panel.add(export_button);

        //Agregar botón "regresar"
        back_button.addActionListener(act_listener);
        input_panel.add(back_button);


        //Agregar panel de entradas
        panel.add(input_panel, BorderLayout.WEST);


        //CREAR TABLA
        ScrollPane table_panel = new ScrollPane(); //Agregar panel con barra de desplazamiento
        table.setDefaultEditor(Object.class, null); //Quitar capacidad de edición
        table_model.setColumnCount(4); //Establecer columnas
        table_model.addRow(new String[]{"Xi", "Xi^2", "Mod M", "Xi+1 / M-1"}); //Establecer encabezados
        table_panel.add(table);
        
        panel.add(table_panel, BorderLayout.CENTER); //Agregar tabla
    }

    //M. INICIALIZAR
    public void initialize() {
        setVisible(true); //Hacer ventana visible
    }

    //M. DE GENERACIÓN
    public void generate() {
        table_model.setRowCount(1); //Restablecer tabla
        numbers.clear(); //Restablecer lista de números
        double r = -1; //Restablecer valor de r
        long xi = seed, xi2 = -1, modM = -1; //Restablecer valores iniciales de los parámetros

        if (iterations == 0) { //Verificar si el no. de iteraciones es 0
            iterations--; //Hacer las iteraciones negativas para que no interfieran con el ciclo

            //Ejecutar las primeras dos iteraciones
            /* Esto es necesario para obtener la semilla verdadera y poder detectar
             * la repetición del ciclo.*/
            for (int i = 0; i < 2; i++) {
                xi2 = xi*xi; //Xi^2
                modM = xi2 % m; //Mod(m)
                r = (double)modM / (double)(m - 1); //No. Aleatorio entre 0 y 1
    
                table_model.addRow(new String[]{ //Agregar fila a la tabla
                    String.valueOf(xi), 
                    String.valueOf(xi2), 
                    String.valueOf(modM), 
                    String.valueOf(r)
                });
    
                xi = modM; //Actualizar Xi
                numbers.add(r); //Agregar número aleatorio a la lista
    
                //Establecer la semilla verdadera
                if (i == 0) {
                    true_seed = (int)modM;
                }
            }
        }

        //Ejecutar el resto de las iteraciones, o hasta repetir el ciclo
        while (iterations != 0) {
            xi2 = xi*xi;
            modM = xi2 % m;
            r = (double)modM / (double)(m - 1);

            //Verificar si se repitió la semilla original o la semilla verdadera
            if ((xi == true_seed || xi == seed) && iterations < 0) {
                break; //Interrumpir ejecución
            }

            table_model.addRow(new String[]{
                String.valueOf(xi), 
                String.valueOf(xi2), 
                String.valueOf(modM), 
                String.valueOf(r)
            });

            xi = modM;
            numbers.add(r);

            //Reducir la cuenta de iteraciones hasta llegar a 0
            if (iterations > 0) {
                iterations--;
            }
        }
    }

    //M. EXPORTAR
    public void export() {

        //DIÁLOGO DE GUARDADO DE ARCHIVOS
        JFileChooser fileChooser = new JFileChooser("/"); //Directorio actual
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY); //Modo solo archivos
        //Nombre por defecto, incluye la semilla verdadera
        fileChooser.setSelectedFile(new File(String.format("BBS_Numbers(%d).txt", true_seed)));
        fileChooser.showSaveDialog(this); //Mostrar diálogo

        File f = fileChooser.getSelectedFile(); //Recuperar archivo seleccionado
        try (FileWriter fw = new FileWriter(f)) {
            for (Double d : numbers) { //Escribir los números de la lista al archivo
                fw.append(d.toString() + "\n");
            }
        } catch (IOException e) {
            System.out.println("Error al guardar"); //Manejo de excepciones
        }
    }

    //M. ES PRIMO
    public static boolean isPrime(int num) {
        if ((num <= 1) || (num > 2 && num%2 == 0)) {
            return false; //Falso si es menor o igual a 1, o mayor a 2 y par
        }
        int top = (int)Math.sqrt(num) + 1; //Checamos hasta la raiz cuadrada +1 del número
        for(int i = 3; i < top; i+=2){
            if(num % i == 0){
                return false; //Falso si tiene divisores
            }
        }
        return true; //Verdadero si no tuvo ningún divisor
    }

    //MAIN
    public static void main(String[] args) {
        new bbsAlgorithm5A().initialize(); //Inicializa la ventana
    }
}