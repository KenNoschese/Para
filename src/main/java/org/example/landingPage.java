package org.example;

import javax.swing.*;
import java.awt.*;

public class landingPage extends JFrame {

    public landingPage() {
        setSize(1920, 1080);
        setTitle("Para!");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel favourites = new JPanel();
        favourites.setPreferredSize(new Dimension(256, 960));
        favourites.setBackground(Color.DARK_GRAY);

        JPanel mainPage = new JPanel();
        mainPage.setPreferredSize(new Dimension(1644, 960));
        mainPage.setLayout(new BorderLayout());

        JPanel head = new JPanel();
        head.setPreferredSize(new Dimension(1181, 160));
        head.setBackground(Color.BLACK);

        JPanel center = new JPanel();
        center.setPreferredSize(new Dimension(1181, 848));
        center.setBackground(Color.GRAY);

        JPanel inputContainer = new JPanel();
        inputContainer.setPreferredSize(new Dimension(1181, 300));
        inputContainer.setOpaque(false);
        inputContainer.setLayout(new FlowLayout(FlowLayout.LEFT, 40, 100));

        JPanel divider = new JPanel();
        divider.setPreferredSize(new Dimension(30, 55));
        divider.setOpaque(false);

        JButton searchButton = new JButton("Search");
        searchButton.setPreferredSize(new Dimension(81, 40));



        JTextField destination = new JTextField();
        destination.setPreferredSize(new Dimension(383, 55));

        JTextField currentLocation = new JTextField();
        currentLocation.setPreferredSize(new Dimension(383, 55));

        inputContainer.add(destination);
        inputContainer.add(currentLocation);
        inputContainer.add(divider);
        inputContainer.add(searchButton);

        center.add(inputContainer);
        mainPage.add(center, BorderLayout.CENTER);
        mainPage.add(head, BorderLayout.NORTH);
        add(favourites, BorderLayout.WEST);
        add(mainPage, BorderLayout.CENTER);

        setVisible(true);
    }
}
