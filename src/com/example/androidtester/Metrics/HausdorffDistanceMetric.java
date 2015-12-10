package com.example.androidtester.Metrics;

import android.graphics.Point;
import android.os.Environment;

import org.json.JSONException;
import org.json.JSONObject;

import com.example.androidtester.Services.AbsService;
import com.example.androidtester.Services.ImageProc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;


public class HausdorffDistanceMetric extends AbsMetric {

	private double getMayorDistancia(Vector<Point> posAPI, JSONObject posManual)
			throws JSONException {
		double distRightMayor = 100000;

		for (Point p : posAPI) {
			double x1 = p.x;
			double x2 = (double) ((JSONObject) posManual.get("eye_right"))
					.get("x");
			double y1 = p.y;
			double y2 = (double) ((JSONObject) posManual.get("eye_right"))
					.get("y");
			double distRight = Math.hypot(x1 - x2, y1 - y2);
			if (distRightMayor > distRight) {
				distRightMayor = distRight;
			}
		}

		double distLeftMayor = 100000;

		for (Point p : posAPI) {
			double x1 = p.x;
			double x2 = (double) ((JSONObject) posManual.get("eye_left"))
					.get("x");
			double y1 = p.y;
			double y2 = (double) ((JSONObject) posManual.get("eye_left"))
					.get("y");
			double distLeft = Math.hypot(x1 - x2, y1 - y2);
			if (distLeftMayor > distLeft) {
				distLeftMayor = distLeft;
			}
		}

		if (distLeftMayor > distRightMayor)
			return distLeftMayor;
		return distRightMayor;

	}

	public JSONObject getPosicionManual(File archivo) throws JSONException,
			IOException {

		// #LX LY RX RY: CONSIDERA POSICION DE LOS OJOS RESPECTO A LA PERSONA,
		// NO A LA IMEGEN. (POR ESO LO INVERTIMOS)
		// EL ARCHIVO TIENE LOS PUNTOS CORRESPONDIENTES AL CENTRO DEL OJO.
		JSONObject posiciones = new JSONObject();
		FileReader fr = new FileReader(archivo);
		BufferedReader buffer = new BufferedReader(fr);
		@SuppressWarnings("unused")
		String linea1 = buffer.readLine();
		String linea = buffer.readLine();

		int primerOcurrencia = linea.indexOf("\t", 0);
		String XR = linea.substring(0, primerOcurrencia);
		Double eyeRightX = new Double(XR);

		int segundaOcurrencia = linea.indexOf("\t", primerOcurrencia + 1);
		String YR = linea.substring(primerOcurrencia + 1, segundaOcurrencia);
		Double eyeRightY = new Double(YR);

		int tercerOcurrencia = linea.indexOf("\t", segundaOcurrencia + 1);
		String XL = linea.substring(segundaOcurrencia + 1, tercerOcurrencia);
		Double eyeLeftX = new Double(XL);

		String YL = linea.substring(tercerOcurrencia + 1, linea.length());
		Double eyeLeftY = new Double(YL);

		JSONObject eye_left = new JSONObject();
		eye_left.put("x", eyeLeftX);
		eye_left.put("y", eyeLeftY);

		JSONObject eye_right = new JSONObject();
		eye_right.put("x", eyeRightX);
		eye_right.put("y", eyeRightY);

		posiciones.put("eye_left", eye_left);
		posiciones.put("eye_right", eye_right);
		buffer.close();
		return posiciones;

	}

	@Override
	public Object getDato(AbsService service, ImageProc image) {
		// TODO Auto-generated method stub
        if (image.getError()==null){
        if (image.getFaces() != null) {
            if (image.getEyesPoints() != null) {

                String file_name=new File(image.getFile_path()).getName();
				file_name=file_name.replace(".jpg",".eye");
                String eyes_path= Environment.getExternalStorageDirectory().getPath()+"/windows/BstSharedFolder/EyePos/"+file_name;
                File f = new File(eyes_path.toString());

                JSONObject eyes_prec;

                try {
                    eyes_prec = getPosicionManual(f);
                    return (float) getMayorDistancia(image.getEyesPoints(),
                            eyes_prec);
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    return (float)-1;
                } // Obtenemos los x's e y's del archivo Eyepos/nombredelaimage.txt
            }
            else
                return (float)-2;
        } else {
            return (float)-3;
        }
        return (float)0;}
        else
        {return (float)-5;}

    }

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "Hausdorff Distance";
	}

	@Override
	public String getMedida() {
		// TODO Auto-generated method stub
		return "pixel";
	}
}