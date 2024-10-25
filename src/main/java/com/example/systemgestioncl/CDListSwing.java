package com.example.systemgestioncl;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Properties;

public class CDListSwing {
    private JTable table;
    private DefaultTableModel tableModel;

    public CDListSwing() {
        // Set up JFrame
        JFrame frame = new JFrame("CD List");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);
        frame.setLayout(new BorderLayout());

        // Set up table model
        tableModel = new DefaultTableModel(new Object[]{"ID", "Title", "Artist", "Year"}, 0);
        table = new JTable(tableModel);
        frame.add(new JScrollPane(table), BorderLayout.CENTER);

        // Load CDs
        loadCDs();

        frame.setVisible(true);
    }

    private void loadCDs() {
        try {
            // Set up JNDI properties
            Properties jndiProps = new Properties();
            jndiProps.put(Context.INITIAL_CONTEXT_FACTORY, "org.wildfly.naming.client.WildFlyInitialContextFactory");
            jndiProps.put(Context.PROVIDER_URL, "http-remoting://localhost:8081");
            jndiProps.put("jboss.naming.client.ejb.context", true);

            // Create the initial context
            Context context = new InitialContext(jndiProps);

            // Define the JNDI name for your CDService EJB
            String jndiName = "ejb:/EJB-1.0-SNAPSHOT/CDServiceImpl!com.example.systemgestioncl.CDService";

            // Lookup the EJB
            CDService cdService = (CDService) context.lookup(jndiName);

            // Fetch the list of CDs
            List<CD> cdList = cdService.listCDs();

            // Add CDs to table model
            for (CD cd : cdList) {
                tableModel.addRow(new Object[]{cd.getId(), cd.getTitle(), cd.getArtist(), cd.getYear()});
            }

        } catch (NamingException e) {
            e.printStackTrace();
            // Handle naming exception
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(CDListSwing::new);
    }
}

