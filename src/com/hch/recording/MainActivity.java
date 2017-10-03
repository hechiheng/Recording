package com.hch.recording;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

@SuppressLint("SimpleDateFormat")
public class MainActivity extends Activity {
	private static String tag = "MainActivityLog";
	private Button button1;
	private ListView listView;
	private ArrayAdapter<String> adapter;

	private MediaPlayer mediaPlayer;
	private MediaRecorder mediaRecorder;

	private String filePath;
	private File file;
	private List<String> fileNameList = new ArrayList<String>();
	private String fileName;

	private boolean isRecord = false;
	private int position = -1;

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			adapter.notifyDataSetChanged();
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		filePath = Environment.getExternalStorageDirectory().getAbsolutePath()
				+ "/recording/";
		file = new File(filePath);
		if (!file.exists()) {
			file.mkdir();
		}

		button1 = (Button) findViewById(R.id.button1);
		listView = (ListView) findViewById(R.id.listView);

		String[] fileNames = file.list();
		for (String name : fileNames) {
			fileNameList.add(name);
		}
		adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, fileNameList);
		listView.setAdapter(adapter);

		button1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				isRecord = isRecord ? false : true;
				// Log.d(tag, getFileName());
				if (mediaRecorder == null) {
					mediaRecorder = new MediaRecorder();
				}
				if (isRecord) {
					fileName = getFileName();
					button1.setText("停止录音");
					mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
					mediaRecorder
							.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
					mediaRecorder.setOutputFile(filePath + fileName);
					mediaRecorder
							.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
					try {
						mediaRecorder.prepare();
						mediaRecorder.start();
					} catch (IOException e) {
						Log.e(tag, Log.getStackTraceString(e));
					}
				} else {
					button1.setText("开始录音");
					mediaRecorder.stop();
					mediaRecorder.release();
					mediaRecorder = null;
					fileNameList.add(fileName);
					Message message = new Message();
					handler.sendMessage(message);
					Toast.makeText(MainActivity.this,
							"录音保存在：" + filePath + fileName, Toast.LENGTH_SHORT)
							.show();

				}

			}
		});

		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Log.d(tag, fileNameList.get(arg2));
				if (mediaPlayer == null) {
					mediaPlayer = new MediaPlayer();
				}
				try {
					if (mediaPlayer.isPlaying() && position == arg2) {
						mediaPlayer.stop();
					} else {
						mediaPlayer.stop();
						mediaPlayer.seekTo(0);
						mediaPlayer.setDataSource(filePath
								+ fileNameList.get(arg2));
						mediaPlayer.prepare();
						mediaPlayer.start();
					}
				} catch (IOException e) {
					Log.e(tag, Log.getStackTraceString(e));
				}
				position = arg2;

			}
		});

	}

	private String getFileName() {
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
		String fileName = format.format(new Date()) + ".amr";
		return fileName;
	}
}
