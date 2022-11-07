package com.aleksei.game2048;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        //V hlavni metode tridy Main se vytvori novy model a regulator zalozeny na tomto modelu.
        Model model = new Model();
        Controller controller = new Controller(model);
        //V metode main tridy Main se vytvori objekt typu JFrame.
        JFrame game = new JFrame();
        //Ve hre (objekt typu JFrame) budeme volat nejake metody, abychom to mohli na obrazovce zobrazit správně
        game.setTitle("2048");
        game.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        game.setSize(450, 500);
        game.setResizable(false);
        game.add(controller.getView());
        game.setLocationRelativeTo(null);
        game.setVisible(true);
        
    }
}
