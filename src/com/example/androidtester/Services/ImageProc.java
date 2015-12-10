package com.example.androidtester.Services;

import android.graphics.Point;
import android.graphics.Rect;

import java.util.Vector;

public class ImageProc {
	private String file_path;
	private Vector<Rect> faces;
	
	private Vector<Rect> eyesRects;
	private Vector<Rect> noseRects;
	private Vector<Rect> mouthRects;

	private Vector<Point> eyesP = null;
	private Vector<Point> nosesP = null;
	private Vector<Point> mouthsP = null;
	private Vector<Point> earsP = null;
	
	private Vector<String> Genders = null;
	private Vector<Boolean> Glasses = null;
	private Vector<Boolean> SunGlasses = null;
	private Vector<Boolean> Smile = null;
	private Vector<String> Orientations = null;

	private Vector<Float> FaceOrientation = null;
	private Vector<Point> cheeksP = null;

	private Integer error = null;

	public ImageProc(String path_image) {
		this.setFile_path(path_image);
	}

	public Vector<String> getGender() {
		return Genders;
	}

	public void addGender(String gender) {
		if (Genders==null){
			Genders=new Vector<String>();
		}
		Genders.add(gender);
	}

	public Vector<Boolean> getGlasses() {
		return Glasses;
	}

	public void addGlasses(Boolean glasses) {
		if (Glasses==null){
			Glasses = new Vector<Boolean>();
		}
		Glasses.add(glasses);
	}

	public Vector<Boolean> getSunGlasses() {
		return SunGlasses;
	}

	public void addSunGlasses(Boolean sunGlasses) {
		if (SunGlasses==null){
			SunGlasses = new Vector<Boolean>();
		}
		SunGlasses.add(sunGlasses);
	}

	public Vector<Boolean> getSmile() {
		return Smile;
	}

	public void addSmile(Boolean smile) {
		if (Smile==null){
			Smile = new Vector<Boolean>();
		}
		Smile.add(smile);
	}

	public Vector<Rect> getFaces() {
		return faces;
	}

	public void addFace(Rect cara) {
		if (faces==null){
			faces=new Vector<Rect>();
		}
		faces.add(cara);
	}

	public Vector<Point> getEyesPoints() {
		return eyesP;
	}

	public void addEyesPoint(Point eyeP) {
		if (eyesP==null){
			eyesP=new Vector<Point>();
		}
		this.eyesP.add(eyeP);
	}

	public Vector<Point> getNosePoints() {
		return nosesP;
	}

	public void addNosePoint(Point noseP) {
		if (nosesP==null){
			nosesP=new Vector<Point>();
		}
		nosesP.add(noseP);
	}

	public Vector<Point> getMouthPoints() {
		return mouthsP;
	}

	public void addMouthPoint(Point point) {
		if (mouthsP==null){
			mouthsP=new Vector<Point>();
		}
		mouthsP.add(point);
	}

	public String getFile_path() {
		return file_path;
	}

	public void setFile_path(String file_path) {
		this.file_path = file_path;
	}

	public Vector<Rect> getEyesRects() {
		return eyesRects;
	}

	public void addEyeRect(Rect eyeRect) {
		if (eyesRects==null){
			eyesRects=new Vector<Rect>();
		}
		eyesRects.add(eyeRect);
		if (eyeRect!=null)
			addEyesPoint(new Point((int)eyeRect.centerX(), (int)eyeRect.centerY()));	// Agregamos el punto del medio para Hausdorff
		else
			addEyesPoint(new Point(0,0));
	}

	public Vector<Rect> getNoseRects() {
		return noseRects;
	}

	public void addNoseRect(Rect noseRect) {
		if (noseRects==null){
			noseRects=new Vector<Rect>();
		}
		noseRects.add(noseRect);
	}

	public Vector<Rect> getMouthRects() {
		return mouthRects;
	}

	public void addMouthRect(Rect mouthRect) {
		if (mouthRects==null){
			mouthRects=new Vector<Rect>();
		}
		mouthRects.add(mouthRect);
	}

	public Vector<String> getOrientations() {
		return Orientations;
	}

	public void addOrientation(String orientation) {
		if (Orientations==null){
			Orientations=new Vector<String>();
		}
		Orientations.add(orientation);
	}

	public Vector<Point> getEarsPoints() {
		return earsP;
	}

	public void addEarPoint(Point earsP) {
		if (this.earsP==null){
			this.earsP=new Vector<Point>();
		}
		this.earsP.add(earsP);
	}

	public void addFaceOrientation(float faceOrientationP) {
		if (FaceOrientation==null){
			FaceOrientation=new Vector<Float>();
		}
		FaceOrientation.add(faceOrientationP);
	}

	public Vector<Float> getFaceOrientationPoints() {
		return FaceOrientation;
	}

	public void addCheekPoint(Point cheeksP){
		if (this.cheeksP==null){
			this.cheeksP=new Vector<Point>();
		}
		this.cheeksP.add(cheeksP);
	}

	public Vector<Point> getCheeksPoints(){
		return cheeksP;
	}

	public Integer getError() {
		return error;
	}

	public void setError(Integer error) {
		this.error = error;
	}
}
