package com.powerblock.railwaycalendar;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class CalculatorFragment extends Fragment implements DatePickerFragment.parentCommunicateInterface {
	
	private ActionBarActivity mParent;
	private ImageButton bChangeDate;
	private ImageButton bTrainDate;
	private TextView mDateShow;
	private TextView mTrainDateShow;
	private TextView mPeriodShow;
	private DatabaseHandler mDbHandler;
	private EditText mYearEditText;
	private EditText mDayEditText;
	private EditText mWeekEditText;
	
	@Override 
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		super.onCreateView(inflater, container, savedInstanceState);
		super.setRetainInstance(false);
		View layout =  inflater.inflate(R.layout.calculator_test_layout, container, false);
		bChangeDate = (ImageButton) layout.findViewById(R.id.editButton1);
		bTrainDate = (ImageButton) layout.findViewById(R.id.editButton2);
		mDateShow = (TextView) layout.findViewById(R.id.textView1);
		mTrainDateShow = (TextView) layout.findViewById(R.id.textView2);
		mPeriodShow = (TextView) layout.findViewById(R.id.periodTextView);
		//mArrowsView = (ImageView) layout.findViewById(R.id.arrows_image);
		mDbHandler = new DatabaseHandler(mParent);
		setCurrentDate();
		addListenersToButtons();
		return layout;
	}
	
	@Override
	public void onAttach(Activity activity){
		super.onAttach(activity);
		mParent = (ActionBarActivity) activity;
	}
	
	@Override
	public void onStart(){
		super.onStart();
	}
	
	private void setCurrentDate(){
		final Calendar c = Calendar.getInstance(Locale.US);
		
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH);
		int day = c.get(Calendar.DAY_OF_MONTH);
		
		mDateShow.setText(new StringBuilder().append(day).append("-").append(month + 1).append("-").append(year).toString());
		calculateTrainTime(year, month, day);
		
	}
	
	private void addListenersToButtons(){
		bChangeDate.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				createDateDialog();
			}
		});
		bTrainDate.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				createTrainWeekDialog();
			}
		});
	}
	
	private void createDateDialog(){
		DialogFragment newFrag = new DatePickerFragment(this, mDateShow);
		newFrag.show(mParent.getSupportFragmentManager(), "datePicker");
	}
	
	private void createTrainWeekDialog(){
		AlertDialog.Builder builder = new AlertDialog.Builder(mParent);
		final View v = mParent.getLayoutInflater().inflate(R.layout.numberpickerdialog, null);
		builder.setTitle("Enter Week and Day");
		builder.setView(v).setPositiveButton("Set", new DialogInterface.OnClickListener(){

			@Override
			public void onClick(DialogInterface dialog, int which) {
				mWeekEditText = (EditText) v.findViewById(R.id.editText1);   
				mDayEditText = (EditText) v.findViewById(R.id.editTextDays); 
				mYearEditText = (EditText) v.findViewById(R.id.yearEditText);
				String weekNoText = mWeekEditText.getText().toString();
				String dayNoText  = mDayEditText.getText().toString();
				String yearNoText = mYearEditText.getText().toString();
				if(!weekNoText.equals("") && !dayNoText.equals("") &&! yearNoText.equals("")){
					int weekNo = Integer.parseInt(weekNoText);
					int dayNo = Integer.parseInt(dayNoText);
					int year = Integer.parseInt(yearNoText);
					if(weekNo > 52 || dayNo > 7){
						Toast.makeText(mParent.getApplicationContext(), "Unsupported date", Toast.LENGTH_SHORT).show();
						return;
					}
					calculateRealTime(year, weekNo, dayNo);
					mTrainDateShow.setText(new StringBuilder().append("Week: " ).append(weekNo).append("  Day: ").append(dayNo).toString());
					mPeriodShow.setText("Period: " + calculatePeriodAndWeek(weekNo));
				} else {
					Toast.makeText(mParent, "Please make sure you have filled in all the boxes", Toast.LENGTH_LONG).show();
				}
			}
			}).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		}).show();
	}
	
	@SuppressLint("SimpleDateFormat")
	public void calculateRealTime(int givenYear, int givenWeek, int givenDay) {
		if(givenDay > 7){
			return;
		}
		ContentValues values = mDbHandler.getDate(givenYear);
		if(values.getAsInteger(DatabaseHandler.KEY_DAY) == -1){
			Toast.makeText(mParent, "This Year is not supported", Toast.LENGTH_LONG).show();
			return;
		}
		int startMonth = values.getAsInteger(DatabaseHandler.KEY_MONTH);
		int startDay = values.getAsInteger(DatabaseHandler.KEY_DAY);
		int month = startMonth;
		int day = startDay;
		int k = 0;
		for(int i = 1; i <= givenWeek; i++){
			k = switchMonth(month, givenYear);
			for(int j = 1; j<=7; j++){
				if(i==givenWeek && j==givenDay) break;
				day++;
				if(day>k){
					day=1;
					month++;
				}
				if(month == 13){
					month=1;
					givenYear++;
				}
			}
		}

		int period,pDay;
		period = ((givenWeek-1)/4) + 1;
		pDay = ((givenWeek-1)%4) + 1;
		if(givenYear==2015){
			period=((givenWeek-2)/4) + 1;
			pDay = ((givenWeek-2)%4) + 1;
			if(givenWeek==1){
				period = 0;
				pDay=0;
			}
		}

		Calendar cal = Calendar.getInstance(Locale.US);
		cal.clear();
		cal.set(Calendar.MONTH, month-1);
		cal.set(Calendar.DAY_OF_MONTH, day);
		cal.set(Calendar.YEAR, givenYear);
		Date result = cal.getTime();

		DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
		String dateString = df.format(result);

		mDateShow.setText(dateString);
		mPeriodShow.setText("Period: " + period + "/" + pDay);
	}

	@Override
	public void calculateTrainTime(int givenYear, int givenMonth, int givenDay) {
		boolean less = false;
		ContentValues values = mDbHandler.getDate(givenYear);
		if(values.getAsInteger(DatabaseHandler.KEY_YEAR) == -1){
			Toast.makeText(mParent, String.valueOf(givenYear) + " is not a supported year", Toast.LENGTH_LONG).show();
			return;
		}
		int startMonth = values.getAsInteger(DatabaseHandler.KEY_MONTH);
		int startDay = values.getAsInteger(DatabaseHandler.KEY_DAY);
		int startYear = values.getAsInteger(DatabaseHandler.KEY_YEAR);

		if(givenMonth+1 < startMonth){
			less = true;
		} else if(givenMonth+1==startMonth && givenDay < startDay){
			less = true;
		}

		if(less){
			givenYear --;
			givenMonth += 12;
		}

		values = mDbHandler.getDate(givenYear);
		if(values.getAsInteger(DatabaseHandler.KEY_YEAR) == -1){
			Toast.makeText(mParent, String.valueOf(givenYear) + " is not a supported year", Toast.LENGTH_LONG).show();
			return;
		}
		startMonth = values.getAsInteger(DatabaseHandler.KEY_MONTH);
		startDay = values.getAsInteger(DatabaseHandler.KEY_DAY);

		int dayCount,weekCount,k;
		dayCount=0;
		weekCount = 1;
		//first Month
		for(int i = 0; i <= givenMonth + 1; i++){
			if(i==0){
				i=startMonth;
			}
			if(i==13){
				givenYear++;
			}
			k = switchMonth((i>12)? i-12:i, givenYear);
			for(int j = 1; j<=k;j++){
				if(i==startMonth && j==1) j=startDay;
				if(i==givenMonth+1) k=givenDay;
				dayCount++;
				if(dayCount==8){
					weekCount++;
					dayCount = 1;
				}
			}
		}
		int period,pDay;
		period = ((weekCount-1)/4) + 1;
		pDay = ((weekCount-1)%4) + 1;
		if(givenYear==2015 || (givenYear==2016 && less)){
			period=((weekCount-2)/4) + 1;
			pDay = ((weekCount-2)%4) + 1;
			if(weekCount==1){
				period = 0;
				pDay=0;
			}
		}

		mTrainDateShow.setText("Week: " + weekCount + " Day: " + dayCount);
		mPeriodShow.setText("Period: " + period + "/" + pDay);
		
	}

	int switchMonth(int n, int year){
		int k=0;
		switch(n){
			case 1:
				k=31;
				break;
			case 2:
				k=28;
				if((year-2000)%4==0) k = 29;
				break;
			case 3:
				k=31;
				break;
			case 4:
				k=30;
				break;
			case 5:
				k=31;
				break;
			case 6:
				k=30;
				break;
			case 7:
				k=31;
				break;
			case 8:
				k=31;
				break;
			case 9:
				k=30;
				break;
			case 10:
				k=31;
				break;
			case 11:
				k=30;
				break;
			case 12:
				k=31;
				break;
		}
		return k;
	}

	private String calculatePeriodAndWeek(int weekNo){
		int result;
		
		//gets the period number
		int periodNum = 1;
		int weekNoForPeriod = weekNo - 1;
		int base = 4;
		while(true){
			if(weekNoForPeriod < base){
				break;
			} else {
				periodNum += 1;
				base += 4;		
			}
		}
		
		//gets the day number in the period
		int periodForCalc = periodNum - 1;
		int baseMod = periodForCalc * 4;
		if(baseMod == 0){
			result = weekNo;
		} else {
			result = weekNo % baseMod;
		}
		//mPeriodShow.setText("Period: " + String.valueOf(periodNum) + "/" + String.valueOf(result));
		return String.valueOf(periodNum) + "/" + String.valueOf(result);
	}

}
