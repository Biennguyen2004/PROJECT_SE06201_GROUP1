package com.example.smarthome.ui.main;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.smarthome.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MicActivity extends AppCompatActivity {

//    private FloatingActionButton btnMic;
//    private MediaRecorder recorder;
//    private String audioFilePath;
//    private boolean isRecording = false;
//    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB
//    private static final String SERVER_URL = "https://bef5-42-1-77-222.ngrok-free.app/api/audio/upload";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mic);

//        btnMic = findViewById(R.id.btnMic);
//
//        // Yêu cầu quyền
//        requestPermissions();
//
//        btnMic.setOnTouchListener((v, event) -> {
//            switch (event.getAction()) {
//                case MotionEvent.ACTION_DOWN:
//                    startRecording();
//                    return true;
//
//                case MotionEvent.ACTION_UP:
//                case MotionEvent.ACTION_CANCEL:
//                    stopRecording();
//                    uploadAudioFile(); // Gửi file lên server
//                    v.performClick();
//                    return true;
//            }
//            return false;
//        });
    }

//    // Bắt đầu ghi âm
//    private void startRecording() {
//        try {
//            File audioDir = getExternalFilesDir(Environment.DIRECTORY_MUSIC);
//            if (audioDir == null) {
//                Log.e("Audio File", "Không thể lấy thư mục lưu trữ!");
//                return;
//            }
//
//            if (!audioDir.exists() && !audioDir.mkdirs()) {
//                Log.e("Audio File", "Không thể tạo thư mục lưu file!");
//                return;
//            }
//
//            audioFilePath = audioDir.getAbsolutePath() + "/recorded_audio.mp3";
//
//            recorder = new MediaRecorder();
//            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
//            recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
//            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
//            recorder.setAudioSamplingRate(44100);
//            recorder.setAudioEncodingBitRate(128000);
//            recorder.setOutputFile(audioFilePath);
//
//            recorder.prepare();
//            recorder.start();
//            isRecording = true;
//
//            btnMic.setSupportBackgroundTintList(ContextCompat.getColorStateList(this, android.R.color.holo_red_dark));
//            btnMic.setImageResource(R.drawable.ic_mic_recording);
//
//            Toast.makeText(this, "Đang ghi âm...", Toast.LENGTH_SHORT).show();
//            Log.d("Audio File", "Bắt đầu ghi âm!");
//
//        } catch (IOException e) {
//            Log.e("Audio File", "Lỗi khi chuẩn bị ghi âm: " + e.getMessage());
//            if (recorder != null) {
//                recorder.release();
//                recorder = null;
//            }
//        }
//    }
//
//    // Dừng ghi âm
//    private void stopRecording() {
//        if (isRecording && recorder != null) {
//            try {
//                recorder.stop();
//                recorder.release();
//                recorder = null;
//                isRecording = false;
//
//                File file = new File(audioFilePath);
//                if (!file.exists() || file.length() == 0) {
//                    Log.e("Audio File", "Lưu file thất bại hoặc rỗng!");
//                    return;
//                }
//
//                btnMic.setSupportBackgroundTintList(ContextCompat.getColorStateList(this, R.color.purple_500));
//                btnMic.setImageResource(R.drawable.ic_mic);
//
//                Toast.makeText(this, "Ghi âm xong!", Toast.LENGTH_SHORT).show();
//                Log.d("Audio File", "File đã lưu tại: " + audioFilePath + " | Kích thước: " + file.length() + " bytes");
//
//            } catch (RuntimeException e) {
//                Log.e("Audio File", "Lỗi khi dừng ghi âm: " + e.getMessage());
//                if (recorder != null) {
//                    recorder.release();
//                    recorder = null;
//                }
//            }
//        } else {
//            Log.e("Audio File", "⚠ Không thể dừng ghi âm vì chưa bắt đầu!");
//        }
//    }
//
//    // Gửi file ghi âm lên server
//    private void uploadAudioFile() {
//        new Thread(() -> {
//            File audioFile = new File(audioFilePath);
//            if (!audioFile.exists() || audioFile.length() == 0) {
//                Log.e("Upload", "⚠ File không tồn tại hoặc rỗng!");
//                runOnUiThread(() -> Toast.makeText(MicActivity.this, "File không tồn tại!", Toast.LENGTH_SHORT).show());
//                return;
//            }
//
//            // Kiểm tra dung lượng file
//            if (audioFile.length() > MAX_FILE_SIZE) {
//                Log.e("Upload", "⚠ File quá lớn (>5MB), không thể gửi.");
//                runOnUiThread(() -> Toast.makeText(MicActivity.this, "File quá lớn! (5MB max)", Toast.LENGTH_SHORT).show());
//                return;
//            }
//
//            Log.d("Upload", "Đang gửi file có kích thước: " + audioFile.length() + " bytes");
//
//            OkHttpClient client = new OkHttpClient();
//            String fileType = "audio/mpeg";
//            RequestBody requestFile = RequestBody.create(MediaType.parse(fileType), audioFile);
//
//            RequestBody requestBody = new MultipartBody.Builder()
//                    .setType(MultipartBody.FORM)
//                    .addFormDataPart("file", "recorded_audio.mp3", requestFile)
//                    .build();
//
//            Request request = new Request.Builder()
//                    .url(SERVER_URL)
//                    .post(requestBody)
//                    .build();
//
//            try {
//                Response response = client.newCall(request).execute();
//                if (response.isSuccessful()) {
//                    String serverResponse = response.body().string();
//                    Log.d("Upload", "File uploaded successfully! Server response: " + serverResponse);
//                    runOnUiThread(() -> Toast.makeText(MicActivity.this, "Gửi thành công!", Toast.LENGTH_SHORT).show());
//                } else {
//                    Log.e("Upload", "Upload failed! Response code: " + response.code() + " - " + response.message());
//                    runOnUiThread(() -> Toast.makeText(MicActivity.this, "Gửi thất bại!", Toast.LENGTH_SHORT).show());
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//                Log.e("Upload", "Lỗi khi gửi file: " + e.getMessage());
//            }
//        }).start();
//    }
//
//    // Yêu cầu quyền
//    private void requestPermissions() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
//                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, 1000);
//            }
//        }
//    }
}
