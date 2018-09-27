package net.simpledev.leo.uapp;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by LEoe on 8/16/2018.
 */

public class Uapp extends Application {
	@Override
	public void onCreate() {
		super.onCreate();
		FirebaseDatabase.getInstance().setPersistenceEnabled(true);
	}
}
