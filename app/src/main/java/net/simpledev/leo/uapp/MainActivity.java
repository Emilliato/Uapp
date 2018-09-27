package net.simpledev.leo.uapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
	private static final int RC_SIGN_IN = 1;
	private ListView url_list;
	private ArrayList<MyUrl> urlList;
	private FloatingActionButton fab;
	
	public static final String USER_ID= "user_id";
	public static final String URL_ID = "urlid";
	public static final String URL_NAME = "urlname";
	public static final String URL_DESCRIPTION = "urldescription";
	
	private String user_id="" ;
	
	private DatabaseReference databaseReference;
	private FirebaseAuth firebaseAuth;
	private FirebaseAuth.AuthStateListener authStateListener;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		initialize();//initializes the global variables
		
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		//Detaches the authentication listener whe the activity is paused
		firebaseAuth.removeAuthStateListener(authStateListener);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		//Attaches the authentication listener whe the activity is paused
		firebaseAuth.addAuthStateListener(authStateListener);
	}
	
	private void initialize(){
		//Views initialisation
		url_list = findViewById(R.id.url_list);
		fab = findViewById(R.id.fabAddUrl);
		
		//Database related objects
		firebaseAuth = FirebaseAuth.getInstance();
		
		//Check if the user is signed in already!
		authStateListener = new FirebaseAuth.AuthStateListener() {
			@Override
			public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
				FirebaseUser user = firebaseAuth.getCurrentUser();
				if (user==null){
					createSignInIntent();//Start a new activity to get a user id
				}
					//showToast("Signed in as "+ firebaseAuth.getCurrentUser().getDisplayName());
			}
		};
		//These are for grabbing a user's id
		FirebaseUser user = firebaseAuth.getCurrentUser();
		try {
			if (user==null){
				createSignInIntent();
			}else
			user_id = user.getUid();
		}catch (NullPointerException ex){
			Log.e("MainActivity : ",ex.getMessage());
		}
		
		databaseReference =FirebaseDatabase.getInstance().getReference(AddUrl.DATABASE).child(user_id);
		databaseReference.keepSynced(true);
		
		//Listener for the floating action button
		fab.setOnClickListener(this);
		
		//User Interface Update
		urlList = new ArrayList<>();
		url_list.setOnItemClickListener(setAdapter);
		
		
	}
	private void createSignInIntent() {
		List<AuthUI.IdpConfig> providers = Arrays.asList(
				new AuthUI.IdpConfig.EmailBuilder().build(),
				new AuthUI.IdpConfig.GoogleBuilder().build()
		);
		startActivityForResult(
				AuthUI.getInstance()
						.createSignInIntentBuilder()
						.setIsSmartLockEnabled(false)
						.setAvailableProviders(providers)
						.build(),
				RC_SIGN_IN
		);
	}
	
	private AdapterView.OnItemClickListener setAdapter= new  AdapterView.OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
			
			MyUrl test = urlList.get(i);
			
			
			String stringName = test.getUrlName();
			String stringURl = test.getUrlString();
			String stringId = test.getUrlId();
			
			Intent intent = new Intent(getApplicationContext(),ViewUrl.class);
			intent.putExtra(URL_NAME,stringName);
			intent.putExtra(URL_DESCRIPTION,stringURl);
			intent.putExtra(URL_ID,stringId);
			intent.putExtra(USER_ID,user_id);
			
			startActivity(intent);
			
		}
	};
	
	@Override
	public void onClick(View view) {
		if (view == fab) {
			Intent myIntent = new Intent(getApplicationContext(), AddUrl.class);
			startActivity(myIntent);
		}
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		databaseReference.addValueEventListener(new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot dataSnapshot) {
				urlList.clear();
				for(DataSnapshot urlSnapshot : dataSnapshot.getChildren()){
					MyUrl myUrl = urlSnapshot.getValue(MyUrl.class);
					urlList.add(myUrl);
				}
				UrlAdapter adapter = new UrlAdapter(getApplicationContext(),urlList);
			url_list.setAdapter(adapter);
			}
			
			@Override
			public void onCancelled(DatabaseError databaseError) {
				showToast(databaseError.getMessage());
			}
		});
	}
	
	private void showToast(String msg){
		Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main,menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int itemSelected =item.getItemId();
		if (itemSelected==R.id.s_out){
			showSignDialog();
		}
		if (itemSelected==R.id.s_name){
			String set ;
		    try {
				set = firebaseAuth.getCurrentUser().getDisplayName();
				if (!TextUtils.isEmpty(set))
				{
					item.setTitle(set);
				}else
					item.setTitle(R.string.oops);
			
			}catch (NullPointerException ex){
		    	Log.e("Main Act => User Name",ex.getMessage());
			}
		}
		
		return super.onOptionsItemSelected(item);
	}
	private void showSignDialog(){
		AlertDialog.Builder builder= new AlertDialog.Builder(this);
		
		LayoutInflater inflater =getLayoutInflater();
		@SuppressLint("InflateParams") final View dialogView = inflater.inflate(R.layout.signout_item,null);
		
		builder.setView(dialogView);
		builder.setTitle("\tSign Out ");
		
		final Button btYes = dialogView.findViewById(R.id.bt_yes);
		final Button btNo = dialogView.findViewById(R.id.bt_no);
		
		final AlertDialog dialog = builder.create();
		dialog.show();
		btYes.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				signOut();
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
	private void signOut() {
		firebaseAuth.signOut();
		if (firebaseAuth.getCurrentUser()==null){
			finish();
		}
	
	}
	
	
}
