package org.owasp.seraphimdroid;

import java.util.ArrayList;
import java.util.List;

import org.owasp.seraphimdroid.adapter.AppLockerAdapter;
import org.owasp.seraphimdroid.database.DatabaseHelper;

import android.content.ContentValues;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class AppLockFragment extends Fragment {

	private ListView lvAppLockerList;

	private List<String> appList;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_application_locker,
				container, false);

		lvAppLockerList = (ListView) view.findViewById(R.id.lv_app_locker);
		appList = new ArrayList<String>();
		prepareList();

		// DatabaseHelper dbHelper = new DatabaseHelper(getActivity());
		// Cursor cursor = dbHelper.getReadableDatabase().rawQuery(
		// "SELECT * FROM locks", null);

		lvAppLockerList
				.setAdapter(new AppLockerAdapter(getActivity(), appList));

		return view;
	}

	private boolean isSystemPackage(PackageInfo packageInfo) {
		return ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) ? true
				: false;
	}

	private void prepareList() {
		PackageManager pm = getActivity().getPackageManager();
		List<ApplicationInfo> appInfoList = pm
				.getInstalledApplications(PackageManager.GET_META_DATA);

		for (ApplicationInfo appInfo : appInfoList) {
			try {
				if (!isSystemPackage(pm.getPackageInfo(appInfo.packageName,
						PackageManager.GET_META_DATA)) && !appInfo.packageName.equals("org.owasp.seraphimdroid")) {
					appList.add(appInfo.packageName);
				}
				// addToDatabase(appInfo.packageName);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// TODO Auto-generated method stub
		inflater.inflate(R.menu.main, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		setHasOptionsMenu(true);
		super.onCreate(savedInstanceState);
	}

	private boolean addToDatabase(String pkgName) {
		DatabaseHelper dbHelper = new DatabaseHelper(getActivity());
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		String sql = "SELECT * FROM locks where package_name=\'" + pkgName
				+ "\'";

		Cursor cursor = db.rawQuery(sql, null);

		if (cursor.moveToNext()) {
			cursor.close();
			db.close();
			dbHelper.close();
			return false;

		} else {

			ContentValues cv = new ContentValues();
			cv.put("package_name", pkgName);
			cv.put("locked", 0);
			db.insert(DatabaseHelper.TABLE_LOCKS, null, cv);
			cursor.close();
			db.close();
			dbHelper.close();
			return true;
		}

	}
}
