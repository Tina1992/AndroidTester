package com.example.androidtester.Services;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.objdetect.CascadeClassifier;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;

public class OpenCVService extends AbsService {

	private static final String TAG = "OpenCVService";

	private Activity caller;

    static{ System.loadLibrary("opencv_java3"); }

    public OpenCVService(Activity act) {
		caller = act;
        BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(act) {
            @Override
            public void onManagerConnected(int status) {
                switch (status) {
                    case LoaderCallbackInterface.SUCCESS: {
                        Log.i(TAG, "OpenCV loaded successfully");

                    } break;
                    default:
                    {

                        Log.i(TAG, "OpenCV loaded error");
                        super.onManagerConnected(status);
                    } break;
                }
            }
        };

		addAvailableAtt("Eyes");
		addAvailableAtt("Mouth");
		addAvailableAtt("Nose");
		addAvailableAtt("Glasses");
		addAvailableAtt("FaceOrientation");
	}

	private CascadeClassifier initClassifier(String name) {
		String rawName=name.replace(".xml", "");
		String pack=caller.getPackageName();
		int id= caller.getResources().getIdentifier(rawName, "raw", caller.getPackageName());
		InputStream is = caller.getResources().openRawResource(id);
		File cascadeDir = caller.getDir("cascade", Context.MODE_PRIVATE);
		File mCascadeFile = new File(cascadeDir, name);
		FileOutputStream os = null;
		try {
			os = new FileOutputStream(mCascadeFile);
			byte[] buffer = new byte[4096];
			int bytesRead;
			while ((bytesRead = is.read(buffer)) != -1) {
				os.write(buffer, 0, bytesRead);
			}
			is.close();
			os.close();
			return new CascadeClassifier(mCascadeFile.getAbsolutePath());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public ImageProc getFaceRecognition(File f, Hashtable<String, Boolean> opts) {
		// TODO Auto-generated method stub
		startTime = System.nanoTime();
		CascadeClassifier faceDetector = initClassifier("lbpcascade_frontalface.xml");
		MatOfRect faceDetections = new MatOfRect();
		imageProc = new ImageProc(f.getAbsolutePath());
		Mat image = Imgcodecs.imread(f.getAbsolutePath());
		faceDetector.detectMultiScale(image, faceDetections);
		for (Rect face : faceDetections.toArray()) {
			imageProc.addFace(RectToRectangle(face));
			if (opts.get("FaceOrientation")) {
				imageProc.addOrientation("frontal");
			}
			processOptions(image, face, opts);
		}
		CascadeClassifier faceDetector2 = initClassifier("lbpcascade_profileface.xml");
		MatOfRect faceDetections2 = new MatOfRect();
		faceDetector2.detectMultiScale(image, faceDetections2);
		for (Rect face : faceDetections2.toArray()) {
			imageProc.addFace(RectToRectangle(face));
			if (opts.get("FaceOrientation")) {
				imageProc.addOrientation("profile");
			}
			processOptions(image, face, opts);
		}
		endTime = System.nanoTime();
		return imageProc;
	}

	private void processOptions(Mat image, Rect submat,
			Hashtable<String, Boolean> opts) {
		for (String o : opts.keySet()) {
			if (opts.get(o) == true) {
				switch (o) {
				case "Mouth": {
					CascadeClassifier faceDetector2 = initClassifier("mouth.xml");
					MatOfRect mouthDetection = new MatOfRect();
					Rect r = new Rect();
					r.x = submat.x;
					r.height = submat.height / 2;
					r.y = submat.y + submat.height / 2;
					r.width = submat.width;
					faceDetector2.detectMultiScale(image.submat(r),
							mouthDetection);
					for (Rect mouth : mouthDetection.toArray()) {
						mouth.x = r.x + mouth.x;
						mouth.y = r.y + mouth.y;
						imageProc.addMouthRect(RectToRectangle(mouth));
					}
					break;
				}
				case "Eyes": {
					CascadeClassifier faceDetector2 = initClassifier("haarcascade_eye.xml");
					MatOfRect eyesDetection = new MatOfRect();
					faceDetector2.detectMultiScale(image.submat(submat),
							eyesDetection);
					for (Rect eye : eyesDetection.toArray()) {
						eye.x = submat.x + eye.x;
						eye.y = submat.y + eye.y;
						imageProc.addEyeRect(RectToRectangle(eye));
					}
					break;
				}
				case "Nose": {
					CascadeClassifier faceDetector2 = initClassifier("nariz.xml");
					MatOfRect noseDetection = new MatOfRect();
					faceDetector2.detectMultiScale(image.submat(submat),
							noseDetection);
					for (Rect nose : noseDetection.toArray()) {
						nose.x = submat.x + nose.x;
						nose.y = submat.y + nose.y;
						imageProc.addNoseRect(RectToRectangle(nose));
					}
					break;
				}
				case "Glasses": {
					CascadeClassifier faceDetector2 = initClassifier(
							"haarcascade_eye_tree_eyeglasses.xml");
					MatOfRect noseDetection = new MatOfRect();
					faceDetector2.detectMultiScale(image.submat(submat),
							noseDetection);
					if (noseDetection.toArray().length != 0) {
						imageProc.addGlasses(true);
					} else
						imageProc.addGlasses(false);
					break;
				}
				case "Smile": {
					Rect r = new Rect();
					r.x = submat.x;
					r.height = submat.height / 2;
					r.y = submat.y + submat.height / 2;
					r.width = submat.width;
					boolean smile=false;
					int i=1;
					while ((!smile)&&(i<=5)){
						CascadeClassifier faceDetector2 = initClassifier(
								"smiled_0" + i + ".xml");
						MatOfRect mouthDetection = new MatOfRect();
						faceDetector2.detectMultiScale(image.submat(r),
								mouthDetection);
						if (mouthDetection.toArray().length!=0){
							imageProc.addSmile(true);
							smile=true;
						}
						if (i<5)
							i++;
						else{
							imageProc.addSmile(false);
							smile=true;
						}
					}
					break;
				}
				}
			}
		}
	}

	private android.graphics.Rect RectToRectangle(Rect r) {
		android.graphics.Rect ret = new android.graphics.Rect();
		ret.set(r.x, r.y, r.width, r.height);
		return ret;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "OpenCV";
	}

}
