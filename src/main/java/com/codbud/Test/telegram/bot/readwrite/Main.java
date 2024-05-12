package com.codbud.Test.telegram.bot.readwrite;

import java.io.*;
import java.util.Arrays;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException {
        String text = "Hello world!";

//        try (FileOutputStream fos = new FileOutputStream("notes.txt")) {
//            byte[] bytes = text.getBytes();
//            fos.write(bytes);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }

//        try(FileInputStream fin = new FileInputStream("notes.txt")){
//            int i;
//            while ((i = fin.read()) != -1){
//                System.out.println((char)i);
//            }
//        }
//
//        try(FileInputStream fin = new FileInputStream("notes.txt");
//            BufferedInputStream bis = new BufferedInputStream(fin)){
//            int i;
//            while ((i = fin.read()) != -1){
//                System.out.println((char)i);
//            }
//        }
//
//        try(FileOutputStream fos = new FileOutputStream("notes.txt");
//        BufferedOutputStream bos = new BufferedOutputStream(fos)){
//            byte[] bytes = text.getBytes();
//            fos.write(bytes);
//        }

//        try(FileWriter writer = new FileWriter("notes.txt", true)){
//            //Запись всей строки
//            writer.write(text);
//            //Запись по символам
//            writer.append('\n');
//            writer.append('!');
//            //До записываем и очищаем буфер
//            writer.flush();
//        }

//        try(FileReader reader = new FileReader("notes.txt")){
//            int c;
//            while ((c = reader.read()) != -1){
//                System.out.print((char) c);
//            }
//        }

//        try(BufferedReader bufferedReader = new BufferedReader(new FileReader("notes.txt"))){
//            String s;
//            while ((s = bufferedReader.readLine()) != null){
//                System.out.println(s);
//            }
//        }
//        try(BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("notes.txt"))) {
//            String text2 = "Hello World!";
//            bufferedWriter.write(text2);
//        }

        try (Scanner scanner = new Scanner(new File("test.csv"))) {
            String[] parsedCSV = new String[6];
            while (scanner.hasNextLine()) {
                parsedCSV = scanner.nextLine().split(",");
            }
            System.out.println(Arrays.toString(parsedCSV));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
