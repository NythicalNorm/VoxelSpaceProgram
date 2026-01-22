package com.nythicalnorm.voxelspaceprogram.planettexgen;

import net.minecraft.util.RandomSource;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class MapGenTestDisplay {
    private static JLabel imageLabel;

    public static void main(String[] args) {
        JFrame frame = new JFrame("Map Window");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1500, 750);

        // Replace with your image file path
        imageLabel = new JLabel(new ImageIcon(generateImage()));

        //frame.add(imageLabel);
        frame.getContentPane().add(BorderLayout.NORTH, imageLabel);
        frame.setVisible(true);
        frame.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
                //System.out.println("Key pressed code=" + e.getKeyCode() + ", char=" + e.getKeyChar());
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    System.out.println("Starting another gen");
                    imageLabel.setIcon(new ImageIcon(generateImage()));
                }
            }
        });
    }

    public static Image generateImage() {
        RandomSource random = RandomSource.create();
        long starttime = System.nanoTime();

        BufferedImage planetMap = PlanetMapGen.GenerateMap(random, GradientSupplier.STAR_GRADIENT);

        float timeDiff = ((float)(System.nanoTime() - starttime))/1000000f;
        System.out.println("Generation took : " + timeDiff + " milliseconds");
        int targetWidth = 1000; // Desired width
        int targetHeight = 1000; // Desired height

        return planetMap.getScaledInstance(targetWidth, targetHeight, Image.SCALE_FAST);
    }
}