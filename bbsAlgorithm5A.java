import javax.swing.BorderFactory;
import javax.swing.JButton;
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
import java.util.ArrayList;

public class bbsAlgorithm5A extends JFrame{
    int p;
    int q;
    int m;
    int seed;
    int true_seed;
    int iterations;
    boolean parameters_valid = false;

    ArrayList<Double> numbers = new ArrayList<>();

    JPanel panel = new JPanel(new BorderLayout(10, 10));
    JTextField seed_input = new JTextField(8);
    JTextField p_input = new JTextField(8);
    JTextField q_input = new JTextField(8);
    JTextField m_input = new JTextField(8);
    JTextField i_input = new JTextField(8);

    JButton gen_button = new JButton("generar");
    JButton export_button = new JButton("exportar");

    JTable table = new JTable();
    DefaultTableModel table_model = ((DefaultTableModel)table.getModel());

    Color error_tint = Color.getHSBColor(.0f, .3f, 1f);

    DocumentListener doc_Listener = new DocumentListener() {
        @Override
        public void insertUpdate(DocumentEvent e) {changedUpdate(e);}
        @Override
        public void removeUpdate(DocumentEvent e) {changedUpdate(e);}

        @Override
        public void changedUpdate(DocumentEvent e) {
            try {
                Document event_doc = e.getDocument();
                String doc_text = e.getDocument().getText(0, e.getDocument().getLength());
                if (event_doc == seed_input.getDocument()) {
                    try {
                        seed = Integer.parseInt(doc_text);
                    } catch (Exception ex) {
                        seed = -1;
                    }
                    if (seed >= m || seed <= 0) {
                        parameters_valid = false;
                        seed_input.setBackground(error_tint);
                        throw new IllegalArgumentException();
                    } else {
                        parameters_valid = true;
                        seed_input.setBackground(Color.white);
                    }
                } else if (event_doc == p_input.getDocument()) {
                    try {
                        p = Integer.parseInt(doc_text);
                    } catch (Exception ex) {
                        p = -1;
                    }
                    if (!isPrime(p) || !(p % 4 == 3)) {
                        parameters_valid = false;
                        p_input.setBackground(error_tint);
                        throw new IllegalArgumentException();
                    } else {
                        parameters_valid = true;
                        p_input.setBackground(Color.white);
                        m = p*q;
                        m_input.setText(String.valueOf(m));
                        seed_input.setText(String.valueOf(seed));
                    }
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
                } else if (event_doc == i_input.getDocument()) {
                    try {
                        iterations = Integer.parseInt(doc_text);
                    } catch (Exception ex) {
                        iterations = -1;
                    }
                    if (iterations < 0) {
                        parameters_valid = false;
                        i_input.setBackground(error_tint);
                        throw new IllegalArgumentException();
                    } else {
                        parameters_valid = true;
                        i_input.setBackground(Color.white);
                    }
                }
            } catch (Exception ex) {}
        }
    };

    ActionListener act_listener = new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == gen_button && parameters_valid) {
                generate();
            }
        }
        
    };

    bbsAlgorithm5A() {
        super("Algoritmo Blum Blum Shub");
        setSize(800, 600);
        setContentPane(panel);

        JLabel title = new JLabel(getTitle());
        title.setFont(title.getFont().deriveFont(Font.PLAIN, 24));
        panel.add(title, BorderLayout.NORTH);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel input_panel = new JPanel(new GridLayout(20, 1, 5, 5));
        input_panel.add(new JLabel("semilla"));
        seed_input.setToolTipText("la semilla debe ser positiva y menor a m");
        seed_input.getDocument().addDocumentListener(doc_Listener);
        input_panel.add(seed_input);

        input_panel.add(new JLabel("valor p"));
        p_input.setToolTipText("p debe ser primo y positivo");
        p_input.getDocument().addDocumentListener(doc_Listener);
        input_panel.add(p_input);

        input_panel.add(new JLabel("valor q"));
        q_input.setToolTipText("q debe ser primo y positivo");
        q_input.getDocument().addDocumentListener(doc_Listener);
        input_panel.add(q_input);

        input_panel.add(new JLabel("m"));
        m_input.setEditable(false);
        input_panel.add(m_input);

        input_panel.add(new JLabel("iteraciones"));
        i_input.setToolTipText("el número de iteraciones debe ser 0 o mayor, 0 genera hasta completar el periodo");
        i_input.getDocument().addDocumentListener(doc_Listener);
        input_panel.add(i_input);

        gen_button.addActionListener(act_listener);
        input_panel.add(gen_button);

        input_panel.add(export_button);

        panel.add(input_panel, BorderLayout.WEST);

        ScrollPane table_panel = new ScrollPane();
        table.setDefaultEditor(Object.class, null);
        table_model.setColumnCount(4);
        table_model.addRow(new String[]{"Xi", "Xi^2", "Mod M", "Xi+1 / M-1"});
        table_panel.add(table);
        
        panel.add(table_panel, BorderLayout.CENTER);
    }

    public void initialize() {
        setVisible(true);
    }

    public void generate() {
        table_model.setRowCount(1);
        double r = -1;
        long xi = seed, xi2 = -1, modM = -1;
        if (iterations == 0) {
            iterations--;
        }
        xi2 = xi*xi;
        modM = xi2 % m;
        r = (double)modM / (double)(m - 1);
        table_model.addRow(new String[]{
            String.valueOf(xi), 
            String.valueOf(xi2), 
            String.valueOf(modM), 
            String.valueOf(r)
        });
        xi = modM;
        true_seed = (int)modM;
        while (iterations != 0) {
            System.out.println(xi);
            xi2 = xi*xi;
            modM = xi2 % m;
            r = (double)modM / (double)(m - 1);
            table_model.addRow(new String[]{
                String.valueOf(xi), 
                String.valueOf(xi2), 
                String.valueOf(modM), 
                String.valueOf(r)
            });
            xi = modM;
            iterations--;
            if (xi == true_seed && iterations < 0) {
                break;
            }            
        }
    }

    public static boolean isPrime(int num) {
        if ((num <= 1) || (num > 2 && num%2 == 0)) {
            return false;
        }
        int top = (int)Math.sqrt(num) + 1;
        for(int i = 3; i < top; i+=2){
            if(num % i == 0){
                return false;
            }
        }
        return true;
    }

    public static void main(String[] args) {
        new bbsAlgorithm5A().initialize();
    }
}