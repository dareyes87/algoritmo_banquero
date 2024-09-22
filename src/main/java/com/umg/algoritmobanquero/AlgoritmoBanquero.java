package com.umg.algoritmobanquero;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AlgoritmoBanquero extends JFrame {

    private JTable tablaAsignado, tablaMax, tablaDisponible, tablaResultado;
    private DefaultTableModel modeloAsignado, modeloMax, modeloDisponible, modeloResultado;
    private JButton btnComprobar;
    private int numProcesos;
    private int numRecursos;

    public AlgoritmoBanquero() {
        // PEDIMOS AL USUARIO EL NUMERO DE PROCESOS Y LOS TIPOS DE RECURSOS
        pedirDatosIniciales();

        setTitle("ALGORITMO DEL BANQUERO");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        setLayout(new BorderLayout());

        // JPANEL TABLAS
        JPanel panelTablas = new JPanel(new GridLayout(4, 1));

        // TABLA PARA RECURSOS ACTUALES
        modeloAsignado = new DefaultTableModel(numProcesos, numRecursos);
        tablaAsignado = new JTable(modeloAsignado);
        panelTablas.add(new JScrollPane(tablaAsignado));
        panelTablas.add(new JLabel("RECURSOS ACTUALES"));

        // TABLA PARA RECURSOS MAXIMOS
        modeloMax = new DefaultTableModel(numProcesos, numRecursos);
        tablaMax = new JTable(modeloMax);
        panelTablas.add(new JScrollPane(tablaMax));
        panelTablas.add(new JLabel("RECURSOS MAXIMOS"));

        // Tabla PARA RECURSOS DISPONIBLES
        modeloDisponible = new DefaultTableModel(1, numRecursos);
        tablaDisponible = new JTable(modeloDisponible);
        panelTablas.add(new JScrollPane(tablaDisponible));
        panelTablas.add(new JLabel("RECURSOS DISPONIBLES"));

        // TABLA PARA EL RESULTADO
        modeloResultado = new DefaultTableModel(0, 1);
        tablaResultado = new JTable(modeloResultado);
        panelTablas.add(new JScrollPane(tablaResultado));
        panelTablas.add(new JLabel("RESULTADO"));

        add(panelTablas, BorderLayout.CENTER);

        // BTN PARA EMPEZAR EL ALGORITMO
        btnComprobar = new JButton("Comprobar Estado Seguro");
        add(btnComprobar, BorderLayout.SOUTH);

        // LISTENER BTN
        btnComprobar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                comprobarEstadoSeguro();
            }
        });
    }

    private void pedirDatosIniciales() {
        try {
            String procesosInput = JOptionPane.showInputDialog(this, "INGRESE EL NUMERO DE PROCESOS:", "ENTRADA INICIAL", JOptionPane.QUESTION_MESSAGE);
            numProcesos = Integer.parseInt(procesosInput);

            String recursosInput = JOptionPane.showInputDialog(this, "INGRESE EL NUMERO DE TIPOS DE RECURSOS:", "ENTRADA INICIAL", JOptionPane.QUESTION_MESSAGE);
            numRecursos = Integer.parseInt(recursosInput);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "ENTRADA NO VALIDA.", "ERROR!", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }
    }

    // ALGORITMO DEL BAMQUERO
    private void comprobarEstadoSeguro() {
        //OBTENEMOS LOS DATOS DE LAS TABLAS
        int[][] asignado = obtenerDatosDeTabla(tablaAsignado, numProcesos, numRecursos);
        int[][] max = obtenerDatosDeTabla(tablaMax, numProcesos, numRecursos);
        int[] disponible = obtenerDatosDeFila(tablaDisponible, numRecursos);

        int[][] necesidad = new int[numProcesos][numRecursos]; //CREAMOS LA TABLA DE LA NECESIDADES DEL PROCESO
        for (int i = 0; i < numProcesos; i++) {
            for (int j = 0; j < numRecursos; j++) {
                necesidad[i][j] = max[i][j] - asignado[i][j]; //INSERTADOS DATOS EN LA TABLA DE NECESIDADES DEL PROCESO
            }
        }

        //EVALUAMOS EL ESTADO DEL PROCESO
        if (esSegura(asignado, necesidad, disponible)) {
            modeloResultado.addRow(new Object[]{"EL SISTEMA ESTA EN UN ESTADO SEGURO"});
        } else {
            modeloResultado.addRow(new Object[]{"EL SISTEMA NO ESTA EN UN ESTADO SEGURO, RIESGO DE INTERBLOQUEO"});
        }
    }

    // VERIFICAMOS SI ES SEGURO
    private boolean esSegura(int[][] asignado, int[][] necesidad, int[] disponible) {
        int[] trabajo = new int[numRecursos];
        boolean[] finalizado = new boolean[numProcesos];
        
        System.arraycopy(disponible, 0, trabajo, 0, numRecursos);
        int count = 0;

        while (count < numProcesos) {
            boolean encontrado = false;
            for (int i = 0; i < numProcesos; i++) { //EJECUTAMOS PROCESO POR PROCESO
                if (!finalizado[i]) { //EJECUTAR SIEMPRE QUE EL PROCESO NO ESTE FINALIZADO (FALSE)
                    int j; //RECURSO
                    for (j = 0; j < numRecursos; j++) {
                        if (necesidad[i][j] > trabajo[j]) { //SI LAS NECESIDADES DEL RECUROS SON MAYORES A RECURSO DISPONIBLE ROMPE!
                            break;
                        }
                    }

                    if (j == numRecursos) {
                        for (int k = 0; k < numRecursos; k++) {
                            trabajo[k] += asignado[i][k]; //LIBERAMOS LOS RECURSOS
                        }
                        finalizado[i] = true;
                        encontrado = true;
                        count++;
                    }
                }
            }
            if (!encontrado) {
                return false;
            }
        }
        return true;
    }

    // Método para obtener los datos de una tabla
    private int[][] obtenerDatosDeTabla(JTable tabla, int filas, int columnas) {
        int[][] datos = new int[filas][columnas];
        for (int i = 0; i < filas; i++) {
            for (int j = 0; j < columnas; j++) {
                datos[i][j] = Integer.parseInt(tabla.getValueAt(i, j).toString());
            }
        }
        return datos;
    }

    // Método para obtener los datos de una fila (para la tabla de recursos disponibles)
    private int[] obtenerDatosDeFila(JTable tabla, int columnas) {
        int[] datos = new int[columnas];
        for (int j = 0; j < columnas; j++) {
            datos[j] = Integer.parseInt(tabla.getValueAt(0, j).toString());
        }
        return datos;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new AlgoritmoBanquero().setVisible(true);
            }
        });
    }

}
