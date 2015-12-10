package com.example.androidtester.TestTool;

import android.os.Environment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import com.example.androidtester.Metrics.AbsMetric;
import com.example.androidtester.Services.AbsService;
import com.example.androidtester.Services.ImageProc;


public class Tester {

    public  void test() {
        // TODO Auto-generated method stub
        String title = "";
        PrintWriter writer;
        String workingDir = Environment.getExternalStorageDirectory().getPath()+"/windows/BstSharedFolder/";
        for (AbsMetric m : ParserPlan.metricsMet) {
            if (m.getMedida() != null)
                title += ", " + m.getName() + " (in " + m.getMedida() + "s)";
            else
                title += ", " + m.getName();
        }
        try {
            writer = new PrintWriter(workingDir + "result.csv", "UTF-8");
            writer.println("Execution number, image name, Provider name"
                    + title);
            System.out.println(ParserPlan.atributos);
            System.out.println(ParserPlan.providers);
            System.out.println(ParserPlan.metrics);
            System.out.println(ParserPlan.imagesDir);
            for (int i = 0; i < ParserPlan.tries; i++) {
                for (AbsService s : ParserPlan.providersServices) {
                    for (String sD : ParserPlan.imagesDir) {
                        File fD = new File(sD);
                        File[] fArray;
                        if (s.getLimRequest() == -1) {
                            fArray = fD.listFiles();
                        } else {
                            File[] aux = fD.listFiles();
                            int cantPhoto=s.getLimRequest();
                            if (aux.length<cantPhoto){
                                cantPhoto=aux.length;
                            }
                            fArray = new File[cantPhoto];
                            for (int j = 0; j < cantPhoto; j++) {
                                fArray[j] = aux[j];
                            }
                        }
                        for (File fI : fArray) {
                            if (fI.getAbsolutePath().contains(".jpg")) {
                                ImageProc im = s.getFaceRecognition(fI,
                                        ParserPlan.atributos);
                                String line = new String();
                                File file=new File(im.getFile_path());
                                String name = file.getName();
                                line = i + 1 + "º, " + name + ", "
                                        + s.getName();
                                for (AbsMetric m : ParserPlan.metricsMet) {
                                    try {
                                        float dato = (float) m.getDato(s, im);
                                        if (dato > 0) {
                                            line += ", " + dato;
                                        } else
                                            line += ", error - " + errors(dato, im);
                                    } catch (ClassCastException e) {
                                        // TODO Auto-generated catch block
                                        try {
                                            String dato = (String) m.getDato(s,
                                                    im);
                                            line += ", " + dato;
                                        } catch (ClassCastException e1) {
                                            // TODO Auto-generated catch block
                                            e1.printStackTrace();
                                        }
                                    }
                                }
                                System.out.println(fI.getAbsolutePath());
                                writer.println(line);
                            }
                        }
                    }
                }
            }
            writer.close();
        } catch (FileNotFoundException | UnsupportedEncodingException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
    }

    private static String errors(float dato, ImageProc im) {
        if (dato == -1) {
            return "archivo incorrecto para Hausdorff";
        }
        if (dato == -2) {
            return "no se encontraron ojos en la imagen";
        }
        if (dato == -3) {
            return "no se encontraron caras en la imagen";
        }
        if (dato == -5){
            return "en la respuesta del servicio: "+im.getError();
        }
        return "archivo incorrecto para Precision";
    }
}
