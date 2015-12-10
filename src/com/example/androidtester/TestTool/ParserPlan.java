package com.example.androidtester.TestTool;

import android.app.Activity;

import org.xml.sax.SAXException;

import com.example.androidtester.Metrics.AbsMetric;
import com.example.androidtester.Services.AbsService;
import com.example.androidtester.Services.GooglePlayService;
import com.example.androidtester.Services.OpenCVService;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Hashtable;
import java.util.Vector;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class ParserPlan {
    public static Hashtable<String, Boolean> atributos = new Hashtable<>();
    public static Vector<String> providers = new Vector<>();
    public static Vector<String> metrics = new Vector<>();
    public static Vector<String> imagesDir = new Vector<>();
    public static int tries = 1;

    public static Vector<AbsService> providersServices = new Vector<>();
    public static Vector<AbsMetric> metricsMet = new Vector<>();

    private static Activity caller;
    public ParserPlan(Activity act) {
        caller=act;
    }


    public void decodePlan(File f) {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser saxParser;
        try {
            saxParser = factory.newSAXParser();
            eraseContent();
            PlanHandler planHandler = new PlanHandler();
            saxParser.parse(f, planHandler);
            decodeServices();
            decodeMetrics();
        } catch (ParserConfigurationException | SAXException | IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
    }

    private void eraseContent() {
        if ((atributos!=null)&&(!atributos.isEmpty())){
            atributos = new Hashtable<>();
        }
        if ((providers!=null)&&(!providers.isEmpty())){
            providers = new Vector<>();
        }
        if ((metrics!=null)&&(!metrics.isEmpty())){
            metrics = new Vector<>();
        }
        if ((providersServices!=null)&&(!providersServices.isEmpty())){
            providersServices = new Vector<>();
        }
        if ((metricsMet!=null)&&(!metricsMet.isEmpty())){
            metricsMet = new Vector<>();
        }

    }

    private void decodeServices() {
        for (String s : providers) {
            try {
                if ((!s.contains("GooglePlay"))&&(!s.contains("OpenCV"))){

                    Object instanceOfMyClass = Class.forName("com.example.androidtester.Services." + s + "Service")
                            .newInstance();

                    providersServices.add((AbsService) instanceOfMyClass);}
                else
                {
                    if (s.contains("GooglePlay")){
                    String className="com.example.androidtester.Services." + s + "Service";
                    Class<GooglePlayService> _tempClass = (Class<GooglePlayService>) Class.forName(className);
                    Constructor<GooglePlayService> ctor = _tempClass.getDeclaredConstructor(Activity.class);
                    GooglePlayService googlePlay = ctor.newInstance(caller);
                    providersServices.add(googlePlay);}
                    else
                    {
                        String className="com.example.androidtester.Services." + s + "Service";
                        Class<OpenCVService> _tempClass = (Class<OpenCVService>) Class.forName(className);
                        Constructor<OpenCVService> ctor = _tempClass.getDeclaredConstructor(Activity.class);
                        OpenCVService googlePlay = ctor.newInstance(caller);
                        providersServices.add(googlePlay);
                    }
                }
            } catch (ClassNotFoundException e) {
                // TODO Auto-generated catch block
                System.err.println("Error con la clase "+s+". Clase no implementada. No será considerada en el testing.");
            } catch (SecurityException | InstantiationException | IllegalArgumentException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
    }

    private void decodeMetrics() {
        for (String s : metrics) {
            try {
                Object instanceOfMyClass = Class.forName("com.example.androidtester.Metrics." + s + "Metric")
                        .newInstance();

                metricsMet.add((AbsMetric) instanceOfMyClass);
            } catch (ClassNotFoundException | IllegalAccessException | IllegalArgumentException | InstantiationException | SecurityException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
    }

    public void addDirectory(String d){
        imagesDir.add(d);
    }

}
