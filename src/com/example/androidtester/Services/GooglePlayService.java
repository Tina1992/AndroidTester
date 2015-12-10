package com.example.androidtester.Services;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.Log;
import android.util.SparseArray;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.google.android.gms.vision.face.Landmark;
import com.mashape.unirest.http.exceptions.UnirestException;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.List;


public class GooglePlayService extends AbsService{
    private Hashtable<String,Integer> Landmark_Constants=new Hashtable<String,Integer>();
    private Activity act;
    public GooglePlayService(Activity a){
        super();
        this.act=a;
        addAvailableAtt("Eyes");
        addAvailableAtt("Mouth");
        addAvailableAtt("Cheeks");
        addAvailableAtt("Ears");
        addAvailableAtt("Smile");
        addAvailableAtt("Nose");
        addAvailableAtt("FaceOrientation");
        Landmark_Constants.put("BOTTON_MOUTH", 0);
        Landmark_Constants.put("LEFT_CHEEK",1);
        Landmark_Constants.put("LEFT_EAR",3);
        Landmark_Constants.put("LEFT_EYE",4);
        Landmark_Constants.put("LEFT_MOUTH",5);
        Landmark_Constants.put("NOSE_BASE",6);
        Landmark_Constants.put("RIGHT_CHEEK",7);
        Landmark_Constants.put("RIGHT_EAR",9);
        Landmark_Constants.put("RIGHT_EYE",10);
        Landmark_Constants.put("RIGHT_MOUTH",11);
        Log.i("google play", "constructor");
    }
    public JSONObject post(File file, Hashtable<String, Boolean> options) throws UnirestException, IOException, JSONException {
        imageProc=new ImageProc(file.getAbsolutePath());
        JSONObject resultado=new JSONObject();
        //OBTENIENDO LOS ARCHIVOS.
        InputStream inputstream = new java.io.FileInputStream(file);
        Bitmap image = BitmapFactory.decodeStream(inputstream);
        //OBTENIENDO EL CONTEXTO DE LA ACTIVIDAD:
        Context context=act.getApplicationContext();
        //CREANDO EL DETECTOR
        FaceDetector detector = new FaceDetector.Builder(context)
                .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                .setLandmarkType(FaceDetector.ALL_LANDMARKS)
                .build();
        Frame frame = new Frame.Builder().setBitmap(image).build();
        SparseArray<Face> faces = detector.detect(frame);
        //OBTENCION DEL ROSTRO DETECTADO:
        for(int i=0;i<faces.size();i++){
            Face rostro=faces.valueAt(i);
            //new Rect(X  of the left side, Y  of the top, X  of the right side, Y  of the bottom)
            int coordX=(int)rostro.getPosition().x;
            int coordY=(int)rostro.getPosition().y;
            int width=(int)rostro.getWidth();
            int height=(int)rostro.getHeight();
            imageProc.addFace(new Rect(coordX,coordY,coordX+width,coordY+height));
            List<Landmark> landmarks=rostro.getLandmarks();
            for(int j=0;j<landmarks.size();j++){
                int Constant=landmarks.get(j).getType();
                for(String k: Landmark_Constants.keySet())
                    if(Landmark_Constants.get(k)==Constant)
                        resultado.put(k, landmarks.get(j).getPosition());
            }

            if(options.get("Eyes")){
                if(resultado.get("RIGHT_EYE")!=null)
                    imageProc.addEyesPoint(pointFaPoint((PointF) resultado.get("RIGHT_EYE"))); 	//OJO IZQUIERDO MIRANDO LA IMAGEN.
                if(resultado.get("LEFT_EYE")!=null)
                    imageProc.addEyesPoint(pointFaPoint((PointF) resultado.get("LEFT_EYE")));	//OJO DERECHO MIRANDO LA IMAGEN.
            }
            if(options.get("Mouth")){
                //Punto central
                int x=-1;
                int y=-1;
                int distancia=0;
                boolean rm=landmarks.contains("RIGHT_MOUTH");
                boolean lm=landmarks.contains("LEFT_MOUTH");
                boolean bm=landmarks.contains("BOTTON_MOUTH");
                if(!rm)
                    if(lm && bm) {
                        x=(pointFaPoint((PointF) resultado.get("BOTTON_MOUTH"))).x;
                        y=(pointFaPoint((PointF) resultado.get("LEFT_MOUTH"))).y;
                    }
                    else
                    {
                        if(!lm)
                            if(rm && bm) {
                                x=(pointFaPoint((PointF) resultado.get("BOTTON_MOUTH"))).x;
                                y=(pointFaPoint((PointF) resultado.get("RIGHT_MOUTH"))).y;
                            }
                            else
                            {
                                if(!bm)
                                    distancia = ((pointFaPoint((PointF) resultado.get("LEFT_MOUTH"))).x - (pointFaPoint((PointF) resultado.get("RIGHT_MOUTH"))).x)/2;
                                x=(pointFaPoint((PointF) resultado.get("RIGHT_MOUTH"))).x + distancia;
                                y=(pointFaPoint((PointF) resultado.get("LEFT_MOUTH"))).y;
                            }
                    }
                if(!(x==-1) && !(y==-1))
                    imageProc.addMouthPoint(new Point(x,y));
            }
            if(options.get("Ears")){
                if(landmarks.contains("RIGHT_EAR"))
                    imageProc.addEarPoint(pointFaPoint((PointF) resultado.get("RIGHT_EAR")));
                if(landmarks.contains("LEFT_EAR"))
                    imageProc.addEarPoint(pointFaPoint((PointF) resultado.get("LEFT_EAR")));
            }
            if(options.get("Cheeks")){
                if(landmarks.contains("RIGHT_CHEEK"))
                    imageProc.addCheekPoint(pointFaPoint((PointF) resultado.get("RIGHT_CHEEK")));
                if(landmarks.contains("LEFT_CHEEK"))
                    imageProc.addCheekPoint(pointFaPoint((PointF) resultado.get("LEFT_CHEEK")));
            }
            if(options.get("Nose")){
                if(landmarks.contains("NOSE_BASE"))
                    imageProc.addNosePoint(pointFaPoint((PointF) resultado.get("NOSE_BASE")));
            }
            if(options.get("FaceOrientation")){
                //Positive euler y is when the face turns toward the right side of the of the image
                imageProc.addFaceOrientation(rostro.getEulerY());
                //Positive euler z is a counter-clockwise rotation within the image plane
                imageProc.addFaceOrientation(rostro.getEulerZ());
            }
            if(options.get("Smile")){
                if(landmarks.contains("NOSE_BASE") && landmarks.contains("LEFT_MOUTH") && landmarks.contains("RIGHT_MOUTH"))
                    if(rostro.getIsSmilingProbability()>0.5)
                        imageProc.addSmile(true);
                    else
                        imageProc.addSmile(false);
                else
                    imageProc.addSmile(false);
            }
        }
        return null;
    }
    private Point pointFaPoint(PointF pf){
        Point p=new Point();
        p.x=(int) pf.x;
        p.y=(int) pf.y;
        return p;
    }
    public ImageProc getFaceRecognition(File f, Hashtable<String, Boolean> opts) {
        try {
            startTime = System.nanoTime();
            this.post(f, opts);
            endTime = System.nanoTime();
            Log.i("google play", "realizo el post de GooglePlay");
        } catch (UnirestException e) {
            Log.i("google play:getFace","Excepcion unirest");
            e.printStackTrace();
        } catch (IOException e) {
            Log.i("google play:getFace","Excepcion IOException");
            e.printStackTrace();
        } catch (JSONException e) {
            Log.i("google play:getFace","Excepcion JSONException");
            e.printStackTrace();
        }
        return imageProc;
    }
    public String getName() {
        return "GooglePlay";
    }
}

