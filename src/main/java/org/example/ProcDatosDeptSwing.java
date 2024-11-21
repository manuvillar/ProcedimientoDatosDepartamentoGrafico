package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class ProcDatosDeptSwing {
    public static void main(String[] args) {
        // Crear la ventana principal
        JFrame frame = new JFrame("Consultar Datos de Departamento");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 250);
        frame.setLayout(new BorderLayout());

        // Crear el panel para la entrada de datos
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new FlowLayout());

        JLabel label = new JLabel("Número de Departamento:");
        JTextField deptField = new JTextField(10);
        JButton consultarButton = new JButton("Consultar");
        inputPanel.add(label);
        inputPanel.add(deptField);
        inputPanel.add(consultarButton);

        // Crear el área de texto para mostrar el resultado
        JTextArea resultArea = new JTextArea(7, 30);
        resultArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(resultArea);

        // Añadir los componentes a la ventana
        frame.add(inputPanel, BorderLayout.NORTH);
        frame.add(scrollPane, BorderLayout.CENTER);

        // Añadir funcionalidad al botón
        consultarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String depInput = deptField.getText().trim();
                if (!depInput.matches("\\d+")) {
                    JOptionPane.showMessageDialog(frame, "Por favor, introduce un número válido.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                int depNumber = Integer.parseInt(depInput);
                try {
                    // Conectar a la base de datos
                    Connection conexion = DriverManager.getConnection(
                            "jdbc:mysql://localhost/practica", "root", "practica");

                    // Preparar la llamada al procedimiento almacenado
                    String sql = "{ call datos_dept(?, ?, ?) }";
                    CallableStatement llamada = conexion.prepareCall(sql);

                    // Asignar el valor del parámetro de entrada
                    llamada.setInt(1, depNumber);

                    // Registrar los parámetros de salida
                    llamada.registerOutParameter(2, Types.VARCHAR); // Nombre del departamento
                    llamada.registerOutParameter(3, Types.VARCHAR); // Localidad del departamento

                    // Ejecutar la llamada
                    llamada.executeUpdate();

                    // Obtener los resultados
                    String nombreDept = llamada.getString(2);
                    String localidadDept = llamada.getString(3);

                    // Mostrar los resultados
                    resultArea.setText("Resultados:\n");
                    resultArea.append("Nombre del Departamento: " + nombreDept + "\n");
                    resultArea.append("Localidad del Departamento: " + localidadDept + "\n");

                    // Cerrar la llamada y la conexión
                    llamada.close();
                    conexion.close();
                } catch (SQLException ex) {
                    resultArea.setText("Error de conexión o consulta:\n" + ex.getMessage());
                    ex.printStackTrace();
                }
            }
        });

        // Mostrar la ventana
        frame.setVisible(true);
    }
}