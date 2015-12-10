package com.kulak.izabel.cameraapp;


import java.util.ArrayList;
import java.util.List;

public class ColorsReader {


    public List<ColorPick> getColorPicks() {
        return colorPicks;
    }

    private static List<ColorPick> colorPicks = new ArrayList<>();
    private static String colors =
            "800M ònieænobiaàa;255;255;255\n" +
            "803M zniewalaj•ca wanilia;251;240;220\n" +
            "802M ogniste chilli;153;81;86\n" +
            "812M zioàowy ogr¢dek;218;224;141\n" +
            "807M bananowy suflet;246;221;111\n" +
            "804M òmietankowa pokusa;251;240;212\n" +
            "806M cynamonowa òwieca;213;190;159\n" +
            "805M grota solna;188;191;188\n" +
            "810M r¢æana k•piel;227;176;185\n" +
            "801M jagodowy koktajl;138;90;119\n" +
            "808M soczyste mango ;222;158;97\n" +
            "809M morelowy sorbet;226;167;137\n" +
            "811M grecka oliwka;196;196;66\n" +
            "901S ònieænobiaàa;255;255;255\n" +
            "903S migdaàowy olejek;249;232;175\n" +
            "901S jaòminowy aromat;252;243;228\n" +
            "908S wodny masaæ;220;226;218\n" +
            "902S karmelowe muffiny;241;227;204\n" +
            "905S ciasto brzoskwiniowe;235;195;146\n" +
            "904S kompozycja smak¢w;249;231;129\n" +
            "907S relaksuj•ca k•piel;183;200;221\n" +
            "906S rzymska àa´nia;172;117;136\n" +
            "800M ònieænobiaàa;255;255;255\n" +
            "803M zniewalaj•ca wanilia;251;240;220\n" +
            "802M ogniste chilli;153;81;86\n" +
            "812M zioàowy ogr¢dek;218;224;141\n" +
            "807M bananowy suflet;246;221;111\n" +
            "804M òmietankowa pokusa;251;240;212\n" +
            "806M cynamonowa òwieca;213;190;159\n" +
            "805M grota solna;188;191;188\n" +
            "810M r¢æana k•piel;227;176;185\n" +
            "801M jagodowy koktajl;138;90;119\n" +
            "808M soczyste mango ;222;158;97\n" +
            "809M morelowy sorbet;226;167;137\n" +
            "811M grecka oliwka;196;196;66\n" +
            "901S ònieænobiaàa;255;255;255\n" +
            "903S migdaàowy olejek;249;232;175\n" +
            "901S jaòminowy aromat;252;243;228\n" +
            "908S wodny masaæ;220;226;218\n" +
            "902S karmelowe muffiny;241;227;204\n" +
            "905S ciasto brzoskwiniowe;235;195;146\n" +
            "904S kompozycja smak¢w;249;231;129\n" +
            "907S relaksuj•ca k•piel;183;200;221\n" +
            "906S rzymska àa´nia;172;117;136\n";


    public static void readColors() {
        String[] lines = colors.split("\n");
        colorPicks.clear();
        for (String line : lines) {
            String[] fields = line.split(";");
            colorPicks.add(new ColorPick(fields[0], Integer.valueOf(fields[1]), Integer.valueOf(fields[2]), Integer.valueOf(fields[3])));

        }
    }

}
