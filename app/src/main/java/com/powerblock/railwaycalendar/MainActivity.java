package com.powerblock.railwaycalendar;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

@SuppressLint("SimpleDateFormat")
public class MainActivity extends ActionBarActivity{
	//This is a test
	private String[] mTitles;
	private DrawerLayout mDrawerLayout;
	private ActionBarDrawerToggle mDrawerToggle;
	private ListView mDrawerList;
	private CharSequence mTitle;
	private ArrayList<Fragment> mFragArray = new ArrayList<Fragment>();

	private FragmentManager mFragManager;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		if(savedInstanceState != null){
			savedInstanceState.clear();
		}
		
		super.onCreate(savedInstanceState);
		final ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayShowCustomEnabled(true);
		actionBar.setCustomView(getLayoutInflater().inflate(R.layout.actionbar_image_view, null));
		setContentView(R.layout.activity_main);
		
		mFragManager = getSupportFragmentManager();
		

		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		
		mTitles = getResources().getStringArray(R.array.navArray);
		mDrawerList = (ListView) findViewById(R.id.left_drawer);
		mTitle = getTitle();
		
		mDrawerList.setAdapter(new CustomListAdapter(this, R.layout.drawer_list_item, mTitles));
		
		mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
		
		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.drawable.ic_navigation_drawer, R.string.drawer_open, R.string.drawer_close){
			
			public void onDrawerClosed(View view){
				getSupportActionBar().setTitle(mTitle);
				supportInvalidateOptionsMenu();
			}
			
			public void onDrawerOpened(View drawerView){
				getSupportActionBar().setTitle(mTitle);
				supportInvalidateOptionsMenu();
			}
			
		};
		
		mDrawerLayout.setDrawerListener(mDrawerToggle);
		
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeButtonEnabled(true);
		
		addFragment();
	}
	
	@Override
	protected void onPostCreate(Bundle savedInstanceState){
		super.onPostCreate(savedInstanceState);
		mDrawerToggle.syncState();
	}
	
	
	@Override
	public void onConfigurationChanged(Configuration newConfig){
		super.onConfigurationChanged(newConfig);
		mDrawerToggle.onConfigurationChanged(newConfig);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		if(mDrawerToggle.onOptionsItemSelected(item)){
			return true;
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		return super.onPrepareOptionsMenu(menu);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		return false;
	}
	
	@Override
	public boolean onKeyDown(int keycode, KeyEvent e){
		if(keycode == KeyEvent.KEYCODE_MENU){
			 if (!mDrawerLayout.isDrawerOpen(mDrawerList)) {
				 mDrawerLayout.openDrawer(mDrawerList);
		     } else if(mDrawerLayout.isDrawerOpen(mDrawerList)){
		    	 mDrawerLayout.closeDrawer(mDrawerList);
		     }
			 return true;
		}
		return super.onKeyDown(keycode, e);
	}
	
	private void addFragment(){
		CalculatorFragment calcFrag = new CalculatorFragment();
		FragmentTransaction fragTransact = mFragManager.beginTransaction();
		fragTransact.replace(R.id.fragment_parent, calcFrag);
		fragTransact.commit();
		mFragArray.add(calcFrag);
	}
	
	public void selectFrag(String text, int position){
		FragmentTransaction fragTransact = mFragManager.beginTransaction();
		
		Fragment frag = null;
		boolean split = false;
		CharSequence title = "";
		Log.v("text",text);
		if(text.equals("Date Calculator")){
			Log.v("Switch replace", "calculator");
			frag = new CalculatorFragment();
			title = "Calculator";
		} else if(text.equals("Railway Calendar")){
			Log.v("Switch replace", "pdffragment");
			frag = new PdfFragment();
			title = "Calendar";
		} else if(text.equals("Split")){
			split = true;
			title = "Calculator/Calendar";
		}
		
		Log.v("split", String.valueOf(split));
		for(int i = 0; i < mFragArray.size(); i++){
			fragTransact.remove(mFragArray.get(i));
			Log.v("FragArray", "removed");
			
		}
		mFragArray.clear();
		if(split == false){	
			fragTransact.replace(R.id.fragment_parent, frag).commit();
			mFragArray.add(frag);
		} else {
			frag = new CalculatorFragment();
			fragTransact.replace(R.id.fragment_parent, frag).commit();
			mFragArray.add(frag);
			frag = new PdfFragment();
			mFragManager.beginTransaction().add(R.id.fragment_parent, frag).commit();
			mFragArray.add(frag);
		}
		
		mDrawerList.setItemChecked(position, true);
		setTitle(title);
		mDrawerLayout.closeDrawer(mDrawerList);
		
	}
	
	@Override
	public void setTitle(CharSequence title){
		mTitle = title;
		getSupportActionBar().setTitle(title);
	}
	
	private class DrawerItemClickListener implements ListView.OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			TextView tView = (TextView) view;
			Log.v("frag", tView.getText().toString());
			selectFrag(tView.getText().toString(), position);
		}
		
	}
	
}