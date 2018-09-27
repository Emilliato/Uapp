package net.simpledev.leo.uapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddUrl extends AppCompatActivity implements View.OnClickListener {
	
	 EditText urlName, urlString;
	 Button saveUrl;
	 private DatabaseReference databaseUrl;
	public   static final String DATABASE ="urls";
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_url);
		
		urlName = findViewById(R.id.et_url_name);
		urlString =findViewById(R.id.et_url);
		saveUrl =findViewById(R.id.bt_save);
		FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
		FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
		
		String u_id="";
		try {
			u_id = firebaseUser.getUid();
		}catch (NullPointerException ex){
			Log.e("Add Url",ex.getMessage());
		}
		databaseUrl = FirebaseDatabase.getInstance().getReference(DATABASE).child(u_id);
		
		saveUrl.setOnClickListener(this);
		
	}
	
	@Override
	public void onClick(View view) {
		if (view==saveUrl){
			String name= urlName.getText().toString();
			String url_string= urlString.getText().toString();
			
				if (name.isEmpty()||url_string.isEmpty()){
				showToast("Enter all fields please!");
				}else {
					saveUrl(name,url_string);
					urlString.setText("");
					urlName.setText("");
				}
		}
	}
	private void saveUrl(String name, String urlString){
		
		 String urlId = databaseUrl.push().getKey();
		 databaseUrl.child(urlId).setValue(new MyUrl(urlId,name,urlString));
		 
		 showToast("Data Inserted");
		 
		finish();
	}
	private void showToast(String msg){
		Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
	}
}
