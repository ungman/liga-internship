package ru.liga;


import ru.liga.songtask.domain.Controller;


public class Main {

    public static void main(String[] args) throws Exception {
//        args=new String[]{"C:\\Users\\Home-PC\\Desktop\\test1\\Wrecking Ball.mid","change","-trans","0","-tempo","100"};
        Controller controller = new Controller(args);
        controller.makeOperations();
//        SongUtils.playTrack("D:\\trash\\liga-internship\\javacore-song-task\\target\\Wrecking Ball -trans "+args[3]+" -tempo "+args[5]+".mid");

    }


}