package net.simpledev.leo.uapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ViewUrl extends AppCompatActivity implements View.OnClickListener {
	
	TextView ur_name,ur_desc;
	String urlId,urlD,urlN,userId ;
	Button edit ,delete;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_url);
		ur_name= findViewById(R.id.tvUrlName);
		ur_desc =findViewById(R.id.tvUrl);
		
		setData();
	}
	private void setData(){
		Intent intent = getIntent();
		urlId =intent.getStringExtra(MainActivity.URL_ID);
		urlD = intent.getStringExtra(MainActivity.URL_DESCRIPTION);
		urlN =intent.getStringExtra(MainActivity.URL_NAME);
		userId = intent.getStringExtra(MainActivity.USER_ID);
		
		//Populate The text Views
		ur_name.setText(urlN);
		ur_desc.setText(urlD);
		
		
		ur_desc.setOnClickListener(this);
		edit = findViewById(R.id.bt_edit);
		delete = findViewById(R.id.bt_delete);
		edit.setOnClickListener(this);
		delete.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View view) {
		if (view==ur_desc){
			openBrowser();
		}
		if (view==edit){
			showUpdateDialog(urlN,urlD);
		}
		if (view==delete){
			showDeleteDialog(urlId,urlN);
		}
	}
	
	private void delete(String mid){
		DatabaseReference reference = FirebaseDatabase.getInstance().getReference(AddUrl.DATABASE).child(userId).child(mid);
		reference.removeValue();
		showToast("Deleted Successfully");
		
	}
	private void update( String id, String name, String description){
		
		DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(AddUrl.DATABASE).child(userId).child(id);
		
		MyUrl url = new MyUrl(id,name,description);
		
		databaseReference.setValue(url);
		
		showToast("Url Updated");
		
	}
	private void showDeleteDialog(final String mId, String mName){
		AlertDialog.Builder builder= new AlertDialog.Builder(this);
		
		LayoutInflater inflater =getLayoutInflater();
		@SuppressLint("InflateParams") final View dialogView = inflater.inflate(R.layout.delete_item,null);
		
		builder.setView(dialogView);
		builder.setTitle("\tDelete "+mName);
		
		final Button btYes = dialogView.findViewById(R.id.bt_yes);
		final Button btNo = dialogView.findViewById(R.id.bt_no);
		
		final AlertDialog dialog = builder.create();
		dialog.show();
		btYes.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
			delete(mId);
			dialog.dismiss();
			finish();
			}
		});
		
		btNo.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				dialog.dismiss();
			}
		});
	}
	@SuppressLint("SetTextI18n")
	private void showUpdateDialog(String name, String desc){
		AlertDialog.Builder builder= new AlertDialog.Builder(this);
		
		LayoutInflater inflater =getLayoutInflater();
		@SuppressLint("InflateParams") final View dialogView = inflater.inflate(R.layout.activity_add_url,null);
		
		builder.setView(dialogView);
		builder.setTitle("Updating "+name);
		
		final EditText urlName =dialogView.findViewById(R.id.et_url_name);
		final EditText urlDesc = dialogView.findViewById(R.id.et_url);
		final Button btSave = dialogView.findViewById(R.id.bt_save);
		btSave.setText("UPDATE");
		urlName.setText(name);
		urlDesc.setText(desc);
		
		final AlertDialog dialog = builder.create();
		dialog.show();
		
		btSave.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				String name1 = urlName.getText().toString().trim();
				String descript = urlDesc.getText().toString().trim();
				String id = urlId;
				update(id,name1,descript);
				dialog.dismiss();
				finish();
			}
		});
		
		
		
		
		
	}
	private void openBrowser(){
		Intent intent = new Intent();
		
		intent.setAction(Intent.ACTION_VIEW);
		intent.setData(Uri.parse(urlD));
		startActivity(intent);
		
	}
	private void showToast(String msg){
		Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
	}
}
